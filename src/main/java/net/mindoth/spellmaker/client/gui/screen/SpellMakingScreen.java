package net.mindoth.spellmaker.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.client.gui.menu.SpellMakingMenu;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.sigil.SigilItem;
import net.mindoth.spellmaker.registries.ModData;
import net.mindoth.spellmaker.util.DataHelper;
import net.mindoth.spellmaker.util.SpellColor;
import net.mindoth.spellmaker.util.spellform.AbstractSpellForm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SpellMakingScreen extends AbstractContainerScreen<SpellMakingMenu> implements ContainerListener {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "textures/gui/spell_making_screen.png");
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
        this.name.setMaxLength(25);
        //this.name.setResponder(this::onNameChanged);
        this.name.setValue("");
        this.addWidget(this.name);
        this.setInitialFocus(this.name);
        this.name.setEditable(false);
        //Widgets
        buildButtons(x, y);
    }

    private void buildButtons(int x, int y) {
        this.craftButton = addWidget(Button.builder(Component.literal(""), this::handleCraftButton)
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
            if ( !slot.getItem().has(DataComponents.CUSTOM_NAME) && string.equals(slot.getItem().getHoverName().getString()) ) string = "";
            if ( this.menu.makeSpell(string) ) this.name.setValue("");
        }
        else if ( this.menu.isReadyToDump() ) {
            if ( this.menu.dumpSpell() && slot.getItem().has(DataComponents.CUSTOM_NAME) ) this.name.setValue(slot.getItem().getHoverName().getString());
        }
    }

    private void handleLeftSpellFormButton(Button button) {
        if ( !this.menu.isReadyToMake() ) return;
        CompoundTag tag = new CompoundTag();
        List<AbstractSpellForm> list = this.menu.getFormList();
        AbstractSpellForm form = this.menu.getSpellForm();
        AbstractSpellForm newForm;
        if ( form == list.get(0) ) newForm = list.get(list.size() - 1);
        else newForm = list.get(list.indexOf(form) - 1);
        tag.putString(ParchmentItem.NBT_KEY_SPELL_FORM, DataHelper.getStringFromForm(newForm));
        this.menu.editSpellForm(tag);
    }

    private void handleRightSpellFormButton(Button button) {
        if ( !this.menu.isReadyToMake() ) return;
        CompoundTag tag = new CompoundTag();
        List<AbstractSpellForm> list = this.menu.getFormList();
        AbstractSpellForm form = this.menu.getSpellForm();
        AbstractSpellForm newForm;
        if ( form == list.get(list.size() - 1) ) newForm = list.get(0);
        else newForm = list.get(list.indexOf(form) + 1);
        tag.putString(ParchmentItem.NBT_KEY_SPELL_FORM, DataHelper.getStringFromForm(newForm));
        this.menu.editSpellForm(tag);
    }

    private void handleLeftMagnitudeButton(Button button) {
        int index = this.magnitudeListLeft.indexOf(button);
        int magnitude = this.menu.getMagnitude().get(index);
        if ( !canEditStat((byte)0, index, false, magnitude) ) return;
        List<Integer> newList = new ArrayList<>(this.menu.getMagnitude());
        newList.set(index, magnitude - 1);
        this.menu.editSpellStats((byte)0, newList);
    }

    private void handleRightMagnitudeButton(Button button) {
        int index = this.magnitudeListRight.indexOf(button);
        int magnitude = this.menu.getMagnitude().get(index);
        if ( !canEditStat((byte)0, index, true, magnitude) ) return;
        List<Integer> newList = new ArrayList<>(this.menu.getMagnitude());
        newList.set(index, magnitude + 1);
        this.menu.editSpellStats((byte)0, newList);
    }

    private void handleLeftDurationButton(Button button) {
        int index = this.durationListLeft.indexOf(button);
        int duration = this.menu.getDuration().get(index);
        if ( !canEditStat((byte)1, index, false, duration) ) return;
        List<Integer> newList = new ArrayList<>(this.menu.getDuration());
        newList.set(index, duration - 1);
        this.menu.editSpellStats((byte)1, newList);
    }

    private void handleRightDurationButton(Button button) {
        int index = this.durationListRight.indexOf(button);
        int duration = this.menu.getDuration().get(index);
        if ( !canEditStat((byte)1, index, true, duration) ) return;
        List<Integer> newList = new ArrayList<>(this.menu.getDuration());
        newList.set(index, duration + 1);
        this.menu.editSpellStats((byte)1, newList);
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
        AbstractModScreen.renderTexture(button, graphics, TEXTURE, x, y, v, u, 0, 7, 11, 256, 256);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.name.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        //Action button
        if ( this.menu.isReadyToMake() || this.menu.isReadyToDump() ) {
            int xIcon = x + CRAFT_BUTTON_OFFSET_X;
            int yIcon = y + CRAFT_BUTTON_OFFSET_Y;
            AbstractModScreen.renderTexture(this.craftButton, graphics, TEXTURE, xIcon, yIcon,
                    this.menu.isReadyToMake() ? 176 : 176 + 16, this.craftButton.isMouseOver(mouseX, mouseY) ? 32 : 16,
                    0, 16, 16, 256, 256);
            Component name = this.menu.isReadyToMake() ? Component.translatable("tooltip.spellmaker.make") : Component.translatable("tooltip.spellmaker.dump");
            if ( mouseX >= xIcon && mouseX <= xIcon + 16 && mouseY >= yIcon && mouseY <= yIcon + 16 ) graphics.renderTooltip(this.font, name, mouseX, mouseY);
        }

        //Spell Form buttons
        if ( this.menu.isReadyToMake() ) {
            renderArrowButton(this.leftSpellFormButton, graphics, x + LEFT_SPELL_FORM_BUTTON_OFFSET_X, y + SPELL_FORM_BUTTON_OFFSET_Y, 176,
                    this.leftSpellFormButton.isMouseOver(mouseX, mouseY) ? 59 : 48);
            renderArrowButton(this.rightSpellFormButton, graphics, x + RIGHT_SPELL_FORM_BUTTON_OFFSET_X, y + SPELL_FORM_BUTTON_OFFSET_Y, 183,
                    this.rightSpellFormButton.isMouseOver(mouseX, mouseY) ? 59 : 48);
        }

        //Stat buttons
        for ( int i = 0; i < this.magnitudeListLeft.size(); i++ ) {
            if ( canEditStat((byte)0, i, false, this.menu.getMagnitude().get(i)) ) {
                renderArrowButton(this.magnitudeListLeft.get(i), graphics, x + LEFT_MAGNITUDE_BUTTON_OFFSET_X, y + MAGNITUDE_BUTTON_OFFSET_Y + 18 * i, 176,
                        this.magnitudeListLeft.get(i).isMouseOver(mouseX, mouseY) ? 59 : 48);
            }
        }
        for ( int i = 0; i < this.magnitudeListRight.size(); i++ ) {
            if ( canEditStat((byte)0, i, true, this.menu.getMagnitude().get(i)) ) {
                renderArrowButton(this.magnitudeListRight.get(i), graphics, x + RIGHT_MAGNITUDE_BUTTON_OFFSET_X, y + MAGNITUDE_BUTTON_OFFSET_Y + 18 * i, 183,
                        this.magnitudeListRight.get(i).isMouseOver(mouseX, mouseY) ? 59 : 48);
            }
        }
        for ( int i = 0; i < this.durationListLeft.size(); i++ ) {
            if ( canEditStat((byte)1, i, false, this.menu.getDuration().get(i)) ) {
                renderArrowButton(this.durationListLeft.get(i), graphics, x + LEFT_DURATION_BUTTON_OFFSET_X, y + DURATION_BUTTON_OFFSET_Y + 18 * i, 176,
                        this.durationListLeft.get(i).isMouseOver(mouseX, mouseY) ? 59 : 48);
            }
        }
        for ( int i = 0; i < this.durationListRight.size(); i++ ) {
            if ( canEditStat((byte)1, i, true, this.menu.getDuration().get(i)) ) {
                renderArrowButton(this.durationListRight.get(i), graphics, x + RIGHT_DURATION_BUTTON_OFFSET_X, y + DURATION_BUTTON_OFFSET_Y + 18 * i, 183,
                        this.durationListRight.get(i).isMouseOver(mouseX, mouseY) ? 59 : 48);
            }
        }

        //Spell Form icon rendering
        if ( this.menu.isReadyToMake() ) {
            ResourceLocation iconBg = ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "textures/gui/spellform/icon_background.png");
            ResourceLocation icon = getSpellIcon();
            int xIcon = x + LEFT_SPELL_FORM_BUTTON_OFFSET_X + 9;
            int yIcon = y + SPELL_FORM_BUTTON_OFFSET_Y - 2;
            graphics.blit(iconBg, xIcon, yIcon, 0, 0, 16, 16, 16, 16);
            graphics.blit(icon, xIcon, yIcon, 0, 0, 16, 16, 16, 16);
            Component name = Component.translatable("spellform.spellmaker." + this.menu.getSpellForm().getName());
            if ( mouseX >= xIcon && mouseX <= xIcon + 16 && mouseY >= yIcon && mouseY <= yIcon + 16 ) {
                graphics.fill(RenderType.guiOverlay(), xIcon, yIcon, xIcon + 16, yIcon + 16, Integer.MAX_VALUE);
                graphics.renderTooltip(this.font, name, mouseX, mouseY);
            }
        }

        //Stat number strings
        boolean showMag = false;
        boolean showDur = false;
        for ( int i = 0; i < this.menu.slots.size(); i++ ) {
            if ( this.menu.slots.get(i) instanceof SpellMakingMenu.SigilSlot sigilSlot && sigilSlot.getItem().getItem() instanceof SigilItem sigil ) {
                if ( !showMag && sigil.canModifyMagnitude() ) showMag = true;
                if ( !showDur && sigil.canModifyDuration() ) showDur = true;
            }
        }
        //Stat name plate
        int boxX = x + LEFT_SPELL_FORM_BUTTON_OFFSET_X + 35;
        int boxY = y + SPELL_FORM_BUTTON_OFFSET_Y - 3;
        for ( int i = 0; i < 2; i++ ) {
            if ( (i == 0 && showMag) || (i == 1 && showDur) ) {
                graphics.blit(TEXTURE, boxX + 54 * i, boxY, 194, 70, 54, 18, 256, 256);
            }
        }
        for ( int i = 0; i < this.maxSlots; i++ ) {
            //Magnitude plates
            if ( this.menu.isReadyToMake() && this.menu.getCraftSlots().getItem(i + 1).getItem() instanceof SigilItem sigil && sigil.canModifyMagnitude() ) {
                int magX = x + LEFT_SPELL_FORM_BUTTON_OFFSET_X + 53;
                int magY = y + SPELL_FORM_BUTTON_OFFSET_Y + 15;
                graphics.blit(TEXTURE, magX, magY + 18 * i, 176, 70, 18, 18, 256, 256);
            }
            //Duration plates
            if ( this.menu.isReadyToMake() && this.menu.getCraftSlots().getItem(i + 1).getItem() instanceof SigilItem sigil && sigil.canModifyDuration() ) {
                int durX = x + LEFT_SPELL_FORM_BUTTON_OFFSET_X + 107;
                int durY = y + SPELL_FORM_BUTTON_OFFSET_Y + 15;
                graphics.blit(TEXTURE, durX, durY + 18 * i, 176, 70, 18, 18, 256, 256);
            }
        }
        if ( this.menu.isReadyToMake() ) {
            int stringX = x + LEFT_SPELL_FORM_BUTTON_OFFSET_X + 62;
            int stringY = y + SPELL_FORM_BUTTON_OFFSET_Y;
            if ( showMag ) {
                graphics.drawCenteredString(this.font, Component.translatable("tooltip.spellmaker.magnitude"),
                        stringX, stringY - 7 + this.font.lineHeight, 16777215);
            }
            if ( showDur ) {
                graphics.drawCenteredString(this.font, Component.translatable("tooltip.spellmaker.duration"),
                        stringX + 54, stringY - 7 + this.font.lineHeight, 16777215);
            }

            for ( int i = 0; i < this.menu.howManySigilSlotsOpen(); i++ ) {
                if ( this.menu.getCraftSlots().getItem(i + 1).getItem() instanceof SigilItem sigil ) {
                    int numOffY = 18 * i;
                    if ( sigil.canModifyMagnitude() ) {
                        graphics.drawCenteredString(this.font, String.valueOf(this.menu.getMagnitude().get(i)),
                                x + LEFT_MAGNITUDE_BUTTON_OFFSET_X + 17, y + MAGNITUDE_BUTTON_OFFSET_Y - 7 + this.font.lineHeight + numOffY, 16777215);
                    }
                    if ( sigil.canModifyDuration() ) {
                        graphics.drawCenteredString(this.font, String.valueOf(this.menu.getDuration().get(i)),
                                x + LEFT_DURATION_BUTTON_OFFSET_X + 17, y + DURATION_BUTTON_OFFSET_Y - 7 + this.font.lineHeight + numOffY, 16777215);
                    }
                }
            }
        }
        //Locked Slots
        int off = 1;
        for ( int i = 0; i < this.menu.slots.size(); i++ ) {
            if ( this.menu.getSlot(i) instanceof SpellMakingMenu.SigilSlot slot ) {
                int xPos = x + LEFT_SPELL_FORM_BUTTON_OFFSET_X + 9;
                int yPos = y + SPELL_FORM_BUTTON_OFFSET_Y - 2 + 18 * off;
                int u = this.menu.isReadyToMake() ? 176 : 192;
                if ( !slot.isOpen ) graphics.blit(TEXTURE, xPos, yPos, u, 0, 16, 16, 256, 256);
                off++;
            }
        }
        //Casting cost
        if ( this.menu.isReadyToMake() ) {
            ItemStack scroll = this.menu.assemble(this.menu.getCraftSlots());
            CompoundTag tag = ModData.getLegacyTag(scroll);
            int cost = ParchmentItem.calculateSpellCost(this.menu.getSpellForm(), DataHelper.createMapFromTag(tag));
            Component component = Component.literal(String.valueOf(cost)).setStyle(Style.EMPTY.withBold(true));
            graphics.drawString(this.font, component, x + 16 - this.font.width(String.valueOf(cost)) / 2, y + 66, 5804213, false);
        }
    }

    private ResourceLocation getSpellIcon() {
        AbstractSpellForm form = this.menu.getSpellForm();
        List<ItemStack> sigilList = Lists.newArrayList();
        for ( Slot slot : this.menu.slots ) if ( slot instanceof SpellMakingMenu.SigilSlot ) sigilList.add(slot.getItem());
        List<Integer> magnitudeList = this.menu.getMagnitude();
        List<Integer> durationList = this.menu.getDuration();
        return SpellColor.getSpellIcon(form, sigilList, magnitudeList, durationList);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        //this.name.tick();
        if ( this.craftButton.isFocused() ) this.craftButton.setFocused(false);
        if ( this.menu.isReadyToMake() || this.menu.isReadyToDump() ) {
            if ( !this.craftButton.visible ) this.craftButton.visible = true;
        }
        else {
            if ( this.craftButton.visible ) this.craftButton.visible = false;
        }
        if ( this.leftSpellFormButton.isFocused() ) this.leftSpellFormButton.setFocused(false);
        if ( this.rightSpellFormButton.isFocused() ) this.rightSpellFormButton.setFocused(false);
        if ( this.menu.isReadyToMake() ) {
            if ( !this.leftSpellFormButton.visible ) this.leftSpellFormButton.visible = true;
            if ( !this.rightSpellFormButton.visible ) this.rightSpellFormButton.visible = true;
        }
        else {
            if ( this.leftSpellFormButton.visible ) this.leftSpellFormButton.visible = false;
            if ( this.rightSpellFormButton.visible ) this.rightSpellFormButton.visible = false;
        }

        for ( int i = 0; i < this.magnitudeListLeft.size(); i++ ) {
            Button button = this.magnitudeListLeft.get(i);
            if ( button.isFocused() ) button.setFocused(false);
            if ( canEditStat((byte)0, i, false, this.menu.getMagnitude().get(i)) ) {
                if ( !button.visible ) button.visible = true;
            }
            else {
                if ( button.visible ) button.visible = false;
            }
        }
        for ( int i = 0; i < this.magnitudeListRight.size(); i++ ) {
            Button button = this.magnitudeListRight.get(i);
            if ( button.isFocused() ) button.setFocused(false);
            if ( canEditStat((byte)0, i, true, this.menu.getMagnitude().get(i)) ) {
                if ( !button.visible ) button.visible = true;
            }
            else {
                if ( button.visible ) button.visible = false;
            }
        }
        for ( int i = 0; i < this.durationListLeft.size(); i++ ) {
            Button button = this.durationListLeft.get(i);
            if ( button.isFocused() ) button.setFocused(false);
            if ( canEditStat((byte)1, i, false, this.menu.getDuration().get(i)) ) {
                if ( !button.visible ) button.visible = true;
            }
            else {
                if ( button.visible ) button.visible = false;
            }
        }
        for ( int i = 0; i < this.durationListRight.size(); i++ ) {
            Button button = this.durationListRight.get(i);
            if ( button.isFocused() ) button.setFocused(false);
            if ( canEditStat((byte)1, i, true, this.menu.getDuration().get(i)) ) {
                if ( !button.visible ) button.visible = true;
            }
            else {
                if ( button.visible ) button.visible = false;
            }
        }
    }

    private boolean canEditStat(byte flag, int index, boolean isIncrease, int stat) {
        if ( !this.menu.isReadyToMake() || this.menu.getCraftSlots().getItem(index + 1).isEmpty() ) return false;
        if ( !(this.menu.getCraftSlots().getItem(index + 1).getItem() instanceof SigilItem sigil) ) return false;
        if ( flag == 0 ) {
            if ( isIncrease ) return stat < sigil.getMaxMagnitude();
            else return stat > sigil.getMinMagnitude();
        }
        else if ( flag == 1 ) {
            if ( isIncrease ) return stat < sigil.getMaxDuration();
            else return stat > sigil.getMinDuration();
        }
        return true;
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
