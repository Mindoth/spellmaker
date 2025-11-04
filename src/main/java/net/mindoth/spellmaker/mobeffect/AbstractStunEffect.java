package net.mindoth.spellmaker.mobeffect;

import net.mindoth.spellmaker.registries.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class AbstractStunEffect extends MobEffect {
    public AbstractStunEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    public static boolean isStunned(LivingEntity living) {
        if ( living.hasEffect(ModEffects.PARALYSIS)
                || living.hasEffect(ModEffects.SLEEP) ) return true;
        else return false;
    }
}
