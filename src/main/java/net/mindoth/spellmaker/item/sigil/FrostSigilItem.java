package net.mindoth.spellmaker.item.sigil;

import net.mindoth.shadowizardlib.util.MultiBlockHitResult;
import net.mindoth.shadowizardlib.util.MultiEntityHitResult;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.List;

public class FrostSigilItem extends SigilItem {
    public FrostSigilItem(Properties pProperties, SpellColor color, int cost, int maxMagnitude, int magnitudeMultiplier, int maxDuration, int durationMultiplier) {
        super(pProperties, color, cost, maxMagnitude, magnitudeMultiplier, maxDuration, durationMultiplier);
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

    @Override
    public void effectOnBlock(List<Integer> stats, MultiBlockHitResult result) {
        int duration = stats.get(1);
        int freezeTicks = duration * 20;
        Level level = result.getPos().getLevel();
        for ( BlockPos blockPos : result.getBlocks() ) {
            BlockState blockState = level.getBlockState(blockPos);
            BlockState frozenBlock = Blocks.FROSTED_ICE.defaultBlockState();
            if ( blockState.getBlock() instanceof LiquidBlock liquid && liquid.getFluid() == Fluids.WATER && frozenBlock.canSurvive(level, blockPos)
                    && level.isUnobstructed(frozenBlock, blockPos, CollisionContext.empty()) ) {
                level.setBlockAndUpdate(blockPos, frozenBlock);
                level.scheduleTick(blockPos, Blocks.FROSTED_ICE, freezeTicks);
            }
        }
    }
}
