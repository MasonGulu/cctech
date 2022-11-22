package github.shrekshellraiser.cctech.common.data;

import github.shrekshellraiser.cctech.common.ModBlocks;
import github.shrekshellraiser.cctech.common.item.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        // Example for later
//        ShapedRecipeBuilder.shaped(ModBlocks.CASSETTE_DECK.get())
//                .define('e', ModItems.TAPE_HEAD.get())
//                .pattern("eee")
//                .pattern("   ")
//                .pattern("   ")
//                .save(pFinishedRecipeConsumer);
    }
}
