package github.shrekshellraiser.cctech.common.item;

import github.shrekshellraiser.cctech.CCTech;
import github.shrekshellraiser.cctech.common.ModCreativeModeTab;
import github.shrekshellraiser.cctech.common.item.tape.CassetteItem;
import github.shrekshellraiser.cctech.common.item.tape.ReelItem;
import github.shrekshellraiser.cctech.common.config.CCTechCommonConfigs;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CCTech.MODID);

    private static final int DEFAULT_SIZE = CCTechCommonConfigs.CASSETTE_DEFAULT.get();

    public static final RegistryObject<Item> IRON_CASSETTE = ITEMS.register("iron_cassette", () ->
            new CassetteItem(CCTechCommonConfigs.IRON_CASSETTE.get(), DEFAULT_SIZE));
    public static final RegistryObject<Item> GOLD_CASSETTE = ITEMS.register("gold_cassette", () ->
            new CassetteItem(CCTechCommonConfigs.GOLD_CASSETTE.get(), DEFAULT_SIZE));
    public static final RegistryObject<Item> DIAMOND_CASSETTE = ITEMS.register("diamond_cassette", () ->
            new CassetteItem(CCTechCommonConfigs.DIAMOND_CASSETTE.get(), DEFAULT_SIZE));
    public static final RegistryObject<Item> CREATIVE_CASSETTE = ITEMS.register("creative_cassette", () ->
            new CassetteItem(CCTechCommonConfigs.CREATIVE_CASSETTE.get(), DEFAULT_SIZE));

    public static final RegistryObject<Item> TAPE = ITEMS.register("tape", () -> new Item(
            new Item.Properties().tab(ModCreativeModeTab.CCTECH_TAB)));

    public static final RegistryObject<Item> TAPE_HEAD = ITEMS.register("tape_head", () -> new Item(
            new Item.Properties().tab(ModCreativeModeTab.CCTECH_TAB)));

    public static final RegistryObject<Item> IRON_REEL = ITEMS.register("iron_reel", () ->
            new ReelItem(CCTechCommonConfigs.IRON_REEL.get(), DEFAULT_SIZE));
    public static final RegistryObject<Item> GOLD_REEL = ITEMS.register("gold_reel", () ->
            new ReelItem(CCTechCommonConfigs.GOLD_REEL.get(), DEFAULT_SIZE));
    public static final RegistryObject<Item> DIAMOND_REEL = ITEMS.register("diamond_reel", () ->
            new ReelItem(CCTechCommonConfigs.DIAMOND_REEL.get(), DEFAULT_SIZE));
    public static final RegistryObject<Item> CREATIVE_REEL = ITEMS.register("creative_reel", () ->
            new ReelItem(CCTechCommonConfigs.CREATIVE_REEL.get(), DEFAULT_SIZE));

//    public static final RegistryObject<Item> CD = ITEMS.register("cd", () ->
//            new CDItem(10000, 100, 32));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
