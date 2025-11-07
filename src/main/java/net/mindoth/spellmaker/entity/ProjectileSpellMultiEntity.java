package net.mindoth.spellmaker.entity;

import com.google.common.collect.Lists;
import net.mindoth.shadowizardlib.client.particle.ember.EmberParticleProvider;
import net.mindoth.shadowizardlib.event.LightEvents;
import net.mindoth.shadowizardlib.event.ShadowEvents;
import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.shadowizardlib.util.MultiBlockHitResult;
import net.mindoth.shadowizardlib.util.MultiEntityHitResult;
import net.mindoth.spellmaker.item.sigil.SigilItem;
import net.mindoth.spellmaker.registries.ModEntities;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ProjectileSpellMultiEntity extends AbstractSpellEntity {

    public ProjectileSpellMultiEntity(EntityType<ProjectileSpellMultiEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ProjectileSpellMultiEntity(Level level) {
        super(ModEntities.SPELL_PROJECTILE_MULTI.get(), level);
    }

    @Override
    protected void doMobEffects(EntityHitResult result) {
        Level level = level();
        AABB box = this.getBoundingBox().inflate(2.2D, 2.2D, 2.2D).move(result.getEntity().getBoundingBox().getCenter().subtract(this.getBoundingBox().getCenter()));
        List<Entity> list = new ArrayList<>(level.getEntities(this, box).stream().filter((entity -> entity instanceof LivingEntity)).toList());
        if ( !list.contains(result.getEntity()) ) list.add(result.getEntity());
        MultiEntityHitResult mEntityResult = new MultiEntityHitResult(this, list, new DimVec3(this.position(), this.level()));
        for ( SigilItem sigil : getMap().keySet() ) sigil.effectOnEntity(this.getOwner(), this, getMap().get(sigil), mEntityResult);

        List<BlockPos> blocks = Lists.newArrayList();
        for ( int x = this.getBlockX() -1; x < this.getBlockX() + 2; x++ ) {
            for ( int y = this.getBlockY() -1; y < this.getBlockY() + 2; y++ ) {
                for ( int z = this.getBlockZ() - 1; z < this.getBlockZ() + 2; z++ ) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        MultiBlockHitResult mBlockResult = new MultiBlockHitResult(Direction.UP, false, blocks, new DimVec3(position(), level));
        for ( SigilItem sigil : getMap().keySet() ) sigil.effectOnBlock(this.getOwner(), this, getMap().get(sigil), mBlockResult);
        aoeEntitySpellParticles(level, box, (float)box.getYsize() * 0.5F, getParticleStats());
    }

    @Override
    protected void doBlockEffects(BlockHitResult result) {
        Level level = level();
        AABB box = this.getBoundingBox().inflate(2.2D, 2.2D, 2.2D);
        List<Entity> list = new ArrayList<>(level.getEntities(this, box).stream().filter((entity -> entity instanceof LivingEntity)).toList());
        MultiEntityHitResult mEntityResult = new MultiEntityHitResult(this, list, new DimVec3(this.position(), this.level()));
        for ( SigilItem sigil : getMap().keySet() ) sigil.effectOnEntity(this.getOwner(), this, getMap().get(sigil), mEntityResult);

        List<BlockPos> blocks = Lists.newArrayList();
        BlockPos blockPos = getPosOfFace(result.getBlockPos(), result.getDirection());
        for ( int x = blockPos.getX() -1; x < blockPos.getX() + 2; x++ ) {
            for ( int y = blockPos.getY() -1; y < blockPos.getY() + 2; y++ ) {
                for ( int z = blockPos.getZ() - 1; z < blockPos.getZ() + 2; z++ ) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        MultiBlockHitResult mBlockResult = new MultiBlockHitResult(Direction.UP, false, blocks, new DimVec3(position(), level));
        for ( SigilItem sigil : getMap().keySet() ) sigil.effectOnBlock(this.getOwner(), this, getMap().get(sigil), mBlockResult);
        aoeEntitySpellParticles(level, box, (float)box.getYsize() * 0.5F, getParticleStats());
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

    @Override
    protected void doClientTickEffects() {
        if ( isRemoved() ) return;
        if ( !level().isClientSide() ) return;
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
                for ( int i = 0; i < 2; i++ ) {
                    float particleSize = Math.min(0.8F, (0.8F * 0.1F) * this.tickCount);
                    float sphereSize = 0.8F / 4;
                    float randX = (float)((Math.random() * (sphereSize - (-sphereSize))) + (-sphereSize));
                    float randY = (float)((Math.random() * (sphereSize - (-sphereSize))) + (-sphereSize));
                    float randZ = (float)((Math.random() * (sphereSize - (-sphereSize))) + (-sphereSize));
                    int life = 4;
                    world.addParticle(EmberParticleProvider.createData(LightEvents.getParticleColor(getParticleStats()), particleSize, life, true, LightEvents.getParticleType(getParticleStats())),
                            true, true, pos.x + randX + d5 * (double)j / 4.0D, pos.y + randY + d6 * (double)j / 4.0D, pos.z + randZ + d1 * (double)j / 4.0D, 0, 0, 0);
                }
                float particleSize = 0.25F;
                int life = 1 + new Random().nextInt(11);
                world.addParticle(EmberParticleProvider.createData(LightEvents.getParticleColor(getParticleStats()), particleSize, life, true, LightEvents.getParticleType(getParticleStats())),
                        true, true, pos.x, pos.y, pos.z, vecX * speed, vecY * speed, vecZ * speed);
            }
        }
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
