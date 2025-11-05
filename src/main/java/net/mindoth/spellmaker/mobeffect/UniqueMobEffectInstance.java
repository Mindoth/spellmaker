package net.mindoth.spellmaker.mobeffect;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class UniqueMobEffectInstance extends MobEffectInstance {

    private final ResourceLocation id;
    public ResourceLocation getId() {
        return this.id;
    }

    public UniqueMobEffectInstance(Holder<MobEffect> effect, int duration, int amplifier, boolean ambient, boolean visible, ResourceLocation id) {
        super(effect, duration, amplifier, ambient, visible, visible);
        this.id = id;
    }
}
