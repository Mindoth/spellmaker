package net.mindoth.spellmaker.mobeffect;

import net.mindoth.spellmaker.registries.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class ParalysisEffect extends MobEffect {
    public ParalysisEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void addAttributeModifiers(LivingEntity living, AttributeMap map, int pAmplifier) {
        if ( living instanceof Mob mob && !mob.isNoAi() ) mob.setNoAi(true);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity living, AttributeMap map, int pAmplifier) {
        if ( living instanceof Mob mob && !ModEffects.isStunned(mob) ) mob.setNoAi(false);
    }
}
