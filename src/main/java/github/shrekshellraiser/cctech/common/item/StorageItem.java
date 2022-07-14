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

public class StorageItem extends Item {
    private final String UUIDTag = "cctech.uuid";

    public StorageItem(Properties pProperties) {
        super(pProperties);
    }

    public String getUUID(ItemStack stack) {
        String uuid;
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains(UUIDTag))
                uuid = tag.getString("cctech.uuid"); // get a string from tag cctech.uuid
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

    public static String getDeviceDir() {
        throw new RuntimeException("Don't forget to overwrite getDeviceDir!");
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown() && pStack.hasTag() && pStack.getTag().contains(UUIDTag)) {
            String uuid = pStack.getTag().getString(UUIDTag);
            pTooltipComponents.add(new TextComponent(uuid));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
