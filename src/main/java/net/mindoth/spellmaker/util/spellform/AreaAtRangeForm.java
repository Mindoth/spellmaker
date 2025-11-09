package net.mindoth.spellmaker.util.spellform;

import net.mindoth.spellmaker.entity.AbstractSpellEntity;
import net.mindoth.spellmaker.entity.ProjectileSpellMultiEntity;
import net.mindoth.spellmaker.item.sigil.AbstractSigilItem;
import net.mindoth.spellmaker.util.DataHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.apache.commons.compress.utils.Lists;

import java.util.LinkedHashMap;
import java.util.List;

public class AreaAtRangeForm extends AbstractSpellForm {
    public AreaAtRangeForm(float cost) {
        super(cost);
    }

    @Override
    public void castMagick(Entity source, Entity directSource, LinkedHashMap<AbstractSigilItem, List<Integer>> map) {
        Level level = source.level();
        ProjectileSpellMultiEntity projectile = new ProjectileSpellMultiEntity(level);
        projectile.setOwner(source);
        projectile.getEntityData().set(AbstractSpellEntity.RED, (int)Math.floor(getColorStats(map).get("red")));
        projectile.getEntityData().set(AbstractSpellEntity.GREEN, (int)Math.floor(getColorStats(map).get("green")));
        projectile.getEntityData().set(AbstractSpellEntity.BLUE, (int)Math.floor(getColorStats(map).get("blue")));
        projectile.getEntityData().set(AbstractSpellEntity.TYPE, (int)Math.floor(getColorStats(map).get("type")));
        projectile.getEntityData().set(AbstractSpellEntity.SIGIL_LIST, DataHelper.getStringFromSigilList(map.keySet().stream().toList()));
        List<Integer> magnitudes = Lists.newArrayList();
        List<Integer> durations = Lists.newArrayList();
        for ( AbstractSigilItem sigil : map.keySet() ) {
            magnitudes.add(map.get(sigil).get(0));
            durations.add(map.get(sigil).get(1));
        }
        projectile.getEntityData().set(AbstractSpellEntity.MAGNITUDES, DataHelper.getStringFromStats(magnitudes));
        projectile.getEntityData().set(AbstractSpellEntity.DURATIONS, DataHelper.getStringFromStats(durations));
        projectile.setPos(source.getEyePosition());
        projectile.shoot(source.getLookAngle().x, source.getLookAngle().y, source.getLookAngle().z, projectile.getSpeed(), 0.0F);
        level.addFreshEntity(projectile);
    }
}
