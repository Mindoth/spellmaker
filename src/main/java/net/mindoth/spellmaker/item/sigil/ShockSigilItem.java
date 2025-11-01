package net.mindoth.spellmaker.item.sigil;

import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.spellmaker.registries.ModEffects;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class ShockSigilItem extends SigilItem {
    public ShockSigilItem(Properties pProperties, SpellColor color, int cost, int maxMagnitude, int magnitudeMultiplier, int maxDuration, int durationMultiplier) {
        super(pProperties, color, cost, maxMagnitude, magnitudeMultiplier, maxDuration, durationMultiplier);
    }

    @Override
    public void effectOnAllEntitiesInList(Entity source, Entity directSource, Entity target, List<Integer> stats, DimVec3 location) {
        if ( !target.isAttackable() || !target.isAlive() ) return;
        int magnitude = stats.get(0);
        if ( magnitude > 0 ) target.hurt(getSource(DamageTypes.LIGHTNING_BOLT, source, directSource), magnitude);
        int duration = stats.get(1);
        int paralysisTicks = duration * 20;
        if ( target instanceof LivingEntity living && living.isInWaterOrRain() ) {
            living.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), paralysisTicks, 0, false, false));
        }
    }
}
