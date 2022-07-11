package com.shrekshellraiser.cctech.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CCTechCommonConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> IRON_CASSETTE;
    public static final ForgeConfigSpec.ConfigValue<Integer> GOLD_CASSETTE;
    public static final ForgeConfigSpec.ConfigValue<Integer> DIAMOND_CASSETTE;
    public static final ForgeConfigSpec.ConfigValue<Integer> CREATIVE_CASSETTE;

    public static final ForgeConfigSpec.ConfigValue<Integer> IRON_REEL;
    public static final ForgeConfigSpec.ConfigValue<Integer> GOLD_REEL;
    public static final ForgeConfigSpec.ConfigValue<Integer> DIAMOND_REEL;
    public static final ForgeConfigSpec.ConfigValue<Integer> CREATIVE_REEL;

    static {
        final int MAX_SIZE = 0xFFFF;
        BUILDER.push("CCTech Config");

        IRON_CASSETTE = BUILDER.defineInRange("Iron Cassette Size", 4000, 1, MAX_SIZE);
        GOLD_CASSETTE = BUILDER.defineInRange("Gold Cassette Size", 8000, 1, MAX_SIZE);
        DIAMOND_CASSETTE = BUILDER.defineInRange("Diamond Cassette Size", 12000, 1, MAX_SIZE);
        CREATIVE_CASSETTE = BUILDER.defineInRange("Creative Cassette Size", 16000, 1, MAX_SIZE);

        IRON_REEL = BUILDER.defineInRange("Iron Reel Size", 12000, 1, MAX_SIZE);
        GOLD_REEL = BUILDER.defineInRange("Gold Reel Size", 16000, 1, MAX_SIZE);
        DIAMOND_REEL = BUILDER.defineInRange("Diamond Reel Size", 20000, 1, MAX_SIZE);
        CREATIVE_REEL = BUILDER.defineInRange("Creative Reel Size", 24000, 1, MAX_SIZE);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
