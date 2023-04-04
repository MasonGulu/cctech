package github.shrekshellraiser.cctech.common.data;

import github.shrekshellraiser.cctech.CCTech;
import github.shrekshellraiser.cctech.common.item.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, CCTech.MODID, existingFileHelper);

    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.CREATIVE_CASSETTE.get());
        simpleItem(ModItems.DIAMOND_CASSETTE.get());
        simpleItem(ModItems.GOLD_CASSETTE.get());
        simpleItem(ModItems.IRON_CASSETTE.get());

        simpleItem(ModItems.CREATIVE_REEL.get());
        simpleItem(ModItems.DIAMOND_REEL.get());
        simpleItem(ModItems.GOLD_REEL.get());
        simpleItem(ModItems.IRON_REEL.get());

        simpleItem(ModItems.TAPE_HEAD.get());
        simpleItem(ModItems.TAPE.get());

        simpleItem(ModItems.MAG_CARD.get());
    }

    // Credit for this goes to https://www.youtube.com/c/TKaupenjoe
    private void simpleItem(Item item) {
        withExistingParent(item.getRegistryName().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(CCTech.MODID, "item/" + item.getRegistryName().getPath()));
    }
}
