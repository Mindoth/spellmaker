package net.mindoth.spellmaker.client.gui.screen;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.weapon.SpellBookItem;
import net.mindoth.spellmaker.network.RemoveScrollFromBookPacket;
import net.mindoth.spellmaker.network.UpdateBookDataPacket;
import net.mindoth.spellmaker.registries.ModData;
import net.mindoth.spellmaker.util.DataHelper;
import net.mindoth.spellmaker.util.SpellColor;
import net.mindoth.spellmaker.util.spellform.AbstractSpellForm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.HashMap;
import java.util.List;

public class SpellBookScreen extends AbstractModScreen {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SpellMaker.MOD_ID, "textures/gui/spell_book_screen.png");

    public final ItemStack book;
    public int getSelectedSlot() {
        return ModData.getLegacyTag(this.book).getInt(SpellBookItem.NBT_KEY_BOOK_SLOT).get();
    }
    private List<ItemStack> itemList = Lists.newArrayList();
    public final List<ItemStack> scrollList;
    private List<List<ItemStack>> pageList;
    private int spreadNumber;
    private List<Button> slotButtonList = Lists.newArrayList();
    private final int yOffset = 8;
    private final int leftButtonOffsetX = -114 + 6;
    private final int rightButtonOffsetX = 20 - 8;
    private final int squareSpacingX = 26;
    private final int squareSpacingY = 34;
    private List<Button> selectButtonList = Lists.newArrayList();
    private HashMap<Button, Button> swapButtonMap = new HashMap<>();
    private List<Button> upSwapButtonList = Lists.newArrayList();
    private List<Button> downSwapButtonList = Lists.newArrayList();
    private final int leftSwapButtonOffsetX = 16;
    private final int rightSwapButtonOffsetX = 103;

    private final int arrowOffsetY = 68;
    private final int arrowOffsetX = 94;
    private Button rightArrow;
    private final int rightArrowOffsetX = this.arrowOffsetX;
    private Button leftArrow;
    private final int leftArrowOffsetX = -18 - this.arrowOffsetX;

    protected SpellBookScreen(ItemStack book, int spreadNumber) {
        super(Component.literal(""));
        this.spreadNumber = spreadNumber;
        this.book = book;
        CompoundTag bookTag = ModData.getOrCreateLegacyTag(this.book);
        this.scrollList = SpellBookItem.getScrollListFromBook(bookTag);
        this.scrollList.removeIf(ItemStack::isEmpty);
        createPages(false);
    }

    public static void open(ItemStack stack, int spreadNumber) {
        Minecraft instance = Minecraft.getInstance();
        if ( !(instance.screen instanceof SpellBookScreen) ) instance.setScreen(new SpellBookScreen(stack, spreadNumber));
    }

    private boolean isFirstPage() {
        return this.spreadNumber == 0;
    }

    private boolean isLastPage() {
        return this.spreadNumber == this.pageList.size() - 1 || this.itemList.isEmpty();
    }

    public void createPages(boolean refreshBook) {
        if ( refreshBook ) ModData.setLegacyTag(this.book, ModData.getLegacyTag(SpellBookItem.constructBook(this.book, this.scrollList)));
        this.itemList = Lists.newArrayList();
        for ( ItemStack stack : this.scrollList ) this.itemList.add(stack);
        this.pageList = Lists.newArrayList();
        List<ItemStack> page = Lists.newArrayList();
        for ( ItemStack stack : this.itemList ) {
            page.add(stack);
            if ( page.size() == SpellBookItem.pageSize || this.itemList.get(this.itemList.size() - 1) == stack ) {
                this.pageList.add(page);
                page = Lists.newArrayList();
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        buildButtons();
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
    }

    private void handleButtonVisibility() {
        handleSlotButtonVisibility();
        handleSwapButtonVisibility();
        handleSelectButtonVisibility();
    }

    public void publicClearWidgets() {
        this.clearWidgets();
    }

    public void buildButtons() {
        int x = minecraft.getWindow().getGuiScaledWidth() / 2;
        int y = minecraft.getWindow().getGuiScaledHeight() / 2;
        this.slotButtonList = Lists.newArrayList();
        this.selectButtonList = Lists.newArrayList();
        this.swapButtonMap = new HashMap<>();
        this.upSwapButtonList = Lists.newArrayList();
        this.downSwapButtonList = Lists.newArrayList();
        this.clearWidgets();
        boolean isRightPage = false;
        int row = 0;
        int column = 0;
        for (int i = 0; i < SpellBookItem.pageSize; i++ ) {
            if ( column == SpellBookItem.maxColumns ) {
                row++;
                column = 0;
                if ( row == SpellBookItem.maxRows ) {
                    row = 0;
                    isRightPage = !isRightPage;
                }
            }

            int xPos = isRightPage ? x + (column * this.squareSpacingX) + this.rightButtonOffsetX : x + (column * this.squareSpacingX) + this.leftButtonOffsetX;
            int yPos = y - 74 + (row * this.squareSpacingY) + this.yOffset;

            buildSlotButton(xPos - 1, yPos - 1);

            column++;
        }
        handleButtonVisibility();

        //Page Arrows
        this.rightArrow = addRenderableWidget(Button.builder(Component.literal(""), this::handlePageRight)
                .bounds(x + this.rightArrowOffsetX, y + this.arrowOffsetY, 18, 10)
                .build());
        if ( isLastPage() && this.rightArrow.visible ) this.rightArrow.visible = false;
        if ( !isLastPage() && !this.rightArrow.visible ) this.rightArrow.visible = true;

        this.leftArrow = addRenderableWidget(Button.builder(Component.literal(""), this::handlePageLeft)
                .bounds(x + this.leftArrowOffsetX, y + this.arrowOffsetY, 18, 10)
                .build());
        if ( isFirstPage() && this.leftArrow.visible ) this.leftArrow.visible = false;
        if ( !isFirstPage() && !this.leftArrow.visible ) this.leftArrow.visible = true;
    }

    private void buildSelectButton(int xPos, int yPos) {
        Button button = addRenderableWidget(Button.builder(Component.literal(""), this::handleSelectButton)
                .bounds(xPos - 1 + 24, yPos - 5, 77, 28)
                .build());
        this.selectButtonList.add(button);
    }

    private void buildSlotButton(int xPos, int yPos) {
        Button button = addRenderableWidget(Button.builder(Component.literal(""), this::handleSlotButton)
                .bounds(xPos - 1, yPos - 1, 18, 18)
                .build());
        this.slotButtonList.add(button);
        buildSelectButton(xPos, yPos);
        if ( (this.slotButtonList.size() - 1) % SpellBookItem.maxColumns == 0 ) {
            buildUpSwapButton(xPos + 1, yPos + 1, button);
            buildDownSwapButton(xPos + 1, yPos + 12, button);
        }
    }

    private void buildUpSwapButton(int xPos, int yPos, Button slotButton) {
        if ( this.upSwapButtonList.size() >= SpellBookItem.maxRows ) xPos += this.rightSwapButtonOffsetX;
        else xPos -= this.leftSwapButtonOffsetX;
        Button button = addRenderableWidget(Button.builder(Component.literal(""), this::handleSwapButton)
                .bounds(xPos - 1, yPos - 1, 11, 7)
                .build());
        this.upSwapButtonList.add(button);
        this.swapButtonMap.put(button, slotButton);
    }

    private void buildDownSwapButton(int xPos, int yPos, Button slotButton) {
        if ( this.downSwapButtonList.size() >= SpellBookItem.maxRows ) xPos += this.rightSwapButtonOffsetX;
        else xPos -= this.leftSwapButtonOffsetX;
        Button button = addRenderableWidget(Button.builder(Component.literal(""), this::handleSwapButton)
                .bounds(xPos - 1, yPos - 1, 11, 7)
                .build());
        this.downSwapButtonList.add(button);
        this.swapButtonMap.put(button, slotButton);
    }

    private void handleSelectButton(Button button) {
        if ( !this.selectButtonList.contains(button) ) return;
        int index = this.selectButtonList.indexOf(button) + (SpellBookItem.pageSize * (this.spreadNumber));
        ClientPacketDistributor.sendToServer(new UpdateBookDataPacket(this.book, this.scrollList, index, false, false));
    }

    private void handleSlotButton(Button button) {
        if ( !this.slotButtonList.contains(button) ) return;
        ItemStack stack = getStackFromSlot(button);
        if ( stack.isEmpty() ) return;
        Item item = stack.getItem();
        if ( item instanceof ParchmentItem ) {
            int index = this.scrollList.indexOf(stack);
            ClientPacketDistributor.sendToServer(new RemoveScrollFromBookPacket(this.book, this.scrollList, index, true, true));
        }
    }

    private void handleSwapButton(Button button) {
        if ( !this.upSwapButtonList.contains(button) && !this.downSwapButtonList.contains(button) ) return;
        boolean isUp = this.upSwapButtonList.contains(button);
        ItemStack stack = this.itemList.get(this.itemList.indexOf(getStackFromSlot(this.swapButtonMap.get(button))));
        if ( stack.isEmpty() ) return;
        if ( stack.getItem() instanceof ParchmentItem ) {
            int index = this.scrollList.indexOf(stack);
            int newSlot = getSelectedSlot();

            ItemStack first = this.scrollList.get(index).copy();
            ItemStack second;
            if ( isUp ) {
                second = this.scrollList.get(index - 1).copy();
                this.scrollList.set(index - 1, first);
                if ( index == getSelectedSlot() ) newSlot = getSelectedSlot() - 1;
                else if ( index == getSelectedSlot() - 1 ) newSlot = getSelectedSlot();
                else if ( index == getSelectedSlot() + 1 ) newSlot = getSelectedSlot() + 1;
            }
            else {
                second = this.scrollList.get(index + 1).copy();
                this.scrollList.set(index + 1, first);
                if ( index == getSelectedSlot() ) newSlot = getSelectedSlot() + 1;
                else if ( index == getSelectedSlot() + 1 ) newSlot = getSelectedSlot();
                else if ( index == getSelectedSlot() - 1 ) newSlot = getSelectedSlot() - 1;
            }
            this.scrollList.set(index, second);
            ClientPacketDistributor.sendToServer(new UpdateBookDataPacket(this.book, this.scrollList, newSlot, true, false));
        }
    }

    private void handleSelectButtonVisibility() {
        for ( Button button : this.selectButtonList ) {
            int stackIndex = this.selectButtonList.indexOf(button) + (SpellBookItem.pageSize * (this.spreadNumber));
            if ( stackIndex >= this.itemList.size() && button.visible ) button.visible = false;
            else if ( !button.visible ) button.visible = true;
        }
    }

    private void handleSlotButtonVisibility() {
        for ( Button button : this.slotButtonList ) {
            int stackIndex = this.slotButtonList.indexOf(button) + (SpellBookItem.pageSize * (this.spreadNumber));
            if ( stackIndex >= this.itemList.size() && button.visible ) button.visible = false;
            else if ( !button.visible ) button.visible = true;
        }
    }

    private void handleSwapButtonVisibility() {
        for ( Button button : this.upSwapButtonList ) {
            //first item on first page
            if ( this.upSwapButtonList.indexOf(button) == 0 && this.spreadNumber == 0 ) button.visible = false;
            else {
                if ( !this.swapButtonMap.get(button).visible && button.visible ) button.visible = false;
                else if ( !button.visible ) button.visible = true;
            }
        }
        for ( Button button : this.downSwapButtonList ) {
            //last item on page
            //no next page
            if ( (this.downSwapButtonList.indexOf(button) == this.downSwapButtonList.size() - 1 && isLastPage())
                    || (this.downSwapButtonList.indexOf(button) < this.downSwapButtonList.size() - 1
                    && !(getStackFromSlot(this.swapButtonMap.get(this.downSwapButtonList.get(this.downSwapButtonList.indexOf(button) + 1))).getItem() instanceof ParchmentItem)) )
                button.visible = false;
            else {
                if ( !this.swapButtonMap.get(button).visible && button.visible ) button.visible = false;
                else if ( !button.visible ) button.visible = true;
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        int x = minecraft.getWindow().getGuiScaledWidth() / 2;
        int y = minecraft.getWindow().getGuiScaledHeight() / 2;

        //Background
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x - 140, y - 90, 0, 0, 280, 180, 280, 280);

        //Arrows
        if ( this.rightArrow.visible ) renderTexture(this.rightArrow, graphics, TEXTURE, x + this.rightArrowOffsetX, y + this.arrowOffsetY,
                0, 180, 10, 18, 10, 280, 280);
        if ( this.leftArrow.visible ) renderTexture(this.leftArrow, graphics, TEXTURE, x + this.leftArrowOffsetX, y + this.arrowOffsetY,
                18, 180, 10, 18, 10, 280, 280);

        iterateSpellList(graphics, x, y);

        //Page number
        for ( int i = 0; i < 2; i++ ) {
            int pageNum = this.spreadNumber * 2 + 1 + i;
            Component pageNumTxt = Component.literal(String.valueOf(pageNum)).setStyle(Style.EMPTY.withBold(true));
            int textX = x - (this.font.width(pageNumTxt) / 2);
            int pageNumXOff = 67;
            int pageNumX = pageNum % 2 == 0 ? textX + pageNumXOff : textX - pageNumXOff;
            graphics.drawString(this.font, pageNumTxt, pageNumX, y + this.arrowOffsetY, ARGB.opaque(0), false);
        }

        iterateSpellListOver(graphics, mouseX, mouseY);
    }

    private void iterateSpellListOver(GuiGraphics graphics, int mouseX, int mouseY) {
        for ( List<ItemStack> page : this.pageList ) {
            if ( this.spreadNumber == this.pageList.indexOf(page) ) {
                boolean isRightPage = false;
                int row = 0;
                int column = 0;
                for ( int i = 0; i < page.size(); i++ ) {
                    ItemStack stack = page.get(i);
                    //Spot calc
                    if ( column == SpellBookItem.maxColumns ) {
                        row++;
                        column = 0;
                        if ( row == SpellBookItem.maxRows ) {
                            row = 0;
                            isRightPage = !isRightPage;
                        }
                    }
                    if ( stack.getItem() instanceof ParchmentItem ) {
                        if ( this.slotButtonList.get(i).isHovered() ) {

                            List<Component> components = getTooltipFromItem(this.minecraft, stack);
                            List<ClientTooltipComponent> clientComponents = Lists.newArrayList();
                            for ( Component component : components ) {
                                clientComponents.add(ClientTooltipComponent.create(component.getVisualOrderText()));
                            }
                            graphics.renderTooltip(this.font, clientComponents, mouseX, mouseY,
                                    DefaultTooltipPositioner.INSTANCE, stack.get(DataComponents.TOOLTIP_STYLE));
                        }
                    }
                    column++;
                }
            }
        }
    }

    private void iterateSpellList(GuiGraphics graphics, int x, int y) {
        boolean spellSelected = false;
        for ( List<ItemStack> page : this.pageList ) {
            if ( this.spreadNumber == this.pageList.indexOf(page) ) {
                boolean isRightPage = false;
                int row = 0;
                int column = 0;
                for ( int i = 0; i < page.size(); i++ ) {
                    ItemStack stack = page.get(i);
                    CompoundTag bookTag = ModData.getLegacyTag(this.book);
                    //Spot calc
                    if ( column == SpellBookItem.maxColumns ) {
                        row++;
                        column = 0;
                        if ( row == SpellBookItem.maxRows ) {
                            row = 0;
                            isRightPage = !isRightPage;
                        }
                    }
                    int xPos = isRightPage ? x + (column * this.squareSpacingX) + this.rightButtonOffsetX : x + (column * this.squareSpacingX) + this.leftButtonOffsetX;
                    int yPos = y - 74 + (row * this.squareSpacingY) + this.yOffset;
                    if ( stack.getItem() instanceof ParchmentItem ) {
                        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xPos - 3, yPos - 9, 58, 180, 105, 34, 280, 280);
                        Component spellTitle = stack.has(DataComponents.CUSTOM_NAME) ? Component.literal(stack.getHoverName().getString())
                                : Component.translatable("tooltip.spellmaker.untitled").setStyle(Style.EMPTY.withItalic(true));
                        int titleX = xPos + 61;
                        int titleY = yPos - 5;
                        renderSpellName(graphics, spellTitle, titleX, titleY);
                        //Selected Spell
                        if ( !spellSelected && !this.book.isEmpty() && bookTag != null && bookTag.contains(SpellBookItem.NBT_KEY_BOOK_SLOT) ) {
                            if ( getSelectedSlot() > -1 && getSelectedSlot() < this.itemList.size() && stack == this.scrollList.get(getSelectedSlot()) ) {
                                graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xPos + 19, yPos - 9, 163, 180, 83, 34, 280, 280);
                                spellSelected = true;
                            }
                        }
                        if ( this.selectButtonList.get(i).isHovered() ) {
                            graphics.fill(RenderPipelines.GUI, xPos + 22, yPos - 6, xPos + 99, yPos + 22, Integer.MIN_VALUE);
                        }
                    }
                    if ( stack.getItem() instanceof ParchmentItem ) {
                        if ( this.slotButtonList.get(i).isHovered() ) {
                            renderItemWithDecorations(graphics, stack, xPos, yPos);
                            graphics.fill(RenderPipelines.GUI, xPos, yPos, xPos + 16, yPos + 16, Integer.MIN_VALUE);
                        }
                        else {
                            ResourceLocation icon = getSpellIcon(stack);
                            graphics.blit(RenderPipelines.GUI_TEXTURED, icon, xPos, yPos, 0, 0, 16, 16, 16, 16);
                        }
                    }
                    //Swap Arrows
                    if ( stack.getItem() instanceof ParchmentItem ) {
                        int index = i / SpellBookItem.maxColumns;
                        int newX = xPos;
                        if ( index >= SpellBookItem.maxRows ) newX += this.rightSwapButtonOffsetX;
                        else newX -= this.leftSwapButtonOffsetX;
                        renderTexture(this.upSwapButtonList.get(index), graphics, TEXTURE, newX - 1, yPos - 1,
                                36, 180, 7, 11, 7, 280, 280);
                        renderTexture(this.downSwapButtonList.get(index), graphics, TEXTURE, newX - 1, yPos + 10,
                                47, 180, 7, 11, 7, 280, 280);
                    }
                    column++;
                }
            }
        }
    }

    private ResourceLocation getSpellIcon(ItemStack scroll) {
        CompoundTag tag = ModData.getLegacyTag(scroll);
        AbstractSpellForm form = DataHelper.getFormFromNbt(tag);
        List<ItemStack> sigilList = DataHelper.getSpellStackFromTag(tag);
        List<Integer> magnitudeList = DataHelper.getStatsFromString(tag.getString(ParchmentItem.NBT_KEY_SPELL_MAGNITUDES).get());
        List<Integer> durationList = DataHelper.getStatsFromString(tag.getString(ParchmentItem.NBT_KEY_SPELL_DURATIONS).get());
        return SpellColor.getSpellIcon(form, sigilList, magnitudeList, durationList);
    }

    private void renderSpellName(GuiGraphics graphics, Component spellTitle, int titleX, int titleY) {
        String name = spellTitle.getString();
        List<Component> nameLineList = Lists.newArrayList();
        int length = this.font.width(spellTitle);
        int limit = 75;
        if ( length < limit ) nameLineList.add(spellTitle);
        else {
            List<String> lines = putTextToLines(name, this.font, limit);
            lines.forEach(s -> nameLineList.add(Component.literal(s).setStyle(spellTitle.getStyle())));
        }
        for ( int i = 0; i < nameLineList.size(); i++ ) {
            Component component = nameLineList.get(i);
            int height = titleY - (nameLineList.size() - 1) * 4;
            graphics.drawString(this.font, component, titleX - this.font.width(component) / 2, height + this.font.lineHeight * (i + 1), ARGB.opaque(0), false);
        }
    }

    private void handlePageLeft(Button button) {
        if ( !isFirstPage() ) {
            this.spreadNumber--;
            handleButtonVisibility();
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }
    }

    private void handlePageRight(Button button) {
        if ( !isLastPage() ) {
            this.spreadNumber++;
            handleButtonVisibility();
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }
    }

    private ItemStack getStackFromSlot(Button button) {
        int stackIndex = this.slotButtonList.indexOf(button) + (SpellBookItem.pageSize * this.spreadNumber);
        if ( stackIndex >= this.itemList.size() ) return ItemStack.EMPTY;
        return this.pageList.get(this.spreadNumber).get(this.slotButtonList.indexOf(button));
    }

    @Override
    public void tick() {
        if ( this.rightArrow.isFocused() ) this.rightArrow.setFocused(false);
        if ( isLastPage() && this.rightArrow.visible ) this.rightArrow.visible = false;
        if ( !isLastPage() && !this.rightArrow.visible ) this.rightArrow.visible = true;
        if ( this.leftArrow.isFocused() ) this.leftArrow.setFocused(false);
        if ( isFirstPage() && this.leftArrow.visible ) this.leftArrow.visible = false;
        if ( !isFirstPage() && !this.leftArrow.visible ) this.leftArrow.visible = true;
        for ( Button button : this.upSwapButtonList ) if ( button.isFocused() ) button.setFocused(false);
        for ( Button button : this.downSwapButtonList ) if ( button.isFocused() ) button.setFocused(false);
        for ( Button button : this.selectButtonList ) if ( button.isFocused() ) button.setFocused(false);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        Minecraft instance = Minecraft.getInstance();
        if ( instance.player != null ) {
            Player player = instance.player;
            if ( event.key() == instance.options.keyInventory.getKey().getValue() ) {
                player.closeContainer();
                return true;
            }
        }
        return super.keyPressed(event);
    }
}
