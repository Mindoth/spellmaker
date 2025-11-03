package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.entity.ProjectileSpellMultiEntity;
import net.mindoth.spellmaker.entity.ProjectileSpellSingleEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, SpellMaker.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ProjectileSpellSingleEntity>> SPELL_PROJECTILE_SINGLE
            = registerEntity(EntityType.Builder.<ProjectileSpellSingleEntity>of(ProjectileSpellSingleEntity::new,
            MobCategory.MISC).sized(0.5F, 0.5F), "spell_projectile_single");

    public static final DeferredHolder<EntityType<?>, EntityType<ProjectileSpellMultiEntity>> SPELL_PROJECTILE_MULTI
            = registerEntity(EntityType.Builder.<ProjectileSpellMultiEntity>of(ProjectileSpellMultiEntity::new,
            MobCategory.MISC).sized(0.5F, 0.5F), "spell_projectile_multi");


    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(EntityType.Builder<T> builder, String entityName) {
        return ENTITIES.register(entityName, () -> builder.build(entityName));
    }
}
