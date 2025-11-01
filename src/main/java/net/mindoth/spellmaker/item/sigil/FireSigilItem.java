package net.mindoth.spellmaker.item.sigil;

import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public class FireSigilItem extends SigilItem {
    public FireSigilItem(Properties pProperties, SpellColor color, int cost, int maxMagnitude, int magnitudeMultiplier, int maxDuration, int durationMultiplier) {
        super(pProperties, color, cost, maxMagnitude, magnitudeMultiplier, maxDuration, durationMultiplier);
    }

    @Override
    public void effectOnAllEntitiesInList(Entity source, Entity directSource, Entity target, List<Integer> stats, DimVec3 location) {
        if ( !target.isAttackable() || !target.isAlive() || target.fireImmune() ) return;
        int magnitude = stats.get(0);
        if ( magnitude > 0 ) target.hurt(getSource(DamageTypes.ON_FIRE, source, directSource), magnitude);
        int duration = stats.get(1);
        int fireTicks = duration * 20;
        if ( target.getRemainingFireTicks() > 0 && target.getRemainingFireTicks() < fireTicks ) {
            fireTicks = target.getRemainingFireTicks() + (fireTicks - target.getRemainingFireTicks());
        }
        if ( fireTicks > 0 ) target.setSecondsOnFire(fireTicks / 20);
    }

    @Override
    public void effectOnAllBlocksInList(Entity source, Entity directSource, BlockPos target, List<Integer> stats, DimVec3 location, Direction direction, boolean isInside) {
        Level level = location.getLevel();
        BlockState blockState = level.getBlockState(target);
        if ( !CampfireBlock.canLight(blockState) && !CandleBlock.canLight(blockState) && !CandleCakeBlock.canLight(blockState) ) {
            BlockPos blockPos1 = target.relative(direction);
            if ( BaseFireBlock.canBePlacedAt(level, blockPos1, direction) ) {
                BlockState blockState1 = BaseFireBlock.getState(level, blockPos1);
                level.setBlock(blockPos1, blockState1, 11);
            }
        }
        else level.setBlock(target, blockState.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)), 11);
    }
}
