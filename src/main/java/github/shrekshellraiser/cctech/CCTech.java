package github.shrekshellraiser.cctech;

import github.shrekshellraiser.cctech.common.ModBlocks;
import github.shrekshellraiser.cctech.common.ModBlockEntities;
import github.shrekshellraiser.cctech.common.item.ModItems;
import github.shrekshellraiser.cctech.common.config.CCTechCommonConfigs;
import github.shrekshellraiser.cctech.common.network.ModMessages;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
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

        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::commonSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CCTechCommonConfigs.SPEC,
                "cctech-common.toml");

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModMessages.register();
    }

    private void setup(final FMLCommonSetupEvent event) {
        // preinitcode

    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.REEL_TO_REEL.get(), RenderType.cutout());
    }
}
