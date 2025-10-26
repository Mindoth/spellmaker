package net.mindoth.spellmaker.mixin;

import net.mindoth.spellmaker.mobeffect.PolymorphEffect;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//@Mixin(Player.class)
public class PlayerMixin {

    /*@Inject(method = "getDimensions", at = @At(value = "RETURN"), cancellable = true)
    public void getSize(Pose pose, CallbackInfoReturnable<EntityDimensions> callback) {
        LivingEntity living = (LivingEntity)(Object)this;
        if ( PolymorphEffect.isPolymorphed(living) && PolymorphEffect.getPolymorphDimensions(living) != null ) {
            callback.setReturnValue(PolymorphEffect.getPolymorphDimensions(living));
        }
    }

    @Inject(method = "getStandingEyeHeight", at = @At("RETURN"), cancellable = true)
    public void getHeight(Pose pose, EntityDimensions entityDimensions, CallbackInfoReturnable<Float> callback) {
        LivingEntity living = (LivingEntity)(Object)this;
        if ( PolymorphEffect.isPolymorphed(living) && PolymorphEffect.getPolymorphDimensions(living) != null ) {
            callback.setReturnValue(PolymorphEffect.getPolymorphDimensions(living).height * 0.85F);
        }
    }*/
}
