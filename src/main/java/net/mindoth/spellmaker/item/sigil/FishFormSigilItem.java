package net.mindoth.spellmaker.item.sigil;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class FishFormSigilItem extends PolymorphSigilItem {
    public FishFormSigilItem(Properties pProperties, SpellColor color, int cost, int minMagnitude, int maxMagnitude, int magnitudeMultiplier, int minDuration, int maxDuration, int durationMultiplier, ResourceLocation uuid, EntityType entityType) {
        super(pProperties, color, cost, minMagnitude, maxMagnitude, magnitudeMultiplier, minDuration, maxDuration, durationMultiplier, uuid, entityType);
    }

    @Override
    protected AttributeModifier getSpeedModifier() {
        return new AttributeModifier(POLYMORPH_SPEED_MODIFIER_UUID, -0.1D, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected AttributeModifier getSwimSpeedModifier() {
        return new AttributeModifier(getUUID(), 4.0D, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected boolean canSprint(LivingEntity living) {
        boolean isFish = PolymorphSigilItem.isFish(living);
        return isFish && living.isInWater();
    }

    @Override
    public List<Holder<MobEffect>> polymorphEffects(LivingEntity living) {
        List<Holder<MobEffect>> list = Lists.newArrayList();
        if ( living instanceof Player && living.isUnderWater() ) list.add(MobEffects.NIGHT_VISION);
        return list;
    }
}
