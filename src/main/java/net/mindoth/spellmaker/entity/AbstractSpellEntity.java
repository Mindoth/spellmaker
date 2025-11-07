package net.mindoth.spellmaker.entity;

import net.mindoth.spellmaker.item.sigil.SigilItem;
import net.mindoth.spellmaker.util.DataHelper;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractSpellEntity extends Projectile {

    public AbstractSpellEntity(EntityType<? extends AbstractSpellEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        if ( level().isClientSide() ) doClientTickEffects();
        if ( !level().isClientSide() ) {
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

    //TODO: handle portal travel for projectiles
    public void handleHitDetection() {
        HitResult result = getHitResult(position(), this, this::hitFilter, getDeltaMovement(), level());
        boolean flag = false;
        if ( result.getType() == HitResult.Type.BLOCK ) {
            BlockPos blockpos = ((BlockHitResult)result).getBlockPos();
            BlockState blockstate = this.level().getBlockState(blockpos);
            /*if ( blockstate.is(Blocks.NETHER_PORTAL) ) {
                flag = true;
            }
            else if ( blockstate.is(Blocks.END_GATEWAY) ) {
                BlockEntity blockentity = level().getBlockEntity(blockpos);
                if ( blockentity instanceof TheEndGatewayBlockEntity && TheEndGatewayBlockEntity.canEntityTeleport(this) ) {
                    TheEndGatewayBlockEntity.teleportEntity(level(), blockpos, blockstate, this, (TheEndGatewayBlockEntity)blockentity);
                }
                flag = true;
            }*/
        }
        if ( result.getType() != HitResult.Type.MISS && !flag && !EventHooks.onProjectileImpact(this, result) ) onHit(result);
    }

    protected boolean hitFilter(Entity target) {
        return target instanceof LivingEntity;
    }

    protected HitResult getHitResult(Vec3 pStartVec, Entity pProjectile, Predicate<Entity> pFilter, Vec3 pEndVecOffset, Level pLevel) {
        Vec3 vec3 = pStartVec.add(pEndVecOffset);
        HitResult hitresult = pLevel.clip(new ClipContext(pStartVec, vec3, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, pProjectile));
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
        if ( level().isClientSide() ) doClientHitEffects();
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
        if ( level().isClientSide() ) doClientHitEffects();
        else {
            BlockState blockState = level().getBlockState(result.getBlockPos());
            doBlockEffects(result);
            playHitSound(result);
            level().playSound(null, getX(), getY(), getZ(), blockState.getSoundType().getBreakSound(), SoundSource.PLAYERS, 0.3F, 2);
            doDeathEffects();
        }
    }

    protected void doClientTickEffects() {
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

    public LinkedHashMap<SigilItem, List<Integer>> getMap() {
        List<SigilItem> sigilList = DataHelper.getSigilListFromString(this.entityData.get(SIGIL_LIST));
        List<Integer> magnitudes = DataHelper.getStatsFromString(this.entityData.get(MAGNITUDES));
        List<Integer> durations = DataHelper.getStatsFromString(this.entityData.get(DURATIONS));
        return DataHelper.createMapFromLists(sigilList, magnitudes, durations);
    }

    public float getSpeed() {
        return 1.0F;
    }

    public int getLife() {
        return 160;
    }

    public double getDefaultGravity() {
        //return 0.03D;
        return 0.015D;
    }

    public static final EntityDataAccessor<Integer> RED = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> GREEN = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> BLUE = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<String> SIGIL_LIST = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> MAGNITUDES = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> DURATIONS = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.STRING);

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(RED, input.getInt("red").get());
        this.entityData.set(GREEN, input.getInt("green").get());
        this.entityData.set(BLUE, input.getInt("blue").get());
        this.entityData.set(TYPE, input.getInt("type").get());

        this.entityData.set(SIGIL_LIST, input.getString("sigil_list").get());
        this.entityData.set(MAGNITUDES, input.getString("magnitudes").get());
        this.entityData.set(DURATIONS, input.getString("durations").get());
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("red", this.entityData.get(RED));
        output.putInt("green", this.entityData.get(GREEN));
        output.putInt("blue", this.entityData.get(BLUE));
        output.putInt("type", this.entityData.get(TYPE));

        output.putString("sigil_list", this.entityData.get(SIGIL_LIST));
        output.putString("magnitudes", this.entityData.get(MAGNITUDES));
        output.putString("durations", this.entityData.get(DURATIONS));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(RED, -1);
        builder.define(GREEN, -1);
        builder.define(BLUE, -1);
        builder.define(TYPE, 1);

        builder.define(SIGIL_LIST, "");
        builder.define(MAGNITUDES, "");
        builder.define(DURATIONS, "");
    }

    /*@Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }*/
}
