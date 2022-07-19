package github.shrekshellraiser.cctech.client.screen.slot;

import github.shrekshellraiser.cctech.common.item.tape.ReelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ModReelSlot extends SlotItemHandler {

    public ModReelSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return stack.getItem() instanceof ReelItem;
    }
}
