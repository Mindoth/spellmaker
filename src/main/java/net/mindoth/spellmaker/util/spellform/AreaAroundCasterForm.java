package net.mindoth.spellmaker.util.spellform;

import com.google.common.collect.Lists;
import net.mindoth.shadowizardlib.event.LightEvents;
import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.shadowizardlib.util.MultiBlockHitResult;
import net.mindoth.shadowizardlib.util.MultiEntityHitResult;
import net.mindoth.spellmaker.entity.ProjectileSpellMultiEntity;
import net.mindoth.spellmaker.item.sigil.SigilItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.LinkedHashMap;
import java.util.List;

public class AreaAroundCasterForm extends AbstractSpellForm {
    public AreaAroundCasterForm(float cost) {
        super(cost);
    }

    @Override
    public void castMagick(Entity source, Entity directSource, LinkedHashMap<SigilItem, List<Integer>> map) {
        Level level = source.level();
        AABB box = source.getBoundingBox().inflate(2.0D, 0.0D, 2.0D);
        List<Entity> list = level.getEntities(source, box).stream().filter((entity -> entity instanceof LivingEntity)).toList();
        MultiEntityHitResult mEntityResult = new MultiEntityHitResult(source, list, new DimVec3(source.position(), source.level()));
        for ( SigilItem sigil : map.keySet() ) sigil.effectOnEntity(source, directSource, map.get(sigil), mEntityResult);

        List<BlockPos> blocks = Lists.newArrayList();
        for ( int x = source.getBlockX() -1; x < source.getBlockX() + 2; x++ ) {
            for ( int y = source.getBlockY(); y < source.getBlockY() + 2; y++ ) {
                for ( int z = source.getBlockZ() - 1; z < source.getBlockZ() + 2; z++ ) {
                    BlockPos newPos = new BlockPos(x, y, z);
                    if ( !newPos.equals(source.getOnPos()) && !newPos.equals(source.getOnPos().above()) ) blocks.add(newPos);
                }
            }
        }
        MultiBlockHitResult mBlockResult = new MultiBlockHitResult(Direction.UP, false, blocks, new DimVec3(source.position(), level));
        for ( SigilItem sigil : map.keySet() ) sigil.effectOnBlock(source, directSource, map.get(sigil), mBlockResult);
        ProjectileSpellMultiEntity.aoeEntitySpellParticles(level, box, (float)box.getYsize() * 0.5F, getColorStats(map));
    }
}
