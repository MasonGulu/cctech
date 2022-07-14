package github.shrekshellraiser.cctech.screen;

import github.shrekshellraiser.cctech.common.block.ModBlocks;
import github.shrekshellraiser.cctech.common.blockentities.tape.CassetteDeckBlockEntity;
import github.shrekshellraiser.cctech.screen.slot.ModCassetteSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;

public class CassetteDeckMenu extends BaseStorageMenu {
    private final CassetteDeckBlockEntity blockEntity;
    private final Level level;

    public CassetteDeckMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public CassetteDeckMenu(int pContainerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.CASSETTE_DECK.get(), pContainerId);
        checkContainerSize(inv, 1);
        blockEntity = ((CassetteDeckBlockEntity) entity);
        this.level = inv.player.level;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            this.addSlot(new ModCassetteSlot(handler, 0, 80, 35));
        });
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.CASSETTE_DECK.get());
    }

}
