package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = SpellMaker.MOD_ID)
public class ModAttributes {

    public static class MagickAttribute extends RangedAttribute {
        public MagickAttribute(String pDescriptionId, double pDefaultValue, double pMin, double pMax) {
            super(pDescriptionId, pDefaultValue, pMin, pMax);
        }
    }

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, SpellMaker.MOD_ID);

    public static final DeferredHolder<Attribute, Attribute> MANA_REGENERATION = ATTRIBUTES.register("mana_regeneration",
            () -> (new MagickAttribute("attribute.spellmaker.mana_regeneration", 1.0D, 0.0D, Integer.MAX_VALUE).setSyncable(true)));

    public static final DeferredHolder<Attribute, Attribute> MANA_MAX = ATTRIBUTES.register("mana_max",
            () -> (new MagickAttribute("attribute.spellmaker.mana_max", 100.0D, 0.0D, Integer.MAX_VALUE).setSyncable(true)));

    public static final DeferredHolder<Attribute, Attribute> MANA_COST_MULTIPLIER = ATTRIBUTES.register("mana_cost_multiplier",
            () -> (new MagickAttribute("attribute.spellmaker.mana_cost_multiplier", 1.0D, 1.0D, Integer.MAX_VALUE).setSyncable(true)));

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(entity -> ATTRIBUTES.getEntries().forEach(attribute -> event.add(entity, attribute)));
    }
}
