package com.snowbird.snowlib.screens;

import com.snowbird.snowlib.ScreenRegistry;
import com.snowbird.snowlib.screens.options.dropdown.AbstractDropdownList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

/**
 * A "master" config screen that shows all registered config screens as Chrome-style tabs:
 *  - The child screens fill the entire window from y=0 to bottom.
 *  - The tabs are drawn on top at a higher Z-level, so they appear above the child screen.
 *  - Child screens can be constructed with an empty Component to remove their title.
 *  - Scrollable lists (like OptionsList) or picklists use normal scissoring (offset=0).
 */
public class MasterConfigScreen extends Screen {

	// We dynamically set each tab’s width based on text, plus padding
	private static final int TAB_HORIZONTAL_PADDING = 16;
	// Vertical padding above/below text
	private static final int TAB_VERTICAL_PADDING   = 8;
	// Spacing between tabs
	private static final int TAB_SPACING = 3;
	// How slanted the corners are
	private static final int TAB_CORNER_SLOPE = 8;

	private final List<Integer> tabWidths = new ArrayList<>();
	private int tabHeight;

	// The list of actual child screens
	private final List<Screen> childScreens = new ArrayList<>();
	// Index of which tab/screen is currently selected
	private int selectedTabIndex = 0;

	public MasterConfigScreen() {
		// We can still keep a master title if you want, or use Component.empty() to hide it
		super(Component.literal("Master Configuration"));
	}

	@Override
	protected void init() {
		// Gather all mod-registered screens
		List<ScreenRegistry.ConfigScreenRegistration> regs = ScreenRegistry.getRegisteredScreens();
		this.childScreens.clear();
		this.tabWidths.clear();

		// For each registration, create the child screen
		for (ScreenRegistry.ConfigScreenRegistration reg : regs) {
			Screen child = reg.getScreenSupplier().get();
			this.childScreens.add(child);
		}

		// Compute each tab's width from the title text
		for (ScreenRegistry.ConfigScreenRegistration reg : regs) {
			int textWidth = this.font.width(reg.getTitle());
			int widthWithPadding = textWidth + TAB_HORIZONTAL_PADDING;
			this.tabWidths.add(widthWithPadding);
		}

		// Determine the tab's total height from the font lineHeight
		this.tabHeight = this.font.lineHeight + TAB_VERTICAL_PADDING;

		// Default to the first tab if available
		if (!childScreens.isEmpty()) {
			selectedTabIndex = Mth.clamp(selectedTabIndex, 0, childScreens.size() - 1);
			initChildScreen(selectedTabIndex);
		}
	}

	/**
	 * We let the child fill the entire window. Then, we draw the tabs on top (higher Z).
	 */
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		// Standard MC background
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		List<ScreenRegistry.ConfigScreenRegistration> regs = ScreenRegistry.getRegisteredScreens();
		if (regs.isEmpty()) {
			// If no screens
			guiGraphics.drawCenteredString(
				this.font,
				Component.literal("No config screens registered."),
				this.width / 2,
				this.height / 2,
				0xFFFFFF
			);
			return;
		}

		// Let the child fill the entire area (y=0..this.height)
		// We do not shift the child down. That means offset=0 for scissoring.
		if (selectedTabIndex >= 0 && selectedTabIndex < childScreens.size()) {
			Screen child = childScreens.get(selectedTabIndex);
			if (child != null) {
				// Because we are not shifting the child down, we set global offset to 0
				AbstractDropdownList.globalRenderOffsetY = 0;

				// Render the child first
				child.render(guiGraphics, mouseX, mouseY, partialTicks);

				// Reset
				AbstractDropdownList.globalRenderOffsetY = 0;
			}
		}

		// Now draw the tabs on top, at a higher Z-level
		guiGraphics.pose().pushPose();
		// Push them "above" the child
		guiGraphics.pose().translate(0, 0, 400);
		renderTabs(guiGraphics);
		guiGraphics.pose().popPose();
	}

	/**
	 * Initialize the child screen at the given index to match the full window size.
	 */
	private void initChildScreen(int index) {
		if (index < 0 || index >= childScreens.size()) return;
		Screen child = childScreens.get(index);
		if (child != null) {
			// Use the full width & height
			child.init(this.minecraft, this.width, this.height);
		}
	}

	/**
	 * Draw all tabs (Chrome-like) at top with black fill, sloped corners, dynamic widths, etc.
	 * The selected tab is drawn last to appear above the other tabs without altering its position.
	 */
	private void renderTabs(GuiGraphics guiGraphics) {
		List<ScreenRegistry.ConfigScreenRegistration> regs = ScreenRegistry.getRegisteredScreens();
		int totalTabs = regs.size();
		if (totalTabs == 0) return;

		// Calculate total width including spacing
		int totalWidth = 0;
		for (int i = 0; i < totalTabs; i++) {
			totalWidth += tabWidths.get(i);
			if (i < totalTabs - 1) {
				totalWidth += TAB_SPACING;
			}
		}

		int xStart = (this.width - totalWidth) / 2;
		int yStart = 0; // Already adjusted to move the tab bar upwards

		// Store x positions for each tab
		List<Integer> tabXPositions = new ArrayList<>();
		for (int i = 0; i < totalTabs; i++) {
			tabXPositions.add(xStart);
			xStart += tabWidths.get(i) + TAB_SPACING;
		}

		// First, render all unselected tabs
		for (int i = 0; i < totalTabs; i++) {
			if (i == selectedTabIndex) continue; // Skip the selected tab for now
			boolean isSelected = false;
			int tabWidth = tabWidths.get(i);
			int x = tabXPositions.get(i);
			int y = yStart;

			// Draw the unselected tab
			drawChromeTab(guiGraphics, x, y, tabWidth, this.tabHeight, isSelected);

			// Draw the tab text
			Component tabText = regs.get(i).getTitle();
			int textWidth = this.font.width(tabText);
			int textX = x + (tabWidth - textWidth) / 2;
			int textY = y + (this.tabHeight - this.font.lineHeight) / 2;
			guiGraphics.drawString(this.font, tabText, textX, textY, 0xFFFFFFFF);
		}

		// Now, render the selected tab last
		if (selectedTabIndex >= 0 && selectedTabIndex < totalTabs) {
			int i = selectedTabIndex;
			boolean isSelected = true;
			int tabWidth = tabWidths.get(i);
			int x = tabXPositions.get(i);
			int y = yStart;

			// Draw the selected tab without altering its position
			drawChromeTab(guiGraphics, x, y, tabWidth, this.tabHeight, isSelected);

			// Draw the tab text
			Component tabText = regs.get(i).getTitle();
			int textWidth = this.font.width(tabText);
			int textX = x + (tabWidth - textWidth) / 2;
			int textY = y + (this.tabHeight - this.font.lineHeight) / 2;
			guiGraphics.drawString(this.font, tabText, textX, textY, 0xFFFFFFFF);
		}
	}

	/**
	 * A single "Chrome" tab: black with outward sloping corners,
	 * or a slightly lighter fill if selected, with a white border.
	 */
	private void drawChromeTab(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean selected) {
		// Define colors based on selection
		int fillColor = selected ? 0xFF505050 : 0xFF000000; // Lighter gray for selected, black for others
		int borderColor = selected ? 0xFFFFFFFF : 0xFFAAAAAA; // White border for selected, gray for others

		// Draw the tab background with outward sloping corners
		for (int row = 0; row < TAB_CORNER_SLOPE; row++) {
			int left = x - row;
			int right = x + width + row;
			guiGraphics.fill(left, y + row, right, y + row + 1, fillColor);
		}
		int rectTop = y + TAB_CORNER_SLOPE;
		int rectBottom = y + height;
		guiGraphics.fill(x - TAB_CORNER_SLOPE, rectTop, x + width + TAB_CORNER_SLOPE, rectBottom, fillColor);

		// Draw the outline
		for (int row = 0; row < TAB_CORNER_SLOPE; row++) {
			int left = x - row;
			int right = x + width + row;
			// Left border pixel
			guiGraphics.fill(left, y + row, left + 1, y + row + 1, borderColor);
			// Right border pixel
			guiGraphics.fill(right - 1, y + row, right, y + row + 1, borderColor);
		}
		// Bottom border
		guiGraphics.fill(x - TAB_CORNER_SLOPE, rectBottom - 1, x + width + TAB_CORNER_SLOPE, rectBottom, borderColor);
		// Left vertical border
		guiGraphics.fill(x - TAB_CORNER_SLOPE, rectTop, x - TAB_CORNER_SLOPE + 1, rectBottom, borderColor);
		// Right vertical border
		guiGraphics.fill(x + width + TAB_CORNER_SLOPE - 1, rectTop, x + width + TAB_CORNER_SLOPE, rectBottom, borderColor);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		List<ScreenRegistry.ConfigScreenRegistration> regs = ScreenRegistry.getRegisteredScreens();
		if (regs.isEmpty()) {
			return super.mouseClicked(mouseX, mouseY, button);
		}
		// Check if a tab was clicked
		int totalTabs = regs.size();
		int totalWidth = 0;
		for (int i = 0; i < totalTabs; i++) {
			totalWidth += tabWidths.get(i);
			if (i < totalTabs - 1) {
				totalWidth += TAB_SPACING;
			}
		}
		int xStart = (this.width - totalWidth) / 2;
		int yStart = 0; // Adjusted to match renderTabs yStart

		for (int i = 0; i < totalTabs; i++) {
			int w = tabWidths.get(i);
			if (mouseX >= xStart && mouseX < xStart + w && mouseY >= yStart && mouseY < yStart + tabHeight) {
				if (selectedTabIndex != i) {
					selectedTabIndex = i;
					initChildScreen(i);
				}
				return true;
			}
			xStart += w + TAB_SPACING;
		}

		// Pass to child if not on a tab
		if (selectedTabIndex >= 0 && selectedTabIndex < childScreens.size()) {
			Screen child = childScreens.get(selectedTabIndex);
			if (child != null) {
				if (child.mouseClicked(mouseX, mouseY, button)) {
					return true;
				}
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (selectedTabIndex >= 0 && selectedTabIndex < childScreens.size()) {
			Screen child = childScreens.get(selectedTabIndex);
			if (child != null && child.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
				return true;
			}
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (selectedTabIndex >= 0 && selectedTabIndex < childScreens.size()) {
			Screen child = childScreens.get(selectedTabIndex);
			if (child != null && child.mouseReleased(mouseX, mouseY, button)) {
				return true;
			}
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta, double scrollFactor) {
		if (selectedTabIndex >= 0 && selectedTabIndex < childScreens.size()) {
			Screen child = childScreens.get(selectedTabIndex);
			if (child != null && child.mouseScrolled(mouseX, mouseY, scrollDelta, scrollFactor)) {
				return true;
			}
		}
		return super.mouseScrolled(mouseX, mouseY, scrollDelta, scrollFactor);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
