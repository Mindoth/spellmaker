package net.mindoth.spellmaker.item.armor;

import net.mindoth.spellmaker.SpellMaker;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record AttributeContainer(Holder<Attribute> attribute, double value, AttributeModifier.Operation operation) {

    public AttributeModifier createModifier(String slot) {
        var attributeName = Identifier.parse(attribute.getRegisteredName()).getPath();
        return new AttributeModifier(Identifier.fromNamespaceAndPath(SpellMaker.MOD_ID, String.format("%s_%s_modifier", slot, attributeName)), value, operation);
    }
}