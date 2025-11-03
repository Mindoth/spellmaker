package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.util.spellform.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ModSpellForms {
    public static final ResourceKey<Registry<AbstractSpellForm>> SPELL_FORM_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "spell_forms"));
    public static final DeferredRegister<AbstractSpellForm> SPELL_FORMS = DeferredRegister.create(SPELL_FORM_REGISTRY_KEY, SpellMaker.MOD_ID);

    public static final DeferredHolder<AbstractSpellForm, CasterOnlyForm> CASTER_ONLY = SPELL_FORMS.register("caster_only",
            () -> new CasterOnlyForm(1));

    public static final DeferredHolder<AbstractSpellForm, ByTouchForm> BY_TOUCH = SPELL_FORMS.register("by_touch",
            () -> new ByTouchForm(1));

    public static final DeferredHolder<AbstractSpellForm, SingleTargetAtRangeForm> SINGLE_TARGET_AT_RANGE = SPELL_FORMS.register("single_target_at_range",
            () -> new SingleTargetAtRangeForm(1));

    public static final DeferredHolder<AbstractSpellForm, AreaAroundCasterForm> AREA_AROUND_CASTER = SPELL_FORMS.register("area_around_caster",
            () -> new AreaAroundCasterForm(1.5F));

    public static final DeferredHolder<AbstractSpellForm, AreaAtRangeForm> AREA_AT_RANGE = SPELL_FORMS.register("area_at_range",
            () -> new AreaAtRangeForm(2.0F));

    //public static final Supplier<IForgeRegistry<AbstractSpellForm>> SPELL_FORM_REGISTRY = SPELL_FORMS.makeRegistry(() -> new RegistryBuilder<AbstractSpellForm>().disableSaving().disableOverrides());
    public static final Registry<AbstractSpellForm> SPELL_FORM_REGISTRY = new RegistryBuilder<>(SPELL_FORM_REGISTRY_KEY).create();
}
