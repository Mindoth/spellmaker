package net.mindoth.spellmaker.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;

import java.util.List;

public class MultiEntityHitResult extends EntityHitResult {

    private final List<Entity> entities;
    public List<Entity> getEntities() {
        return this.entities;
    }

    private final DimVec3 dimVec3;
    public DimVec3 getPos() {
        return this.dimVec3;
    }

    public MultiEntityHitResult(Entity pEntity, List<Entity> entities, DimVec3 dimVec3) {
        super(pEntity, dimVec3.getPos());
        this.entities = entities;
        this.dimVec3 = dimVec3;
    }
}
