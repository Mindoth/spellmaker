package net.mindoth.spellmaker.util;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DimVec3 {

    private final Vec3 pos;
    public Vec3 getPos() {
        return this.pos;
    }

    private final Level level;
    public Level getLevel() {
        return this.level;
    }

    public DimVec3(Vec3 pos, Level level) {
        this.pos = pos;
        this.level = level;
    }
}
