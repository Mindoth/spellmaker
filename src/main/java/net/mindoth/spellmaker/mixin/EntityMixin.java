package net.mindoth.spellmaker.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityMixin {

    @Accessor("wasTouchingWater")
    boolean getWasTouchingWater();

    @Accessor("wasTouchingWater")
    void setWasTouchingWater(boolean wasTouchingWater);
}
