package net.mindoth.spellmaker.util;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.sigil.SigilItem;
import net.mindoth.spellmaker.util.spellform.AbstractSpellForm;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public enum SpellColor {
    /*MIX(-1, -1, -1, 1, "mix"),
    DARK_RED(170, 25, 25, 1, "dark_red"),
    RED(255, 85, 85, 1, "red"),
    GOLD(255, 170, 25, 1, "gold"),
    YELLOW(255, 255, 85, 1, "yellow"),
    DARK_GREEN(25, 170, 25, 1, "dark_green"),
    GREEN(85, 225, 85, 1, "green"),
    AQUA(85, 255, 255, 1, "aqua"),
    DARK_AQUA(25, 170, 170, 1, "dark_aqua"),
    DARK_BLUE(25, 25, 170, 1, "dark_blue"),
    BLUE(85, 85, 255, 1, "blue"),
    LIGHT_PURPLE(255, 85, 255, 1, "light_purple"),
    DARK_PURPLE(170, 25, 170, 1, "dark_purple"),
    WHITE(255, 255, 255, 1, "white"),
    GRAY(170, 170, 170, 1, "gray"),
    DARK_GRAY(85, 85, 85, 1, "dark_gray"),
    BLACK(0, 0, 0, 1, "black");*/

    MIX(-1, -1, -1, 1, "mix"),
    ARCANE(170, 25, 170, 1, "arcane"),
    FIRE(255, 170, 25, 1, "fire"),
    FROST(125, 180, 255, 1, "frost"),
    LIGHTNING(25, 170, 170, 1, "lightning"),
    NATURE(25, 170, 25, 1, "nature");

    public final int r;
    public final int g;
    public final int b;
    public final int type;
    public final String string;

    SpellColor(int r, int g, int b, int type, String string) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.type = type;
        this.string = string;
    }

    public static ResourceLocation getSpellIcon(AbstractSpellForm form, List<SigilItem> sigilList, List<Integer> magnitudeList, List<Integer> durationList) {
        ResourceLocation icon = SpellColor.getSpellIconPath(form, SpellColor.MIX);
        if ( !sigilList.isEmpty() ) {
            SigilItem highestCostSigil = ParchmentItem.getHighestCostSigil(DataHelper.createMapFromLists(sigilList, magnitudeList, durationList));
            if ( highestCostSigil != null ) icon = SpellColor.getSpellIconPath(form, highestCostSigil.getColor());
        }
        return icon;
    }

    private static ResourceLocation getSpellIconPath(AbstractSpellForm form, SpellColor color) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("textures/gui/spellform/");
        stringBuilder.append(color.string + "/");
        stringBuilder.append(form.getName());
        stringBuilder.append("_");
        stringBuilder.append(color.string);
        stringBuilder.append(".png");
        return new ResourceLocation(SpellMaker.MOD_ID, stringBuilder.toString());
    }
}
