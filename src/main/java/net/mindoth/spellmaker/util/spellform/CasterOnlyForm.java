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
    public CasterOnlyForm(float cost) {
        super(cost);
    }

    @Override
    public void castMagick(Entity source, Entity directSource, LinkedHashMap<SigilItem, List<Integer>> map) {
        MultiEntityHitResult mResult = new MultiEntityHitResult(source, Collections.singletonList(source), new DimVec3(source.position(), source.level()));
        for ( SigilItem sigil : map.keySet() ) sigil.effectOnEntity(source, directSource, map.get(sigil), mResult);
        LightEvents.addEnchantParticles(source, 0.15F, getColorStats(map));
    }
}
