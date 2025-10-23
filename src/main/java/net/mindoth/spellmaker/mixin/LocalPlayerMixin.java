package net.mindoth.spellmaker.mixin;

import net.mindoth.spellmaker.mobeffect.PolymorphEffect;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(method = "canStartSprinting", at = @At("RETURN"))
    public boolean preventSprintingWhilePolymorphed(CallbackInfoReturnable<Boolean> callback) {
        LivingEntity living = (LivingEntity)(Object) this;
        AttributeInstance speedModifier = living.getAttribute(Attributes.MOVEMENT_SPEED);
        if ( speedModifier != null && speedModifier.hasModifier(PolymorphEffect.POLYMORPH_SPEED_MODIFIER) ) return false;
        else return callback.getReturnValue();
    }
}
