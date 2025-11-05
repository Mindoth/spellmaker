package net.mindoth.spellmaker.item.sigil;

import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.List;

public class FrostSigilItem extends SigilItem {
    public FrostSigilItem(Properties pProperties, SpellColor color, int cost, int minMagnitude, int maxMagnitude, int magnitudeMultiplier, int minDuration, int maxDuration, int durationMultiplier) {
        super(pProperties, color, cost, minMagnitude, maxMagnitude, magnitudeMultiplier, minDuration, maxDuration, durationMultiplier);
    }

    @Override
    public void effectOnAllEntitiesInList(Entity source, Entity directSource, Entity target, List<Integer> stats, DimVec3 location) {
        if ( !target.isAttackable() || !target.isAlive() || !target.canFreeze() ) return;
        int magnitude = stats.get(0);
        if ( magnitude > 0 ) target.hurt(getSource(DamageTypes.FREEZE, source, directSource), magnitude);
        int duration = stats.get(1);
        int freezeTicks = duration * 20;
        if ( target.getTicksFrozen() > 0 && target.getTicksFrozen() < freezeTicks ) {
            freezeTicks = target.getTicksFrozen() + (freezeTicks - target.getTicksFrozen());
        }
        if ( freezeTicks > 0 ) target.setTicksFrozen(freezeTicks * 4);
    }

    @Override
    public void effectOnAllBlocksInList(Entity source, Entity directSource, BlockPos target, List<Integer> stats, DimVec3 location, Direction direction, boolean isInside) {
        int duration = stats.get(1);
        int freezeTicks = duration;
        Level level = location.getLevel();
        BlockState blockState = level.getBlockState(target);
        BlockState frozenBlock = Blocks.FROSTED_ICE.defaultBlockState();
        if ( blockState.getBlock() instanceof LiquidBlock liquid && liquid.fluid == Fluids.WATER && frozenBlock.canSurvive(level, target)
                && level.isUnobstructed(frozenBlock, target, CollisionContext.empty()) ) {
            level.setBlockAndUpdate(target, frozenBlock);
            level.scheduleTick(target, Blocks.FROSTED_ICE, freezeTicks);
        }
    }
}
