package github.shrekshellraiser.cctech.common.data;

import github.shrekshellraiser.cctech.CCTech;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, CCTech.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // well, this would be nice to have, yet all my blocks have custom blockStates.
        // I don't know enough to implement my own stuff here.
    }
}
