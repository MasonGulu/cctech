package github.shrekshellraiser.cctech.common.item.tape;

import github.shrekshellraiser.cctech.CCTech;
import github.shrekshellraiser.cctech.common.item.ModItems;
import github.shrekshellraiser.cctech.common.item.StorageItem;
import github.shrekshellraiser.cctech.config.CCTechCommonConfigs;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TapeItem extends StorageItem {
    protected int defaultLength;
    protected int maxLength;
    public TapeItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        if (Screen.hasShiftDown()) {
            String length = ((TapeItem) pStack.getItem()).getLength(pStack) + " / " + (((TapeItem) pStack.getItem()).maxLength);
            pTooltipComponents.add(new TextComponent(length));
        }
    }

    public final static String LengthTag = "cctech.length";
    public int getLength(ItemStack stack) {
        int length = defaultLength;
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains(LengthTag))
                length = tag.getInt(LengthTag); // get a string from tag cctech.uuid
            else {
                tag.putInt(LengthTag, length);
                stack.setTag(tag);
            }
        } else {
            CompoundTag tag = new CompoundTag();
            tag.putInt(LengthTag, length);
            stack.setTag(tag);
        }
        return length;
    }

    public boolean setLength(ItemStack stack, int length) {
        CCTech.LOGGER.debug("Target length "+length);
        length = Math.min(length, maxLength);
        int oldLength = getLength(stack); // ensure that the length tag exists
        length = Math.max(oldLength, length); // ensure not to shrink the tape if it's larger than the max allowed size
        CompoundTag tag = stack.getTag();
        tag.putInt(LengthTag, length);
        stack.setTag(tag);
        return length != oldLength;
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack pStack = pPlayer.getItemInHand(pHand);
        if (!pLevel.isClientSide()) {
            // attempt to remove a piece of tape from the inventory and increase the length of the TapeItem in the player's hand
            Inventory inventory = pPlayer.getInventory();
            int slot = inventory.findSlotMatchingItem(new ItemStack(ModItems.TAPE.get()));
            if (slot == -1 && !pPlayer.isCreative()) {
                return InteractionResultHolder.fail(pStack);
            }
            TapeItem item = (TapeItem) pStack.getItem();
            if (item.setLength(pStack, item.getLength(pStack) + CCTechCommonConfigs.TAPE_SIZE.get())) {
                if (!pPlayer.isCreative())
                    inventory.removeItem(slot, 1); // don't try to remove the item from a creative player
                return InteractionResultHolder.success(pStack);
            }
        }
        return InteractionResultHolder.fail(pStack);
    }
}
