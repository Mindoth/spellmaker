package net.mindoth.spellmaker.item;

import net.mindoth.spellmaker.util.DimVec3;
import net.mindoth.spellmaker.util.MultiBlockHitResult;
import net.mindoth.spellmaker.util.MultiEntityHitResult;
import net.minecraft.world.item.Item;

import java.util.List;

public class RuneItem extends Item {
    public RuneItem(Properties pProperties) {
        super(pProperties);
    }

    public void effectOnEntity(List<Integer> stats, MultiEntityHitResult result) {
    }

    public void effectOnBlock(List<Integer> stats, MultiBlockHitResult result) {
    }

    public void effectOnPos(List<Integer> stats, DimVec3 pos) {
    }
}
