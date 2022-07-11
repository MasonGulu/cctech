package com.shrekshellraiser.cctech.common.item;

import net.minecraft.world.item.ItemStack;

public interface IStorageItem {
    int getLength();
    String getUUID(ItemStack stack);
}