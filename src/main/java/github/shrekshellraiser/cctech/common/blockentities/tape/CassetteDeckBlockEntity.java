package github.shrekshellraiser.cctech.common.blockentities.tape;

import github.shrekshellraiser.cctech.CCTech;
import github.shrekshellraiser.cctech.common.ModProperties;
import github.shrekshellraiser.cctech.common.blockentities.ModBlockEntities;
import github.shrekshellraiser.cctech.common.item.tape.CassetteItem;
import github.shrekshellraiser.cctech.common.peripheral.CassetteDeckPeripheral;
import github.shrekshellraiser.cctech.screen.CassetteDeckMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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

public class CassetteDeckBlockEntity extends TapeBlockEntity {
    public CassetteDeckBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.CASSETTE_DECK.get(), pWorldPosition, pBlockState);
        deviceDir = CassetteItem.getDeviceDir();
    }

    protected CassetteDeckPeripheral peripheral = new CassetteDeckPeripheral(this);

    @Override
    public @NotNull Component getDisplayName() {
        return new TranslatableComponent("block.cctech.cassette_deck");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return new CassetteDeckMenu(pContainerId, pPlayerInventory, this);
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

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, CassetteDeckBlockEntity pBlockEntity) {
        boolean hasTape = pBlockEntity.getItem() instanceof CassetteItem;
        if (hasTape != pState.getValue(ModProperties.FILLED)) {
            CCTech.LOGGER.debug("State of cassette deck changed");
            pState = pState.setValue(ModProperties.FILLED, hasTape);
            pLevel.setBlock(pPos, pState, 3);
            setChanged(pLevel, pPos, pState);
        }
    }
}
