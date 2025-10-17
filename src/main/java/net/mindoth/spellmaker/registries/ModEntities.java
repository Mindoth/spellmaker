package net.mindoth.spellmaker.registries;

import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.entity.ProjectileSpellMultiEntity;
import net.mindoth.spellmaker.entity.ProjectileSpellSingleEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SpellMaker.MOD_ID);

    public static final RegistryObject<EntityType<ProjectileSpellSingleEntity>> SPELL_PROJECTILE_SINGLE
            = registerEntity(EntityType.Builder.<ProjectileSpellSingleEntity>of(ProjectileSpellSingleEntity::new,
            MobCategory.MISC).sized(0.5F, 0.5F), "spell_projectile_single");

    public static final RegistryObject<EntityType<ProjectileSpellMultiEntity>> SPELL_PROJECTILE_MULTI
            = registerEntity(EntityType.Builder.<ProjectileSpellMultiEntity>of(ProjectileSpellMultiEntity::new,
            MobCategory.MISC).sized(0.5F, 0.5F), "spell_projectile_multi");


    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(EntityType.Builder<T> builder, String entityName) {
        return ENTITIES.register(entityName, () -> builder.build(entityName));
    }
}
