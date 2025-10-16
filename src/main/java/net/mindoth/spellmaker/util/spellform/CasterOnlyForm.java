package net.mindoth.spellmaker.util.spellform;

import net.mindoth.spellmaker.item.RuneItem;
import net.mindoth.spellmaker.util.DimVec3;
import net.mindoth.spellmaker.util.MultiEntityHitResult;
import net.mindoth.spellmaker.util.SpellForm;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;

import java.util.*;

public class CasterOnlyForm extends SpellForm {
    public CasterOnlyForm(String name) {
        super(name);
    }

    @Override
    public void castMagick(Entity caster, LinkedHashMap<RuneItem, List<Integer>> map) {
        for ( RuneItem rune : map.keySet() ) {
            rune.effectOnEntity(map.get(rune), new MultiEntityHitResult(caster, Collections.singletonList(caster), new DimVec3(caster.position(), caster.level())));
        }
    }
}
