package com.shrekshellraiser.cctech.common.blockentities;

import com.shrekshellraiser.cctech.CCTech;
import com.shrekshellraiser.cctech.common.ModProperties;
import com.shrekshellraiser.cctech.common.item.CassetteItem;
import com.shrekshellraiser.cctech.common.item.IStorageItem;
import com.shrekshellraiser.cctech.common.item.ReelItem;
import com.shrekshellraiser.cctech.common.peripheral.ReelToReelPeripheral;
import com.shrekshellraiser.cctech.screen.ReelToReelMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

public class ReelToReelBlockEntity extends BaseStorageBlockEntity {

    @Override
    protected ItemStackHandler createItemHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                assert level != null;
                if (!level.isClientSide()) {
                    ItemStack item = getStackInSlot(slot);
                    if (item.getItem() instanceof IStorageItem && !deviceInserted) {
                        // cassette has just been inserted
                        CCTech.LOGGER.debug("Reel inserted");
                        loadData(item);
                        deviceInserted = true;
                    } else if ((uuid != null) && deviceInserted) {
                        // cassette has been removed
                        CCTech.LOGGER.debug("Reel removed");
                        pointer = 0;
                        saveData(item);
                        deviceInserted = false;
                        uuid = null;
                    }
                }
            }
        };
    }
    public ReelToReelBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.REEL_TO_REEL.get(), pWorldPosition, pBlockState);
        deviceDir = "reel";
    }
    protected ReelToReelPeripheral peripheral = new ReelToReelPeripheral(this);
    @Override
    public @NotNull Component getDisplayName() {
        return new TextComponent("Reel To Reel");
    }
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return new ReelToReelMenu(pContainerId, pPlayerInventory, this);
    }
    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction direction) {
        if (cap == CAPABILITY_PERIPHERAL) {
            if (peripheralCap == null) {
                peripheralCap = LazyOptional.of(() -> peripheral);
            }
            return peripheralCap.cast();
        } else if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public void drops() {
        pointer = 0;
        super.drops();
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, ReelToReelBlockEntity pBlockEntity) {
        boolean hasTape = pBlockEntity.getItem() instanceof ReelItem;
        if (hasTape != pState.getValue(ModProperties.FILLED)) {
            CCTech.LOGGER.debug("State of cassette deck changed");
            pState = pState.setValue(ModProperties.FILLED, hasTape);
            pLevel.setBlock(pPos, pState, 3);
            setChanged(pLevel, pPos, pState);
        }
    }

    public int getPointer() {
        return pointer;
    }
}
