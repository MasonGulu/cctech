package github.shrekshellraiser.cctech.common.data;

import github.shrekshellraiser.cctech.CCTech;
import github.shrekshellraiser.cctech.common.item.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, CCTech.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
            if (entry.get() instanceof BlockItem blockItem) {
                block(blockItem);
            }
        }
    }

    protected ItemModelBuilder block(BlockItem blockItem) {
        return itemModels().withExistingParent(blockItem.getRegistryName().getPath(),
                CCTech.MODID + ":block/" + blockItem.getBlock().getRegistryName().getPath());
    }
}
