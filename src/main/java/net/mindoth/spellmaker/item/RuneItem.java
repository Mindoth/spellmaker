package net.mindoth.spellmaker.item;

import net.mindoth.shadowizardlib.util.MultiBlockHitResult;
import net.mindoth.shadowizardlib.util.MultiEntityHitResult;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.world.item.Item;

import java.util.List;

public abstract class RuneItem extends Item {
    private final SpellColor color;
    public SpellColor getColor() {
        return this.color;
    }
    private final int maxMagnitude;
    public int getMaxMagnitude() {
        return this.maxMagnitude;
    }
    private final int maxDuration;
    public int getMaxDuration() {
        return this.maxDuration;
    }
    private final int cost;
    public int getCost() {
        return this.cost;
    }
    private final int magnitudeMultiplier;
    public int getMagnitudeMultiplier() {
        return this.magnitudeMultiplier;
    }
    private final int durationMultiplier;
    public int getDurationMultiplier() {
        return this.durationMultiplier;
    }

    public RuneItem(Properties pProperties, SpellColor color, int maxMagnitude, int maxDuration, int cost, int magnitudeMultiplier, int durationMultiplier) {
        super(pProperties);
        this.color = color;
        this.maxMagnitude = maxMagnitude;
        this.maxDuration = maxDuration;
        this.cost = cost;
        this.magnitudeMultiplier = magnitudeMultiplier;
        this.durationMultiplier = durationMultiplier;
    }

    public void effectOnEntity(List<Integer> stats, MultiEntityHitResult result) {
    }

    public void effectOnBlock(List<Integer> stats, MultiBlockHitResult result) {
    }
}
