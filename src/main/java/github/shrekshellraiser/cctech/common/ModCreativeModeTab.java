package github.shrekshellraiser.cctech.common;

import github.shrekshellraiser.cctech.common.block.ModBlocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeModeTab {
    public static final CreativeModeTab CCTECH_TAB = new CreativeModeTab("cctech_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModBlocks.CASSETTE_DECK.get());
        }
    };
}
