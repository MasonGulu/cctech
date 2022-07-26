package github.shrekshellraiser.cctech.common.item.sectormedia;

public class ZipDiskItem extends SectorItem {

    public ZipDiskItem(int cylinders, int heads, int sectors) {
        super(cylinders, heads, sectors, 512);
    }

    public static String getDeviceDir() {
        return "zip";
    }
}
