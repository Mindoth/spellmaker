package net.mindoth.spellmaker.item;

import net.mindoth.spellmaker.util.MultiBlockHitResult;
import net.mindoth.spellmaker.util.MultiEntityHitResult;
import net.minecraft.world.item.Item;

import java.util.List;

public abstract class RuneItem extends Item {
    private final boolean hasMagnitude;
    public boolean getHasMagnitude() {
        return this.hasMagnitude;
    }
    private final boolean hasDuration;
    public boolean getHasDuration() {
        return this.hasDuration;
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

    public RuneItem(Properties pProperties, boolean hasMagnitude, boolean hasDuration, int cost, int magnitudeMultiplier, int durationMultiplier) {
        super(pProperties);
        this.hasMagnitude = hasMagnitude;
        this.hasDuration = hasDuration;
        this.cost = cost;
        this.magnitudeMultiplier = magnitudeMultiplier;
        this.durationMultiplier = durationMultiplier;
    }

    public void effectOnEntity(List<Integer> stats, MultiEntityHitResult result) {
    }

    public void effectOnBlock(List<Integer> stats, MultiBlockHitResult result) {
    }
}
