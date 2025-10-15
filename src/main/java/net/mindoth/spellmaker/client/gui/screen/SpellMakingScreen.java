package net.mindoth.spellmaker.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.client.gui.menu.SpellMakingMenu;
import net.mindoth.spellmaker.registries.ModSpellForms;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SpellMakingScreen extends AbstractContainerScreen<SpellMakingMenu> implements ContainerListener {

    private static final ResourceLocation TEXTURE = new ResourceLocation(SpellMaker.MOD_ID, "textures/gui/spell_making_screen.png");
    private EditBox name;
    private Button craftButton;
    private final int CRAFT_BUTTON_OFFSET_X = 39;
    private Button dumpButton;
    private final int DUMP_BUTTON_OFFSET_X = CRAFT_BUTTON_OFFSET_X + 18;

    private ModSpellForms.SpellForm spellForm;
    private final int SPELL_FORM_BUTTON_OFFSET_Y = this.menu.getTopRowHeight() + 2;
    private Button leftSpellFormButton;
    private final int LEFT_SPELL_FORM_BUTTON_OFFSET_X = 123;
    private Button rightSpellFormButton;
    private final int RIGHT_SPELL_FORM_BUTTON_OFFSET_X = LEFT_SPELL_FORM_BUTTON_OFFSET_X + 37;

    private int magnitude;
    private final int MAGNITUDE_BUTTON_OFFSET_Y = this.menu.getTopRowHeight() + 24;
    private Button leftMagnitudeButton;
    private final int LEFT_MAGNITUDE_BUTTON_OFFSET_X = 123;
    private Button rightMagnitudeButton;
    private final int RIGHT_MAGNITUDE_BUTTON_OFFSET_X = LEFT_MAGNITUDE_BUTTON_OFFSET_X + 37;

    private int duration;
    private final int DURATION_BUTTON_OFFSET_Y = this.menu.getTopRowHeight() + 42;
    private Button leftDurationButton;
    private final int LEFT_DURATION_BUTTON_OFFSET_X = 123;
    private Button rightDurationButton;
    private final int RIGHT_DURATION_BUTTON_OFFSET_X = LEFT_DURATION_BUTTON_OFFSET_X + 37;

    private int area;
    private final int AREA_BUTTON_OFFSET_Y = this.menu.getTopRowHeight() + 60;
    private Button leftAreaButton;
    private final int LEFT_AREA_BUTTON_OFFSET_X = 123;
    private Button rightAreaButton;
    private final int RIGHT_AREA_BUTTON_OFFSET_X = LEFT_AREA_BUTTON_OFFSET_X + 37;

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
        this.spellForm = ModSpellForms.CASTER_ONLY.get();
        this.magnitude = 1;
        this.duration = 1;
        this.area = 1;
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.name = new EditBox(this.font, x + 17, y + 20, 142, 12, Component.translatable("container.ancientmagicks.name"));
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
                .bounds(x + this.CRAFT_BUTTON_OFFSET_X, y + this.menu.getTopRowHeight(), 16, 16)
                .build());
        this.dumpButton = addRenderableWidget(Button.builder(Component.literal(""), this::handleDumpButton)
                .bounds(x + this.DUMP_BUTTON_OFFSET_X, y + this.menu.getTopRowHeight(), 16, 16)
                .build());

        //Arrow Buttons
        this.leftSpellFormButton = addRenderableWidget(Button.builder(Component.literal(""), this::handleLeftSpellFormButton)
                .bounds(x + LEFT_SPELL_FORM_BUTTON_OFFSET_X, y + SPELL_FORM_BUTTON_OFFSET_Y, 7, 11)
                .build());
        this.rightSpellFormButton = addRenderableWidget(Button.builder(Component.literal(""), this::handleRightSpellFormButton)
                .bounds(x + RIGHT_SPELL_FORM_BUTTON_OFFSET_X, y + SPELL_FORM_BUTTON_OFFSET_Y, 7, 11)
                .build());
        this.leftMagnitudeButton = addRenderableWidget(Button.builder(Component.literal(""), this::handleLeftMagnitudeButton)
                .bounds(x + LEFT_MAGNITUDE_BUTTON_OFFSET_X, y + MAGNITUDE_BUTTON_OFFSET_Y, 7, 11)
                .build());
        this.rightMagnitudeButton = addRenderableWidget(Button.builder(Component.literal(""), this::handleRightMagnitudeButton)
                .bounds(x + RIGHT_MAGNITUDE_BUTTON_OFFSET_X, y + MAGNITUDE_BUTTON_OFFSET_Y, 7, 11)
                .build());
        this.leftDurationButton = addRenderableWidget(Button.builder(Component.literal(""), this::handleLeftDurationButton)
                .bounds(x + LEFT_DURATION_BUTTON_OFFSET_X, y + DURATION_BUTTON_OFFSET_Y, 7, 11)
                .build());
        this.rightDurationButton = addRenderableWidget(Button.builder(Component.literal(""), this::handleRightDurationButton)
                .bounds(x + RIGHT_DURATION_BUTTON_OFFSET_X, y + DURATION_BUTTON_OFFSET_Y, 7, 11)
                .build());
        this.leftAreaButton = addRenderableWidget(Button.builder(Component.literal(""), this::handleLeftAreaButton)
                .bounds(x + LEFT_AREA_BUTTON_OFFSET_X, y + AREA_BUTTON_OFFSET_Y, 7, 11)
                .build());
        this.rightAreaButton = addRenderableWidget(Button.builder(Component.literal(""), this::handleRightAreaButton)
                .bounds(x + RIGHT_AREA_BUTTON_OFFSET_X, y + AREA_BUTTON_OFFSET_Y, 7, 11)
                .build());
    }

    private void handleCraftButton(Button button) {
        Slot slot = this.menu.getSlot(0);
        if ( slot.hasItem() && this.menu.isCleanParchment(slot.getItem()) ) {
            String string = this.name.getValue();
            if ( !slot.getItem().hasCustomHoverName() && string.equals(slot.getItem().getHoverName().getString()) ) string = "";
            if ( this.menu.craftSpell(string) ) this.name.setValue("");
        }
    }

    private void handleDumpButton(Button button) {
        Slot slot = this.menu.getSlot(0);
        if ( slot.hasItem() && !this.menu.isCleanParchment(slot.getItem()) ) {
            if ( this.menu.dumpSpell() && slot.getItem().hasCustomHoverName() ) this.name.setValue(slot.getItem().getHoverName().getString());
        }
    }

    private void handleLeftSpellFormButton(Button button) {
        if ( !this.menu.isReadyToCraft() ) return;
        List<ModSpellForms.SpellForm> list = ModSpellForms.REGISTRY.get().getValues().stream().toList();
        if ( this.spellForm == list.get(0) ) this.spellForm = list.get(list.size() - 1);
        else this.spellForm = list.get(list.indexOf(this.spellForm) - 1);
    }

    private void handleRightSpellFormButton(Button button) {
        if ( !this.menu.isReadyToCraft() ) return;
        List<ModSpellForms.SpellForm> list = ModSpellForms.REGISTRY.get().getValues().stream().toList();
        if ( this.spellForm == list.get(list.size() - 1) ) this.spellForm = list.get(0);
        else this.spellForm = list.get(list.indexOf(this.spellForm) + 1);
    }

    private void handleLeftMagnitudeButton(Button button) {
        if ( !this.menu.isReadyToCraft() ) return;
        if ( this.magnitude > 0 ) this.magnitude--;
    }

    private void handleRightMagnitudeButton(Button button) {
        if ( !this.menu.isReadyToCraft() ) return;
        if ( this.magnitude < 64 ) this.magnitude++;
    }

    private void handleLeftDurationButton(Button button) {
        if ( !this.menu.isReadyToCraft() ) return;
        if ( this.duration > 0 ) this.duration--;
    }

    private void handleRightDurationButton(Button button) {
        if ( !this.menu.isReadyToCraft() ) return;
        if ( this.duration < 64 ) this.duration++;
    }

    private void handleLeftAreaButton(Button button) {
        if ( !this.menu.isReadyToCraft() ) return;
        if ( this.area > 0 ) this.area--;
    }

    private void handleRightAreaButton(Button button) {
        if ( !this.menu.isReadyToCraft() ) return;
        if ( this.area < 64 ) this.area++;
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
        if ( this.dumpButton.isFocused() ) this.dumpButton.setFocused(false);
        if ( this.leftSpellFormButton.isFocused() ) this.leftSpellFormButton.setFocused(false);
        if ( this.rightSpellFormButton.isFocused() ) this.rightSpellFormButton.setFocused(false);
        if ( this.leftMagnitudeButton.isFocused() ) this.leftMagnitudeButton.setFocused(false);
        if ( this.rightMagnitudeButton.isFocused() ) this.rightMagnitudeButton.setFocused(false);
        if ( this.leftDurationButton.isFocused() ) this.leftDurationButton.setFocused(false);
        if ( this.rightDurationButton.isFocused() ) this.rightDurationButton.setFocused(false);
        if ( this.leftAreaButton.isFocused() ) this.leftAreaButton.setFocused(false);
        if ( this.rightAreaButton.isFocused() ) this.rightAreaButton.setFocused(false);
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

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.name.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        //Action buttons
        if ( this.menu.isReadyToCraft() ) {
            this.craftButton.renderTexture(graphics, TEXTURE, x + CRAFT_BUTTON_OFFSET_X, y + this.menu.getTopRowHeight(),
                    176, 16, 16, 16, 16, 256, 256);
            this.dumpButton.renderTexture(graphics, TEXTURE, x + DUMP_BUTTON_OFFSET_X, y + this.menu.getTopRowHeight(),
                    176 + 16, 48, 0, 16, 16, 256, 256);
        }
        else {
            this.craftButton.renderTexture(graphics, TEXTURE, x + CRAFT_BUTTON_OFFSET_X, y + this.menu.getTopRowHeight(),
                    176, 48, 0, 16, 16, 256, 256);
            if ( this.menu.isReadyToDump() ) {
                this.dumpButton.renderTexture(graphics, TEXTURE, x + DUMP_BUTTON_OFFSET_X, y + this.menu.getTopRowHeight(),
                        176 + 16, 16, 16, 16, 16, 256, 256);
            }
            else {
                this.dumpButton.renderTexture(graphics, TEXTURE, x + DUMP_BUTTON_OFFSET_X, y + this.menu.getTopRowHeight(),
                        176 + 16, 48, 0, 16, 16, 256, 256);
            }
        }

        int arrowTxtDiff = this.menu.isReadyToCraft() ? 11 : 0;
        int arrowTxtY = this.menu.isReadyToCraft() ? 64 : 86;

        //Spell Form buttons
        this.leftSpellFormButton.renderTexture(graphics, TEXTURE, x + LEFT_SPELL_FORM_BUTTON_OFFSET_X, y + SPELL_FORM_BUTTON_OFFSET_Y,
                176, arrowTxtY, arrowTxtDiff, 7, 11, 256, 256);
        this.rightSpellFormButton.renderTexture(graphics, TEXTURE, x + RIGHT_SPELL_FORM_BUTTON_OFFSET_X, y + SPELL_FORM_BUTTON_OFFSET_Y,
                183, arrowTxtY, arrowTxtDiff, 7, 11, 256, 256);

        //Stat buttons
        this.leftMagnitudeButton.renderTexture(graphics, TEXTURE, x + LEFT_MAGNITUDE_BUTTON_OFFSET_X, y + MAGNITUDE_BUTTON_OFFSET_Y,
                176, arrowTxtY, arrowTxtDiff, 7, 11, 256, 256);
        this.rightMagnitudeButton.renderTexture(graphics, TEXTURE, x + RIGHT_MAGNITUDE_BUTTON_OFFSET_X, y + MAGNITUDE_BUTTON_OFFSET_Y,
                183, arrowTxtY, arrowTxtDiff, 7, 11, 256, 256);
        this.leftDurationButton.renderTexture(graphics, TEXTURE, x + LEFT_DURATION_BUTTON_OFFSET_X, y + DURATION_BUTTON_OFFSET_Y,
                176, arrowTxtY, arrowTxtDiff, 7, 11, 256, 256);
        this.rightDurationButton.renderTexture(graphics, TEXTURE, x + RIGHT_DURATION_BUTTON_OFFSET_X, y + DURATION_BUTTON_OFFSET_Y,
                183, arrowTxtY, arrowTxtDiff, 7, 11, 256, 256);
        this.leftAreaButton.renderTexture(graphics, TEXTURE, x + LEFT_AREA_BUTTON_OFFSET_X, y + AREA_BUTTON_OFFSET_Y,
                176, arrowTxtY, arrowTxtDiff, 7, 11, 256, 256);
        this.rightAreaButton.renderTexture(graphics, TEXTURE, x + RIGHT_AREA_BUTTON_OFFSET_X, y + AREA_BUTTON_OFFSET_Y,
                183, arrowTxtY, arrowTxtDiff, 7, 11, 256, 256);

        //Spell Form icon rendering
        if ( this.menu.isReadyToCraft() ) {
            ResourceLocation icon = new ResourceLocation(SpellMaker.MOD_ID, "textures/gui/spellform/" + this.spellForm.getName() + ".png");
            int xIcon = x + LEFT_SPELL_FORM_BUTTON_OFFSET_X + 14;
            int yIcon = y + SPELL_FORM_BUTTON_OFFSET_Y - 2;
            ModScreen.drawTexture(icon, xIcon, yIcon, 0, 0, 16, 16, 16, 16, graphics);
            Component name = Component.translatable("spellform.spellmaker." + this.spellForm.getName());
            if ( mouseX >= xIcon && mouseX <= xIcon + 16 && mouseY >= yIcon && mouseY <= yIcon + 16 ) {
                graphics.fill(RenderType.guiOverlay(), xIcon, yIcon, xIcon + 16, yIcon + 16, Integer.MAX_VALUE);
                graphics.renderTooltip(this.font, name, mouseX, mouseY);
            }

            //Stat number strings
            graphics.drawCenteredString(this.font, String.valueOf(this.magnitude),
                    x + LEFT_MAGNITUDE_BUTTON_OFFSET_X + 22, y + MAGNITUDE_BUTTON_OFFSET_Y - 7 + this.font.lineHeight, 16777215);
            graphics.drawCenteredString(this.font, Component.translatable("tooltip.spellmaker.magnitude"),
                    x + 68, y + MAGNITUDE_BUTTON_OFFSET_Y - 7 + this.font.lineHeight, 16777215);

            graphics.drawCenteredString(this.font, String.valueOf(this.duration),
                    x + LEFT_DURATION_BUTTON_OFFSET_X + 22, y + DURATION_BUTTON_OFFSET_Y - 7 + this.font.lineHeight, 16777215);
            graphics.drawCenteredString(this.font, Component.translatable("tooltip.spellmaker.duration"),
                    x + 68, y + DURATION_BUTTON_OFFSET_Y - 7 + this.font.lineHeight, 16777215);

            graphics.drawCenteredString(this.font, String.valueOf(this.area),
                    x + LEFT_AREA_BUTTON_OFFSET_X + 22, y + AREA_BUTTON_OFFSET_Y - 7 + this.font.lineHeight, 16777215);
            graphics.drawCenteredString(this.font, Component.translatable("tooltip.spellmaker.area"),
                    x + 68, y + AREA_BUTTON_OFFSET_Y - 7 + this.font.lineHeight, 16777215);
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
