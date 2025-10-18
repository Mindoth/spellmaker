package net.mindoth.spellmaker.util;

public enum SpellColor {
    BLUE(85, 255, 255, 1),
    PURPLE(170, 25, 170, 1),
    YELLOW(255, 170, 25, 1),
    GREEN(25, 170, 25, 1),
    BLACK(0, 0, 0, 1),
    WHITE(255, 255, 255, 1);

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
