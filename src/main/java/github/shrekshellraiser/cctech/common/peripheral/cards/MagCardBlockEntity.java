package github.shrekshellraiser.cctech.common.peripheral.cards;

import dan200.computercraft.api.peripheral.IPeripheral;
import github.shrekshellraiser.cctech.CCTech;
import github.shrekshellraiser.cctech.common.ModBlockEntities;
import github.shrekshellraiser.cctech.common.ModBlocks;
import github.shrekshellraiser.cctech.common.item.StorageItem;
import github.shrekshellraiser.cctech.common.item.tape.CassetteItem;
import github.shrekshellraiser.cctech.common.item.tape.TapeItem;
import github.shrekshellraiser.cctech.common.network.ModMessages;
import github.shrekshellraiser.cctech.common.network.packet.ItemStackSyncS2CPacket;
import github.shrekshellraiser.cctech.server.FileManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

public class MagCardBlockEntity extends BlockEntity {
    protected IPeripheral peripheral;
    protected LazyOptional<IPeripheral> peripheralCap;

    protected LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public MagCardBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.MAG_CARD_READER.get(), pWorldPosition, pBlockState);
        peripheral = new MagCardPeripheral(this);
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction direction) {
        if (cap == CAPABILITY_PERIPHERAL) {
            if (peripheralCap == null) {
                peripheralCap = LazyOptional.of(() -> peripheral);
            }
            return peripheralCap.cast();
        }
        return super.getCapability(cap, direction);
    }

    public void cardScanned(String writerUUID, String contents) {
        ((MagCardPeripheral)peripheral).cardScanned(writerUUID, contents);
    }
}
