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
    public SpellForm(String name) {
        this.name = name;
    }

    public void castMagick(Entity caster, LinkedHashMap<RuneItem, List<Integer>> map) {
    }
}
