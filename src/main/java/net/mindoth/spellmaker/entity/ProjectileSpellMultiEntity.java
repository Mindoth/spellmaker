package net.mindoth.spellmaker.entity;

import com.google.common.collect.Lists;
import net.mindoth.shadowizardlib.client.particle.ember.EmberParticleProvider;
import net.mindoth.shadowizardlib.client.particle.ember.ParticleColor;
import net.mindoth.shadowizardlib.event.ShadowEvents;
import net.mindoth.shadowizardlib.network.PacketSendCustomParticles;
import net.mindoth.shadowizardlib.network.ShadowNetwork;
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
import net.minecraft.world.phys.*;

import java.util.*;

import static net.mindoth.shadowizardlib.event.ShadowEvents.defaultStats;
import static net.mindoth.shadowizardlib.event.ShadowEvents.summonParticleLine;

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
                world.addParticle(EmberParticleProvider.createData(getParticleColor(), 0.5F, 8, false, getRenderType()),
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
            for ( Entity entity : level.getEntities(null, box).stream().filter((entity -> entity instanceof LivingEntity)).toList() ) {
                for ( RuneItem rune : this.map.keySet() ) {
                    rune.effectOnEntity(this.map.get(rune), new MultiEntityHitResult(this, Collections.singletonList(entity), new DimVec3(entity.position(), entity.level())));
                }
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
            aoeEntitySpellParticles(level, box, (float)box.getYsize() * 0.5F, defaultStats());
        }
    }

    @Override
    protected void doBlockEffects(BlockHitResult result) {
        if ( this.map != null ) {
            Level level = level();
            AABB box = this.getBoundingBox().inflate(1.4D, 1.4D, 1.4D);
            for ( Entity entity : level.getEntities(null, box).stream().filter((entity -> entity instanceof LivingEntity)).toList() ) {
                for ( RuneItem rune : this.map.keySet() ) {
                    rune.effectOnEntity(this.map.get(rune), new MultiEntityHitResult(this, Collections.singletonList(entity), new DimVec3(entity.position(), entity.level())));
                }
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
            aoeEntitySpellParticles(level, box, (float)box.getYsize() * 0.5F, defaultStats());
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
        addAoeParticles(false, level, box, 0.15F, 8, stats);
    }

    protected void addAoeParticles(boolean targetBlocks, Level level, AABB box, float size, int age, HashMap<String, Float> stats) {
        Vec3 center = box.getCenter();
        double maxX = box.maxX;
        double minX = box.minX;
        double maxY = box.maxY;
        double minY = box.minY;
        double maxZ = box.maxZ;
        double minZ = box.minZ;

        if ( targetBlocks ) {
            int amountX = 4 * (int)box.getXsize();
            int amountY = 4 * (int)box.getYsize();
            int amountZ = 4 * (int)box.getZsize();

            //VectorPos for each corner
            Vec3 pos0 = new Vec3(minX, minY, minZ);
            Vec3 pos1 = new Vec3(maxX, minY, minZ);
            Vec3 pos2 = new Vec3(minX, minY, maxZ);
            Vec3 pos3 = new Vec3(maxX, minY, maxZ);
            Vec3 pos4 = new Vec3(minX, maxY, minZ);
            Vec3 pos5 = new Vec3(maxX, maxY, minZ);
            Vec3 pos6 = new Vec3(minX, maxY, maxZ);
            Vec3 pos7 = new Vec3(maxX, maxY, maxZ);
            //Bottom corners
            generateParticles(pos0, center, level, size, age, 0, 0, 0, stats);
            generateParticles(pos1, center, level, size, age, 0, 0, 0, stats);
            generateParticles(pos2, center, level, size, age, 0, 0, 0, stats);
            generateParticles(pos3, center, level, size, age, 0, 0, 0, stats);
            //Top corners
            generateParticles(pos4, center, level, size, age, 0, 0, 0, stats);
            generateParticles(pos5, center, level, size, age, 0, 0, 0, stats);
            generateParticles(pos6, center, level, size, age, 0, 0, 0, stats);
            generateParticles(pos7, center, level, size, age, 0, 0, 0, stats);
            //Bottom edges
            summonParticleLine(pos0, pos1, amountX, center, level, size, age, stats);
            summonParticleLine(pos0, pos2, amountZ, center, level, size, age, stats);
            summonParticleLine(pos3, pos1, amountZ, center, level, size, age, stats);
            summonParticleLine(pos3, pos2, amountX, center, level, size, age, stats);
            //Middle edges
            summonParticleLine(pos0, pos4, amountY, center, level, size, age, stats);
            summonParticleLine(pos1, pos5, amountY, center, level, size, age, stats);
            summonParticleLine(pos2, pos6, amountY, center, level, size, age, stats);
            summonParticleLine(pos3, pos7, amountY, center, level, size, age, stats);
            //Top edges
            summonParticleLine(pos4, pos5, amountX, center, level, size, age, stats);
            summonParticleLine(pos4, pos6, amountZ, center, level, size, age, stats);
            summonParticleLine(pos7, pos5, amountZ, center, level, size, age, stats);
            summonParticleLine(pos7, pos6, amountX, center, level, size, age, stats);
        }
        else {
            int amount = 4 * Math.max((int)box.getYsize(), (int)box.getXsize());
            for ( int i = 0; i < amount; i++ ) {
                double vec = 0.05D + (0.25D - 0.05D) * new Random().nextDouble();
                //double vec = 0.15D;
                generateParticles(new Vec3(maxX, center.y - 0.5D + new Random().nextDouble(), minZ + (maxZ - minZ) * new Random().nextDouble()), center, level, size, age, 0, vec, 0, stats);
                generateParticles(new Vec3(minX, center.y - 0.5D + new Random().nextDouble(), minZ + (maxZ - minZ) * new Random().nextDouble()), center, level, size, age, 0, vec, 0, stats);
                generateParticles(new Vec3(minX + (maxX - minX) * new Random().nextDouble(), center.y - 0.5D + new Random().nextDouble(), minZ), center, level, size, age, 0, vec, 0, stats);
                generateParticles(new Vec3(minX + (maxX - minX) * new Random().nextDouble(), center.y - 0.5D + new Random().nextDouble(), maxZ), center, level, size, age, 0, vec, 0, stats);
            }
        }
    }

    public void generateParticles(Vec3 pos, Vec3 center, Level level, float size, int age, double vecX, double vecY, double vecZ, HashMap<String, Float> stats) {
        ParticleColor.IntWrapper color = new ParticleColor.IntWrapper(getParticleColor());
        ShadowNetwork.sendToNearby(new PacketSendCustomParticles(color.r, color.g, color.b, size, age, false, getRenderType(), pos.x, pos.y, pos.z, vecX, vecY, vecZ), level, center);
    }
}
