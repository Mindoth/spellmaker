package net.mindoth.spellmaker.capability.playermagic;

import net.minecraft.nbt.CompoundTag;

public class PlayerMagick {

    public static final String SM_MAGICK = "sm_magick";
    public static final String SM_CURRENT_MANA = "sm_current_mana";

    private double currentMana;
    public double getCurrentMana() {
        return currentMana;
    }
    public void setCurrentMana(double currentMana) {
        this.currentMana = currentMana;
    }

    public void copyFrom(PlayerMagick source) {
        this.currentMana = source.currentMana;
    }

    public void saveNBTData(CompoundTag tag) {
        tag.putDouble(SM_CURRENT_MANA, this.currentMana);
    }

    public void loadNBTData(CompoundTag tag) {
        this.currentMana = tag.getDouble(SM_CURRENT_MANA);
    }
}
