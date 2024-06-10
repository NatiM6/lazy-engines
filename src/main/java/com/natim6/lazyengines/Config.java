package com.natim6.lazyengines;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.EnumValue<PassiveConfig> NON_BURNER = BUILDER
            .comment("Heat level of a passive non-burner source")
            .defineEnum("passive_source_mode", PassiveConfig.PASSIVE);
    public static final ForgeConfigSpec.EnumValue<PassiveConfig> PASSIVE_BURNER = BUILDER
            .comment("Heat level of a passive burner")
            .defineEnum("passive_burner_mode", PassiveConfig.PASSIVE);
    public static final ForgeConfigSpec.IntValue KINDLED_BURNER = BUILDER
            .comment("Heat level of a kindled burner")
            .defineInRange("kindled_burner_heat", 1, 1, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue SEETHING_BURNER = BUILDER
            .comment("Heat level of a seething burner")
            .defineInRange("seething_burner_heat", 2, 2, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue TANKS_PER_HEAT = BUILDER
            .comment("Amount of tanks required to support each level of heat")
            .defineInRange("tanks_per_heat_level", 4, 1, Integer.MAX_VALUE);

    static final ForgeConfigSpec SPEC = BUILDER.build();
}
