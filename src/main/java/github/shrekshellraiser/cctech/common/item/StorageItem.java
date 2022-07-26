package github.shrekshellraiser.cctech.common.item;

import net.minecraft.client.gui.screens.Screen;
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

public abstract class StorageItem extends Item {
    private final String UUIDTag = "cctech.uuid";
    private final String LabelTag = "cctech.label";

    public StorageItem(Properties pProperties) {
        super(pProperties);
    }

    public String getUUID(ItemStack stack) {
        String uuid;
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains(UUIDTag))
                uuid = tag.getString(UUIDTag); // get a string from tag cctech.uuid
            else {
                uuid = UUID.randomUUID().toString();
                tag.putString(UUIDTag, uuid);
                stack.setTag(tag);
            }
        } else {
            CompoundTag tag = new CompoundTag();
            uuid = UUID.randomUUID().toString();
            tag.putString(UUIDTag, uuid);
            stack.setTag(tag);
        }
        return uuid;
    }

    public String getLabel(ItemStack stack) {
        String label = null;
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains(LabelTag))
                label = tag.getString(LabelTag); // get a string from tag cctech.uuid
        }
        return label;
    }

    public void setLabel(ItemStack stack, String label) {
        CompoundTag tag;
        if (stack.hasTag()) {
            tag = stack.getTag();
            if (tag.contains(LabelTag))
                tag.putString(LabelTag, label);
            else {
                tag.putString(LabelTag, label);
            }
        } else {
            tag = new CompoundTag();
            tag.putString(LabelTag, label);
        }
        stack.setTag(tag);
    }

    public void removeLabel(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains(LabelTag))
                tag.remove(LabelTag);
        }
    }


    public static String getDeviceDir() {
        throw new RuntimeException("Don't forget to overwrite getDeviceDir!");
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        String label = getLabel(pStack);
        if (label != null)
            pTooltipComponents.add(new TextComponent(label));
        if (Screen.hasShiftDown() && pStack.hasTag() && pStack.getTag().contains(UUIDTag)) {
            String uuid = pStack.getTag().getString(UUIDTag);
            pTooltipComponents.add(new TextComponent(uuid));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    public abstract int getSize(ItemStack item);
}
