package github.shrekshellraiser.cctech.client.screen.slot;

import github.shrekshellraiser.cctech.common.item.sectormedia.ZipDiskItem;
import github.shrekshellraiser.cctech.common.item.tape.CassetteItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ModZipSlot extends SlotItemHandler {

    public ModZipSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return stack.getItem() instanceof ZipDiskItem;
    }
}
