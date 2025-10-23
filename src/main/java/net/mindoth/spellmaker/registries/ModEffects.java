package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.mobeffect.ParalysisEffect;
import net.mindoth.spellmaker.mobeffect.PolymorphEffect;
import net.mindoth.spellmaker.mobeffect.SleepEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, SpellMaker.MOD_ID);

    public static final RegistryObject<ParalysisEffect> PARALYSIS = EFFECTS.register("paralysis", () -> new ParalysisEffect(MobEffectCategory.HARMFUL, 0));
    public static final RegistryObject<SleepEffect> SLEEP = EFFECTS.register("sleep", () -> new SleepEffect(MobEffectCategory.HARMFUL, 0));
    public static final RegistryObject<PolymorphEffect> POLYMORPH = EFFECTS.register("polymorph", () -> new PolymorphEffect(MobEffectCategory.HARMFUL, 0));
}
