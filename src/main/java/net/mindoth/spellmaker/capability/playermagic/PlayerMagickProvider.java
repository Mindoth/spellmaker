package net.mindoth.spellmaker.capability.playermagic;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

public class PlayerMagickProvider implements IAttachmentSerializer<MagickData> {

    private MagickData magick = null;
    private MagickData createPlayerMagic() {
        if ( this.magick == null ) this.magick = new MagickData();
        return this.magick;
    }

    @Override
    public MagickData read(IAttachmentHolder iAttachmentHolder, ValueInput valueInput) {
        var data = createPlayerMagic();
        data.loadNBTData(valueInput);
        return data;
    }

    @Override
    public boolean write(MagickData magickData, ValueOutput valueOutput) {
        createPlayerMagic().saveNBTData(valueOutput);
        return true;
    }
}
