package net.mindoth.spellmaker.item.armor;

import com.google.common.base.Suppliers;
import net.mindoth.spellmaker.registries.ModAttributes;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.function.Supplier;

public class ModArmorItem extends ArmorItem {
    private final Supplier<ItemAttributeModifiers> defaultModifiers;

    public ModArmorItem(Holder<ArmorMaterial> pMaterial, Type pType, Properties pProperties, AttributeContainer... attributes) {
        super(pMaterial, pType, pProperties);

        this.defaultModifiers = Suppliers.memoize(
                () -> {
                    int i = pMaterial.value().getDefense(pType);
                    float f = pMaterial.value().toughness();
                    ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
                    EquipmentSlotGroup equipmentslotgroup = EquipmentSlotGroup.bySlot(pType.getSlot());
                    ResourceLocation resourcelocation = ResourceLocation.withDefaultNamespace("armor." + pType.getName());
                    builder.add(
                            Attributes.ARMOR, new AttributeModifier(resourcelocation, i, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup
                    );
                    builder.add(
                            Attributes.ARMOR_TOUGHNESS, new AttributeModifier(resourcelocation, f, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup
                    );
                    float f1 = pMaterial.value().knockbackResistance();
                    if (f1 > 0.0F) {
                        builder.add(
                                Attributes.KNOCKBACK_RESISTANCE,
                                new AttributeModifier(resourcelocation, f1, AttributeModifier.Operation.ADD_VALUE),
                                equipmentslotgroup
                        );
                    }
                    for (AttributeContainer holder : attributes) {
                        builder.add(holder.attribute(), holder.createModifier(pType.getSlot().getName()), equipmentslotgroup);
                    }
                    return builder.build();
                }
        );
    }

    public static AttributeContainer[] withMagickAttributes(int mana, double discount) {
        return new AttributeContainer[]{new AttributeContainer(ModAttributes.MANA_MAX, mana, AttributeModifier.Operation.ADD_VALUE),
                new AttributeContainer(ModAttributes.MANA_COST_MULTIPLIER, discount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)};
    }
}
