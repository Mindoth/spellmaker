package net.mindoth.spellmaker.item;

public interface ModDyeableItem {
    String TAG_COLOR = "color";
    String TAG_DISPLAY = "display";
    int WHITE = 0xFFFFFFFF;
    int GRAY = 0xFFB2B2B2;
    int BROWN = 0xFFA06540;
    int BLUE = 0xFF5890B5;
    int RED = 0xFFC14A43;

    default int getDefaultColor() {
        return WHITE;
    }
}
