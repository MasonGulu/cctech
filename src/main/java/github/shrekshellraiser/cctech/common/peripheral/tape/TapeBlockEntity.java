package github.shrekshellraiser.cctech.common.peripheral.tape;

import dan200.computercraft.api.lua.ILuaCallback;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import github.shrekshellraiser.cctech.common.item.StorageItem;
import github.shrekshellraiser.cctech.common.item.tape.TapeItem;
import github.shrekshellraiser.cctech.common.peripheral.NoDeviceException;
import github.shrekshellraiser.cctech.common.peripheral.StorageBlockEntity;
import github.shrekshellraiser.cctech.server.FileManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Timer;

import static github.shrekshellraiser.cctech.server.FileManager.POINTER_SIZE;

public abstract class TapeBlockEntity extends StorageBlockEntity {
    protected byte[] data = new byte[2]; // data of cassette loaded
    protected int pointer;
    protected String deviceDir;

    public TapeBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return null;
    }



    protected void assertReady() throws LuaException {
        if (!deviceInserted) {
            throw new NoDeviceException();
        }
    }

    public void setHandler(ItemStackHandler itemStackHandler) {
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, itemStackHandler.getStackInSlot(i));
        }
    }

    public String readData(int amount) throws LuaException {
        assertReady();
        dataChanged = true;
        byte[] chars = new byte[amount];
        try {
            System.arraycopy(data, pointer+POINTER_SIZE, chars, 0, amount);
            pointer += amount;
        } catch (IndexOutOfBoundsException e) {
            pointer = Math.max(pointer, 0);
            pointer = Math.min(pointer, data.length - POINTER_SIZE);
            throw new LuaException("Attempt to read past tape end.");
        }
        return new String(chars);
    }

    public int seekRel(int offset) throws LuaException {
        assertReady();
        dataChanged = true;
        int startingPointer = pointer;
        pointer += offset;
        pointer = Math.max(pointer, 0);
        pointer = Math.min(pointer, data.length - POINTER_SIZE);
        return pointer - startingPointer;
    }

    public int seekAbs(int target) throws LuaException {
        assertReady();
        dataChanged = true;
        pointer = target;
        pointer = Math.max(pointer, 0);
        pointer = Math.min(pointer, data.length - POINTER_SIZE);
        return target - pointer;
    }

    public boolean write(String str) throws LuaException {
        assertReady();
        byte[] chars = str.getBytes(StandardCharsets.UTF_8);
        dataChanged = true;
        try {
            System.arraycopy(chars, 0, data, pointer+POINTER_SIZE, str.length());
            pointer = pointer + str.length();
        } catch (ArrayIndexOutOfBoundsException e) {
            pointer = data.length - POINTER_SIZE;
            return false;
        }
        return true;
    }
    public boolean setLabel(String label) throws LuaException {
        assertReady();
        ItemStack item = itemHandler.getStackInSlot(0);
        ((StorageItem) item.getItem()).setLabel(item, label);
        return true;
    }

    public boolean clearLabel() throws LuaException {
        assertReady();
        ItemStack item = itemHandler.getStackInSlot(0);
        ((StorageItem) item.getItem()).removeLabel(item);
        return true;
    }

    public int getSize() throws LuaException {
        assertReady();
        return data.length - POINTER_SIZE;
    }

    @Override
    protected void loadData(ItemStack item) {
        uuid = ((StorageItem) item.getItem()).getUUID(item);
        data = FileManager.getData(deviceDir, uuid, ((TapeItem) item.getItem()).getLength(item) + POINTER_SIZE);
        pointer = FileManager.getPointer(data);
    }
    @Override
    protected void saveData(ItemStack item) {
        FileManager.saveData(data, pointer, deviceDir, uuid);
    }



}
