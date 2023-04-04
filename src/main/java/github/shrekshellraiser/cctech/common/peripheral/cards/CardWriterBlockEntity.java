package github.shrekshellraiser.cctech.common.peripheral.cards;

import dan200.computercraft.api.lua.ILuaCallback;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.NotAttachedException;
import github.shrekshellraiser.cctech.common.ModBlockEntities;
import github.shrekshellraiser.cctech.common.ModProperties;
import github.shrekshellraiser.cctech.common.item.StorageItem;
import github.shrekshellraiser.cctech.common.item.cards.MagCardItem;
import github.shrekshellraiser.cctech.common.item.tape.CassetteItem;
import github.shrekshellraiser.cctech.common.item.tape.TapeItem;
import github.shrekshellraiser.cctech.common.peripheral.NoDeviceException;
import github.shrekshellraiser.cctech.common.peripheral.StorageBlockEntity;
import github.shrekshellraiser.cctech.server.FileManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static github.shrekshellraiser.cctech.server.FileManager.POINTER_SIZE;

public class CardWriterBlockEntity extends StorageBlockEntity {
    private String writerUUID;
    public CardWriterBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.CARD_WRITER.get(), pWorldPosition, pBlockState);
        peripheral = new CardWriterPeripheral(this);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return null;
    }

    public void setHandler(ItemStackHandler itemStackHandler) {
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, itemStackHandler.getStackInSlot(i));
        }
    }

    public boolean setLabel(String label) throws LuaException {
        assertReady();
        ItemStack item = itemHandler.getStackInSlot(0);
        ((MagCardItem) item.getItem()).setLabel(item, label);
        return true;
    }

    public boolean clearLabel() throws LuaException {
        assertReady();
        ItemStack item = itemHandler.getStackInSlot(0);
        ((MagCardItem) item.getItem()).removeLabel(item);
        return true;
    }

    public String getLabel() throws LuaException {
        assertReady();
        ItemStack item = itemHandler.getStackInSlot(0);
        return ((MagCardItem) item.getItem()).getLabel(item);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, CardWriterBlockEntity pBlockEntity) {

    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        if (writerUUID == null) {
            writerUUID = String.valueOf(UUID.randomUUID());
        }
        tag.putString("cctech.uuid", writerUUID);
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("cctech.uuid")) {
            writerUUID = nbt.getString("cctech.uuid");
        } else {
            writerUUID = String.valueOf(UUID.randomUUID());
        }
    }

    public void onRightClick(Level pLevel, Player pPlayer, InteractionHand pHand) {
        if (pPlayer.getMainHandItem().getItem() instanceof MagCardItem
                && getItem() == Items.AIR) {
            itemHandler.setStackInSlot(0, pPlayer.getMainHandItem());
            pPlayer.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.AIR));
        } else if (pPlayer.getMainHandItem().getItem() == Items.AIR) {
            // empty hand
            pPlayer.setItemSlot(EquipmentSlot.MAINHAND, itemHandler.getStackInSlot(0));
            itemHandler.setStackInSlot(0, new ItemStack(Items.AIR));
        }
    }

    private void assertReady() throws LuaException {
        BlockState state = getBlockState();
        if (state.getValue(ModProperties.OPEN)) {
            throw new LuaException("Door open");
        }
        if (!(getItem() instanceof MagCardItem)) {
            throw new LuaException("No card inserted");
        }
    }

    public void writeCard(String contents) throws LuaException {
        assertReady();
        MagCardItem item = (MagCardItem)getItem();
        ItemStack stack = getRenderStack();
        item.setContents(stack, contents);
        item.setUUID(stack, writerUUID);
    }

    public HashMap<String,String> readCard() throws LuaException {
        assertReady();
        HashMap<String,String> map = new HashMap<>();
        MagCardItem item = (MagCardItem)getItem();
        ItemStack stack = getRenderStack();
        String uuid = item.getUUID(stack);
        String contents = item.getContents(stack);
        map.put("uuid", uuid);
        map.put("contents", contents);
        if (uuid != null && contents != null) {
            return map;
        }
        return null;
    }

    public String getUUID() {
        return writerUUID;
    }
}