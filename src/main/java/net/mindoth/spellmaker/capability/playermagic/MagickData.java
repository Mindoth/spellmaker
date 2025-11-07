package net.mindoth.spellmaker.capability.playermagic;

import net.mindoth.spellmaker.capability.ModCapabilities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MagickData {

    public static final String SM_CURRENT_MANA = "sm_current_mana";
    public static final String SM_NOT_FIRST_LOGIN = "sm_not_first_login";

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

    public void saveNBTData(ValueOutput valueOutput) {
        valueOutput.putBoolean(SM_NOT_FIRST_LOGIN, this.notFirstLogin);
        valueOutput.putDouble(SM_CURRENT_MANA, this.currentMana);
    }

    public void loadNBTData(ValueInput valueInput) {
        this.notFirstLogin = valueInput.getBooleanOr(SM_NOT_FIRST_LOGIN, false);
        this.currentMana = valueInput.getDoubleOr(SM_CURRENT_MANA, 0);
    }

    private boolean notFirstLogin;
    public boolean getNotFirstLogin() {
        return notFirstLogin;
    }
    public void setNotFirstLogin(boolean notFirstLogin) {
        this.notFirstLogin = notFirstLogin;
    }

    private double currentMana;
    public double getCurrentMana() {
        return currentMana;
    }
    public void setCurrentMana(double currentMana) {
        this.currentMana = currentMana;
    }

    public void copyFrom(MagickData source) {
        this.notFirstLogin = source.notFirstLogin;
        this.currentMana = source.currentMana;
    }
}
