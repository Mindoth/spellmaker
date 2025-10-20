package net.mindoth.spellmaker.client.gui.screen;

import com.google.common.collect.Lists;
import net.mindoth.spellmaker.SpellMaker;
import net.mindoth.spellmaker.item.ParchmentItem;
import net.mindoth.spellmaker.item.SpellBookItem;
import net.mindoth.spellmaker.network.ModNetwork;
import net.mindoth.spellmaker.network.PacketRemoveScrollFromBook;
import net.mindoth.spellmaker.network.PacketUpdateBookData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class SpellBookScreen extends ModScreen {

    private static final ResourceLocation TEXTURE = new ResourceLocation(SpellMaker.MOD_ID, "textures/gui/spell_book_screen.png");

    private final ItemStack book;
    private int getSelectedSlot() {
        return this.book.getTag().getInt(SpellBookItem.NBT_KEY_BOOK_SLOT);
    }
    private List<ItemStack> itemList = Lists.newArrayList();
    private final List<ItemStack> scrollList;
    private List<List<ItemStack>> pageList;
    private int spreadNumber;
    private List<Button> slotButtonList = Lists.newArrayList();
    private final int leftButtonOffsetX = -114 + 5;
    private final int rightButtonOffsetX = 20 - 6;
    private final int squareSpacingX = 26;
    private final int squareSpacingY = 26;
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

    private final int maxRows = 5;
    private final int maxColumns = 1;

    protected SpellBookScreen(ItemStack book, int spreadNumber) {
        super(Component.literal(""));
        this.spreadNumber = spreadNumber;
        this.book = book;
        this.scrollList = SpellBookItem.getScrollListFromBook(this.book.getOrCreateTag());
        this.scrollList.removeIf(ItemStack::isEmpty);
        createPages(false);
    }

    public static void open(ItemStack stack, int spreadNumber) {
        Minecraft MINECRAFT = Minecraft.getInstance();
        if ( !(MINECRAFT.screen instanceof SpellBookScreen) ) MINECRAFT.setScreen(new SpellBookScreen(stack, spreadNumber));
    }

    public static int getNewSlotFromScrollRemoval(int oldSlot, int bookSlot) {
        int newSlot = oldSlot;
        if ( oldSlot == bookSlot ) newSlot = -1;
        else if ( oldSlot < bookSlot ) newSlot = bookSlot - 1;
        return newSlot;
    }

    private boolean isFirstPage() {
        return this.spreadNumber == 0;
    }

    private boolean isLastPage() {
        return this.spreadNumber == this.pageList.size() - 1 || this.itemList.isEmpty();
    }

    private void createPages(boolean refreshBook) {
        if ( refreshBook ) this.book.setTag(SpellBookItem.constructBook(this.book, this.scrollList).getTag());
        this.itemList = Lists.newArrayList();
        for ( ItemStack stack : this.scrollList ) this.itemList.add(stack);
        this.pageList = Lists.newArrayList();
        List<ItemStack> page = Lists.newArrayList();
        for ( ItemStack stack : this.itemList ) {
            page.add(stack);
            if ( page.size() == this.maxRows * this.maxColumns * 2 || this.itemList.get(this.itemList.size() - 1) == stack ) {
                this.pageList.add(page);
                page = Lists.newArrayList();
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        buildButtons(minecraft.getWindow().getGuiScaledWidth() / 2, minecraft.getWindow().getGuiScaledHeight() / 2);
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
    }

    private void handleButtonVisibility() {
        handleSlotButtonVisibility();
        handleSwapButtonVisibility();
        handleSelectButtonVisibility();
    }

    private void buildButtons(int x, int y) {
        this.slotButtonList = Lists.newArrayList();
        this.selectButtonList = Lists.newArrayList();
        this.swapButtonMap = new HashMap<>();
        this.upSwapButtonList = Lists.newArrayList();
        this.downSwapButtonList = Lists.newArrayList();
        this.clearWidgets();
        boolean isRightPage = false;
        int row = 0;
        int column = 0;
        for ( int i = 0; i < this.maxRows * this.maxColumns * 2; i++ ) {
            if ( column == this.maxColumns ) {
                row++;
                column = 0;
                if ( row == this.maxRows ) {
                    row = 0;
                    isRightPage = !isRightPage;
                }
            }

            int xPos = isRightPage ? x + (column * this.squareSpacingX) + this.rightButtonOffsetX : x + (column * this.squareSpacingX) + this.leftButtonOffsetX;
            int yPos = y - 74 + (row * this.squareSpacingY);

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

    private void buildSlotButton(int xPos, int yPos) {
        Button button = addRenderableWidget(Button.builder(Component.literal(""), this::handleSlotButton)
                .bounds(xPos - 1, yPos - 1, 18, 18)
                .build());
        this.slotButtonList.add(button);
        buildSelectButton(xPos, yPos);
        if ( (this.slotButtonList.size() - 1) % this.maxColumns == 0 ) {
            buildUpSwapButton(xPos, yPos, button);
            buildDownSwapButton(xPos, yPos + 11, button);
        }
    }

    private void buildSelectButton(int xPos, int yPos) {
        Button button = addRenderableWidget(Button.builder(Component.literal(""), this::handleSelectButton)
                .bounds(xPos - 1 + 24, yPos + 1, 77, 16)
                .build());
        this.selectButtonList.add(button);
    }

    private void handleSelectButton(Button button) {
        if ( !this.selectButtonList.contains(button) ) return;
        int index = this.selectButtonList.indexOf(button) + ((2 * this.maxColumns * this.maxRows) * (this.spreadNumber));
        ModNetwork.sendToServer(new PacketUpdateBookData(this.book, this.scrollList, index));
        this.book.getTag().putInt(SpellBookItem.NBT_KEY_BOOK_SLOT, index);
    }

    private void handleSelectButtonVisibility() {
        for ( Button button : this.selectButtonList ) {
            int stackIndex = this.selectButtonList.indexOf(button) + ((2 * this.maxColumns * this.maxRows) * (this.spreadNumber));
            if ( stackIndex >= this.itemList.size() && button.visible ) button.visible = false;
            else if ( !button.visible ) button.visible = true;
        }
    }

    private void handleSlotButton(Button button) {
        if ( !this.slotButtonList.contains(button) ) return;
        ItemStack stack = getStackFromSlot(button);
        if ( stack.isEmpty() ) return;
        Item item = stack.getItem();
        if ( item instanceof ParchmentItem ) {
            int index = this.scrollList.indexOf(stack);
            ModNetwork.sendToServer(new PacketRemoveScrollFromBook(this.book, this.scrollList, index));
            this.scrollList.remove(index);

            int newSlot = getNewSlotFromScrollRemoval(index, getSelectedSlot());
            this.book.getTag().putInt(SpellBookItem.NBT_KEY_BOOK_SLOT, newSlot);

            createPages(true);
            this.clearWidgets();
            buildButtons(minecraft.getWindow().getGuiScaledWidth() / 2, minecraft.getWindow().getGuiScaledHeight() / 2);
        }
    }

    private void handleSlotButtonVisibility() {
        for ( Button button : this.slotButtonList ) {
            int stackIndex = this.slotButtonList.indexOf(button) + ((2 * this.maxColumns * this.maxRows) * (this.spreadNumber));
            if ( stackIndex >= this.itemList.size() && button.visible ) button.visible = false;
            else if ( !button.visible ) button.visible = true;
        }
    }

    private void buildUpSwapButton(int xPos, int yPos, Button slotButton) {
        if ( this.upSwapButtonList.size() >= this.maxRows ) xPos += this.rightSwapButtonOffsetX;
        else xPos -= this.leftSwapButtonOffsetX;
        Button button = addRenderableWidget(Button.builder(Component.literal(""), this::handleSwapButton)
                .bounds(xPos - 1, yPos - 1, 13, 9)
                .build());
        this.upSwapButtonList.add(button);
        this.swapButtonMap.put(button, slotButton);
    }

    private void buildDownSwapButton(int xPos, int yPos, Button slotButton) {
        if ( this.downSwapButtonList.size() >= this.maxRows ) xPos += this.rightSwapButtonOffsetX;
        else xPos -= this.leftSwapButtonOffsetX;
        Button button = addRenderableWidget(Button.builder(Component.literal(""), this::handleSwapButton)
                .bounds(xPos - 1, yPos - 1, 13, 9)
                .build());
        this.downSwapButtonList.add(button);
        this.swapButtonMap.put(button, slotButton);
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

            ModNetwork.sendToServer(new PacketUpdateBookData(this.book, this.scrollList, newSlot));
            this.book.getTag().putInt(SpellBookItem.NBT_KEY_BOOK_SLOT, newSlot);

            createPages(true);
            this.clearWidgets();
            buildButtons(minecraft.getWindow().getGuiScaledWidth() / 2, minecraft.getWindow().getGuiScaledHeight() / 2);
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
        renderBackground(graphics);
        drawTexture(TEXTURE, x - 140, y - 90, 0, 0, 280, 180, 280, 202, graphics);

        //Arrows
        if ( this.rightArrow.visible ) this.rightArrow.renderTexture(graphics, TEXTURE, x + this.rightArrowOffsetX, y + this.arrowOffsetY,
                0, 180, 10, 18, 10, 280, 202);
        if ( this.leftArrow.visible ) this.leftArrow.renderTexture(graphics, TEXTURE, x + this.leftArrowOffsetX, y + this.arrowOffsetY,
                18, 180, 10, 18, 10, 280, 202);

        boolean spellSelected = false;
        for ( List<ItemStack> page : this.pageList ) {
            if ( this.spreadNumber == this.pageList.indexOf(page) ) {
                boolean isRightPage = false;
                int row = 0;
                int column = 0;
                for ( int i = 0; i < page.size(); i++ ) {
                    ItemStack stack = page.get(i);

                    //Spot calc
                    if ( column == this.maxColumns ) {
                        row++;
                        column = 0;
                        if ( row == this.maxRows ) {
                            row = 0;
                            isRightPage = !isRightPage;
                        }
                    }

                    int xPos = isRightPage ? x + (column * this.squareSpacingX) + this.rightButtonOffsetX - 1 : x + (column * this.squareSpacingX) + this.leftButtonOffsetX;
                    int yPos = y - 74 + (row * this.squareSpacingY);

                    if ( stack.getItem() instanceof ParchmentItem ) {
                        drawTexture(TEXTURE, xPos - 3, yPos - 3, 58, 180, 105, 22, 280, 202, graphics);
                        if ( stack.hasCustomHoverName() ) {
                            Component spellTitle = Component.literal(stack.getHoverName().getString());
                            int titleX = xPos + 61;
                            int titleY = yPos - 5 + this.font.lineHeight;
                            graphics.drawString(this.font, spellTitle, titleX - this.font.width(spellTitle) / 2, titleY, 0, false);
                        }
                        //Selected Spell
                        if ( !spellSelected && !this.book.isEmpty() && this.book.hasTag() && this.book.getTag().contains(SpellBookItem.NBT_KEY_BOOK_SLOT) ) {
                            if ( getSelectedSlot() > -1 && getSelectedSlot() < this.itemList.size() && stack == this.scrollList.get(getSelectedSlot()) ) {
                                drawTexture(TEXTURE, xPos + 19, yPos - 3, 163, 180, 83, 22, 280, 202, graphics);
                                spellSelected = true;
                            }
                        }
                        if ( this.selectButtonList.get(i).isHovered() ) {
                            graphics.fill(RenderType.guiOverlay(), xPos + 22, yPos, xPos + 99, yPos + 16, Integer.MIN_VALUE);
                        }
                    }

                    renderItemWithDecorations(graphics, stack, xPos, yPos);
                    if ( this.slotButtonList.get(i).isHovered() ) {
                        graphics.fill(RenderType.guiOverlay(), xPos, yPos, xPos + 16, yPos + 16, Integer.MAX_VALUE);
                        if ( stack.getItem() instanceof ParchmentItem ) graphics.renderTooltip(this.font, stack, mouseX, mouseY);
                    }

                    //Swap Arrows
                    if ( stack.getItem() instanceof ParchmentItem ) {
                        int index = i / this.maxColumns;
                        int newX = xPos;
                        if ( index >= this.maxRows ) newX += this.rightSwapButtonOffsetX;
                        else newX -= this.leftSwapButtonOffsetX;
                        this.upSwapButtonList.get(index).renderTexture(graphics, TEXTURE, newX - 1, yPos - 1,
                                36, 180, 7, 11, 7, 280, 202);
                        this.downSwapButtonList.get(index).renderTexture(graphics, TEXTURE, newX - 1, yPos + 10,
                                47, 180, 7, 11, 7, 280, 202);
                    }

                    column++;
                }
            }
        }

        //Page number
        for ( int i = 0; i < 2; i++ ) {
            int pageNum = this.spreadNumber * 2 + 1 + i;
            Component pageNumTxt = Component.literal(String.valueOf(pageNum)).setStyle(Style.EMPTY.withBold(true));
            int textX = x - (this.font.width(pageNumTxt) / 2);
            int pageNumXOff = 67;
            int pageNumX = pageNum % 2 == 0 ? textX + pageNumXOff : textX - pageNumXOff;
            graphics.drawString(this.font, pageNumTxt, pageNumX, y + this.arrowOffsetY, 0, false);
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
        int stackIndex = this.slotButtonList.indexOf(button) + ((2 * this.maxColumns * this.maxRows) * (this.spreadNumber));
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
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        Minecraft instance = Minecraft.getInstance();
        if ( instance.player != null ) {
            Player player = instance.player;
            if ( key == instance.options.keyInventory.getKey().getValue() ) {
                player.closeContainer();
                return true;
            }
        }
        return super.keyPressed(key, scanCode, modifiers);
    }
}
