package com.snowbird.snowlib.screens.options;

import com.snowbird.snowlib.GuiColorScheme;
import com.snowbird.snowlib.screens.options.entries.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A scrollable list that can contain various "Entry" objects:
 * text fields, checkboxes, dropdowns, tables, etc.
 *
 * Each Entry is drawn in a row, but individual entries can have custom heights.
 */
public class OptionsList extends AbstractSelectionList<OptionsList.Entry> {

	private static final int SCROLLBAR_WIDTH = 6;
	private static final int MARGIN = 10; // Increased from 6 to 10 for better padding

	/**
	 * Color scheme used by this OptionsList (and all its entries).
	 * Defaults to a new GuiColorScheme instance, but can be overridden
	 * by the owning screen.
	 */
	private GuiColorScheme colorScheme = new GuiColorScheme();

	public OptionsList(Minecraft mc, int width, int height, int top, int bottom, int itemHeight) {
		super(mc, width, height, top, bottom, itemHeight);
	}

	/**
	 * Allows the parent screen to override the color scheme for this OptionsList.
	 */
	public void setColorScheme(GuiColorScheme colorScheme) {
		this.colorScheme = colorScheme;
	}

	public GuiColorScheme getColorScheme() {
		return this.colorScheme;
	}

	@Override
	public int addEntry(Entry entry) {
		int index = super.addEntry(entry);

		// If this entry is a TableEntry, set its children's parent list
		if (entry instanceof TableEntry tableEntry) {
			for (List<Entry> row : tableEntry.tableEntries) {
				for (Entry cellEntry : row) {
					cellEntry.setParentList(this);
				}
			}
		} else {
			entry.setParentList(this);
		}
		return index;
	}

	@Override
	public int getRowWidth() {
		return this.width - SCROLLBAR_WIDTH - (2 * MARGIN);
	}

	@Override
	protected int getScrollbarPosition() {
		return this.x1 - SCROLLBAR_WIDTH;
	}

	@Override
	public int getRowLeft() {
		return this.x0 + MARGIN;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		// Render any open dropdown sub-lists above everything else
		for (Entry entry : getAllEntries()) {
			if (entry instanceof ScrollableDropdownEntry dropdownEntry && dropdownEntry.isDropdownOpen()) {
				guiGraphics.pose().pushPose();
				guiGraphics.pose().translate(0, 0, 200);  // render above other components
				dropdownEntry.renderDropdown(guiGraphics, mouseX, mouseY, partialTicks);
				guiGraphics.pose().popPose();
			} else if (entry instanceof MultiSelectPicklistEntry<?> picklistEntry && picklistEntry.isDropdownOpen()) {
				guiGraphics.pose().pushPose();
				guiGraphics.pose().translate(0, 0, 200);
				picklistEntry.renderDropdown(guiGraphics, mouseX, mouseY, partialTicks);
				guiGraphics.pose().popPose();
			}
		}
	}

	protected int getRowHeight(int index) {
		Entry entry = this.children().get(index);
		return entry.getHeight();
	}

	@Override
	protected int getMaxPosition() {
		int totalHeight = this.headerHeight;
		for (int i = 0; i < this.getItemCount(); i++) {
			totalHeight += this.getRowHeight(i);
		}
		return totalHeight;
	}

	@Override
	public int getRowTop(int index) {
		int y = this.y0 + this.headerHeight - (int) this.getScrollAmount();
		for (int i = 0; i < index; i++) {
			y += this.getRowHeight(i);
		}
		return y;
	}

	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {
		// Implement narration logic if needed
	}

	@Override
	protected void renderList(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		int rowLeft = this.getRowLeft();
		int rowWidth = this.getRowWidth();

		for (int i = 0; i < this.getItemCount(); ++i) {
			int entryTop = this.getRowTop(i);
			int entryHeight = this.getRowHeight(i);
			int entryBottom = entryTop + entryHeight;

			if (entryBottom >= this.y0 && entryTop <= this.y1) {
				Entry entry = this.getEntry(i);
				int relMouseY = mouseY - entryTop;
				boolean hovered = mouseX >= rowLeft && mouseX <= rowLeft + rowWidth && relMouseY >= 0 && relMouseY < entryHeight;

				guiGraphics.pose().pushPose();
				guiGraphics.pose().translate(0.0F, 0.0F, 0.0F);
				entry.render(guiGraphics, i, entryTop, rowLeft, rowWidth, entryHeight, mouseX, mouseY, hovered, partialTicks);
				guiGraphics.pose().popPose();
			}
		}
	}

	@Override
	public int getRowBottom(int index) {
		return this.getRowTop(index) + this.getRowHeight(index);
	}

	/**
	 * Convenient method to retrieve all entries including table sub-entries.
	 */
	public List<Entry> getAllEntries() {
		List<Entry> allEntries = new ArrayList<>();
		for (Entry entry : this.children()) {
			allEntries.add(entry);
			if (entry instanceof TableEntry tableEntry) {
				for (List<Entry> row : tableEntry.tableEntries) {
					allEntries.addAll(row);
				}
			}
		}
		return allEntries;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		// First, check if the click is on any open dropdown
		for (Entry entry : getAllEntries()) {
			if (entry instanceof ScrollableDropdownEntry dropdownEntry && dropdownEntry.isDropdownOpen()) {
				if (dropdownEntry.handleDropdownClick(mouseX, mouseY, button)) {
					return true;
				}
			} else if (entry instanceof MultiSelectPicklistEntry<?> picklistEntry && picklistEntry.isDropdownOpen()) {
				if (picklistEntry.handleDropdownClick(mouseX, mouseY, button)) {
					return true;
				}
			}
		}

		// Then check if the click is on any entry (including a dropdown button)
		for (Entry entry : getAllEntries()) {
			if (entry.isMouseOver(mouseX, mouseY)) {
				getAllEntries().forEach(e -> e.setFocused(false));
				entry.setFocused(true);
				if (entry.mouseClicked(mouseX, mouseY, button)) {
					return true;
				}
			}
		}

		// If the click was not on any entry or open dropdown, close all dropdowns
		closeAllDropdowns();
		getAllEntries().forEach(e -> e.setFocused(false));

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		Entry hoveredEntry = getEntryUnderMouse(mouseX, mouseY);
		if (hoveredEntry != null && hoveredEntry.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		Entry hoveredEntry = getEntryUnderMouse(mouseX, mouseY);
		if (hoveredEntry != null && hoveredEntry.mouseReleased(mouseX, mouseY, button)) {
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta, double scrollFactor) {
		// If there's an open dropdown and the mouse is over it, pass the scroll to the dropdown
		for (Entry entry : getAllEntries()) {
			if (entry instanceof ScrollableDropdownEntry dropdownEntry && dropdownEntry.isDropdownOpen()) {
				if (dropdownEntry.handleDropdownScroll(mouseX, mouseY, scrollDelta, scrollFactor)) {
					return true;
				}
			} else if (entry instanceof MultiSelectPicklistEntry<?> picklistEntry && picklistEntry.isDropdownOpen()) {
				if (picklistEntry.handleDropdownScroll(mouseX, mouseY, scrollDelta, scrollFactor)) {
					return true;
				}
			}
		}
		// Otherwise, scroll the main list
		return super.mouseScrolled(mouseX, mouseY, scrollDelta, scrollFactor);
	}

	private Entry getEntryUnderMouse(double mouseX, double mouseY) {
		for (Entry entry : this.children()) {
			if (entry.isMouseOver(mouseX, mouseY)) {
				return entry;
			}
		}
		return null;
	}

	private void closeAllDropdowns() {
		for (Entry entry : getAllEntries()) {
			if (entry instanceof ScrollableDropdownEntry scrollableDropdownEntry) {
				scrollableDropdownEntry.setDropdownVisibility(false);
			} else if (entry instanceof MultiSelectPicklistEntry picklistEntry) {
				picklistEntry.setDropdownVisibility(false);
			}
		}
	}

	public void closeAllDropdownsExcept(Entry exceptEntry) {
		for (Entry entry : getAllEntries()) {
			if (entry != exceptEntry) {
				if (entry instanceof ScrollableDropdownEntry scrollableDropdownEntry) {
					scrollableDropdownEntry.setDropdownVisibility(false);
				} else if (entry instanceof MultiSelectPicklistEntry picklistEntry) {
					picklistEntry.setDropdownVisibility(false);
				}
			}
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		for (Entry entry : getAllEntries()) {
			if (entry.isFocused()) {
				if (entry.keyPressed(keyCode, scanCode, modifiers)) {
					return true;
				}
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		for (Entry entry : getAllEntries()) {
			if (entry.isFocused()) {
				if (entry.charTyped(codePoint, modifiers)) {
					return true;
				}
			}
		}
		return super.charTyped(codePoint, modifiers);
	}

	// ---------------------- Add Entry Helpers ----------------------
	public void addCheckboxOption(String name, String description, boolean initialValue, Consumer<Boolean> onChange) {
		this.addEntry(new CheckboxEntry(name, description, initialValue, onChange));
	}

	public void addTextFieldOption(String name, String description, String initialValue, Consumer<String> onChange) {
		this.addEntry(new TextFieldEntry(name, description, initialValue, onChange));
	}

	public void addNumberFieldOption(String name, String description, long initialValue, Consumer<Long> onChange) {
		this.addEntry(new NumberFieldEntry(name, description, initialValue, onChange));
	}

	public void addScrollableDropdownOption(String name, String description, String initialValue, String[] options, Consumer<String> onChange) {
		this.addEntry(new ScrollableDropdownEntry(name, description, initialValue, options, onChange));
	}

	public void addTableOption(String name, String description,
							   List<List<Entry>> tableEntries,
							   List<String> columnHeaders,
							   List<String> rowHeaders,
							   int cellHeight,
							   List<Integer> columnWidths,
							   int headerWidth) {
		this.addEntry(new TableEntry(name, description,
			tableEntries, columnHeaders, rowHeaders,
			cellHeight, columnWidths, headerWidth));
	}

	public <T extends Enum<T>> void addMultiSelectPicklistOption(String title, String description, Set<T> selected, T[] options, Consumer<Set<T>> onChanged) {
		this.addEntry(new MultiSelectPicklistEntry<>(title, description, selected, options, onChanged));
	}


	// ---------------------- Nested Abstract Entry ----------------------
	public abstract static class Entry extends AbstractSelectionList.Entry<Entry> {
		private final String name;
		private final String description;
		protected boolean inTable = false;
		protected OptionsList parentList;

		protected int left;
		protected int top;
		protected int width;
		protected int height = 45; // default for non-table rows

		private boolean focused = false;

		public Entry(String name, String description) {
			this.name = name;
			this.description = description;
		}

		public void setInTable(boolean inTable) {
			this.inTable = inTable;
		}

		public void setPositionAndSize(int left, int top, int width, int height) {
			this.left = left;
			this.top = top;
			this.width = width;
			this.height = height;
		}

		public void setParentList(OptionsList parentList) {
			this.parentList = parentList;
		}

		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			return mouseX >= this.left && mouseX < this.left + this.width
				&& mouseY >= this.top && mouseY < this.top + this.height;
		}

		@Override
		public void setFocused(boolean focused) {
			this.focused = focused;
		}

		@Override
		public boolean isFocused() {
			return this.focused;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public int getHeight() {
			return this.height;
		}

		@Override
		public void render(
			GuiGraphics guiGraphics,
			int index,
			int top,
			int left,
			int width,
			int height,
			int mouseX,
			int mouseY,
			boolean hovered,
			float partialTicks
		) {
			this.setPositionAndSize(left, top, width, height);

			if (!this.inTable) {
				// Use the parent's color scheme
				GuiColorScheme scheme = (this.parentList != null)
					? this.parentList.getColorScheme()
					: new GuiColorScheme(); // fallback if somehow null

				if (this.isFocused()) {
					// "selected/focused" background
					guiGraphics.fill(left, top, left + width, top + height, scheme.getSelectedColor());
				} else if (hovered) {
					// "hover" background
					guiGraphics.fill(left, top, left + width, top + height, scheme.getHoveredColor());

					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(0, 0, 300);
					// Tooltip
					guiGraphics.renderTooltip(
						this.parentList.minecraft.font,
						Component.literal(this.getDescription()),
						mouseX,
						mouseY
					);
					guiGraphics.pose().popPose();
				}

				guiGraphics.drawString(
					this.parentList.minecraft.font,
					this.getName(),
					left + 5,
					top + 5,
					0xFFFFFF
				);
			}
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			return false;
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			return false;
		}

		@Override
		public boolean charTyped(char codePoint, int modifiers) {
			return false;
		}
	}
}
