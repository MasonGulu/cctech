package github.shrekshellraiser.cctech.common.item;

public class CDItem extends SectorItem {
    public CDItem(int tracks, int sectorsPerTrack, int bytesPerSector) {
        super(tracks, sectorsPerTrack, bytesPerSector,1);
    }

    public static String getDeviceDir() {
        return "cd";
    }
}
