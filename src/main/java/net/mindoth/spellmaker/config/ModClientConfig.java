package net.mindoth.spellmaker.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModClientConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue SHOW_MAGICK_NUMBER_VALUE = BUILDER
            .comment("Set this to true if you want to see a numerical value of your magick above the magick bar.")
            .define("showMagickNumberValue", false);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
