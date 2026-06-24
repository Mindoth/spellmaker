package net.mindoth.spellmaker.item.sigil;

import com.google.common.collect.Lists;
import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.List;

public class DispelSigilItem extends AbstractSigilItem {

    public DispelSigilItem(Properties pProperties, SpellColor color, int cost, int minMagnitude, int maxMagnitude, int magnitudeMultiplier, int minDuration, int maxDuration, int durationMultiplier) {
        super(pProperties, color, cost, minMagnitude, maxMagnitude, magnitudeMultiplier, minDuration, maxDuration, durationMultiplier);
    }

    @Override
    public boolean canAffectBlock(Block block) {
        return false;
    }

    @Override
    public void effectOnAllEntitiesInList(Entity source, Entity directSource, Entity target, List<Integer> stats, DimVec3 location) {
        if ( !(target instanceof LivingEntity living) ) return;
        if ( living.getActiveEffects().isEmpty() ) return;
        int magnitude = stats.get(0);
        List<MobEffectInstance> effects = Lists.newArrayList();
        effects.addAll(living.getActiveEffects());
        int loopAmount = magnitude;
        if ( loopAmount > effects.size() ) loopAmount = effects.size();
        Collections.shuffle(effects);
        List<MobEffectInstance> effectsToRemove = Lists.newArrayList();
        for ( int i = 0; i < loopAmount; i++ ) {
            effectsToRemove.add(effects.get(i));
        }
        for ( MobEffectInstance instance : effectsToRemove ) {
            living.removeEffect(instance.getEffect());
        }
    }
}
