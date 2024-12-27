package com.snowbird.snowlib.screens.categories;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

/**
 * A scrollable list of Categories, typically placed on the left side of a config screen.
 */
public class CategoryList extends AbstractSelectionList<CategoryList.CategoryEntry> {
	private final Consumer<Category> categorySelectionConsumer;

	public CategoryList(
		Minecraft mc,
		int width,
		int height,
		int top,
		int bottom,
		int itemHeight,
		List<Category> categories,
		Consumer<Category> categorySelectionConsumer
	) {
		super(mc, width, height, top, bottom, itemHeight);
		this.categorySelectionConsumer = categorySelectionConsumer;

		for (Category category : categories) {
			this.addEntry(new CategoryEntry(category));
		}
	}

	@Override
	protected int getScrollbarPosition() {
		return this.width - 6;
	}

	@Override
	public int getRowWidth() {
		return this.width - 12;
	}

	@Override
	public void setSelected(CategoryEntry entry) {
		super.setSelected(entry);
		categorySelectionConsumer.accept(entry.getCategory());
	}

	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {
		// Provide narration logic if needed
	}

	public class CategoryEntry extends AbstractSelectionList.Entry<CategoryEntry> {
		private final Category category;

		public CategoryEntry(Category category) {
			this.category = category;
		}

		public Category getCategory() {
			return this.category;
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
			// Calculate vertically centered Y position
			int textY = top + (height - CategoryList.this.minecraft.font.lineHeight) / 2;

			// Draw the category name with additional left padding
			guiGraphics.drawString(
				CategoryList.this.minecraft.font,
				this.category.getName(),
				left + 10, // Increased from 5 to 10 for more left padding
				textY,
				0xFFFFFF
			);

			// Highlight hovered category with semi-transparent background
			if (hovered) {
				guiGraphics.fill(left, top, left + width, top + height, 0xAA444444); // Semi-transparent highlight
			}
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			CategoryList.this.setSelected(this);
			return true;
		}
	}
}
