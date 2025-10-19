package net.mindoth.spellmaker.entity;

import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.shadowizardlib.util.MultiBlockHitResult;
import net.mindoth.shadowizardlib.util.MultiEntityHitResult;
import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.registries.ModEntities;
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

    public ProjectileSpellSingleEntity(Level level) {
        super(ModEntities.SPELL_PROJECTILE_SINGLE.get(), level);
    }

    @Override
    protected void doMobEffects(EntityHitResult result) {
        MultiEntityHitResult mResult = new MultiEntityHitResult(this, Collections.singletonList(result.getEntity()), new DimVec3(this.position(), level()));
        for ( RuneItem rune : getMap().keySet() ) rune.effectOnEntity(getMap().get(rune), mResult);
    }

    @Override
    protected void doBlockEffects(BlockHitResult result) {
        MultiBlockHitResult mResult = new MultiBlockHitResult(result.getDirection(), result.isInside(), Collections.singletonList(result.getBlockPos()), new DimVec3(result.getLocation(), level()));
        for ( RuneItem rune : getMap().keySet() ) rune.effectOnBlock(getMap().get(rune), mResult);
    }
}
