package net.mindoth.spellmaker.item.rune;

import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class FishPolymorphItem extends PolymorphRuneItem {
    public FishPolymorphItem(Properties pProperties, SpellColor color, int cost, int maxMagnitude, int magnitudeMultiplier, int maxDuration, int durationMultiplier, UUID uuid, EntityType entityType) {
        super(pProperties, color, cost, maxMagnitude, magnitudeMultiplier, maxDuration, durationMultiplier, uuid, entityType);
    }

    @Override
    protected AttributeModifier getSpeedModifier() {
        return new AttributeModifier(getUUID(), "Polymorph Speed", -10.0D, AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected AttributeModifier getSwimSpeedModifier() {
        return new AttributeModifier(getUUID(), "Polymorph Swim Speed", 4.0D, AttributeModifier.Operation.MULTIPLY_BASE);
    }
}
