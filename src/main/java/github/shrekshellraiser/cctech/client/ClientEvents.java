package github.shrekshellraiser.cctech.client;

import github.shrekshellraiser.cctech.CCTech;
import github.shrekshellraiser.cctech.common.ModBlockEntities;
import github.shrekshellraiser.cctech.common.peripheral.tape.cassette.CassetteDeckBlockEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = CCTech.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {

        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.CASSETTE_DECK.get(),
                    CassetteDeckBlockEntityRenderer::new);
        }
    }
}
