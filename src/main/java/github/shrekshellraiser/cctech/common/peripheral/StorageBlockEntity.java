package github.shrekshellraiser.cctech.common.peripheral;

import dan200.computercraft.api.peripheral.IPeripheral;
import github.shrekshellraiser.cctech.CCTech;
import github.shrekshellraiser.cctech.common.ModProperties;
import github.shrekshellraiser.cctech.common.item.StorageItem;
import github.shrekshellraiser.cctech.common.item.tape.CassetteItem;
import github.shrekshellraiser.cctech.common.item.tape.TapeItem;
import github.shrekshellraiser.cctech.common.network.ModMessages;
import github.shrekshellraiser.cctech.common.network.packet.ItemStackSyncS2CPacket;
import github.shrekshellraiser.cctech.server.FileManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
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

public abstract class StorageBlockEntity extends BlockEntity {
    protected byte[] data = new byte[2]; // data of cassette loaded
    protected String uuid;
    protected boolean deviceInserted = false;
    protected boolean dataChanged = false;
    protected String deviceDir;

    protected IPeripheral peripheral;

    protected LazyOptional<IPeripheral> peripheralCap;

    protected LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public StorageBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
        deviceDir = CassetteItem.getDeviceDir();
    }

    public abstract AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer);

    protected final ItemStackHandler itemHandler = createItemHandler();

    public Item getItem() {
        return itemHandler.getStackInSlot(0).getItem();
    }

    public ItemStack getRenderStack() {
        return itemHandler.getStackInSlot(0);
    }

    protected ItemStackHandler createItemHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                assert level != null;
                if (!level.isClientSide()) {
                    ItemStack item = getStackInSlot(slot);
                    if (item.getItem() instanceof StorageItem && !deviceInserted) {
                        itemInserted(item);
                    } else if ((uuid != null) && deviceInserted) {
                        itemRemoved(item);
                    }
                    ModMessages.sendToClients(new ItemStackSyncS2CPacket(this, worldPosition));
                }
            }
        };
    }
    protected void itemInserted(ItemStack item) {
        CCTech.LOGGER.debug("Storage item inserted");
        loadData(item);
        deviceInserted = true;
    }
    protected void itemRemoved(ItemStack item) {
        saveData(item);
        deviceInserted = false;
        uuid = null;
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
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
//        for (int i = 0; i < itemHandler.getSlots(); i++) {
//            inventory.setItem(i, itemHandler.getStackInSlot(i));
//        } generic drop handling
        inventory.setItem(0, itemHandler.getStackInSlot(0)); // discreet drop handling
        ItemStack cassette = itemHandler.getStackInSlot(0);
        if (cassette.getItem() instanceof StorageItem) {
            CCTech.LOGGER.debug("Cassette dropped out of drive");
            saveData(cassette);
            deviceInserted = false;
        }
        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(tag);
        ItemStack cassette = itemHandler.getStackInSlot(0);
        if (cassette.getItem() instanceof TapeItem) {
            CCTech.LOGGER.debug("Device in drive on drive save");
            saveData(cassette);
            deviceInserted = true;
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = new CompoundTag();
        saveAdditional(nbt);
        return nbt;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        ItemStack cassette = itemHandler.getStackInSlot(0);
        if (cassette.getItem() instanceof TapeItem) {
            CCTech.LOGGER.debug("Device in drive on drive load");
            loadData(cassette);
            deviceInserted = true;
        }
    }
    protected void loadData(ItemStack item) {
        uuid = ((StorageItem) item.getItem()).getUUID(item);
        data = FileManager.getData(deviceDir, uuid, ((StorageItem) item.getItem()).getSize(item));
    }
    protected void saveData(ItemStack item) {
        FileManager.saveData(data, deviceDir, uuid);
    }
}
