package net.mindoth.spellmaker.registries;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class ModKeys {

    public static final String KEY_OPEN_SPELL_BOOK = "key.spellmaker.open_spell_book";
    public static final String KEY_CATEGORY_SPELLMAKER = "key.category.spellmaker";
    public static final KeyMapping OPEN_SPELL_BOOK = new KeyMapping(KEY_OPEN_SPELL_BOOK, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B, KEY_CATEGORY_SPELLMAKER);
}
