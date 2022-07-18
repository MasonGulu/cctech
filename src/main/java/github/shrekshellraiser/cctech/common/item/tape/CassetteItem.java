package github.shrekshellraiser.cctech.common.item.tape;

import github.shrekshellraiser.cctech.common.ModCreativeModeTab;

public class CassetteItem extends TapeItem {
    // possible future qol things... cassettes are in their packaging until they're used once??? maybe apply that to everything
    // tooltip that shows uuid??
    public CassetteItem(int length, int defaultLength) {
        super(new Properties().tab(ModCreativeModeTab.CCTECH_TAB).stacksTo(1));
        this.maxLength = length;
        this.defaultLength = defaultLength;
    }
    public static String getDeviceDir() {
        return "cassette";
    }
}
