package net.mindoth.spellmaker.entity;

import com.google.common.collect.Lists;
import net.mindoth.shadowizardlib.client.particle.ember.EmberParticleProvider;
import net.mindoth.shadowizardlib.event.LightEvents;
import net.mindoth.shadowizardlib.event.ShadowEvents;
import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.registries.ModEntities;
import net.mindoth.spellmaker.util.DimVec3;
import net.mindoth.spellmaker.util.MultiBlockHitResult;
import net.mindoth.spellmaker.util.MultiEntityHitResult;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class ProjectileSpellMultiEntity extends AbstractSpellEntity {

    public ProjectileSpellMultiEntity(EntityType<ProjectileSpellMultiEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ProjectileSpellMultiEntity(Level level, Entity caster, LinkedHashMap<RuneItem, List<Integer>> map) {
        super(ModEntities.SPELL_PROJECTILE_MULTI.get(), level, caster, map);
    }

    @Override
    protected void doClientTickEffects() {
        if ( isRemoved() ) return;
        if ( !level().isClientSide ) return;
        ClientLevel world = (ClientLevel)level();
        Vec3 center = ShadowEvents.getEntityCenter(this);
        Vec3 pos = new Vec3(center.x, getY(), center.z);

        Vec3 vec3 = getDeltaMovement();
        double d5 = vec3.x;
        double d6 = vec3.y;
        double d1 = vec3.z;
        double speed = 0.1D;
        for ( int j = 0; j < 4; j++ ) {
            if ( -this.tickCount < j - 4 ) {
                double variable = 1.0D;
                double vecX = new Random().nextDouble(variable - -variable) + -variable;
                double vecY = new Random().nextDouble(variable - -variable) + -variable;
                double vecZ = new Random().nextDouble(variable - -variable) + -variable;
                world.addParticle(EmberParticleProvider.createData(LightEvents.getParticleColor(getParticleStats()), 0.5F, 8, false, LightEvents.getParticleType(getParticleStats())),
                        pos.x + d5 * (double) j / 4.0D, pos.y + d6 * (double) j / 4.0D, pos.z + d1 * (double) j / 4.0D,
                        vecX * speed, vecY * speed, vecZ * speed);
            }
        }
    }

    @Override
    protected void doMobEffects(EntityHitResult result) {
        if ( this.map != null ) {
            Level level = level();
            AABB box = this.getBoundingBox().inflate(1.4D, 1.4D, 1.4D);
            List<Entity> list = new ArrayList<>(level.getEntities(this, box).stream().filter((entity -> entity instanceof LivingEntity)).toList());
            if ( !list.contains(result.getEntity()) ) list.add(result.getEntity());
            for ( RuneItem rune : this.map.keySet() ) {
                rune.effectOnEntity(this.map.get(rune), new MultiEntityHitResult(this, list, new DimVec3(this.position(), this.level())));
            }
            List<BlockPos> blocks = Lists.newArrayList();
            for ( int x = this.getBlockX() -1; x < this.getBlockX() + 2; x++ ) {
                for ( int y = this.getBlockY() -1; y < this.getBlockY() + 2; y++ ) {
                    for ( int z = this.getBlockZ() - 1; z < this.getBlockZ() + 2; z++ ) {
                        blocks.add(new BlockPos(x, y, z));
                    }
                }
            }
            MultiBlockHitResult mResult = new MultiBlockHitResult(Direction.UP, false, blocks, new DimVec3(position(), level));
            for ( RuneItem rune : this.map.keySet() ) rune.effectOnBlock(this.map.get(rune), mResult);
            aoeEntitySpellParticles(level, box, (float)box.getYsize() * 0.5F, getParticleStats());
        }
    }

    @Override
    protected void doBlockEffects(BlockHitResult result) {
        if ( this.map != null ) {
            Level level = level();
            AABB box = this.getBoundingBox().inflate(1.4D, 1.4D, 1.4D);
            List<Entity> list = new ArrayList<>(level.getEntities(this, box).stream().filter((entity -> entity instanceof LivingEntity)).toList());
            for ( RuneItem rune : this.map.keySet() ) {
                rune.effectOnEntity(this.map.get(rune), new MultiEntityHitResult(this, list, new DimVec3(this.position(), this.level())));
            }
            List<BlockPos> blocks = Lists.newArrayList();
            BlockPos blockPos = getPosOfFace(result.getBlockPos(), result.getDirection());
            for ( int x = blockPos.getX() -1; x < blockPos.getX() + 2; x++ ) {
                for ( int y = blockPos.getY() -1; y < blockPos.getY() + 2; y++ ) {
                    for ( int z = blockPos.getZ() - 1; z < blockPos.getZ() + 2; z++ ) {
                        blocks.add(new BlockPos(x, y, z));
                    }
                }
            }
            MultiBlockHitResult mResult = new MultiBlockHitResult(Direction.UP, false, blocks, new DimVec3(position(), level));
            for ( RuneItem rune : this.map.keySet() ) rune.effectOnBlock(this.map.get(rune), mResult);
            aoeEntitySpellParticles(level, box, (float)box.getYsize() * 0.5F, getParticleStats());
        }
    }

    public static BlockPos getPosOfFace(BlockPos blockPos, Direction face) {
        return switch (face) {
            case UP -> blockPos.above();
            case EAST -> blockPos.east();
            case WEST -> blockPos.west();
            case SOUTH -> blockPos.south();
            case NORTH -> blockPos.north();
            case DOWN -> blockPos.below();
        };
    }

    //TODO: Move to ShadowizardLib
    protected void aoeEntitySpellParticles(Level level, AABB box, float range, HashMap<String, Float> stats) {
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
