package github.shrekshellraiser.cctech.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CCTechCommonConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue CASSETTE_TIME_PER_BYTE;
    public static final ForgeConfigSpec.ConfigValue<Integer> IRON_CASSETTE;
    public static final ForgeConfigSpec.ConfigValue<Integer> GOLD_CASSETTE;
    public static final ForgeConfigSpec.ConfigValue<Integer> DIAMOND_CASSETTE;
    public static final ForgeConfigSpec.ConfigValue<Integer> CREATIVE_CASSETTE;

    public static final ForgeConfigSpec.ConfigValue<Integer> IRON_REEL;
    public static final ForgeConfigSpec.ConfigValue<Integer> GOLD_REEL;
    public static final ForgeConfigSpec.ConfigValue<Integer> DIAMOND_REEL;
    public static final ForgeConfigSpec.ConfigValue<Integer> CREATIVE_REEL;
    public static final ForgeConfigSpec.ConfigValue<Integer> CASSETTE_DEFAULT;
    public static final ForgeConfigSpec.ConfigValue<Integer> TAPE_SIZE;

    static {
        final int MAX_SIZE = 2147483647;
        BUILDER.push("CCTech Config");

        CASSETTE_TIME_PER_BYTE = BUILDER.defineInRange("Cassette Seek Time (ms/b): ", 0.16d, 0, 100);
        // DFPWM is 6,000 bytes per second
        // 360,000 bytes per minute
        IRON_CASSETTE = BUILDER.defineInRange("Iron Cassette Size", 360000*2, 1, MAX_SIZE);
        GOLD_CASSETTE = BUILDER.defineInRange("Gold Cassette Size", 360000*4, 1, MAX_SIZE);
        DIAMOND_CASSETTE = BUILDER.defineInRange("Diamond Cassette Size", 360000*8, 1, MAX_SIZE);
        CREATIVE_CASSETTE = BUILDER.defineInRange("Creative Cassette Size", 360000*60, 1, MAX_SIZE);

        CASSETTE_DEFAULT = BUILDER.defineInRange("Cassette Default Size", 360000, 1, MAX_SIZE);

        TAPE_SIZE = BUILDER.defineInRange("Magnetic Tape Size", 60000, 1, MAX_SIZE);

        IRON_REEL = BUILDER.defineInRange("Iron Reel Size", 360000*4, 1, MAX_SIZE);
        GOLD_REEL = BUILDER.defineInRange("Gold Reel Size", 360000*8, 1, MAX_SIZE);
        DIAMOND_REEL = BUILDER.defineInRange("Diamond Reel Size", 360000*16, 1, MAX_SIZE);
        CREATIVE_REEL = BUILDER.defineInRange("Creative Reel Size", 360000*128, 1, MAX_SIZE);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
