package github.shrekshellraiser.cctech.client.screen.sectormedia;

import github.shrekshellraiser.cctech.client.screen.BaseStorageMenu;
import github.shrekshellraiser.cctech.client.screen.ModMenuTypes;
import github.shrekshellraiser.cctech.client.screen.slot.ModCassetteSlot;
import github.shrekshellraiser.cctech.client.screen.slot.ModZipSlot;
import github.shrekshellraiser.cctech.common.ModBlocks;
import github.shrekshellraiser.cctech.common.peripheral.sectormedia.zip.ZipDriveBlockEntity;
import github.shrekshellraiser.cctech.common.peripheral.tape.cassette.CassetteDeckBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;

public class ZipDriveMenu extends BaseStorageMenu {
    private final ZipDriveBlockEntity blockEntity;
    private final Level level;

    public ZipDriveMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public ZipDriveMenu(int pContainerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.ZIP_DRIVE.get(), pContainerId);
        checkContainerSize(inv, 1);
        blockEntity = ((ZipDriveBlockEntity) entity);
        this.level = inv.player.level;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            this.addSlot(new ModZipSlot(handler, 0, 80, 35));
        });
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.ZIP_DRIVE.get());
    }

}
