package github.shrekshellraiser.cctech.common.peripheral.tape;

import dan200.computercraft.api.peripheral.IPeripheral;
import github.shrekshellraiser.cctech.CCTech;
import github.shrekshellraiser.cctech.common.item.StorageItem;
import github.shrekshellraiser.cctech.common.item.tape.TapeItem;
import github.shrekshellraiser.cctech.server.FileManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import static github.shrekshellraiser.cctech.server.FileManager.POINTER_SIZE;

public abstract class TapeBlockEntity extends BlockEntity implements MenuProvider {
    protected byte[] data = new byte[2]; // data of cassette loaded
    protected String uuid;
    protected int pointer;
    protected boolean deviceInserted = false;
    protected boolean dataChanged = false;
    protected String deviceDir;

    public TapeBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    protected LazyOptional<IPeripheral> peripheralCap;

    protected ItemStackHandler createItemHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                assert level != null;
                if (!level.isClientSide()) {
                    ItemStack item = getStackInSlot(slot);
                    if (item.getItem() instanceof TapeItem && !deviceInserted) {
                        itemInserted(item);
                    } else if ((uuid != null) && deviceInserted) {
                        itemRemoved(item);
                    }
                }
            }
        };
    }

    protected void itemInserted(ItemStack item) {
        // cassette has just been inserted
        CCTech.LOGGER.debug("Cassette inserted");
        loadData(item);
        deviceInserted = true;
    }

    protected void itemRemoved(ItemStack item) {
        // cassette has been removed
        CCTech.LOGGER.debug("Cassette removed");
        saveData(item);
        deviceInserted = false;
        uuid = null;
    }

    protected final ItemStackHandler itemHandler = createItemHandler();

    public Item getItem() {
        return itemHandler.getStackInSlot(0).getItem();
    }

    protected LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

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

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
//        for (int i = 0; i < itemHandler.getSlots(); i++) {
//            inventory.setItem(i, itemHandler.getStackInSlot(i));
//        } generic drop handling
        inventory.setItem(0, itemHandler.getStackInSlot(0)); // discreet drop handling
        ItemStack cassette = itemHandler.getStackInSlot(0);
        if (cassette.getItem() instanceof TapeItem) {
            CCTech.LOGGER.debug("Cassette dropped out of drive");
            saveData(cassette);
            deviceInserted = false;
        }

        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public String readChar() {
        if (!deviceInserted)
            return "";
        dataChanged = true;
        try {
            return String.valueOf((char) (data[(pointer++) + POINTER_SIZE] & 0xFF));
        } catch (IndexOutOfBoundsException e) {
            pointer = Math.max(pointer, 0);
            pointer = Math.min(pointer, data.length - POINTER_SIZE);
        }
        return "";
    }

    public boolean seekRel(int offset) {
        if (!deviceInserted)
            return false;
        dataChanged = true;
        pointer += offset;
        int target = pointer;
        pointer = Math.max(pointer, 0);
        pointer = Math.min(pointer, data.length - POINTER_SIZE);
        return pointer == target;
    }

    public boolean seekAbs(int target) {
        if (!deviceInserted)
            return false;
        dataChanged = true;
        pointer = target;
        pointer = Math.max(pointer, 0);
        pointer = Math.min(pointer, data.length - POINTER_SIZE);
        return pointer == target;
    }

    public boolean writeChar(char ch) {
        if (!deviceInserted)
            return false;
        dataChanged = true;
        try {
            data[(pointer++) + POINTER_SIZE] = (byte) (ch & 0xFF);
        } catch (ArrayIndexOutOfBoundsException e) {
            pointer = data.length - POINTER_SIZE;
            return false;
        }
        return true;
    }
    public boolean setLabel(String label) {
        if (!deviceInserted)
            return false;
        ItemStack item = itemHandler.getStackInSlot(0);
        ((StorageItem) item.getItem()).setLabel(item, label);
        return true;
    }

    public boolean clearLabel() {
        if (!deviceInserted)
            return false;
        ItemStack item = itemHandler.getStackInSlot(0);
        ((StorageItem) item.getItem()).removeLabel(item);
        return true;
    }

    void loadData(ItemStack item) {
        uuid = ((StorageItem) item.getItem()).getUUID(item);
        data = FileManager.getData(deviceDir, uuid, ((TapeItem) item.getItem()).getLength(item) + POINTER_SIZE);
        pointer = FileManager.getPointer(data);
    }

    void saveData(ItemStack item) {
        FileManager.saveData(data, pointer, deviceDir, uuid);
    }


}
