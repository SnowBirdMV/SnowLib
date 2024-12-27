package com.snowbird.snowlib.screens.options.dropdown;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.util.Mth;

/**
 * A base class for a scrollable dropdown list that automatically accounts
 * for a global Y offset (e.g., your entire screen is drawn below a tab bar).
 */
public abstract class AbstractDropdownList<T, E extends AbstractDropdownList<T, E>.Entry>
	extends AbstractSelectionList<E> {

	public static int globalRenderOffsetY = 0;
	protected final int maxVisibleOptions = 5;

	public AbstractDropdownList(int width, int itemHeight) {
		super(Minecraft.getInstance(), width, 0, 0, 0, itemHeight);
	}

	public void setPosition(int left, int top, int width, int height) {
		this.x0 = left;
		this.x1 = left + width;
		this.y0 = top;
		this.y1 = top + height;
		this.width = width;
		this.height = height;
	}

	@Override
	protected int getScrollbarPosition() {
		return this.x1 - 6;
	}

	@Override
	public int getRowWidth() {
		return this.width - 8;
	}

	@Override
	public int getRowTop(int index) {
		return this.y0 + index * this.itemHeight - (int)this.getScrollAmount();
	}

	@Override
	public int getRowBottom(int index) {
		return this.getRowTop(index) + this.itemHeight;
	}

	@Override
	protected int getMaxPosition() {
		return this.getItemCount() * this.itemHeight;
	}

	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {
		// no-op
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		// clamp scroll
		this.setScrollAmount(Mth.clamp(this.getScrollAmount(), 0, this.getMaxScroll()));

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(0, 0, 400); // draw on top

		// Overall semi-transparent background behind the dropdown
		int bgColor = 0xCC000000; // 80% black
		guiGraphics.fill(this.x0, this.y0, this.x1, this.y1, bgColor);

		// Enable scissor
		enableScissor(this.x0, this.y0, this.width, this.height, globalRenderOffsetY);

		// Render the list content
		this.renderList(guiGraphics, mouseX, mouseY, partialTicks);

		disableScissor();

		// White border
		int borderColor = 0xFFFFFFFF;
		guiGraphics.fill(this.x0, this.y0, this.x1, this.y0 + 1, borderColor);
		guiGraphics.fill(this.x0, this.y1 - 1, this.x1, this.y1, borderColor);
		guiGraphics.fill(this.x0, this.y0, this.x0 + 1, this.y1, borderColor);
		guiGraphics.fill(this.x1 - 1, this.y0, this.x1, this.y1, borderColor);

		// Scrollbar
		this.renderScrollbar(guiGraphics);

		guiGraphics.pose().popPose();
	}

	@Override
	protected void renderList(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		int rowLeft = this.x0;
		int rowWidth = this.getRowWidth();

		for (int i = 0; i < this.getItemCount(); ++i) {
			int entryTop = this.getRowTop(i);
			int entryHeight = this.itemHeight;
			int entryBottom = entryTop + entryHeight;

			if (entryBottom > this.y0 && entryTop < this.y1) {
				E entry = this.getEntry(i);
				boolean hovered = (mouseX >= rowLeft && mouseX < rowLeft + rowWidth
					&& mouseY >= entryTop && mouseY < entryBottom);

				guiGraphics.pose().pushPose();
				entry.render(guiGraphics, i, entryTop, rowLeft, rowWidth, entryHeight,
					mouseX, mouseY, hovered, partialTicks);
				guiGraphics.pose().popPose();
			}
		}
	}

	protected void renderScrollbar(GuiGraphics guiGraphics) {
		int maxScroll = this.getMaxScroll();
		if (maxScroll <= 0) {
			return;
		}

		int scrollbarPositionMinX = this.getScrollbarPosition();
		int scrollbarPositionMaxX = scrollbarPositionMinX + 6;
		int listHeight = this.height;

		int scrollbarHeight = (int) ((float)(listHeight * listHeight) / (float)this.getMaxPosition());
		scrollbarHeight = Mth.clamp(scrollbarHeight, 32, listHeight - 8);

		int scrollOffset = (int)this.getScrollAmount();
		int scrollbarTop = scrollOffset * (listHeight - scrollbarHeight) / maxScroll + this.y0;
		scrollbarTop = Math.max(this.y0, scrollbarTop);
		int scrollbarBottom = scrollbarTop + scrollbarHeight;

		guiGraphics.fill(scrollbarPositionMinX, this.y0, scrollbarPositionMaxX, this.y1, 0xFF000000);
		guiGraphics.fill(scrollbarPositionMinX, scrollbarTop, scrollbarPositionMaxX, scrollbarBottom, 0xFFA0A0A0);
		guiGraphics.fill(scrollbarPositionMinX, scrollbarTop, scrollbarPositionMaxX - 1, scrollbarBottom - 1, 0xFF808080);
	}

	private void enableScissor(int x, int y, int width, int height, int offsetY) {
		Minecraft mc = Minecraft.getInstance();
		double scale = mc.getWindow().getGuiScale();

		int scissorX = (int)(x * scale);
		int scissorY = (int)(mc.getWindow().getHeight() - ((y + offsetY) + height) * scale);
		int scissorWidth  = (int)(width  * scale);
		int scissorHeight = (int)(height * scale);

		RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
	}

	private void disableScissor() {
		RenderSystem.disableScissor();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (mouseX < this.x0 || mouseX >= this.x1 || mouseY < this.y0 || mouseY >= this.y1) {
			return false;
		}
		E clickedEntry = this.getEntryUnderMouse(mouseX, mouseY);
		if (clickedEntry != null) {
			return clickedEntry.mouseClicked(mouseX, mouseY, button);
		}
		return true;
	}

	private E getEntryUnderMouse(double mouseX, double mouseY) {
		for (int i = 0; i < this.getItemCount(); i++) {
			int entryTop = getRowTop(i);
			int entryBottom = entryTop + this.itemHeight;
			if (mouseY >= entryTop && mouseY < entryBottom) {
				return this.getEntry(i);
			}
		}
		return null;
	}

	public abstract class Entry extends AbstractSelectionList.Entry<E> {
		protected final T option;
		protected final AbstractDropdownList<T, E> parentList;

		public Entry(T option) {
			if (option == null) {
				throw new IllegalArgumentException("Option cannot be null");
			}
			this.option = option;
			this.parentList = AbstractDropdownList.this;
		}
	}
}
