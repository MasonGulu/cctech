package github.shrekshellraiser.cctech.common.peripheral.tape;

import dan200.computercraft.api.peripheral.IPeripheral;
import github.shrekshellraiser.cctech.CCTech;
import github.shrekshellraiser.cctech.client.screen.tape.CassetteDeckMenu;
import github.shrekshellraiser.cctech.common.item.StorageItem;
import github.shrekshellraiser.cctech.common.item.tape.TapeItem;
import github.shrekshellraiser.cctech.common.peripheral.StorageBlockEntity;
import github.shrekshellraiser.cctech.common.peripheral.tape.cassette.CassetteDeckPeripheral;
import github.shrekshellraiser.cctech.server.FileManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;
import static github.shrekshellraiser.cctech.server.FileManager.POINTER_SIZE;

public abstract class TapeBlockEntity extends StorageBlockEntity {
    protected byte[] data = new byte[2]; // data of cassette loaded
    protected String uuid;
    protected int pointer;
    protected boolean deviceInserted = false;
    protected boolean dataChanged = false;
    protected String deviceDir;

    public TapeBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return new CassetteDeckMenu(pContainerId, pPlayerInventory, this);
    }



    public String readChar() {
        if (!deviceInserted)
            return "";
        dataChanged = true;
        try {
            return String.valueOf((char) (data[(pointer++) + POINTER_SIZE] & 0xFF));
        } catch (IndexOutOfBoundsException e) {
            pointer = Math.max(pointer, 0);
            pointer = Math.min(pointer, data.length - POINTER_SIZE);
        }
        return "";
    }

    public boolean seekRel(int offset) {
        if (!deviceInserted)
            return false;
        dataChanged = true;
        pointer += offset;
        int target = pointer;
        pointer = Math.max(pointer, 0);
        pointer = Math.min(pointer, data.length - POINTER_SIZE);
        return pointer == target;
    }

    public boolean seekAbs(int target) {
        if (!deviceInserted)
            return false;
        dataChanged = true;
        pointer = target;
        pointer = Math.max(pointer, 0);
        pointer = Math.min(pointer, data.length - POINTER_SIZE);
        return pointer == target;
    }

    public boolean writeChar(char ch) {
        if (!deviceInserted)
            return false;
        dataChanged = true;
        try {
            data[(pointer++) + POINTER_SIZE] = (byte) (ch & 0xFF);
        } catch (ArrayIndexOutOfBoundsException e) {
            pointer = data.length - POINTER_SIZE;
            return false;
        }
        return true;
    }
    public boolean setLabel(String label) {
        if (!deviceInserted)
            return false;
        ItemStack item = itemHandler.getStackInSlot(0);
        ((StorageItem) item.getItem()).setLabel(item, label);
        return true;
    }

    public boolean clearLabel() {
        if (!deviceInserted)
            return false;
        ItemStack item = itemHandler.getStackInSlot(0);
        ((StorageItem) item.getItem()).removeLabel(item);
        return true;
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
