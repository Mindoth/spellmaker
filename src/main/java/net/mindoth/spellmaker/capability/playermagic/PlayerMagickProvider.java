package net.mindoth.spellmaker.capability.playermagic;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

public class PlayerMagickProvider implements IAttachmentSerializer<CompoundTag, MagickData> {

    @Override
    public MagickData read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
        var data = createPlayerMagic();
        data.loadNBTData(tag);
        return data;
    }

    @Override
    public @Nullable CompoundTag write(MagickData magickData, HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        createPlayerMagic().saveNBTData(tag);
        return tag;
    }

    private MagickData magick = null;
    private MagickData createPlayerMagic() {
        if ( this.magick == null ) this.magick = new MagickData();
        return this.magick;
    }
}
