package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.util.SpellForm;
import net.mindoth.spellmaker.util.spellform.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModSpellForms {
    public static final ResourceKey<Registry<SpellForm>> SPELL_FORM_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(SpellMaker.MOD_ID, "spell_forms"));
    public static final DeferredRegister<SpellForm> SPELL_FORMS = DeferredRegister.create(SPELL_FORM_REGISTRY_KEY, SpellMaker.MOD_ID);

    public static final RegistryObject<SpellForm> CASTER_ONLY = SPELL_FORMS.register("caster_only",
            () -> new CasterOnlyForm(0));

    public static final RegistryObject<SpellForm> BY_TOUCH = SPELL_FORMS.register("by_touch",
            () -> new ByTouchForm(1));

    public static final RegistryObject<SpellForm> SINGLE_TARGET_AT_RANGE = SPELL_FORMS.register("single_target_at_range",
            () -> new SingleTargetAtRangeForm(1));

    public static final RegistryObject<SpellForm> AREA_AROUND_CASTER = SPELL_FORMS.register("area_around_caster",
            () -> new AreaAroundCasterForm(2));

    public static final RegistryObject<SpellForm> AREA_AT_RANGE = SPELL_FORMS.register("area_at_range",
            () -> new AreaAtRangeForm(4));

    public static final Supplier<IForgeRegistry<SpellForm>> SPELL_FORM_REGISTRY = SPELL_FORMS.makeRegistry(() -> new RegistryBuilder<SpellForm>().disableSaving().disableOverrides());
}
