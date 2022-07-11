package com.shrekshellraiser.cctech.common;

import com.shrekshellraiser.cctech.common.block.ModBlocks;
import com.shrekshellraiser.cctech.common.item.ModItems;
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
