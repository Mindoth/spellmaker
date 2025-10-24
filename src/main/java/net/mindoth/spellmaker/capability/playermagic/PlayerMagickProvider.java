package net.mindoth.spellmaker.capability.playermagic;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerMagickProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<PlayerMagick> PLAYER_MAGICK = CapabilityManager.get(new CapabilityToken<PlayerMagick>() {});

    private PlayerMagick magick = null;
    private final LazyOptional<PlayerMagick> optional = LazyOptional.of(this::createPlayerMagic);

    private PlayerMagick createPlayerMagic() {
        if ( this.magick == null ) this.magick = new PlayerMagick();
        return this.magick;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if ( cap == PLAYER_MAGICK) return optional.cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createPlayerMagic().saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        createPlayerMagic().loadNBTData(tag);
    }
}
