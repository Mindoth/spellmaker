package net.mindoth.spellmaker.item.sigil;

import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;

import java.util.List;

public class GrowthSigilItem extends AbstractSigilItem {

    public GrowthSigilItem(Properties pProperties, SpellColor color, int cost, int minMagnitude, int maxMagnitude, int magnitudeMultiplier, int minDuration, int maxDuration, int durationMultiplier) {
        super(pProperties, color, cost, minMagnitude, maxMagnitude, magnitudeMultiplier, minDuration, maxDuration, durationMultiplier);
    }

    @Override
    public boolean canAffectEntity(Entity target) {
        return false;
    }

    @Override
    public boolean canAffectBlock(Block block) {
        return block instanceof BonemealableBlock;
    }

    @Override
    public void effectOnAllBlocksInList(Entity source, Entity directSource, BlockPos target, List<Integer> stats, DimVec3 location, Direction direction, boolean isInside) {
        Player player = source instanceof Player nullablePlayer ? nullablePlayer : null;
        Level sourceLevel = directSource.level();
        if ( !(sourceLevel instanceof ServerLevel level) ) return;
        BlockState state = level.getBlockState(target);
        if ( !(state.getBlock() instanceof BonemealableBlock block) ) return;
        BonemealEvent event = EventHooks.fireBonemealEvent(player, level, target, state, new ItemStack(Items.BONE_MEAL));
        if ( event.isCanceled() ) return;
        if ( !block.isValidBonemealTarget(level, target, state ) ) return;
        if ( !block.isBonemealSuccess(level, level.getRandom(), target, state) ) return;
        block.performBonemeal(level, level.getRandom(), target, state);
    }
}
