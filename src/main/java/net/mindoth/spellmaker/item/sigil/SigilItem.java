package net.mindoth.spellmaker.item.sigil;

import net.mindoth.shadowizardlib.util.DimVec3;
import net.mindoth.shadowizardlib.util.MultiBlockHitResult;
import net.mindoth.shadowizardlib.util.MultiEntityHitResult;
import net.mindoth.spellmaker.util.SpellColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;

public abstract class SigilItem extends Item {
    private final SpellColor color;
    public SpellColor getColor() {
        return this.color;
    }
    private final int cost;
    public int getCost() {
        return this.cost;
    }
    private final int maxMagnitude;
    public int getMaxMagnitude() {
        return this.maxMagnitude;
    }
    private final int magnitudeMultiplier;
    public int getMagnitudeMultiplier() {
        return this.magnitudeMultiplier;
    }
    private final int maxDuration;
    public int getMaxDuration() {
        return this.maxDuration;
    }
    private final int durationMultiplier;
    public int getDurationMultiplier() {
        return this.durationMultiplier;
    }

    public SigilItem(Properties pProperties, SpellColor color, int cost, int maxMagnitude, int magnitudeMultiplier, int maxDuration, int durationMultiplier) {
        super(pProperties);
        this.color = color;
        this.cost = cost;
        this.maxMagnitude = maxMagnitude;
        this.magnitudeMultiplier = magnitudeMultiplier;
        this.maxDuration = maxDuration;
        this.durationMultiplier = durationMultiplier;
    }

    public void effectOnEntity(Entity source, Entity directSource, List<Integer> stats, MultiEntityHitResult result) {
        for ( Entity entity : result.getEntities() ) effectOnAllEntitiesInList(source, directSource, entity, stats, result.getPos());
    }

    public void effectOnAllEntitiesInList(Entity source, Entity directSource, Entity target, List<Integer> stats, DimVec3 location) {
    }

    public void effectOnBlock(Entity source, Entity directSource, List<Integer> stats, MultiBlockHitResult result) {
        for ( BlockPos block : result.getBlocks() ) effectOnAllBlocksInList(source, directSource, block, stats, result.getPos(), result.getDirection(), result.isInside());
    }

    public void effectOnAllBlocksInList(Entity source, Entity directSource, BlockPos target, List<Integer> stats, DimVec3 location, Direction direction, boolean isInside) {
    }

    protected DamageSource getSource(ResourceKey<DamageType> key, Entity source, Entity directSource) {
        return new DamageSource(getDataDrivenRegistry(Registries.DAMAGE_TYPE).getHolderOrThrow(key), directSource, source);
    }

    public static <T> Registry<T> getDataDrivenRegistry(ResourceKey<? extends Registry<T>> key) {
        return ServerLifecycleHooks.getCurrentServer().registryAccess().registry(key).get();
    }
}
