package net.mindoth.spellmaker.mobeffect;

import net.minecraft.world.entity.LivingEntity;

public interface MobEffectEndCallback {
    void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier);
}
