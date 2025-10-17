package net.mindoth.spellmaker.entity;

import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.registries.ModEntities;
import net.mindoth.spellmaker.util.DimVec3;
import net.mindoth.spellmaker.util.MultiBlockHitResult;
import net.mindoth.spellmaker.util.MultiEntityHitResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class ProjectileSpellSingleEntity extends AbstractSpellEntity {

    public ProjectileSpellSingleEntity(EntityType<ProjectileSpellSingleEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ProjectileSpellSingleEntity(Level level, Entity caster, LinkedHashMap<RuneItem, List<Integer>> map) {
        super(ModEntities.SPELL_PROJECTILE_SINGLE.get(), level, caster, map);
    }

    @Override
    protected void doMobEffects(EntityHitResult result) {
        if ( this.map != null ) {
            MultiEntityHitResult mResult = new MultiEntityHitResult(result.getEntity(), Collections.singletonList(result.getEntity()), new DimVec3(this.position(), level()));
            for ( RuneItem rune : this.map.keySet() ) rune.effectOnEntity(this.map.get(rune), mResult);
        }
    }

    @Override
    protected void doBlockEffects(BlockHitResult result) {
        if ( this.map != null ) {
            MultiBlockHitResult mResult = new MultiBlockHitResult(result.getDirection(), result.isInside(), Collections.singletonList(result.getBlockPos()), new DimVec3(result.getLocation(), level()));
            for ( RuneItem rune : this.map.keySet() ) rune.effectOnBlock(this.map.get(rune), mResult);
        }
    }
}
