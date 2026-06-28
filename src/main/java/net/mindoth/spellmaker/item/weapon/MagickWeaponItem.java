package net.mindoth.spellmaker.item.weapon;

import net.mindoth.spellmaker.item.armor.AttributeContainer;
import net.mindoth.spellmaker.registries.ModAttributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class MagickWeaponItem extends Item {

    public MagickWeaponItem(Properties properties, float damage, float speed, AttributeContainer... extraAttributes) {
        super(properties.attributes(magickWeapon(damage, speed, extraAttributes)));
    }

    public static ItemAttributeModifiers magickWeapon(float damage, float speed, AttributeContainer... extraAttributes) {
        ItemAttributeModifiers.Builder builder;
        if ( damage != 0 || speed != 0 ) {
            builder = ItemAttributeModifiers.builder()
                    .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, damage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                    .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, speed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        }
        else builder = ItemAttributeModifiers.builder();
        for ( AttributeContainer holder : extraAttributes ) {
            builder.add(holder.attribute(), holder.createModifier(EquipmentSlot.MAINHAND.getName()), EquipmentSlotGroup.MAINHAND);
        }

        return builder.build();
    }

    public static AttributeContainer[] withMagickAttributes(int mana, double discount) {
        return new AttributeContainer[] { new AttributeContainer(ModAttributes.MANA_MAX, mana, AttributeModifier.Operation.ADD_VALUE),
                new AttributeContainer(ModAttributes.MANA_COST_MULTIPLIER, discount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE) };
    }
}
