package net.mindoth.spellmaker.capability.playermagic;

public class ClientMagickData {

    private static double currentMana;
    public static double getCurrentMana() {
        return currentMana;
    }
    public static void setCurrentMana(double currentMana) {
        ClientMagickData.currentMana = currentMana;
    }
}
