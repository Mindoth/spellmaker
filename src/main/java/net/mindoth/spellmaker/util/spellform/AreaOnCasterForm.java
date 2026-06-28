package net.mindoth.spellmaker.util.spellform;

import com.google.common.collect.Lists;
import net.mindoth.shadowizardlib.event.LightEvents;
import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.shadowizardlib.util.MultiBlockHitResult;
import net.mindoth.shadowizardlib.util.MultiEntityHitResult;
import net.mindoth.spellmaker.item.sigil.AbstractSigilItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class AreaOnCasterForm extends AbstractSpellForm {

    public AreaOnCasterForm(float cost) {
        super(cost);
    }

    @Override
    public boolean castMagick(Entity source, Entity directSource, LinkedHashMap<AbstractSigilItem, List<Integer>> map) {
        Level level = source.level();
        AABB box = source.getBoundingBox().inflate(1.5D, 0.0D, 1.5D);
        List<Entity> list = level.getEntities(null, box).stream().filter((entity -> entity instanceof LivingEntity)).toList();
        MultiEntityHitResult mEntityResult = new MultiEntityHitResult(source, list, new DimVec3(source.position(), source.level()));
        for ( AbstractSigilItem sigil : map.keySet() ) sigil.effectOnEntity(source, directSource, map.get(sigil), mEntityResult);

        List<BlockPos> blocks = Lists.newArrayList();
        for ( int x = source.getBlockX() -1; x < source.getBlockX() + 2; x++ ) {
            for ( int y = source.getBlockY(); y < source.getBlockY() + 2; y++ ) {
                for ( int z = source.getBlockZ() - 1; z < source.getBlockZ() + 2; z++ ) {
                    BlockPos newPos = new BlockPos(x, y, z);
                    blocks.add(newPos);
                }
            }
        }
        MultiBlockHitResult mBlockResult = new MultiBlockHitResult(Direction.UP, true, blocks, new DimVec3(source.position(), level));
        for ( AbstractSigilItem sigil : map.keySet() ) sigil.effectOnBlock(source, directSource, map.get(sigil), mBlockResult);
        aoeEntitySpellParticles(level, box, (float)box.getYsize() * 0.5F, getColorStats(map));

        return true;
    }

    public static void aoeEntitySpellParticles(Level level, AABB box, float range, HashMap<String, Float> stats) {
        Vec3 center = box.getCenter();
        BlockPos pos = new BlockPos(Mth.floor(center.x), Mth.floor(center.y), Mth.floor(center.z));
        double tempY = center.y;
        for ( int i = pos.getY(); i >= Mth.floor(center.y - range); i-- ) {
            BlockPos tempPos = new BlockPos(pos.getX(), i, pos.getZ());
            if ( level.getBlockState(tempPos).isSolid() ) break;
            else tempY = i;
        }
        box = box.move(0, -(center.y - tempY), 0);
        LightEvents.addAoeParticles(false, level, box, 0.15F, 8, stats);
    }
}
