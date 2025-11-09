package net.mindoth.spellmaker.item.sigil;

import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.spellmaker.registries.ModEffects;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class SleepSigilItem extends AbstractSigilItem {
    public SleepSigilItem(Properties pProperties, SpellColor color, int cost, int minMagnitude, int maxMagnitude, int magnitudeMultiplier, int minDuration, int maxDuration, int durationMultiplier) {
        super(pProperties, color, cost, minMagnitude, maxMagnitude, magnitudeMultiplier, minDuration, maxDuration, durationMultiplier);
    }

    @Override
    public void effectOnAllEntitiesInList(Entity source, Entity directSource, Entity target, List<Integer> stats, DimVec3 location) {
        if ( !target.isAttackable() || !target.isAlive() ) return;
        int duration = stats.get(1);
        int sleepTicks = duration * 20;
        if ( target instanceof LivingEntity living ) {
            living.addEffect(new MobEffectInstance(ModEffects.SLEEP, sleepTicks, 0, false, false));
        }
    }
}
