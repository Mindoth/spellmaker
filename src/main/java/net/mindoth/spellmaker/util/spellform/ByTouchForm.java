package net.mindoth.spellmaker.util.spellform;

import net.mindoth.shadowizardlib.event.LightEvents;
import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.shadowizardlib.util.MultiBlockHitResult;
import net.mindoth.shadowizardlib.util.MultiEntityHitResult;
import net.mindoth.spellmaker.item.sigil.SigilItem;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class ByTouchForm extends AbstractSpellForm {
    public ByTouchForm(float cost) {
        super(cost);
    }

    @Override
    public void castMagick(Entity source, Entity directSource, LinkedHashMap<SigilItem, List<Integer>> map) {
        Entity target = getPointedEntity(source.getEyePosition(), source.getLookAngle(), source, source.level(), 4.5F, 0.25F, true, true, map);
        if ( target != null ) {
            MultiEntityHitResult mResult = new MultiEntityHitResult(source, Collections.singletonList(target), new DimVec3(target.position(), target.level()));
            for ( SigilItem sigil : map.keySet() ) sigil.effectOnEntity(source, directSource, map.get(sigil), mResult);
            LightEvents.addEnchantParticles(target, 0.15F, getColorStats(map));
        }
        else {
            MultiBlockHitResult mResult = getPOVHitResult(source.getEyePosition(), source.getLookAngle(), source, source.level(), ClipContext.Fluid.SOURCE_ONLY, 4.5F);
            for ( SigilItem sigil : map.keySet() ) sigil.effectOnBlock(source, directSource, map.get(sigil), mResult);
            if ( mResult.getBlocks().size() == 1 ) {
                BlockPos blockPos = mResult.getBlocks().get(0);
                LightEvents.addAoeParticles(true, source.level(), new AABB(blockPos), 0.15F, 8, getColorStats(map));
            }
        }
    }

    public static MultiBlockHitResult getPOVHitResult(Vec3 position, Vec3 direction, Entity caster, Level level, ClipContext.Fluid pFluidMode, float range) {
        direction = direction.multiply(range, range, range);
        Vec3 vec31 = position.add(direction);
        BlockHitResult result = level.clip(new ClipContext(position, vec31, ClipContext.Block.OUTLINE, pFluidMode, caster));
        return new MultiBlockHitResult(result.getDirection(), result.isInside(),
                Collections.singletonList(result.getBlockPos()), new DimVec3(result.getLocation(), level));
    }

    private Entity getPointedEntity(Vec3 position, Vec3 direction, Entity caster, Level level, float range, float error, boolean stopsAtSolid, boolean stopsAtLiquid, LinkedHashMap<SigilItem, List<Integer>> map) {
        Vec3 center = position.add(direction.multiply(range, range, range));
        Entity returnEntity = null;
        double playerX = position.x();
        double playerY = position.y();
        double playerZ = position.z();
        double listedEntityX = center.x();
        double listedEntityY = center.y();
        double listedEntityZ = center.z();
        int particleInterval = (int)Math.round(position.distanceToSqr(center));
        //Vec3 startPos = position.add(direction);
        //Vec3 endPos = center;
        for ( int k = 1; k < (1 + particleInterval); k++ ) {
            double lineX = playerX * (1 - ((double) k / particleInterval)) + listedEntityX * ((double) k / particleInterval);
            double lineY = playerY * (1 - ((double) k / particleInterval)) + listedEntityY * ((double) k / particleInterval);
            double lineZ = playerZ * (1 - ((double) k / particleInterval)) + listedEntityZ * ((double) k / particleInterval);
            //endPos = new Vec3(lineX, lineY, lineZ);
            Vec3 start = new Vec3(lineX + error, lineY + error, lineZ + error);
            Vec3 end = new Vec3(lineX - error, lineY - error, lineZ - error);
            AABB area = new AABB(start, end);
            List<Entity> targets = level.getEntities(caster, area);
            Entity target = null;
            double lowestSoFar = Double.MAX_VALUE;
            for ( Entity closestSoFar : targets ) {
                if ( closestSoFar instanceof LivingEntity ) {
                    double testDistance = closestSoFar.distanceToSqr(center);
                    if ( testDistance < lowestSoFar ) target = closestSoFar;
                }
            }
            if ( target != null ) {
                returnEntity = target;
                break;
            }
            if ( stopsAtLiquid && level.getBlockState(new BlockPos(Mth.floor(lineX), Mth.floor(lineY), Mth.floor(lineZ))).getBlock() instanceof LiquidBlock) break;
            if ( stopsAtSolid && level.getBlockState(new BlockPos(Mth.floor(lineX), Mth.floor(lineY), Mth.floor(lineZ))).isSolid() ) break;
        }
        //LightEvents.summonParticleLine(startPos, endPos, particleInterval, startPos, level, 0.1F, 8, getColorStats(map));
        return returnEntity;
    }
}
