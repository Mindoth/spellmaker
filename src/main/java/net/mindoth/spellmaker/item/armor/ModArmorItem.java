package net.mindoth.spellmaker.item.armor;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.capability.ModCapabilities;
import net.mindoth.spellmaker.item.weapon.StaffItem;
import net.mindoth.spellmaker.registries.ModAttributes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import javax.annotation.Nullable;
import java.util.List;

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

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        if ( slot == null || entity.tickCount % 20 != 0 || !(entity instanceof Player player) ) return;
        if ( !player.getAttributes().hasAttribute(ModAttributes.MANA_MAX) || !player.getAttributes().hasAttribute(ModAttributes.MANA_REGENERATION) ) return;
        if ( player.getData(ModCapabilities.MAGICK_DATA) < player.getAttribute(ModAttributes.MANA_MAX).getValue() ) return;
        List<EquipmentSlot> list = getSlotList(player);
        if ( list.isEmpty() ) return;
        int index = player.getRandom().nextInt(0, list.size());
        if ( list.get(index) != slot ) return;
        if ( !(stack.getItem() instanceof ModArmorItem) || !slot.isArmor() ) return;
        stack.hurtAndBreak(-(int)player.getAttribute(ModAttributes.MANA_REGENERATION).getValue(), level, player,
                (holder) -> player.onEquippedItemBroken(stack.getItem(), slot));
    }

    private List<EquipmentSlot> getSlotList(Player player) {
        List<EquipmentSlot> list = Lists.newArrayList();
        if ( player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ModArmorItem && player.getItemBySlot(EquipmentSlot.HEAD).isDamaged() ) list.add(EquipmentSlot.HEAD);
        if ( player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ModArmorItem && player.getItemBySlot(EquipmentSlot.CHEST).isDamaged() ) list.add(EquipmentSlot.CHEST);
        if ( player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ModArmorItem && player.getItemBySlot(EquipmentSlot.LEGS).isDamaged() ) list.add(EquipmentSlot.LEGS);
        if ( player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof ModArmorItem && player.getItemBySlot(EquipmentSlot.FEET).isDamaged() ) list.add(EquipmentSlot.FEET);
        if ( player.getItemBySlot(EquipmentSlot.MAINHAND).getItem() instanceof StaffItem && player.getItemBySlot(EquipmentSlot.MAINHAND).isDamaged() ) list.add(EquipmentSlot.MAINHAND);
        if ( player.getItemBySlot(EquipmentSlot.OFFHAND).getItem() instanceof StaffItem && player.getItemBySlot(EquipmentSlot.OFFHAND).isDamaged() ) list.add(EquipmentSlot.OFFHAND);
        return list;
    }
}
