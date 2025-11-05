package net.mindoth.spellmaker.capability.playermagic;

import net.mindoth.spellmaker.capability.ModCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class MagickData {

    public static final String SM_CURRENT_MANA = "sm_current_mana";

    public static MagickData getPlayerMagickData(LivingEntity livingEntity) {
        return livingEntity.getData(ModCapabilities.MAGICK_DATA);
    }

    private boolean isMob = false;

    public MagickData(boolean isMob) {
        this.isMob = isMob;
    }

    public MagickData() {
        this(false);
    }

    private ServerPlayer serverPlayer = null;

    public MagickData(ServerPlayer serverPlayer) {
        this(false);
        this.serverPlayer = serverPlayer;
    }

    public void saveNBTData(CompoundTag tag) {
        tag.putDouble(SM_CURRENT_MANA, this.currentMana);
    }

    public void loadNBTData(CompoundTag tag) {
        this.currentMana = tag.getDouble(SM_CURRENT_MANA);
    }

    private double currentMana;
    public double getCurrentMana() {
        return currentMana;
    }
    public void setCurrentMana(double currentMana) {
        this.currentMana = currentMana;
    }

    public void copyFrom(MagickData source) {
        this.currentMana = source.currentMana;
    }
}
