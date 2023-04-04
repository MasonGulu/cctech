package github.shrekshellraiser.cctech.common.item.cards;

import github.shrekshellraiser.cctech.common.ModCreativeModeTab;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class MagCardItem extends Item implements DyeableLeatherItem, ItemColor {
    private final String UUIDTag = "cctech.uuid";
    private final String LabelTag = "cctech.label";
    private final String ContentsTag = "cctech.contents";

    public MagCardItem() {
        super(new Properties().tab(ModCreativeModeTab.CCTECH_TAB).stacksTo(1));
    }

    private String getTag(ItemStack stack, String tagName, String def) {
        String value = null;
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains(tagName))
                value = tag.getString(tagName); // get a string from value cctech.uuid
            else if (def != null) {
                value = def;
                tag.putString(tagName, value);
                stack.setTag(tag);
            }
        } else if (def != null) {
            CompoundTag tag = new CompoundTag();
            value = def;
            tag.putString(tagName, value);
            stack.setTag(tag);
        }
        return value;

    }

    private void setTag(ItemStack stack, String tagName, String value) {
        CompoundTag tag;
        if (stack.hasTag()) {
            tag = stack.getTag();
            tag.putString(tagName, value);
        } else {
            tag = new CompoundTag();
            tag.putString(tagName, value);
        }
        stack.setTag(tag);

    }

    public String getUUID(ItemStack stack) {
        return getTag(stack, UUIDTag, null);
    }

    public void setUUID(ItemStack stack, String uuid) {
        setTag(stack, UUIDTag, uuid);
    }
    public String getContents(ItemStack stack) {
        return getTag(stack, ContentsTag, null);
    }

    public void setContents(ItemStack stack, String contents) {
        setTag(stack, ContentsTag, contents);
    }

    public String getLabel(ItemStack stack) {
        return getTag(stack, LabelTag, null);
    }

    public void setLabel(ItemStack stack, String label) {
        setTag(stack, LabelTag, label);
    }

    public void removeLabel(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains(LabelTag))
                tag.remove(LabelTag);
            stack.setTag(tag);
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        String label = getLabel(pStack);
        if (label != null)
            pTooltipComponents.add(new TextComponent(label));
        if (Screen.hasShiftDown() && pStack.hasTag()) {
            String uuid = getUUID(pStack);
            String contents = getContents(pStack);
            if (uuid != null && contents != null) {
                pTooltipComponents.add(new TextComponent(uuid));
                pTooltipComponents.add(new TextComponent(contents));
            }
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public int getColor(ItemStack pStack, int pTintIndex) {
        CompoundTag compoundtag = pStack.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : 0xFFFFFF;
    }
}
