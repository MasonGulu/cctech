package com.shrekshellraiser.cctech.screen.slot;

import com.shrekshellraiser.cctech.common.item.CassetteItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ModCassetteSlot extends SlotItemHandler {

    public ModCassetteSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return stack.getItem() instanceof CassetteItem;
    }
}
