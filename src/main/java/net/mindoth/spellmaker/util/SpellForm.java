package net.mindoth.spellmaker.util;

import net.mindoth.spellmaker.item.RuneItem;
import net.minecraft.world.entity.Entity;

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
}
