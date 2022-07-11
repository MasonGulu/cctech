package com.shrekshellraiser.cctech.common.item;

import com.shrekshellraiser.cctech.common.ModCreativeModeTab;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ReelItem extends Item implements IStorageItem {
    // possible future qol things... cassettes are in their packaging until they're used once??? maybe apply that to everything
    // tooltip that shows uuid??
    private int length;
    public ReelItem(int length) {
        super(new Properties().tab(ModCreativeModeTab.CCTECH_TAB).stacksTo(1));
        this.length = length;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public String getUUID(ItemStack stack) {
        String uuid;
        if (stack.hasTag()) {
            uuid = stack.getTag().getString("cctech.uuid"); // get a string from tag cctech.uuid
        } else {
            CompoundTag tag = new CompoundTag();
            uuid = UUID.randomUUID().toString();
            tag.putString("cctech.uuid", uuid);
            stack.setTag(tag);
        }
        return uuid;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (pStack.hasTag()) {
            String uuid = pStack.getTag().getString("cctech.uuid");
            pTooltipComponents.add(new TextComponent(uuid));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

}
