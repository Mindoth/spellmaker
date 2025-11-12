package net.mindoth.spellmaker.item.sigil;

import net.mindoth.shadowizardlib.event.ShadowEvents;
import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.spellmaker.entity.AbstractSpellEntity;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ForceSigilItem extends AbstractSigilItem {
    public ForceSigilItem(Properties pProperties, SpellColor color, int cost, int minMagnitude, int maxMagnitude, int magnitudeMultiplier, int minDuration, int maxDuration, int durationMultiplier) {
        super(pProperties, color, cost, minMagnitude, maxMagnitude, magnitudeMultiplier, minDuration, maxDuration, durationMultiplier);
    }

    @Override
    public boolean canAffectBlock(Block block) {
        return false;
    }

    @Override
    public void effectOnAllEntitiesInList(Entity source, Entity directSource, Entity target, List<Integer> stats, DimVec3 location) {
        if ( !target.isAttackable() || !target.isAlive() ) return;
        int magnitude = stats.get(0);
        Vec3 direction = ShadowEvents.getEntityCenter(directSource).vectorTo(ShadowEvents.getEntityCenter(target)).normalize();
        if ( directSource instanceof LivingEntity ) direction = directSource.getLookAngle();
        else if ( directSource instanceof AbstractSpellEntity ) direction = directSource.getDeltaMovement().normalize();
        Vec3 towards = target.position().add(direction);
        target.push((towards.x - target.position().x) * magnitude, (towards.y - target.position().y) * magnitude, (towards.z - target.position().z) * magnitude);
        target.hurtMarked = true;
    }
}
