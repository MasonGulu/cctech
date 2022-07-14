package github.shrekshellraiser.cctech.common.item.tape;

import github.shrekshellraiser.cctech.common.item.StorageItem;

public class TapeItem extends StorageItem {
    protected int defaultLength;
    protected int maxLength;
    public TapeItem(Properties pProperties) {
        super(pProperties);
    }

    public int getLength() {
        return defaultLength;
    }
}
