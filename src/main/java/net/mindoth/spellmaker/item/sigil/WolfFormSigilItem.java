package net.mindoth.spellmaker.item.sigil;

import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class WolfFormSigilItem extends PolymorphSigilItem {

    public WolfFormSigilItem(Properties pProperties, SpellColor color, int cost, int minMagnitude, int maxMagnitude, int magnitudeMultiplier, int minDuration, int maxDuration, int durationMultiplier, Identifier id, EntityType entityType) {
        super(pProperties, color, cost, minMagnitude, maxMagnitude, magnitudeMultiplier, minDuration, maxDuration, durationMultiplier, id, entityType);
    }

    @Override
    protected AttributeModifier getSpeedModifier() {
        return new AttributeModifier(POLYMORPH_SPEED_MODIFIER_UUID, 0.05D, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected AttributeModifier getHealthModifier(float currentMaxHealth) {
        return new AttributeModifier(getUUID(), 8.0D - currentMaxHealth, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public int getStrengthModifier() {
        return 6;
    }

    @Override
    public boolean canSprint(LivingEntity living) {
        return true;
    }

    @Override
    public boolean canAttack(LivingEntity living) {
        return !living.hasItemInSlot(EquipmentSlot.MAINHAND) || !isItemWeapon(living.getItemBySlot(EquipmentSlot.MAINHAND));
    }

    private boolean isItemWeapon(ItemStack stack) {
        for ( ItemAttributeModifiers.Entry entry : stack.getAttributeModifiers().modifiers() ) {
            if ( entry.attribute().equals(Attributes.ATTACK_DAMAGE) || entry.attribute().equals(Attributes.ATTACK_SPEED) ) {
                return true;
            }
        }
        return false;
    }
}
