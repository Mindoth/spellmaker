package net.mindoth.spellmaker.util.spellform;

import net.mindoth.shadowizardlib.event.LightEvents;
import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.shadowizardlib.util.MultiEntityHitResult;
import net.mindoth.spellmaker.item.sigil.SigilItem;
import net.minecraft.world.entity.Entity;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class CasterOnlyForm extends AbstractSpellForm {
    public CasterOnlyForm(int cost) {
        super(cost);
    }

    @Override
    public void castMagick(Entity caster, LinkedHashMap<SigilItem, List<Integer>> map) {
        MultiEntityHitResult mResult = new MultiEntityHitResult(caster, Collections.singletonList(caster), new DimVec3(caster.position(), caster.level()));
        for ( SigilItem sigil : map.keySet() ) sigil.effectOnEntity(map.get(sigil), mResult);
        LightEvents.addEnchantParticles(caster, 0.15F, getColorStats(map));
    }
}
