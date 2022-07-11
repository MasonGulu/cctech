package com.shrekshellraiser.cctech.common.item;

import com.shrekshellraiser.cctech.CCTech;
import com.shrekshellraiser.cctech.config.CCTechCommonConfigs;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CCTech.MODID);

    public static final RegistryObject<Item> IRON_CASSETTE = ITEMS.register("iron_cassette", () ->
            new CassetteItem(CCTechCommonConfigs.IRON_CASSETTE.get()));
    public static final RegistryObject<Item> GOLD_CASSETTE = ITEMS.register("gold_cassette", () ->
            new CassetteItem(CCTechCommonConfigs.GOLD_CASSETTE.get()));
    public static final RegistryObject<Item> DIAMOND_CASSETTE = ITEMS.register("diamond_cassette", () ->
            new CassetteItem(CCTechCommonConfigs.DIAMOND_CASSETTE.get()));
    public static final RegistryObject<Item> CREATIVE_CASSETTE = ITEMS.register("creative_cassette", () ->
            new CassetteItem(CCTechCommonConfigs.CREATIVE_CASSETTE.get()));

    public static final RegistryObject<Item> IRON_REEL = ITEMS.register("iron_reel", () ->
            new ReelItem(CCTechCommonConfigs.IRON_REEL.get()));
    public static final RegistryObject<Item> GOLD_REEL = ITEMS.register("gold_reel", () ->
            new ReelItem(CCTechCommonConfigs.GOLD_REEL.get()));
    public static final RegistryObject<Item> DIAMOND_REEL = ITEMS.register("diamond_reel", () ->
            new ReelItem(CCTechCommonConfigs.DIAMOND_REEL.get()));
    public static final RegistryObject<Item> CREATIVE_REEL = ITEMS.register("creative_reel", () ->
            new ReelItem(CCTechCommonConfigs.CREATIVE_REEL.get()));
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
