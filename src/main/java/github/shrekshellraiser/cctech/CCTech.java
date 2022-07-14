package github.shrekshellraiser.cctech;

import github.shrekshellraiser.cctech.common.block.ModBlocks;
import github.shrekshellraiser.cctech.common.blockentities.ModBlockEntities;
import github.shrekshellraiser.cctech.common.item.ModItems;
import github.shrekshellraiser.cctech.config.CCTechCommonConfigs;
import github.shrekshellraiser.cctech.screen.CassetteDeckScreen;
import github.shrekshellraiser.cctech.screen.ModMenuTypes;
import github.shrekshellraiser.cctech.screen.ReelToReelScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("cctech")
public class CCTech {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "cctech";
    public static final String CASSETTE_FOLDER = "cassette";

    public CCTech() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(eventBus);
        ModItems.register(eventBus);
        ModBlockEntities.register(eventBus);
        ModMenuTypes.register(eventBus);

        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CCTechCommonConfigs.SPEC,
                "cctech-common.toml");

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // preinitcode
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        MenuScreens.register(ModMenuTypes.CASSETTE_DECK.get(), CassetteDeckScreen::new);
        MenuScreens.register(ModMenuTypes.REEL_TO_REEL.get(), ReelToReelScreen::new);
    }
}
