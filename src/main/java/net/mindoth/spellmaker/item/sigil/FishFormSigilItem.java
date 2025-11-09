package net.mindoth.spellmaker.item.sigil;

import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

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
}
