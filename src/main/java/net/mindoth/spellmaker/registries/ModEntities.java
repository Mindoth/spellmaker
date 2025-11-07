package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.entity.ProjectileSpellMultiEntity;
import net.mindoth.spellmaker.entity.ProjectileSpellSingleEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, SpellMaker.MOD_ID);

    public static ResourceKey<EntityType<?>> SPELL_PROJECTILE_SINGLE_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.withDefaultNamespace("spell_projectile_single"));
    public static final DeferredHolder<EntityType<?>, EntityType<ProjectileSpellSingleEntity>> SPELL_PROJECTILE_SINGLE =
            ENTITY_TYPES.register("spell_projectile_single", () -> EntityType.Builder.<ProjectileSpellSingleEntity>of(ProjectileSpellSingleEntity::new,
                    MobCategory.MISC).sized(0.5F, 0.5F).build(SPELL_PROJECTILE_SINGLE_KEY));

    public static ResourceKey<EntityType<?>> SPELL_PROJECTILE_MULTI_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.withDefaultNamespace("spell_projectile_multi"));
    public static final DeferredHolder<EntityType<?>, EntityType<ProjectileSpellMultiEntity>> SPELL_PROJECTILE_MULTI =
            ENTITY_TYPES.register("spell_projectile_multi", () -> EntityType.Builder.<ProjectileSpellMultiEntity>of(ProjectileSpellMultiEntity::new,
                    MobCategory.MISC).sized(0.5F, 0.5F).build(SPELL_PROJECTILE_MULTI_KEY));
}
