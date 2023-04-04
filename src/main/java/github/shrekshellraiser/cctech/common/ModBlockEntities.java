package github.shrekshellraiser.cctech.common;

import github.shrekshellraiser.cctech.CCTech;
import github.shrekshellraiser.cctech.common.peripheral.cards.CardWriterBlockEntity;
import github.shrekshellraiser.cctech.common.peripheral.cards.MagCardBlockEntity;
import github.shrekshellraiser.cctech.common.peripheral.tape.cassette.CassetteDeckBlockEntity;
import github.shrekshellraiser.cctech.common.peripheral.tape.reel.ReelToReelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, CCTech.MODID);

    public static final RegistryObject<BlockEntityType<CassetteDeckBlockEntity>> CASSETTE_DECK =
            BLOCK_ENTITIES.register("cassette_deck", () ->
                    BlockEntityType.Builder.of(CassetteDeckBlockEntity::new,
                            ModBlocks.CASSETTE_DECK.get()).build(null));
    public static final RegistryObject<BlockEntityType<ReelToReelBlockEntity>> REEL_TO_REEL =
            BLOCK_ENTITIES.register("reel_to_reel", () ->
                    BlockEntityType.Builder.of(ReelToReelBlockEntity::new,
                            ModBlocks.REEL_TO_REEL.get()).build(null));

    public static final RegistryObject<BlockEntityType<MagCardBlockEntity>> MAG_CARD_READER =
            BLOCK_ENTITIES.register("magnetic_card_reader", () ->
                    BlockEntityType.Builder.of(MagCardBlockEntity::new,
                            ModBlocks.MAG_CARD_READER.get()).build(null));

    public static final RegistryObject<BlockEntityType<CardWriterBlockEntity>> CARD_WRITER =
            BLOCK_ENTITIES.register("card_writer", () ->
                    BlockEntityType.Builder.of(CardWriterBlockEntity::new,
                            ModBlocks.CARD_WRITER.get()).build(null));
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
