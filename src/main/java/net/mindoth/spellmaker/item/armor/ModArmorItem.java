package net.mindoth.spellmaker.item.armor;

import net.mindoth.spellmaker.registries.ModAttributes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;

public class ModArmorItem extends Item {

    public final ArmorMaterial material;
    public final ArmorType type;

    public ModArmorItem(Properties properties, ArmorMaterial material, ArmorType type, AttributeContainer... extraAttributes) {
        super(magickArmor(properties, material, type, extraAttributes));
        this.material = material;
        this.type = type;
    }

    public static Properties magickArmor(Properties properties, ArmorMaterial material, ArmorType type, AttributeContainer... extraAttributes) {
        return properties.durability(type.getDurability(material.durability())).attributes(withDefaultAttributes(material, type, extraAttributes))
                .enchantable(material.enchantmentValue())
                .component(DataComponents.EQUIPPABLE, Equippable.builder(type.getSlot())
                        .setEquipSound(material.equipSound())
                        .setAsset(material.assetId()).build())
                .repairable(material.repairIngredient());
    }

    public static ItemAttributeModifiers withDefaultAttributes(ArmorMaterial material, ArmorType armorType, AttributeContainer... extraAttributes) {
        int i = (Integer)material.defense().getOrDefault(armorType, 0);
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        EquipmentSlotGroup equipmentslotgroup = EquipmentSlotGroup.bySlot(armorType.getSlot());
        ResourceLocation resourcelocation = ResourceLocation.withDefaultNamespace("armor." + armorType.getName());
        builder.add(Attributes.ARMOR, new AttributeModifier(resourcelocation, (double)i, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup);
        builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(resourcelocation, (double)material.toughness(), AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup);
        if ( material.knockbackResistance() > 0.0F ) {
            builder.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(resourcelocation, (double)material.knockbackResistance(), AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup);
        }

        for ( AttributeContainer holder : extraAttributes ) builder.add(holder.attribute(), holder.createModifier(armorType.getSlot().getName()), equipmentslotgroup);

        return builder.build();
    }

    public static AttributeContainer[] withMagickAttributes(int mana, double discount) {
        return new AttributeContainer[] { new AttributeContainer(ModAttributes.MANA_MAX, mana, AttributeModifier.Operation.ADD_VALUE),
                new AttributeContainer(ModAttributes.MANA_COST_MULTIPLIER, discount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE) };
    }
}
