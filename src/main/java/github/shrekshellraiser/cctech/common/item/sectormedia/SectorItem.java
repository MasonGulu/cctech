package github.shrekshellraiser.cctech.common.item.sectormedia;

import github.shrekshellraiser.cctech.common.ModCreativeModeTab;
import github.shrekshellraiser.cctech.common.item.StorageItem;
import net.minecraft.world.item.ItemStack;

public class SectorItem extends StorageItem {
    public int getCylinders() {
        return cylinders;
    }

    public int getHeads() {
        return heads;
    }

    public int getSectors() {
        return sectors;
    }

    public int getSectorSize() {
        return sectorSize;
    }

    public int getCapacity() {
        return capacity;
    }

    protected int cylinders; // same as floppy tracks
    protected int heads;
    protected int sectors;
    protected int sectorSize;

    protected int capacity;

    public SectorItem(int cylinders, int heads, int sectors, int sectorSize) {
        super(new Properties().tab(ModCreativeModeTab.CCTECH_TAB).stacksTo(1));
        this.cylinders = cylinders;
        this.heads = heads;
        this.sectors = sectors;
        this.sectorSize = sectorSize;

        this.capacity = cylinders * heads * sectors * sectorSize;
    }

    @Override
    public int getSize(ItemStack item) {
        return getCapacity();
    }
}
