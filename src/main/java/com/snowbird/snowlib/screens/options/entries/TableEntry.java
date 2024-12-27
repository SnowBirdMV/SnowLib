package com.snowbird.snowlib.screens.options.entries;

import com.snowbird.snowlib.screens.options.OptionsList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

/**
 * A table of rows & columns, each cell containing an OptionsList.Entry.
 */
public class TableEntry extends OptionsList.Entry {
	public final List<List<OptionsList.Entry>> tableEntries;
	public final List<String> rowHeaders;
	public final List<String> columnHeaders;
	public final List<Integer> columnWidths;
	public final int cellHeight;
	public final int headerWidth;

	public TableEntry(
		String name,
		String description,
		List<List<OptionsList.Entry>> tableEntries,
		List<String> columnHeaders,
		List<String> rowHeaders,
		int cellHeight,
		List<Integer> columnWidths,
		int headerWidth
	) {
		super(name, description);
		this.tableEntries = tableEntries;
		this.rowHeaders = rowHeaders;
		this.columnHeaders = columnHeaders;
		this.cellHeight = cellHeight;
		this.columnWidths = columnWidths;
		this.headerWidth = headerWidth;
		this.height = this.getHeight();

		// Mark all children as inTable = true
		for (List<OptionsList.Entry> row : tableEntries) {
			for (OptionsList.Entry cellEntry : row) {
				cellEntry.setInTable(true);
			}
		}
	}

	@Override
	public int getHeight() {
		// one row for the header + rowHeaders. Then each row in table
		return cellHeight + (tableEntries.size() * cellHeight);
	}

	@Override
	public void render(
		GuiGraphics guiGraphics,
		int index,
		int top,
		int ignoredLeft,
		int ignoredWidth,
		int height,
		int mouseX,
		int mouseY,
		boolean hovered,
		float partialTicks
	) {
		// Get the parent list to figure out positioning
		OptionsList optionsList = (OptionsList) this.list;
		int rowLeft = optionsList.getRowLeft();
		int rowWidth = optionsList.getRowWidth();
		int margin = 5;
		int tableLeft = rowLeft + margin;

		int numberOfColumns = tableEntries.get(0).size();
		int remainder = rowWidth - margin * 2
			- columnWidths.stream().mapToInt(Integer::intValue).sum()
			- headerWidth;

		int tableWidth  = columnWidths.stream().mapToInt(Integer::intValue).sum() + remainder;
		int tableHeight = cellHeight * tableEntries.size();
		int tableTop    = top + cellHeight;

		// Column header text vertical position
		int colHeaderTextY = top + (cellHeight - Minecraft.getInstance().font.lineHeight) / 2 + 2;
		// The table's name in the "headerWidth" zone
		int tableHeaderTextX = rowLeft + (headerWidth - Minecraft.getInstance().font.width(this.getName())) / 2;

		// Draw the table "name" in the row header area
		guiGraphics.drawString(
			Minecraft.getInstance().font,
			this.getName(),
			tableHeaderTextX,
			colHeaderTextY,
			0xFFFFFF
		);

		// Column headers
		int currentXOffset = headerWidth + tableLeft;
		for (int i = 0; i < columnHeaders.size(); i++) {
			if (i > 0) {
				currentXOffset += columnWidths.get(i - 1);
				if (i - 1 == columnWidths.size() - 1) {
					currentXOffset += remainder;
				}
			}
			int currentColumnWidth = columnWidths.get(i);
			if (i == columnHeaders.size() - 1) {
				currentColumnWidth += remainder;
			}
			int textWidth = Minecraft.getInstance().font.width(columnHeaders.get(i));
			int textX = currentXOffset + (currentColumnWidth - textWidth) / 2;

			guiGraphics.drawString(
				Minecraft.getInstance().font,
				columnHeaders.get(i),
				textX,
				colHeaderTextY,
				0xFFFFFF
			);
		}

		// Row headers
		for (int i = 0; i < rowHeaders.size(); i++) {
			int rowHeaderTextX = rowLeft + (headerWidth - Minecraft.getInstance().font.width(rowHeaders.get(i))) / 2;
			int textY = top + ((i + 1) * cellHeight) + (cellHeight - Minecraft.getInstance().font.lineHeight) / 2 + 2;
			guiGraphics.drawString(
				Minecraft.getInstance().font,
				rowHeaders.get(i),
				rowHeaderTextX,
				textY,
				0xFFFFFF
			);
		}

		// Render the table entries
		int yOffset = tableTop;
		for (List<OptionsList.Entry> row : tableEntries) {
			int xOffset = tableLeft + headerWidth;
			for (int i = 0; i < row.size(); i++) {
				OptionsList.Entry cellEntry = row.get(i);
				int currentCellWidth = columnWidths.get(i);
				if (i == row.size() - 1) {
					currentCellWidth += remainder;
				}
				boolean cellHovered = mouseX >= xOffset && mouseX < xOffset + currentCellWidth
					&& mouseY >= yOffset && mouseY < yOffset + cellHeight;
				cellEntry.setPositionAndSize(xOffset, yOffset, currentCellWidth, cellHeight);
				cellEntry.render(guiGraphics, index, yOffset, xOffset, currentCellWidth, cellHeight,
					mouseX, mouseY, cellHovered, partialTicks);
				xOffset += currentCellWidth;
			}
			yOffset += cellHeight;
		}

		// Draw lines / borders
		int borderColor = 0xFFFFFFFF;
		int cellBorderColor = 0xFF808080;
		int borderThickness = 2;

		// Vertical lines between columns
		int xOffset = tableLeft + headerWidth;
		for (int i = 0; i < numberOfColumns - 1; i++) {
			xOffset += columnWidths.get(i);
			guiGraphics.fill(xOffset, top, xOffset + 1, tableTop + tableHeight, cellBorderColor);
		}

		// Horizontal lines between rows
		for (int i = 1; i < tableEntries.size(); i++) {
			int y = tableTop + (i * cellHeight);
			guiGraphics.fill(tableLeft, y, tableLeft + tableWidth + headerWidth, y + 1, cellBorderColor);
		}

		// Outer border
		// Top
		guiGraphics.fill(tableLeft - borderThickness, tableTop - borderThickness,
			tableLeft + tableWidth + borderThickness + headerWidth, tableTop,
			borderColor);
		// Bottom
		guiGraphics.fill(tableLeft - borderThickness, tableTop + tableHeight,
			tableLeft + tableWidth + borderThickness + headerWidth, tableTop + tableHeight + borderThickness,
			borderColor);
		// Left
		guiGraphics.fill(tableLeft - borderThickness + headerWidth, top,
			tableLeft + headerWidth, tableTop + tableHeight,
			borderColor);
		// Right
		guiGraphics.fill(tableLeft + tableWidth + headerWidth, tableTop,
			tableLeft + tableWidth + borderThickness + headerWidth, tableTop + tableHeight,
			borderColor);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (List<OptionsList.Entry> row : tableEntries) {
			for (OptionsList.Entry cellEntry : row) {
				if (cellEntry.isMouseOver(mouseX, mouseY)) {
					for (List<OptionsList.Entry> otherRow : tableEntries) {
						for (OptionsList.Entry otherCellEntry : otherRow) {
							otherCellEntry.setFocused(false);
						}
					}
					cellEntry.setFocused(true);
					if (cellEntry.mouseClicked(mouseX, mouseY, button)) {
						return true;
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		for (List<OptionsList.Entry> row : tableEntries) {
			for (OptionsList.Entry cellEntry : row) {
				if (cellEntry.isFocused()) {
					if (cellEntry.keyPressed(keyCode, scanCode, modifiers)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		for (List<OptionsList.Entry> row : tableEntries) {
			for (OptionsList.Entry cellEntry : row) {
				if (cellEntry.isFocused()) {
					if (cellEntry.charTyped(codePoint, modifiers)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		if (!focused) {
			for (List<OptionsList.Entry> row : tableEntries) {
				for (OptionsList.Entry cellEntry : row) {
					cellEntry.setFocused(false);
				}
			}
		}
	}
}
