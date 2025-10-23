package net.mindoth.spellmaker.item.rune;

import net.mindoth.shadowizardlib.util.MultiEntityHitResult;
import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.registries.ModEffects;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class PolymorphRuneItem extends RuneItem {
    public PolymorphRuneItem(Properties pProperties, SpellColor color, int cost, int maxMagnitude, int magnitudeMultiplier, int maxDuration, int durationMultiplier) {
        super(pProperties, color, cost, maxMagnitude, magnitudeMultiplier, maxDuration, durationMultiplier);
    }

    @Override
    public void effectOnEntity(List<Integer> stats, MultiEntityHitResult result) {
        for ( Entity entity : result.getEntities() ) {
            if ( !entity.isAttackable() || !entity.isAlive() || !(entity instanceof LivingEntity living) ) return;
            int duration = stats.get(1);
            int polymorphTicks = duration * 20;
            living.addEffect(new MobEffectInstance(ModEffects.POLYMORPH.get(), polymorphTicks, 0, false, false));
        }
    }
}
