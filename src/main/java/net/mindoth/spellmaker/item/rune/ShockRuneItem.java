package net.mindoth.spellmaker.item.rune;

import net.mindoth.shadowizardlib.util.MultiEntityHitResult;
import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.registries.ModEffects;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class ShockRuneItem extends RuneItem {
    public ShockRuneItem(Properties pProperties, SpellColor color, int maxMagnitude, int maxDuration, int cost, int magnitudeMultiplier, int durationMultiplier) {
        super(pProperties, color, maxMagnitude, maxDuration, cost, magnitudeMultiplier, durationMultiplier);
    }

    @Override
    public void effectOnEntity(List<Integer> stats, MultiEntityHitResult result) {
        for ( Entity entity : result.getEntities() ) {
            if ( !entity.isAttackable() || !entity.isAlive() ) return;
            int magnitude = stats.get(0);
            if ( magnitude > 0 ) entity.hurt(entity.damageSources().lightningBolt(), magnitude);
            int duration = stats.get(1);
            int paralysisTicks = duration * 20;
            if ( entity instanceof LivingEntity living ) living.addEffect(new MobEffectInstance(ModEffects.PARALYSIS.get(), paralysisTicks, 0, false, false));
        }
    }
}
