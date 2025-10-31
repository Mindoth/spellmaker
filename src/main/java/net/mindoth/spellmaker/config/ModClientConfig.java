package net.mindoth.spellmaker.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModClientConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> SHOW_MAGICK_NUMBER_VALUE;

    static {
        BUILDER.push("Client Configs for Ancient Magicks");

        SHOW_MAGICK_NUMBER_VALUE = BUILDER.comment("Set this to true if you want to see a numerical value of your magick above the magick bar.")
                .define("showMagickNumberValue", false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
