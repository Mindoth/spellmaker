package net.mindoth.spellmaker.util;

import net.mindoth.shadowizardlib.event.LightEvents;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.RuneItem;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class SpellForm {

    private final String name;
    public String getName() {
        return this.name;
    }
    private final int cost;
    public int getCost() {
        return this.cost;
    }

    public SpellForm(String name, int cost) {
        this.name = name;
        this.cost = cost;
    }

    public void castMagick(Entity caster, LinkedHashMap<RuneItem, List<Integer>> map) {
    }

    protected HashMap<String, Float> getColorStats(LinkedHashMap<RuneItem, List<Integer>> map) {
        RuneItem rune = ParchmentItem.getHighestCostRune(map);
        if ( rune != null ) return makeSpellParticleStats(rune.getColor());
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
