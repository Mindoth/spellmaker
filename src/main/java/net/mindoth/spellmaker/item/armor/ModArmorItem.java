package net.mindoth.spellmaker.item.armor;

import com.google.common.base.Suppliers;
import net.mindoth.spellmaker.SpellMakerClient;
import net.mindoth.spellmaker.client.model.SimpleRobeModel;
import net.mindoth.spellmaker.registries.ModAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModArmorItem extends Item {
    public ModArmorItem(Properties properties) {
        super(properties);
    }

    /*private final Supplier<ItemAttributeModifiers> defaultModifiers;

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        return this.defaultModifiers.get();
    }

    public static AttributeContainer[] withMagickAttributes(int mana, double discount) {
        return new AttributeContainer[] { new AttributeContainer(ModAttributes.MANA_MAX, mana, AttributeModifier.Operation.ADD_VALUE),
                new AttributeContainer(ModAttributes.MANA_COST_MULTIPLIER, discount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE) };
    }

    public ModArmorItem(Holder<ArmorMaterial> pMaterial, ArmorType pType, Properties pProperties, AttributeContainer... attributes) {
        super(pProperties);
        this.defaultModifiers = Suppliers.memoize(
                () -> {
                    int i = pMaterial.value().defense().get(pType);
                    float f = pMaterial.value().toughness();
                    ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
                    EquipmentSlotGroup equipmentslotgroup = EquipmentSlotGroup.bySlot(pType.getSlot());
                    ResourceLocation resourcelocation = ResourceLocation.withDefaultNamespace("armor." + pType.getName());
                    builder.add(Attributes.ARMOR, new AttributeModifier(resourcelocation, i, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup);
                    builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(resourcelocation, f, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup);
                    float f1 = pMaterial.value().knockbackResistance();
                    if ( f1 > 0.0F ) {
                        builder.add(
                                Attributes.KNOCKBACK_RESISTANCE,
                                new AttributeModifier(resourcelocation, f1, AttributeModifier.Operation.ADD_VALUE),
                                equipmentslotgroup
                        );
                    }
                    for ( AttributeContainer holder : attributes ) builder.add(holder.attribute(), holder.createModifier(pType.getSlot().getName()), equipmentslotgroup);
                    return builder.build();
                }
        );
        //this.model = FMLLoader.getCurrent().getDist() == Dist.CLIENT ? new LazyLoadedValue<>(this::provideArmorModelForSlot) : null;
    }

    //TODO: custom armor model
    private final LazyLoadedValue<HumanoidModel<?>> model;

    @OnlyIn(Dist.CLIENT)
    public HumanoidModel<?> provideArmorModelForSlot() {
        return new SimpleRobeModel(Minecraft.getInstance().getEntityModels().bakeLayer(SpellMakerClient.SIMPLE_ROBE));
    }

    @SuppressWarnings("removal")
    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
                return model != null ? model.get() : _default;
            }
            @Override
            public int getArmorLayerTintColor(ItemStack stack, LivingEntity entity, ArmorMaterial.Layer layer, int layerIdx, int fallbackColor) {
                if ( stack.getItem() instanceof ColorableArmorItem armorItem ) return DyedItemColor.getOrDefault(stack, armorItem.getDefaultColor());
                return IClientItemExtensions.super.getArmorLayerTintColor(stack, entity, layer, layerIdx, fallbackColor);
            }
        });
    }*/
}
