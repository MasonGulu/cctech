package github.shrekshellraiser.cctech.client;

import github.shrekshellraiser.cctech.CCTech;
import github.shrekshellraiser.cctech.client.render.CardWriterBlockEntityRenderer;
import github.shrekshellraiser.cctech.client.render.CassetteDeckBlockEntityRenderer;
import github.shrekshellraiser.cctech.client.render.ReelToReelBlockEntityRenderer;
import github.shrekshellraiser.cctech.common.ModBlockEntities;
import github.shrekshellraiser.cctech.common.item.ModItems;
import github.shrekshellraiser.cctech.common.item.cards.MagCardItem;
import net.minecraft.client.color.item.ItemColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
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
            event.registerBlockEntityRenderer(ModBlockEntities.REEL_TO_REEL.get(),
                    ReelToReelBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.CARD_WRITER.get(),
                    CardWriterBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerItemColors(ColorHandlerEvent.Item event){
            event.getItemColors().register((stack,tint) -> ((MagCardItem) stack.getItem()).getColor(stack), ModItems.MAG_CARD.get());
        }
    }
}
