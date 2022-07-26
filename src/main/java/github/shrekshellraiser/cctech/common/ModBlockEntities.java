package github.shrekshellraiser.cctech.common;

import github.shrekshellraiser.cctech.CCTech;
import github.shrekshellraiser.cctech.common.peripheral.sectormedia.zip.ZipDriveBlockEntity;
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

    public static final RegistryObject<BlockEntityType<ZipDriveBlockEntity>> ZIP_DRIVE =
            BLOCK_ENTITIES.register("zip_drive", () ->
                    BlockEntityType.Builder.of(ZipDriveBlockEntity::new,
                            ModBlocks.ZIP_DRIVE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
