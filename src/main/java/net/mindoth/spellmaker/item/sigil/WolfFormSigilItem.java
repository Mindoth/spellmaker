package net.mindoth.spellmaker.item.sigil;

import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class WolfFormSigilItem extends PolymorphSigilItem {

    public WolfFormSigilItem(Properties pProperties, SpellColor color, int cost, int minMagnitude, int maxMagnitude, int magnitudeMultiplier, int minDuration, int maxDuration, int durationMultiplier, Identifier id, EntityType entityType) {
        super(pProperties, color, cost, minMagnitude, maxMagnitude, magnitudeMultiplier, minDuration, maxDuration, durationMultiplier, id, entityType);
    }

    @Override
    protected AttributeModifier getSpeedModifier() {
        return new AttributeModifier(POLYMORPH_SPEED_MODIFIER_UUID, 0.05D, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected AttributeModifier getHealthModifier(float currentHealth) {
        return new AttributeModifier(getUUID(), 8.0D - currentHealth, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected AttributeModifier getStrengthModifier() {
        return new AttributeModifier(getUUID(), 6.0D, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean canSprint(LivingEntity living) {
        return true;
    }

    @Override
    public boolean canAttack(LivingEntity living) {
        return !living.hasItemInSlot(EquipmentSlot.MAINHAND);
    }
}
