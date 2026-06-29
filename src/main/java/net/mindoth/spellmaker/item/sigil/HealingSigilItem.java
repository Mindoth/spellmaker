package net.mindoth.spellmaker.item.sigil;

import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class HealingSigilItem extends AbstractSigilItem {

    public HealingSigilItem(Properties pProperties, SpellColor color, int cost, int minMagnitude, int maxMagnitude, int magnitudeMultiplier, int minDuration, int maxDuration, int durationMultiplier) {
        super(pProperties, color, cost, minMagnitude, maxMagnitude, magnitudeMultiplier, minDuration, maxDuration, durationMultiplier);
    }

    @Override
    public boolean canAffectEntity(Entity target) {
        return target instanceof LivingEntity living && !living.is(EntityTypeTags.UNDEAD);
    }

    @Override
    public boolean canAffectBlock(Block block) {
        return false;
    }

    @Override
    public void effectOnAllEntitiesInList(Entity source, Entity directSource, Entity target, List<Integer> stats, DimVec3 location) {
        int magnitude = stats.get(0);
        if ( magnitude > 0 && target instanceof LivingEntity living && !living.is(EntityTypeTags.UNDEAD) ) {
            living.heal(magnitude);
        }
    }
}
