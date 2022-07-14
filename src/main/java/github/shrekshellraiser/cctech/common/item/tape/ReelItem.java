package github.shrekshellraiser.cctech.common.item.tape;

import github.shrekshellraiser.cctech.common.ModCreativeModeTab;
import github.shrekshellraiser.cctech.common.item.StorageItem;

public class ReelItem extends TapeItem {
    // possible future qol things... cassettes are in their packaging until they're used once??? maybe apply that to everything
    public ReelItem(int length) {
        super(new Properties().tab(ModCreativeModeTab.CCTECH_TAB).stacksTo(1));
        this.length = length;
    }

    public static String getDeviceDir() {
        return "reel";
    }
}
