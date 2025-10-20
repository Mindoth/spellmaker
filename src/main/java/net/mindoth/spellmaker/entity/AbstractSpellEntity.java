package net.mindoth.spellmaker.entity;

import net.mindoth.shadowizardlib.client.particle.ember.EmberParticleProvider;
import net.mindoth.shadowizardlib.event.LightEvents;
import net.mindoth.shadowizardlib.event.ShadowEvents;
import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.util.DataHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public abstract class AbstractSpellEntity extends Projectile {

    public AbstractSpellEntity(EntityType<? extends AbstractSpellEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        if ( level().isClientSide ) doClientTickEffects();
        if ( !level().isClientSide ) {
            doTickEffects();
            if ( this.tickCount > getLife() ) doExpirationEffects();
        }
        handleHitDetection();
        handleTravel();
    }

    public void handleTravel() {
        this.xOld = getX();
        this.yOld = getY();
        this.zOld = getZ();
        setPos(position().add(getDeltaMovement()));
        this.updateRotation();
        if ( !this.isNoGravity() ) {
            Vec3 vec34 = this.getDeltaMovement();
            this.setDeltaMovement(vec34.x, vec34.y - (double)getGravity(), vec34.z);
        }
    }

    public void handleHitDetection() {
        HitResult result = getHitResult(position(), this, this::hitFilter, getDeltaMovement(), level());
        boolean flag = false;
        if ( result.getType() == HitResult.Type.BLOCK ) {
            BlockPos blockpos = ((BlockHitResult)result).getBlockPos();
            BlockState blockstate = this.level().getBlockState(blockpos);
            if ( blockstate.is(Blocks.NETHER_PORTAL) ) {
                handleInsidePortal(blockpos);
                flag = true;
            }
            else if ( blockstate.is(Blocks.END_GATEWAY) ) {
                BlockEntity blockentity = level().getBlockEntity(blockpos);
                if ( blockentity instanceof TheEndGatewayBlockEntity && TheEndGatewayBlockEntity.canEntityTeleport(this) ) {
                    TheEndGatewayBlockEntity.teleportEntity(level(), blockpos, blockstate, this, (TheEndGatewayBlockEntity)blockentity);
                }
                flag = true;
            }
        }
        if ( result.getType() != HitResult.Type.MISS && !flag && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, result) ) onHit(result);
    }

    protected boolean hitFilter(Entity target) {
        return target instanceof LivingEntity;
    }

    protected HitResult getHitResult(Vec3 pStartVec, Entity pProjectile, Predicate<Entity> pFilter, Vec3 pEndVecOffset, Level pLevel) {
        Vec3 vec3 = pStartVec.add(pEndVecOffset);
        HitResult hitresult = pLevel.clip(new ClipContext(pStartVec, vec3, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, pProjectile));
        if ( hitresult.getType() != HitResult.Type.MISS ) vec3 = hitresult.getLocation();
        HitResult hitresult1 = getEntityHitResult(pLevel, pProjectile, pStartVec, vec3, pProjectile.getBoundingBox().expandTowards(pEndVecOffset).inflate(1.0D), pFilter);
        if ( hitresult1 != null ) hitresult = hitresult1;
        return hitresult;
    }

    @Nullable
    protected EntityHitResult getEntityHitResult(Level pLevel, Entity pProjectile, Vec3 pStartVec, Vec3 pEndVec, AABB pBoundingBox, Predicate<Entity> pFilter) {
        return ProjectileUtil.getEntityHitResult(pLevel, pProjectile, pStartVec, pEndVec, pBoundingBox, pFilter, 0.5F);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if ( level().isClientSide ) doClientHitEffects();
        else {
            if ( result.getEntity() instanceof LivingEntity ) {
                doMobEffects(result);
                playHitSound(result);
                doDeathEffects();
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if ( level().isClientSide ) doClientHitEffects();
        else {
            BlockState blockState = level().getBlockState(result.getBlockPos());
            doBlockEffects(result);
            playHitSound(result);
            level().playSound(null, getX(), getY(), getZ(), blockState.getSoundType().getBreakSound(), SoundSource.PLAYERS, 0.3F, 2);
            doDeathEffects();
        }
    }

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
        double speed = 0.05D;
        for ( int j = 0; j < 4; j++ ) {
            if ( -this.tickCount < j - 4 ) {
                double variable = 1.0D;
                double vecX = new Random().nextDouble(variable - -variable) + -variable;
                double vecY = new Random().nextDouble(variable - -variable) + -variable;
                double vecZ = new Random().nextDouble(variable - -variable) + -variable;
                int life = 8;
                world.addParticle(EmberParticleProvider.createData(LightEvents.getParticleColor(getParticleStats()), 0.1F, life, false, LightEvents.getParticleType(getParticleStats())),
                        pos.x + d5 * (double) j / 4.0D, pos.y + d6 * (double) j / 4.0D, pos.z + d1 * (double) j / 4.0D,
                        vecX * speed, vecY * speed, vecZ * speed);
            }
        }
    }

    protected void doClientHitEffects() {
    }

    protected void doMobEffects(EntityHitResult result) {
    }

    protected void doBlockEffects(BlockHitResult result) {
    }

    protected void playHitSound(HitResult result) {
    }

    protected void doTickEffects() {
    }

    protected void doExpirationEffects() {
        doDeathEffects();
    }

    protected void doDeathEffects() {
        this.discard();
    }

    public HashMap<String, Float> getParticleStats() {
        HashMap<String, Float> map = new HashMap<>();
        map.put("red", (float)this.entityData.get(RED));
        map.put("green", (float)this.entityData.get(GREEN));
        map.put("blue", (float)this.entityData.get(BLUE));
        map.put("type", (float)this.entityData.get(TYPE));
        return map;
    }

    public LinkedHashMap<RuneItem, List<Integer>> getMap() {
        List<RuneItem> runeList = DataHelper.getRuneListFromString(this.entityData.get(RUNE_LIST));
        List<Integer> magnitudes = DataHelper.getStatsFromString(this.entityData.get(MAGNITUDES));
        List<Integer> durations = DataHelper.getStatsFromString(this.entityData.get(DURATIONS));
        return DataHelper.createMapFromLists(runeList, magnitudes, durations);
    }

    public float getSpeed() {
        return 1.0F;
    }

    public int getLife() {
        return 100;
    }

    public float getGravity() {
        //return 0.03F;
        return 0.015F;
    }

    public static final EntityDataAccessor<Integer> RED = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> GREEN = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> BLUE = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<String> RUNE_LIST = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> MAGNITUDES = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> DURATIONS = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.STRING);

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.entityData.set(RED, compound.getInt("red"));
        this.entityData.set(GREEN, compound.getInt("green"));
        this.entityData.set(BLUE, compound.getInt("blue"));
        this.entityData.set(TYPE, compound.getInt("type"));

        this.entityData.set(RUNE_LIST, compound.getString("rune_list"));
        this.entityData.set(MAGNITUDES, compound.getString("magnitudes"));
        this.entityData.set(DURATIONS, compound.getString("durations"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("red", this.entityData.get(RED));
        compound.putInt("green", this.entityData.get(GREEN));
        compound.putInt("blue", this.entityData.get(BLUE));
        compound.putInt("type", this.entityData.get(TYPE));

        compound.putString("rune_list", this.entityData.get(RUNE_LIST));
        compound.putString("magnitudes", this.entityData.get(MAGNITUDES));
        compound.putString("durations", this.entityData.get(DURATIONS));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(RED, -1);
        this.entityData.define(GREEN, -1);
        this.entityData.define(BLUE, -1);
        this.entityData.define(TYPE, 1);

        this.entityData.define(RUNE_LIST, "");
        this.entityData.define(MAGNITUDES, "");
        this.entityData.define(DURATIONS, "");
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
