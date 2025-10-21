package net.mindoth.spellmaker.mixin;

import net.mindoth.spellmaker.mobeffect.AbstractStunEffect;
import net.mindoth.spellmaker.registries.ModEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "isImmobile", at = @At(value = "HEAD"), cancellable = true)
    public void stopMovementWhileSleeping(CallbackInfoReturnable<Boolean> callback) {
        LivingEntity living = (LivingEntity)(Object) this;
        if ( AbstractStunEffect.isStunned(living) && living instanceof Mob ) callback.setReturnValue(true);
    }

    @Inject(method = "checkBedExists", at = @At(value = "HEAD"), cancellable = true)
    public void allowSleepWithMobEffect(CallbackInfoReturnable<Boolean> callback) {
        LivingEntity living = (LivingEntity)(Object) this;
        if ( living.hasEffect(ModEffects.SLEEP.get()) ) callback.setReturnValue(true);
    }

    @Inject(method = "stopSleeping", at = @At(value = "HEAD"), cancellable = true)
    public void stopWakingUpWhileSleeping(CallbackInfo callback) {
        LivingEntity living = (LivingEntity)(Object) this;
        if ( living.hasEffect(ModEffects.SLEEP.get()) ) callback.cancel();
    }
}
