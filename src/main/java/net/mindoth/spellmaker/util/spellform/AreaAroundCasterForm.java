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
    public void castMagick(Entity caster, LinkedHashMap<SigilItem, List<Integer>> map) {
        Level level = caster.level();
        AABB box = caster.getBoundingBox().inflate(2.0D, 0.0D, 2.0D);
        List<Entity> list = level.getEntities(caster, box).stream().filter((entity -> entity instanceof LivingEntity)).toList();
        MultiEntityHitResult mEntityResult = new MultiEntityHitResult(caster, list, new DimVec3(caster.position(), caster.level()));
        for ( SigilItem sigil : map.keySet() ) sigil.effectOnEntity(map.get(sigil), mEntityResult);

        List<BlockPos> blocks = Lists.newArrayList();
        for ( int x = caster.getBlockX() -1; x < caster.getBlockX() + 2; x++ ) {
            for ( int y = caster.getBlockY(); y < caster.getBlockY() + 2; y++ ) {
                for ( int z = caster.getBlockZ() - 1; z < caster.getBlockZ() + 2; z++ ) {
                    BlockPos newPos = new BlockPos(x, y, z);
                    if ( !newPos.equals(caster.getOnPos()) && !newPos.equals(caster.getOnPos().above()) ) blocks.add(newPos);
                }
            }
        }
        MultiBlockHitResult mBlockResult = new MultiBlockHitResult(Direction.UP, false, blocks, new DimVec3(caster.position(), level));
        for ( SigilItem sigil : map.keySet() ) sigil.effectOnBlock(map.get(sigil), mBlockResult);
        ProjectileSpellMultiEntity.aoeEntitySpellParticles(level, box, (float)box.getYsize() * 0.5F, getColorStats(map));
    }
}
