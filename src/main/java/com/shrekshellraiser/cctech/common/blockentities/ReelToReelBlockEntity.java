package com.shrekshellraiser.cctech.common.blockentities;

import com.shrekshellraiser.cctech.CCTech;
import com.shrekshellraiser.cctech.common.ModProperties;
import com.shrekshellraiser.cctech.common.item.CassetteItem;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

public class ReelToReelBlockEntity extends BaseStorageBlockEntity {
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


    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, ReelToReelBlockEntity pBlockEntity) {
        boolean hasTape = pBlockEntity.getItem() instanceof ReelItem;
        if (hasTape != pState.getValue(ModProperties.FILLED)) {
            CCTech.LOGGER.debug("State of cassette deck changed");
            pState = pState.setValue(ModProperties.FILLED, hasTape);
            pLevel.setBlock(pPos, pState, 3);
            setChanged(pLevel, pPos, pState);
        }
    }
}
