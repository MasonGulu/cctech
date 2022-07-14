package github.shrekshellraiser.cctech.common.item;

import github.shrekshellraiser.cctech.common.ModCreativeModeTab;

public class SectorItem extends StorageItem {
    public int getTracks() {
        return tracks;
    }

    public int getSectorsPerTrack() {
        return sectorsPerTrack;
    }

    public int getBytesPerSector() {
        return bytesPerSector;
    }

    public int getSides() {
        return sides;
    }

    public int getSize() {
        return size;
    }

    protected int tracks;
    protected int sectorsPerTrack;
    protected int bytesPerSector;
    protected int sides = 1;

    protected int size;

    public SectorItem(int tracks, int sectorsPerTrack, int bytesPerSector, int sides) {
        super(new Properties().tab(ModCreativeModeTab.CCTECH_TAB).stacksTo(1));
        this.tracks = tracks;
        this.sectorsPerTrack = sectorsPerTrack;
        this.bytesPerSector = bytesPerSector;
        this.sides = sides;
        this.size = tracks * sectorsPerTrack * bytesPerSector * sides;
    }
}
