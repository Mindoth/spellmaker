package net.mindoth.spellmaker.util.spellform;

import net.mindoth.shadowizardlib.event.ShadowEvents;
import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.util.DimVec3;
import net.mindoth.spellmaker.util.MultiBlockHitResult;
import net.mindoth.spellmaker.util.MultiEntityHitResult;
import net.mindoth.spellmaker.util.SpellForm;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class ByTouchForm extends SpellForm {
    public ByTouchForm(String name) {
        super(name);
    }

    @Override
    public void castMagick(Entity caster, LinkedHashMap<RuneItem, List<Integer>> map) {
        Entity target = getPointedEntity(caster.getEyePosition(), caster.getLookAngle(), caster, caster.level(), 4.5F, 0.25F, true);
        if ( target != null ) {
            for ( RuneItem rune : map.keySet() ) {
                rune.effectOnEntity(map.get(rune), new MultiEntityHitResult(caster, Collections.singletonList(target), new DimVec3(caster.position(), caster.level())));
            }
        }
        else {
            MultiBlockHitResult mResult = getPOVHitResult(caster.getEyePosition(), caster.getLookAngle(), caster, caster.level(), ClipContext.Fluid.SOURCE_ONLY, 4.5F);
            if ( !mResult.getPos().getLevel().getBlockState(mResult.getBlocks().get(0)).isAir() ) {
                for ( RuneItem rune : map.keySet() ) rune.effectOnBlock(map.get(rune), mResult);
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

    private Entity getPointedEntity(Vec3 position, Vec3 direction, Entity caster, Level level, float range, float error, boolean stopsAtSolid) {
        Vec3 center = position.add(direction.multiply(range, range, range));
        Entity returnEntity = null;
        double playerX = position.x();
        double playerY = position.y();
        double playerZ = position.z();
        double listedEntityX = center.x();
        double listedEntityY = center.y();
        double listedEntityZ = center.z();
        int particleInterval = (int)Math.round(position.distanceToSqr(center));
        Vec3 startPos = position.add(direction);
        Vec3 endPos = center;
        for ( int k = 1; k < (1 + particleInterval); k++ ) {
            double lineX = playerX * (1 - ((double) k / particleInterval)) + listedEntityX * ((double) k / particleInterval);
            double lineY = playerY * (1 - ((double) k / particleInterval)) + listedEntityY * ((double) k / particleInterval);
            double lineZ = playerZ * (1 - ((double) k / particleInterval)) + listedEntityZ * ((double) k / particleInterval);
            endPos = new Vec3(lineX, lineY, lineZ);
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
            if ( stopsAtSolid && level.getBlockState(new BlockPos(Mth.floor(lineX), Mth.floor(lineY), Mth.floor(lineZ))).isSolid() ) break;
        }
        ShadowEvents.summonParticleLine(startPos, endPos, particleInterval, startPos, level, 0.1F, 8, ShadowEvents.defaultStats());
        return returnEntity;
    }
}
