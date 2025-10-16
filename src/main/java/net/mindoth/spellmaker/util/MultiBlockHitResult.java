package net.mindoth.spellmaker.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class MultiBlockHitResult extends BlockHitResult {

    private final List<BlockPos> blocks;
    public List<BlockPos> getBlocks() {
        return this.blocks;
    }

    private final DimVec3 dimVec3;
    public DimVec3 getPos() {
        return this.dimVec3;
    }

    public MultiBlockHitResult(Direction pDirection, boolean pInside, List<BlockPos> blocks, DimVec3 dimVec3) {
        super(dimVec3.getPos(), pDirection, new BlockPos(Mth.floor(dimVec3.getPos().x), Mth.floor(dimVec3.getPos().y), Mth.floor(dimVec3.getPos().z)), pInside);
        this.blocks = blocks;
        this.dimVec3 = dimVec3;
    }
}
