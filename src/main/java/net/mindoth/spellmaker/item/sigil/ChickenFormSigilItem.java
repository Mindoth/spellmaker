package net.mindoth.spellmaker.item.sigil;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class ChickenFormSigilItem extends PolymorphSigilItem {
    public ChickenFormSigilItem(Properties pProperties, SpellColor color, int cost, int minMagnitude, int maxMagnitude, int magnitudeMultiplier, int minDuration, int maxDuration, int durationMultiplier, ResourceLocation uuid, EntityType entityType) {
        super(pProperties, color, cost, minMagnitude, maxMagnitude, magnitudeMultiplier, minDuration, maxDuration, durationMultiplier, uuid, entityType);
    }

    @Override
    public List<Holder<MobEffect>> polymorphEffects(LivingEntity living) {
        List<Holder<MobEffect>> list = Lists.newArrayList();
        if ( living instanceof Player && !living.onGround() ) list.add(MobEffects.SLOW_FALLING);
        return list;
    }

    //TODO: fix animations
    @Override
    public void extraSync(LivingEntity living, Player player, float partialTick) {
        if ( living instanceof Chicken chicken ) {
            chicken.oFlap = chicken.flap;
            chicken.oFlapSpeed = chicken.flapSpeed;
            chicken.flapSpeed += (chicken.onGround() ? -1.0F : 4.0F) * 0.3F;
            chicken.flapSpeed = Mth.clamp(chicken.flapSpeed, 0.0F, 1.0F);
            if ( !chicken.onGround() && chicken.flapping < 1.0F ) chicken.flapping = 1.0F;
            chicken.flapping *= 0.9F;
            chicken.flap += chicken.flapping * 2.0F;
        }
    }
}
