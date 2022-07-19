package github.shrekshellraiser.cctech.client.screen.tape;

import github.shrekshellraiser.cctech.client.screen.BaseStorageMenu;
import github.shrekshellraiser.cctech.client.screen.ModMenuTypes;
import github.shrekshellraiser.cctech.client.screen.slot.ModReelSlot;
import github.shrekshellraiser.cctech.common.ModBlocks;
import github.shrekshellraiser.cctech.common.peripheral.tape.reel.ReelToReelBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;

public class ReelToReelMenu extends BaseStorageMenu {
    private final ReelToReelBlockEntity blockEntity;
    private final Level level;

    public ReelToReelMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public ReelToReelMenu(int pContainerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.REEL_TO_REEL.get(), pContainerId);
        checkContainerSize(inv, 1);
        blockEntity = ((ReelToReelBlockEntity) entity);
        this.level = inv.player.level;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            this.addSlot(new ModReelSlot(handler, 0, 62, 44));
        });
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.REEL_TO_REEL.get());
    }

}
