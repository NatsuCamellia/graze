package net.natsucamellia.graze;

import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue SEARCH_RADIUS = BUILDER
            .translation("graze.configuration.searchRadius")
            .defineInRange("searchRadius", 8, 1, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue CAP_MULTIPLIER = BUILDER
            .translation("graze.configuration.capMultiplier")
            .defineInRange("capMultiplier", 3, 1, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();
}
