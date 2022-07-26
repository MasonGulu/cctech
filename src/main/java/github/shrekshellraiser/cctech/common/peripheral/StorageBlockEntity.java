package github.shrekshellraiser.cctech.common.peripheral;

import dan200.computercraft.api.peripheral.IPeripheral;
import github.shrekshellraiser.cctech.CCTech;
import github.shrekshellraiser.cctech.client.screen.tape.CassetteDeckMenu;
import github.shrekshellraiser.cctech.common.ModBlockEntities;
import github.shrekshellraiser.cctech.common.ModProperties;
import github.shrekshellraiser.cctech.common.item.StorageItem;
import github.shrekshellraiser.cctech.common.item.tape.CassetteItem;
import github.shrekshellraiser.cctech.common.item.tape.TapeItem;
import github.shrekshellraiser.cctech.common.peripheral.tape.TapeBlockEntity;
import github.shrekshellraiser.cctech.common.peripheral.tape.cassette.CassetteDeckPeripheral;
import github.shrekshellraiser.cctech.server.FileManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;
import static github.shrekshellraiser.cctech.server.FileManager.POINTER_SIZE;

public abstract class StorageBlockEntity extends BlockEntity implements MenuProvider {
    protected byte[] data = new byte[2]; // data of cassette loaded
    protected String uuid;
    protected boolean deviceInserted = false;
    protected boolean dataChanged = false;
    protected String deviceDir;

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
    public abstract @NotNull Component getDisplayName();
    public static void tick(Level pLevel, BlockPos pPos, @NotNull BlockState pState, @NotNull StorageBlockEntity pBlockEntity) {
        boolean hasTape = pBlockEntity.getItem() instanceof StorageItem;
        if (hasTape != pState.getValue(ModProperties.FILLED)) {
            CCTech.LOGGER.debug("State of storage device changed");
            pState = pState.setValue(ModProperties.FILLED, hasTape);
            pLevel.setBlock(pPos, pState, 3);
            setChanged(pLevel, pPos, pState);
        }
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
