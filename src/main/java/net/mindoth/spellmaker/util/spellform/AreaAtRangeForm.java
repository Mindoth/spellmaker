package net.mindoth.spellmaker.util.spellform;

import net.mindoth.spellmaker.entity.ProjectileSpellMultiEntity;
import net.mindoth.spellmaker.entity.ProjectileSpellSingleEntity;
import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.util.SpellForm;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.LinkedHashMap;
import java.util.List;

public class AreaAtRangeForm extends SpellForm {
    public AreaAtRangeForm(String name, int cost) {
        super(name, cost);
    }

    @Override
    public void castMagick(Entity caster, LinkedHashMap<RuneItem, List<Integer>> map) {
        Level level = caster.level();
        ProjectileSpellMultiEntity projectile = new ProjectileSpellMultiEntity(level, caster, map);
        projectile.setPos(caster.getEyePosition());
        projectile.shoot(caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z, projectile.getSpeed(), 0.0F);
        level.addFreshEntity(projectile);
    }
}
