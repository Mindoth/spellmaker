package net.mindoth.spellmaker.item.rune;

import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.util.MultiBlockHitResult;
import net.mindoth.spellmaker.util.MultiEntityHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public class FireRuneItem extends RuneItem {
    public FireRuneItem(Properties pProperties, boolean hasMagnitude, boolean hasDuration) {
        super(pProperties, hasMagnitude, hasDuration);
    }

    @Override
    public void effectOnEntity(List<Integer> stats, MultiEntityHitResult result) {
        for ( Entity entity : result.getEntities() ) {
            if ( !entity.isAttackable() || !entity.isAlive() || entity.fireImmune() ) return;
            int magnitude = stats.get(0);
            if ( magnitude > 0 ) entity.hurt(entity.damageSources().onFire(), magnitude);
            int duration = stats.get(1);
            int fireTicks = duration * 20;
            if ( entity.getRemainingFireTicks() > 0 && entity.getRemainingFireTicks() < fireTicks ) {
                fireTicks = entity.getRemainingFireTicks() + (fireTicks - entity.getRemainingFireTicks());
            }
            if ( fireTicks > 0 ) entity.setSecondsOnFire(fireTicks / 20);
        }
    }

    @Override
    public void effectOnBlock(List<Integer> stats, MultiBlockHitResult result) {
        Level level = result.getPos().getLevel();
        for ( BlockPos blockPos : result.getBlocks() ) {
            BlockState blockState = level.getBlockState(blockPos);
            if ( !CampfireBlock.canLight(blockState) && !CandleBlock.canLight(blockState) && !CandleCakeBlock.canLight(blockState) ) {
                BlockPos blockPos1 = blockPos.relative(result.getDirection());
                if ( BaseFireBlock.canBePlacedAt(level, blockPos1, result.getDirection()) ) {
                    BlockState blockState1 = BaseFireBlock.getState(level, blockPos1);
                    level.setBlock(blockPos1, blockState1, 11);
                }
            }
            else level.setBlock(blockPos, blockState.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)), 11);
        }
    }
}
