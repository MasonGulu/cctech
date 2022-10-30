package github.shrekshellraiser.cctech.common.peripheral.tape.cassette;

import dan200.computercraft.api.lua.LuaException;
import github.shrekshellraiser.cctech.CCTech;
import github.shrekshellraiser.cctech.common.ModProperties;
import github.shrekshellraiser.cctech.common.ModBlockEntities;
import github.shrekshellraiser.cctech.common.item.tape.CassetteItem;
import github.shrekshellraiser.cctech.common.peripheral.tape.TapeBlockEntity;
import github.shrekshellraiser.cctech.client.screen.tape.CassetteDeckMenu;
import net.minecraft.core.BlockPos;
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
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CassetteDeckBlockEntity extends TapeBlockEntity {
    public CassetteDeckBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.CASSETTE_DECK.get(), pWorldPosition, pBlockState);
        deviceDir = CassetteItem.getDeviceDir();
        peripheral = new CassetteDeckPeripheral(this);
    }

    @Override
    public void assertReady() throws LuaException {
        if (getBlockState().getValue(ModProperties.OPEN)) {
            throw new LuaException("Door open");
        }
        super.assertReady();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return new TranslatableComponent("block.cctech.cassette_deck");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return null;// new CassetteDeckMenu(pContainerId, pPlayerInventory, this);
    }

    public void onRightClick(Level pLevel, Player pPlayer, InteractionHand pHand) {
        if (pPlayer.getMainHandItem().getItem() instanceof CassetteItem
             && getItem() == Items.AIR) {
            itemHandler.setStackInSlot(0, pPlayer.getMainHandItem());
            pPlayer.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.AIR));
        } else if (pPlayer.getMainHandItem().getItem() == Items.AIR) {
            // empty hand
            pPlayer.setItemSlot(EquipmentSlot.MAINHAND, itemHandler.getStackInSlot(0));
            itemHandler.setStackInSlot(0, new ItemStack(Items.AIR));
        }
    }
}
