package net.mindoth.spellmaker.mixin;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.item.sigil.PolymorphSigilItem;
import net.mindoth.spellmaker.mobeffect.AbstractStunEffect;
import net.mindoth.spellmaker.mobeffect.PolymorphEffect;
import net.mindoth.spellmaker.mobeffect.SyncedMobEffect;
import net.mindoth.spellmaker.registries.ModEffects;
import net.mindoth.spellmaker.registries.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "onEffectsRemoved", at = @At(value = "HEAD"))
    public void onEffectsRemovedCallback(Collection<MobEffectInstance> instances, CallbackInfo callback) {
        LivingEntity living = (LivingEntity)(Object)this;
        if ( !living.level().isClientSide() ) {
            for ( MobEffectInstance instance : instances ) {
                if ( instance.getEffect().value() instanceof SyncedMobEffect mobEffect ) {
                    mobEffect.onEffectRemoved(living, instance.getAmplifier(), false);
                    if ( living.level().getChunkSource() instanceof ServerChunkCache serverChunk ) {
                        serverChunk.sendToTrackingPlayersAndSelf(living, new ClientboundRemoveMobEffectPacket(living.getId(), instance.getEffect()));
                    }
                }
            }
        }
    }

    @Inject(method = "isImmobile", at = @At(value = "HEAD"), cancellable = true)
    public void stopMovementWhileSleeping(CallbackInfoReturnable<Boolean> callback) {
        LivingEntity living = (LivingEntity)(Object)this;
        if ( AbstractStunEffect.isStunned(living) && living instanceof Mob ) callback.setReturnValue(true);
    }

    //Might be doable with a new method now
    @Inject(method = "checkBedExists", at = @At(value = "HEAD"), cancellable = true)
    public void allowSleepWithMobEffect(CallbackInfoReturnable<Boolean> callback) {
        LivingEntity living = (LivingEntity)(Object)this;
        if ( living.hasEffect(ModEffects.SLEEP) ) callback.setReturnValue(true);
    }

    @Inject(method = "stopSleeping", at = @At(value = "HEAD"), cancellable = true)
    public void stopWakingUpWhileSleeping(CallbackInfo callback) {
        LivingEntity living = (LivingEntity)(Object)this;
        if ( living.hasEffect(ModEffects.SLEEP) ) callback.cancel();
    }

    @Unique
    private static List<Holder<MobEffect>> getEffects(LivingEntity living) {
        List<Holder<MobEffect>> list = Lists.newArrayList();
        PolymorphSigilItem sigil = PolymorphEffect.getFormSigil(living);
        if ( sigil != null ) list.addAll(sigil.polymorphEffects(living));
        return list;
    }

    @Inject(method = "hasEffect", at=@At("HEAD"), cancellable = true)
    private void fishHasEffect(Holder<MobEffect> mobEffect, CallbackInfoReturnable<Boolean> callback) {
        LivingEntity living = (LivingEntity)(Object)this;
        if ( getEffects(living).contains(mobEffect) ) callback.setReturnValue(true);
    }

    @Unique
    private static MobEffectInstance createEffectInstance(Holder<MobEffect> effect) {
        return new MobEffectInstance(effect, -1, 0);
    }

    @Inject(method = "getEffect", at=@At("HEAD"), cancellable = true)
    private void fishGetEffect(Holder<MobEffect> mobEffect, CallbackInfoReturnable<MobEffectInstance> callback) {
        LivingEntity living = (LivingEntity)(Object)this;
        if ( getEffects(living).contains(mobEffect) ) callback.setReturnValue(createEffectInstance(mobEffect));
    }
}
