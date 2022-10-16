package github.shrekshellraiser.cctech.common.peripheral.sectormedia.zip;

import github.shrekshellraiser.cctech.CCTech;
import github.shrekshellraiser.cctech.client.screen.sectormedia.ZipDriveMenu;
import github.shrekshellraiser.cctech.client.screen.tape.CassetteDeckMenu;
import github.shrekshellraiser.cctech.common.ModBlockEntities;
import github.shrekshellraiser.cctech.common.ModProperties;
import github.shrekshellraiser.cctech.common.item.sectormedia.ZipDiskItem;
import github.shrekshellraiser.cctech.common.item.tape.CassetteItem;
import github.shrekshellraiser.cctech.common.peripheral.StorageBlockEntity;
import github.shrekshellraiser.cctech.common.peripheral.sectormedia.SectorBlockEntity;
import github.shrekshellraiser.cctech.common.peripheral.tape.TapeBlockEntity;
import github.shrekshellraiser.cctech.common.peripheral.tape.cassette.CassetteDeckPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

public class ZipDriveBlockEntity extends SectorBlockEntity {
    public ZipDriveBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.ZIP_DRIVE.get(), pWorldPosition, pBlockState);
        deviceDir = ZipDiskItem.getDeviceDir();
        peripheral = new ZipDrivePeripheral(this);
    }

    @Override
    protected void itemInserted(ItemStack item) {
        super.itemInserted(item);
        ((ZipDrivePeripheral)peripheral).deviceInserted();
    }

    @Override
    protected void itemRemoved(ItemStack item) {
        super.itemRemoved(item);
        ((ZipDrivePeripheral)peripheral).deviceRemoved();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return new TranslatableComponent("block.cctech.zip_drive");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return new ZipDriveMenu(pContainerId, pPlayerInventory, this);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, ZipDriveBlockEntity pBlockEntity) {
        boolean hasTape = pBlockEntity.getItem() instanceof ZipDiskItem;
        if (hasTape != pState.getValue(ModProperties.FILLED)) {
            CCTech.LOGGER.debug("State of zip drive changed");
            pState = pState.setValue(ModProperties.FILLED, hasTape);
            pLevel.setBlock(pPos, pState, 3);
            setChanged(pLevel, pPos, pState);
        }
    }
}
