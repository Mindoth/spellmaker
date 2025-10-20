package net.mindoth.spellmaker.util;

public enum SpellColor {
    DARK_RED(170, 25, 25, 1),
    RED(255, 85, 85, 1),
    GOLD(255, 170, 25, 1),
    YELLOW(255, 255, 85, 1),
    DARK_GREEN(25, 170, 25, 1),
    GREEN(85, 225, 85, 1),
    AQUA(85, 255, 255, 1),
    DARK_AQUA(25, 170, 170, 1),
    DARK_BLUE(25, 25, 170, 1),
    BLUE(85, 85, 255, 1),
    LIGHT_PURPLE(255, 85, 255, 1),
    DARK_PURPLE(170, 25, 170, 1),
    WHITE(255, 255, 255, 1),
    GRAY(170, 170, 170, 1),
    DARK_GRAY(85, 85, 85, 1),
    BLACK(0, 0, 0, 1);

    public final int r;
    public final int g;
    public final int b;
    public final int type;

    private SpellColor(int r, int g, int b, int type) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.type = type;
    }
}
