package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public abstract class ModSpellForms {

    public static class SpellForm {
        private final String name;
        public String getName() {
            return this.name;
        }

        public SpellForm(String name) {
            this.name = name;
        }
    }

    public static final ResourceKey<Registry<SpellForm>> SPELL_FORM_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(SpellMaker.MOD_ID, "spell_forms"));
    public static final DeferredRegister<SpellForm> SPELL_FORMS = DeferredRegister.create(SPELL_FORM_REGISTRY_KEY, SpellMaker.MOD_ID);
    public static final Supplier<IForgeRegistry<SpellForm>> SPELL_FORM_REGISTRY = SPELL_FORMS.makeRegistry(() -> new RegistryBuilder<SpellForm>().disableSaving().disableOverrides());

    public static final RegistryObject<SpellForm> CASTER_ONLY = registerSpellForm(new SpellForm("caster_only"));
    public static final RegistryObject<SpellForm> BY_TOUCH = registerSpellForm(new SpellForm("by_touch"));
    public static final RegistryObject<SpellForm> SINGLE_TARGET_AT_RANGE = registerSpellForm(new SpellForm("single_target_at_range"));
    public static final RegistryObject<SpellForm> AREA_AROUND_CASTER = registerSpellForm(new SpellForm("area_around_caster"));
    public static final RegistryObject<SpellForm> AREA_AT_RANGE = registerSpellForm(new SpellForm("area_at_range"));

    private static RegistryObject<SpellForm> registerSpellForm(SpellForm form) {
        return SPELL_FORMS.register(form.getName(), () -> form);
    }
}
