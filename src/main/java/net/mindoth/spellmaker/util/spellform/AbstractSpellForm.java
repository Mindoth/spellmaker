package net.mindoth.spellmaker.util.spellform;

import com.google.common.collect.Lists;
import net.mindoth.shadowizardlib.event.LightEvents;
import net.mindoth.spellmaker.item.sigil.AbstractSigilItem;
import net.mindoth.spellmaker.registries.ModSpellForms;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class AbstractSpellForm {

    public String getName() {
        return ModSpellForms.SPELL_FORM_REGISTRY.getKey(this).getPath();
    }
    private final float cost;
    public float getCost() {
        return this.cost;
    }

    public AbstractSpellForm(float cost) {
        this.cost = cost;
    }

    public void castMagick(Entity source, Entity directSource, LinkedHashMap<AbstractSigilItem, List<Integer>> map) {
    }

    public static AbstractSigilItem getHighestCostSigil(LinkedHashMap<AbstractSigilItem, List<Integer>> map) {
        AbstractSigilItem state = null;
        int highestCost = 0;
        List<Integer> equals = Lists.newArrayList();
        for ( AbstractSigilItem sigil : map.keySet() ) {
            int cost = sigil.getCost();
            List<Integer> stats = map.get(sigil);
            if ( sigil.canModifyMagnitude() ) cost += Math.abs(stats.get(0)) * sigil.getMagnitudeMultiplier();
            if ( sigil.canModifyDuration() ) cost += Math.abs(stats.get(1)) * sigil.getDurationMultiplier();
            if ( cost > highestCost ) {
                highestCost = cost;
                state = sigil;
            }
            else if ( cost == highestCost ) equals.add(cost);
        }
        int highestEqualCost = 0;
        for ( int cost : equals ) if ( cost > highestEqualCost ) highestEqualCost = cost;
        if ( highestEqualCost >= highestCost ) return null;
        return state;
    }

    public HashMap<String, Float> getColorStats(LinkedHashMap<AbstractSigilItem, List<Integer>> map) {
        AbstractSigilItem sigil = getHighestCostSigil(map);
        if ( sigil != null ) return makeSpellParticleStats(sigil.getColor());
        else return LightEvents.defaultStats();
    }

    private static HashMap<String, Float> makeSpellParticleStats(SpellColor color) {
        HashMap<String, Float> stats = new HashMap<>();
        stats.put("red", (float)color.r);
        stats.put("green", (float)color.g);
        stats.put("blue", (float)color.b);
        stats.put("type", (float)color.type);
        return stats;
    }
}
