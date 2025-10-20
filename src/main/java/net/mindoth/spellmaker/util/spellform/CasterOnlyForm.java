package net.mindoth.spellmaker.util.spellform;

import net.mindoth.shadowizardlib.event.LightEvents;
import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.shadowizardlib.util.MultiEntityHitResult;
import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.util.SpellForm;
import net.minecraft.world.entity.Entity;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class CasterOnlyForm extends SpellForm {
    public CasterOnlyForm(int cost) {
        super(cost);
    }

    @Override
    public void castMagick(Entity caster, LinkedHashMap<RuneItem, List<Integer>> map) {
        for ( RuneItem rune : map.keySet() ) {
            rune.effectOnEntity(map.get(rune), new MultiEntityHitResult(caster, Collections.singletonList(caster), new DimVec3(caster.position(), caster.level())));
            LightEvents.addEnchantParticles(caster, 0.15F, getColorStats(map));
        }
    }
}
