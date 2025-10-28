package net.mindoth.spellmaker.mixin;

import net.mindoth.spellmaker.mobeffect.AbstractStunEffect;
import net.mindoth.spellmaker.mobeffect.PolymorphEffect;
import net.mindoth.spellmaker.registries.ModEffects;
import net.mindoth.spellmaker.registries.ModItems;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "isImmobile", at = @At(value = "HEAD"), cancellable = true)
    public void stopMovementWhileSleeping(CallbackInfoReturnable<Boolean> callback) {
        LivingEntity living = (LivingEntity)(Object)this;
        if ( AbstractStunEffect.isStunned(living) && living instanceof Mob ) callback.setReturnValue(true);
    }

    @Inject(method = "checkBedExists", at = @At(value = "HEAD"), cancellable = true)
    public void allowSleepWithMobEffect(CallbackInfoReturnable<Boolean> callback) {
        LivingEntity living = (LivingEntity)(Object)this;
        if ( living.hasEffect(ModEffects.SLEEP.get()) ) callback.setReturnValue(true);
    }

    @Inject(method = "stopSleeping", at = @At(value = "HEAD"), cancellable = true)
    public void stopWakingUpWhileSleeping(CallbackInfo callback) {
        LivingEntity living = (LivingEntity)(Object)this;
        if ( living.hasEffect(ModEffects.SLEEP.get()) ) callback.cancel();
    }

    @Unique
    private static final MobEffectInstance NIGHT_VISION = new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0);
    private static boolean isFishInWater(LivingEntity living) {
        if ( !(living instanceof Player player) ) return false;
        return PolymorphEffect.isPolymorphed(player) && PolymorphEffect.getTransformationSigil(player) == ModItems.FISH_TRANSFORMATION_SIGIL.get() && player.isUnderWater();
    }

    @Inject(method = "hasEffect", at=@At("HEAD"), cancellable = true)
    private void fishHasEffect(MobEffect mobEffect, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity living = (LivingEntity)(Object)this;
        if ( mobEffect == MobEffects.NIGHT_VISION && isFishInWater(living) ) cir.setReturnValue(true);
    }

    @Inject(method = "getEffect", at=@At("HEAD"), cancellable = true)
    private void fishGetEffect(MobEffect mobEffect, CallbackInfoReturnable<MobEffectInstance> cir) {
        LivingEntity living = (LivingEntity)(Object)this;
        if ( mobEffect == MobEffects.NIGHT_VISION && isFishInWater(living) ) cir.setReturnValue(NIGHT_VISION);
    }
}
