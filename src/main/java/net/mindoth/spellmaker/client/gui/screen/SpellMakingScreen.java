package net.mindoth.spellmaker.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.client.gui.menu.RuneSlot;
import net.mindoth.spellmaker.client.gui.menu.SpellMakingMenu;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.registries.ModSpellForms;
import net.mindoth.spellmaker.util.CastingValidator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SpellMakingScreen extends AbstractContainerScreen<SpellMakingMenu> implements ContainerListener {

    private static final ResourceLocation TEXTURE = new ResourceLocation(SpellMaker.MOD_ID, "textures/gui/spell_making_screen.png");
    private EditBox name;
    private Button craftButton;
    private final int CRAFT_BUTTON_OFFSET_X = 152;
    private final int CRAFT_BUTTON_OFFSET_Y = 18;

    private final int SPELL_FORM_BUTTON_OFFSET_Y = 46;
    private Button leftSpellFormButton;
    private final int LEFT_SPELL_FORM_BUTTON_OFFSET_X = 26;
    private Button rightSpellFormButton;
    private final int RIGHT_SPELL_FORM_BUTTON_OFFSET_X = LEFT_SPELL_FORM_BUTTON_OFFSET_X + 27;

    private final int maxSlots = 3;

    private List<Button> magnitudeListLeft = Lists.newArrayList();
    private List<Button> magnitudeListRight = Lists.newArrayList();
    private final int MAGNITUDE_BUTTON_OFFSET_Y = 64;
    private final int LEFT_MAGNITUDE_BUTTON_OFFSET_X = 71;
    private final int RIGHT_MAGNITUDE_BUTTON_OFFSET_X = LEFT_MAGNITUDE_BUTTON_OFFSET_X + 27;

    private List<Button> durationListLeft = Lists.newArrayList();
    private List<Button> durationListRight = Lists.newArrayList();
    private final int DURATION_BUTTON_OFFSET_Y = 64;
    private final int LEFT_DURATION_BUTTON_OFFSET_X = LEFT_MAGNITUDE_BUTTON_OFFSET_X + 54;
    private final int RIGHT_DURATION_BUTTON_OFFSET_X = LEFT_DURATION_BUTTON_OFFSET_X + 27;

    public SpellMakingScreen(SpellMakingMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageHeight += 45;
        inventoryLabelY += 45;
    }

    @Override
    protected void init() {
        super.init();
        subInit();
        this.menu.addSlotListener(this);
    }

    protected void subInit() {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.name = new EditBox(this.font, x + 8, y + 20, 138, 12, Component.translatable("container.spellmaker.name"));
        this.name.setCanLoseFocus(false);
        this.name.setTextColor(-1);
        this.name.setTextColorUneditable(-1);
        this.name.setBordered(true);
        this.name.setMaxLength(50);
        //this.name.setResponder(this::onNameChanged);
        this.name.setValue("");
        this.addWidget(this.name);
        this.setInitialFocus(this.name);
        this.name.setEditable(false);
        //Widgets
        buildButtons(x, y);
    }

    private void buildButtons(int x, int y) {
        this.craftButton = addRenderableWidget(Button.builder(Component.literal(""), this::handleCraftButton)
                .bounds(x + CRAFT_BUTTON_OFFSET_X, y + CRAFT_BUTTON_OFFSET_Y, 16, 16)
                .build());

        //Arrow Buttons
        this.magnitudeListLeft = Lists.newArrayList();
        this.magnitudeListRight = Lists.newArrayList();
        this.durationListLeft = Lists.newArrayList();
        this.durationListRight = Lists.newArrayList();

        this.leftSpellFormButton = addWidget(Button.builder(Component.literal(""), this::handleLeftSpellFormButton)
                .bounds(x + LEFT_SPELL_FORM_BUTTON_OFFSET_X, y + SPELL_FORM_BUTTON_OFFSET_Y, 7, 11)
                .build());
        this.rightSpellFormButton = addWidget(Button.builder(Component.literal(""), this::handleRightSpellFormButton)
                .bounds(x + RIGHT_SPELL_FORM_BUTTON_OFFSET_X, y + SPELL_FORM_BUTTON_OFFSET_Y, 7, 11)
                .build());
        for ( int i = 0; i < this.maxSlots; i++ ) {
            this.magnitudeListLeft.add(addWidget(Button.builder(Component.literal(""), this::handleLeftMagnitudeButton)
                    .bounds(x + LEFT_MAGNITUDE_BUTTON_OFFSET_X, y + MAGNITUDE_BUTTON_OFFSET_Y + 18 * i, 7, 11)
                    .build()));
        }
        for ( int i = 0; i < this.maxSlots; i++ ) {
            this.magnitudeListRight.add(addWidget(Button.builder(Component.literal(""), this::handleRightMagnitudeButton)
                    .bounds(x + RIGHT_MAGNITUDE_BUTTON_OFFSET_X, y + MAGNITUDE_BUTTON_OFFSET_Y + 18 * i, 7, 11)
                    .build()));
        }
        for ( int i = 0; i < this.maxSlots; i++ ) {
            this.durationListLeft.add(addWidget(Button.builder(Component.literal(""), this::handleLeftDurationButton)
                    .bounds(x + LEFT_DURATION_BUTTON_OFFSET_X, y + DURATION_BUTTON_OFFSET_Y + 18 * i, 7, 11)
                    .build()));
        }
        for ( int i = 0; i < this.maxSlots; i++ ) {
            this.durationListRight.add(addWidget(Button.builder(Component.literal(""), this::handleRightDurationButton)
                    .bounds(x + RIGHT_DURATION_BUTTON_OFFSET_X, y + DURATION_BUTTON_OFFSET_Y + 18 * i, 7, 11)
                    .build()));
        }
    }

    private void handleCraftButton(Button button) {
        Slot slot = this.menu.getSlot(0);
        if ( !slot.hasItem() ) return;
        if ( this.menu.isReadyToMake() ) {
            String string = this.name.getValue();
            if ( !slot.getItem().hasCustomHoverName() && string.equals(slot.getItem().getHoverName().getString()) ) string = "";
            if ( this.menu.makeSpell(string) ) this.name.setValue("");
        }
        else if ( this.menu.isReadyToDump() ) {
            if ( this.menu.dumpSpell() && slot.getItem().hasCustomHoverName() ) this.name.setValue(slot.getItem().getHoverName().getString());
        }
    }

    private void handleLeftSpellFormButton(Button button) {
        if ( !this.menu.isReadyToMake() ) return;
        CompoundTag tag = new CompoundTag();
        List<ModSpellForms.SpellForm> list = ModSpellForms.SPELL_FORM_REGISTRY.get().getValues().stream().toList();
        ModSpellForms.SpellForm form = this.menu.getSpellForm();
        ModSpellForms.SpellForm newForm;
        if ( form == list.get(0) ) newForm = list.get(list.size() - 1);
        else newForm = list.get(list.indexOf(form) - 1);
        tag.putString(ParchmentItem.NBT_KEY_SPELL_FORM, CastingValidator.getStringFromForm(newForm));
        this.menu.editSpellForm(tag);
    }

    private void handleRightSpellFormButton(Button button) {
        if ( !this.menu.isReadyToMake() ) return;
        CompoundTag tag = new CompoundTag();
        List<ModSpellForms.SpellForm> list = ModSpellForms.SPELL_FORM_REGISTRY.get().getValues().stream().toList();
        ModSpellForms.SpellForm form = this.menu.getSpellForm();
        ModSpellForms.SpellForm newForm;
        if ( form == list.get(list.size() - 1) ) newForm = list.get(0);
        else newForm = list.get(list.indexOf(form) + 1);
        tag.putString(ParchmentItem.NBT_KEY_SPELL_FORM, CastingValidator.getStringFromForm(newForm));
        this.menu.editSpellForm(tag);
    }

    private void handleLeftMagnitudeButton(Button button) {
        int index = this.magnitudeListLeft.indexOf(button);
        if ( !this.menu.isReadyToMake() || this.menu.getCraftSlots().getItem(index + 1).isEmpty() ) return;
        int magnitude = this.menu.getMagnitude().get(index);
        List<Integer> newList = new ArrayList<Integer>(this.menu.getMagnitude());
        if ( magnitude > 0 ) {
            newList.set(index, magnitude - 1);
            this.menu.editSpellStats((byte)0, newList);
        }
    }

    private void handleRightMagnitudeButton(Button button) {
        int index = this.magnitudeListRight.indexOf(button);
        if ( !this.menu.isReadyToMake() || this.menu.getCraftSlots().getItem(index + 1).isEmpty() ) return;
        int magnitude = this.menu.getMagnitude().get(index);
        List<Integer> newList = new ArrayList<Integer>(this.menu.getMagnitude());
        if ( magnitude < 64 ) {
            newList.set(index, magnitude + 1);
            this.menu.editSpellStats((byte)0, newList);
        }
    }

    private void handleLeftDurationButton(Button button) {
        int index = this.durationListLeft.indexOf(button);
        if ( !this.menu.isReadyToMake() || this.menu.getCraftSlots().getItem(index + 1).isEmpty() ) return;
        int duration = this.menu.getDuration().get(index);
        List<Integer> newList = new ArrayList<Integer>(this.menu.getDuration());
        if ( duration > 0 ) {
            newList.set(index, duration - 1);
            this.menu.editSpellStats((byte)1, newList);
        }
    }

    private void handleRightDurationButton(Button button) {
        int index = this.durationListRight.indexOf(button);
        if ( !this.menu.isReadyToMake() || this.menu.getCraftSlots().getItem(index + 1).isEmpty() ) return;
        int duration = this.menu.getDuration().get(index);
        List<Integer> newList = new ArrayList<Integer>(this.menu.getDuration());
        if ( duration < 64 ) {
            newList.set(index, duration + 1);
            this.menu.editSpellStats((byte)1, newList);
        }
    }

    @Override
    public void slotChanged(AbstractContainerMenu pContainerToSend, int pSlotInd, ItemStack pStack) {
        if ( pSlotInd == 0 ) {
            boolean isEditable = this.menu.isCleanParchment(pStack);
            this.name.setEditable(isEditable);
            this.setFocused(this.name);
            if ( !isEditable ) this.name.setValue("");
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if ( pKeyCode == 256 ) this.minecraft.player.closeContainer();
        return !this.name.keyPressed(pKeyCode, pScanCode, pModifiers) && !this.name.canConsumeInput() ? super.keyPressed(pKeyCode, pScanCode, pModifiers) : true;
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.name.tick();
        if ( this.craftButton.isFocused() ) this.craftButton.setFocused(false);
        if ( this.leftSpellFormButton.isFocused() ) this.leftSpellFormButton.setFocused(false);
        if ( this.rightSpellFormButton.isFocused() ) this.rightSpellFormButton.setFocused(false);
        for ( Button button : this.magnitudeListLeft ) {
            if ( button.isFocused() ) button.setFocused(false);
        }
        for ( Button button : this.magnitudeListRight ) {
            if ( button.isFocused() ) button.setFocused(false);
        }
        for ( Button button : this.durationListLeft ) {
            if ( button.isFocused() ) button.setFocused(false);
        }
        for ( Button button : this.durationListRight ) {
            if ( button.isFocused() ) button.setFocused(false);
        }
    }

    @Override
    public void removed() {
        super.removed();
        this.menu.removeSlotListener(this);
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        String string = this.name.getValue();
        this.init(pMinecraft, pWidth, pHeight);
        this.name.setValue(string);
    }

    @Override
    public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
    }

    private void renderArrowButton(Button button, GuiGraphics graphics, int x, int y, int v, int u) {
        button.renderTexture(graphics, TEXTURE, x, y, v, u,
                0, 7, 11, 256, 256);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.name.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        //Action button
        this.craftButton.renderTexture(graphics, TEXTURE, x + CRAFT_BUTTON_OFFSET_X, y + CRAFT_BUTTON_OFFSET_Y,
                this.menu.isReadyToMake() ? 176 : 176 + 16,
                this.menu.isReadyToMake() || this.menu.isReadyToDump() ? 16 : 0,
                this.menu.isReadyToMake() || this.menu.isReadyToDump() ? 16 : 0, 16, 16, 256, 256);

        //Spell Form buttons
        renderArrowButton(this.leftSpellFormButton, graphics, x + LEFT_SPELL_FORM_BUTTON_OFFSET_X, y + SPELL_FORM_BUTTON_OFFSET_Y, 176,
                !this.menu.isReadyToMake() ? 86 : this.leftSpellFormButton.isMouseOver(mouseX, mouseY) ? 75 : 64);
        renderArrowButton(this.rightSpellFormButton, graphics, x + RIGHT_SPELL_FORM_BUTTON_OFFSET_X, y + SPELL_FORM_BUTTON_OFFSET_Y, 183,
                !this.menu.isReadyToMake() ? 86 : this.rightSpellFormButton.isMouseOver(mouseX, mouseY) ? 75 : 64);

        //Stat buttons
        for ( int i = 0; i < this.magnitudeListLeft.size(); i++ ) {
            renderArrowButton(this.magnitudeListLeft.get(i), graphics, x + LEFT_MAGNITUDE_BUTTON_OFFSET_X, y + MAGNITUDE_BUTTON_OFFSET_Y + 18 * i, 176,
                    !this.menu.isReadyToMake() || this.menu.getCraftSlots().getItem(1 + i).isEmpty() ? 86 : this.magnitudeListLeft.get(i).isMouseOver(mouseX, mouseY) ? 75 : 64);
        }
        for ( int i = 0; i < this.magnitudeListRight.size(); i++ ) {
            renderArrowButton(this.magnitudeListRight.get(i), graphics, x + RIGHT_MAGNITUDE_BUTTON_OFFSET_X, y + MAGNITUDE_BUTTON_OFFSET_Y + 18 * i, 183,
                    !this.menu.isReadyToMake() || this.menu.getCraftSlots().getItem(1 + i).isEmpty() ? 86 : this.magnitudeListRight.get(i).isMouseOver(mouseX, mouseY) ? 75 : 64);
        }
        for ( int i = 0; i < this.durationListLeft.size(); i++ ) {
            renderArrowButton(this.durationListLeft.get(i), graphics, x + LEFT_DURATION_BUTTON_OFFSET_X, y + DURATION_BUTTON_OFFSET_Y + 18 * i, 176,
                    !this.menu.isReadyToMake() || this.menu.getCraftSlots().getItem(1 + i).isEmpty() ? 86 : this.durationListLeft.get(i).isMouseOver(mouseX, mouseY) ? 75 : 64);
        }
        for ( int i = 0; i < this.durationListRight.size(); i++ ) {
            renderArrowButton(this.durationListRight.get(i), graphics, x + RIGHT_DURATION_BUTTON_OFFSET_X, y + DURATION_BUTTON_OFFSET_Y + 18 * i, 183,
                    !this.menu.isReadyToMake() || this.menu.getCraftSlots().getItem(1 + i).isEmpty() ? 86 : this.durationListRight.get(i).isMouseOver(mouseX, mouseY) ? 75 : 64);
        }

        //Spell Form icon rendering
        if ( this.menu.isReadyToMake() ) {
            ResourceLocation icon = new ResourceLocation(SpellMaker.MOD_ID, "textures/gui/spellform/" + this.menu.getSpellForm().getName() + ".png");
            int xIcon = x + LEFT_SPELL_FORM_BUTTON_OFFSET_X + 9;
            int yIcon = y + SPELL_FORM_BUTTON_OFFSET_Y - 2;
            ModScreen.drawTexture(icon, xIcon, yIcon, 0, 0, 16, 16, 16, 16, graphics);
            Component name = Component.translatable("spellform.spellmaker." + this.menu.getSpellForm().getName());
            if ( mouseX >= xIcon && mouseX <= xIcon + 16 && mouseY >= yIcon && mouseY <= yIcon + 16 ) {
                graphics.fill(RenderType.guiOverlay(), xIcon, yIcon, xIcon + 16, yIcon + 16, Integer.MAX_VALUE);
                graphics.renderTooltip(this.font, name, mouseX, mouseY);
            }
        }
        else {
            int xIcon = x + LEFT_SPELL_FORM_BUTTON_OFFSET_X + 9;
            int yIcon = y + SPELL_FORM_BUTTON_OFFSET_Y - 2;
            ModScreen.drawTexture(TEXTURE, xIcon, yIcon, 192, 0, 16, 16, 256, 256, graphics);
        }

        //Stat number strings
        if ( this.menu.isReadyToMake() ) {
            int stringX = x + LEFT_SPELL_FORM_BUTTON_OFFSET_X + 62;
            int stringY = y + SPELL_FORM_BUTTON_OFFSET_Y;
            graphics.drawCenteredString(this.font, Component.translatable("tooltip.spellmaker.magnitude"),
                    stringX, stringY - 7 + this.font.lineHeight, 16777215);
            graphics.drawCenteredString(this.font, Component.translatable("tooltip.spellmaker.duration"),
                    stringX + 54, stringY - 7 + this.font.lineHeight, 16777215);

            for ( int i = 0; i < this.menu.howManyRuneSlotsOpen(); i++ ) {
                if ( !this.menu.getCraftSlots().getItem(1 + i).isEmpty() ) {
                    int numOffY = 18 * i;
                    graphics.drawCenteredString(this.font, String.valueOf(this.menu.getMagnitude().get(i)),
                            x + LEFT_MAGNITUDE_BUTTON_OFFSET_X + 17, y + MAGNITUDE_BUTTON_OFFSET_Y - 7 + this.font.lineHeight + numOffY, 16777215);
                    graphics.drawCenteredString(this.font, String.valueOf(this.menu.getDuration().get(i)),
                            x + LEFT_DURATION_BUTTON_OFFSET_X + 17, y + DURATION_BUTTON_OFFSET_Y - 7 + this.font.lineHeight + numOffY, 16777215);
                }
            }
        }
        //Stat name plate
        if ( !this.menu.isReadyToMake() ) {
            int boxX = x + LEFT_SPELL_FORM_BUTTON_OFFSET_X + 35;
            int boxY = y + SPELL_FORM_BUTTON_OFFSET_Y - 3;
            for ( int i = 0; i < 2; i++ ) {
                ModScreen.drawTexture(TEXTURE, boxX + 54 * i, boxY, 194, 97, 54, 18, 256, 256, graphics);
            }
        }
        //Magnitude plate
        int magX = x + LEFT_SPELL_FORM_BUTTON_OFFSET_X + 53;
        int magY = y + SPELL_FORM_BUTTON_OFFSET_Y + 15;
        for ( int i = 0; i < this.maxSlots; i++ ) {
            if ( this.menu.getCraftSlots().getItem(1 + i).isEmpty() || !this.menu.isReadyToMake() ) {
                ModScreen.drawTexture(TEXTURE, magX, magY + 18 * i, 176, 97, 18, 18, 256, 256, graphics);
            }
        }
        //Duration plate
        int durX = x + LEFT_SPELL_FORM_BUTTON_OFFSET_X + 107;
        int durY = y + SPELL_FORM_BUTTON_OFFSET_Y + 15;
        for ( int i = 0; i < this.maxSlots; i++ ) {
            if ( this.menu.getCraftSlots().getItem(1 + i).isEmpty() || !this.menu.isReadyToMake() ) {
                ModScreen.drawTexture(TEXTURE, durX, durY + 18 * i, 176, 97, 18, 18, 256, 256, graphics);
            }
        }

        //Locked Slots
        int off = 1;
        for ( int i = 0; i < this.menu.slots.size(); i++ ) {
            if ( this.menu.getSlot(i) instanceof RuneSlot slot ) {
                int xPos = x + LEFT_SPELL_FORM_BUTTON_OFFSET_X + 9;
                int yPos = y + SPELL_FORM_BUTTON_OFFSET_Y - 2 + 18 * off;
                int u = this.menu.isReadyToMake() ? 176 : 192;
                if ( !slot.isOpen ) ModScreen.drawTexture(TEXTURE, xPos, yPos, u, 0, 16, 16, 256, 256, graphics);
                off++;
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }
}
