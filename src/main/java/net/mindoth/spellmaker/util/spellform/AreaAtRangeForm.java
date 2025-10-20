package net.mindoth.spellmaker.util.spellform;

import net.mindoth.spellmaker.entity.AbstractSpellEntity;
import net.mindoth.spellmaker.entity.ProjectileSpellMultiEntity;
import net.mindoth.spellmaker.entity.ProjectileSpellSingleEntity;
import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.util.DataHelper;
import net.mindoth.spellmaker.util.SpellForm;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.apache.commons.compress.utils.Lists;

import java.util.LinkedHashMap;
import java.util.List;

public class AreaAtRangeForm extends SpellForm {
    public AreaAtRangeForm(int cost) {
        super(cost);
    }

    @Override
    public void castMagick(Entity caster, LinkedHashMap<RuneItem, List<Integer>> map) {
        Level level = caster.level();
        ProjectileSpellMultiEntity projectile = new ProjectileSpellMultiEntity(level);
        projectile.getEntityData().set(AbstractSpellEntity.RED, (int)Math.floor(getColorStats(map).get("red")));
        projectile.getEntityData().set(AbstractSpellEntity.GREEN, (int)Math.floor(getColorStats(map).get("green")));
        projectile.getEntityData().set(AbstractSpellEntity.BLUE, (int)Math.floor(getColorStats(map).get("blue")));
        projectile.getEntityData().set(AbstractSpellEntity.TYPE, (int)Math.floor(getColorStats(map).get("type")));
        projectile.getEntityData().set(AbstractSpellEntity.RUNE_LIST, DataHelper.getStringFromRuneList(map.keySet().stream().toList()));
        List<Integer> magnitudes = Lists.newArrayList();
        List<Integer> durations = Lists.newArrayList();
        for ( RuneItem rune : map.keySet() ) {
            magnitudes.add(map.get(rune).get(0));
            durations.add(map.get(rune).get(1));
        }
        projectile.getEntityData().set(AbstractSpellEntity.MAGNITUDES, DataHelper.getStringFromStats(magnitudes));
        projectile.getEntityData().set(AbstractSpellEntity.DURATIONS, DataHelper.getStringFromStats(durations));
        projectile.setPos(caster.getEyePosition());
        projectile.shoot(caster.getLookAngle().x, caster.getLookAngle().y, caster.getLookAngle().z, projectile.getSpeed(), 0.0F);
        level.addFreshEntity(projectile);
    }
}
