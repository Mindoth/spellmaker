package net.mindoth.spellmaker.item.rune;

import net.mindoth.shadowizardlib.util.MultiEntityHitResult;
import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class FrostRuneItem extends RuneItem {
    public FrostRuneItem(Properties pProperties, SpellColor color, int maxMagnitude, int maxDuration, int cost, int magnitudeMultiplier, int durationMultiplier) {
        super(pProperties, color, maxMagnitude, maxDuration, cost, magnitudeMultiplier, durationMultiplier);
    }

    @Override
    public void effectOnEntity(List<Integer> stats, MultiEntityHitResult result) {
        for ( Entity entity : result.getEntities() ) {
            if ( !entity.isAttackable() || !entity.isAlive() || !entity.canFreeze() ) return;
            int magnitude = stats.get(0);
            if ( magnitude > 0 ) entity.hurt(entity.damageSources().freeze(), magnitude);
            int duration = stats.get(1);
            int freezeTicks = duration * 20;
            if ( entity.getTicksFrozen() > 0 && entity.getTicksFrozen() < freezeTicks ) {
                freezeTicks = entity.getTicksFrozen() + (freezeTicks - entity.getTicksFrozen());
            }
            if ( freezeTicks > 0 ) entity.setTicksFrozen(freezeTicks * 4);
        }
    }
}
