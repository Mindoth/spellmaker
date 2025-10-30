package net.mindoth.spellmaker.entity;

import net.mindoth.shadowizardlib.client.particle.ember.EmberParticleProvider;
import net.mindoth.shadowizardlib.event.LightEvents;
import net.mindoth.shadowizardlib.event.ShadowEvents;
import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.shadowizardlib.util.MultiBlockHitResult;
import net.mindoth.shadowizardlib.util.MultiEntityHitResult;
import net.mindoth.spellmaker.item.sigil.SigilItem;
import net.mindoth.spellmaker.registries.ModEntities;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.Random;

public class ProjectileSpellSingleEntity extends AbstractSpellEntity {

    public ProjectileSpellSingleEntity(EntityType<ProjectileSpellSingleEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ProjectileSpellSingleEntity(Level level) {
        super(ModEntities.SPELL_PROJECTILE_SINGLE.get(), level);
    }

    @Override
    protected void doMobEffects(EntityHitResult result) {
        MultiEntityHitResult mResult = new MultiEntityHitResult(this, Collections.singletonList(result.getEntity()), new DimVec3(this.position(), level()));
        for ( SigilItem sigil : getMap().keySet() ) sigil.effectOnEntity(getMap().get(sigil), mResult);
    }

    @Override
    protected void doBlockEffects(BlockHitResult result) {
        MultiBlockHitResult mResult = new MultiBlockHitResult(result.getDirection(), result.isInside(), Collections.singletonList(result.getBlockPos()), new DimVec3(result.getLocation(), level()));
        for ( SigilItem sigil : getMap().keySet() ) sigil.effectOnBlock(getMap().get(sigil), mResult);
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
}
