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
    public RuneItem(Properties pProperties, boolean hasMagnitude, boolean hasDuration) {
        super(pProperties);
        this.hasMagnitude = hasMagnitude;
        this.hasDuration = hasDuration;
    }

    public void effectOnEntity(List<Integer> stats, MultiEntityHitResult result) {
    }

    public void effectOnBlock(List<Integer> stats, MultiBlockHitResult result) {
    }
}
