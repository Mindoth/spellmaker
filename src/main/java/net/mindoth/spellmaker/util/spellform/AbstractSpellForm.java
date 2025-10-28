package net.mindoth.spellmaker.util.spellform;

import net.mindoth.shadowizardlib.event.LightEvents;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.sigil.SigilItem;
import net.mindoth.spellmaker.registries.ModSpellForms;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class AbstractSpellForm {

    public String getName() {
        return ModSpellForms.SPELL_FORM_REGISTRY.get().getKey(this).getPath();
    }
    private final int cost;
    public int getCost() {
        return this.cost;
    }

    public AbstractSpellForm(int cost) {
        this.cost = cost;
    }

    public void castMagick(Entity caster, LinkedHashMap<SigilItem, List<Integer>> map) {
    }

    protected HashMap<String, Float> getColorStats(LinkedHashMap<SigilItem, List<Integer>> map) {
        SigilItem sigil = ParchmentItem.getHighestCostSigil(map);
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
