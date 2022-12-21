package github.shrekshellraiser.cctech.common.peripheral.tape.reel;

import dan200.computercraft.api.lua.LuaException;
import github.shrekshellraiser.cctech.common.ModBlockEntities;
import github.shrekshellraiser.cctech.common.ModProperties;
import github.shrekshellraiser.cctech.common.config.CCTechCommonConfigs;
import github.shrekshellraiser.cctech.common.item.tape.ReelItem;
import github.shrekshellraiser.cctech.common.peripheral.tape.TapeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReelToReelBlockEntity extends TapeBlockEntity {

    protected boolean isLocked;

    @Override
    public void assertReady() throws LuaException {
        BlockState state = getBlockState();
        if (state.getValue(ModProperties.OPEN)) {
            throw new LuaException("Door open");
        } else if (!state.getValue(ModProperties.LOCKED)) {
            throw new LuaException("Door unlocked");
        }
        super.assertReady();
    }

    @Override
    protected void itemRemoved(ItemStack item) {
        pointer = 0;
        super.itemRemoved(item);
    }

    @Override
    protected void itemInserted(ItemStack item) {
        super.itemInserted(item);
        pointer = 0;
    }

    public ReelToReelBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.REEL_TO_REEL.get(), pWorldPosition, pBlockState);
        deviceDir = ReelItem.getDeviceDir();
        peripheral = new ReelToReelPeripheral(this);
        ticksPerByte = 1.0 / (CCTechCommonConfigs.REEL_BYTES_PER_SECOND.get() / 20.0);
    }

    public boolean setLock(boolean state) throws LuaException {
         if (getBlockState().getValue(ModProperties.OPEN)) {
             throw new LuaException("Door open");
         }
        isLocked = state;
        return isLocked;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.putBoolean("locked", isLocked);
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        isLocked = nbt.getBoolean("locked");
    }

    @Override
    public void drops() {
        pointer = 0;
        super.drops();
    }

    public int getPointer() {
        return pointer;
    }

    public void onRightClick(Level pLevel, Player pPlayer, InteractionHand pHand) {
        if (pPlayer.getMainHandItem().getItem() instanceof ReelItem
                && getItem() == Items.AIR) {
            itemHandler.setStackInSlot(0, pPlayer.getMainHandItem());
            pPlayer.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.AIR));
        } else if (pPlayer.getMainHandItem().getItem() == Items.AIR) {
            // empty hand
            pPlayer.setItemSlot(EquipmentSlot.MAINHAND, itemHandler.getStackInSlot(0));
            itemHandler.setStackInSlot(0, new ItemStack(Items.AIR));
        }
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, ReelToReelBlockEntity pBlockEntity) {
        TapeBlockEntity.tick(pLevel, pPos, pState, pBlockEntity);
        if (!pLevel.isClientSide()) {
            if (pState.getValue(ModProperties.LOCKED) != pBlockEntity.isLocked) {
                pState = pState.setValue(ModProperties.LOCKED, pBlockEntity.isLocked);
                pLevel.setBlock(pPos, pState, 3);
                pLevel.blockEntityChanged(pPos);
                pLevel.updateNeighbourForOutputSignal(pPos, pState.getBlock());
            }
        }
    }
}
