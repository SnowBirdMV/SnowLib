package com.snowbird.snowlib.screens.options.dropdown;

import com.snowbird.snowlib.GuiColorScheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Set;
import java.util.function.BiConsumer;

/**
 * A dropdown list for multiple enum options at once.
 */
public class MultiSelectDropdownList<T extends Enum<T>>
	extends AbstractDropdownList<T, MultiSelectDropdownList<T>.Entry> {

	private final BiConsumer<T, Boolean> onOptionSelected;
	private GuiColorScheme colorScheme = new GuiColorScheme();

	public MultiSelectDropdownList(
		int width,
		T[] options,
		Set<T> selectedOptions,
		BiConsumer<T, Boolean> onOptionSelected
	) {
		super(width, 20);
		this.onOptionSelected = onOptionSelected;

		for (T option : options) {
			boolean isSelected = selectedOptions.contains(option);
			this.addEntry(new Entry(option, isSelected));
		}

		int visibleOptionCount = Math.min(options.length, maxVisibleOptions);
		this.height = visibleOptionCount * this.itemHeight;
	}

	public void setColorScheme(GuiColorScheme colorScheme) {
		this.colorScheme = colorScheme;
	}

	public GuiColorScheme getColorScheme() {
		return this.colorScheme;
	}

	public class Entry extends AbstractDropdownList<T, Entry>.Entry {
		private boolean isSelected;

		public Entry(T option, boolean isSelected) {
			super(option);
			this.isSelected = isSelected;
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
			int backgroundColor = hovered
				? MultiSelectDropdownList.this.colorScheme.getHoveredColor()
				: 0xFF000000; // idle color
			guiGraphics.fill(left, top, left + width, top + height, backgroundColor);

			// Draw the option name
			guiGraphics.drawString(
				Minecraft.getInstance().font,
				this.option.name(),
				left + 5,
				top + (height - Minecraft.getInstance().font.lineHeight) / 2,
				0xFFFFFFFF
			);

			// If selected, draw a green checkmark on the right
			if (this.isSelected) {
				String checkmark = "\u2713";
				int checkmarkColor = 0xFF00FF00;
				int checkmarkWidth = Minecraft.getInstance().font.width(checkmark);
				int checkmarkX = left + width - checkmarkWidth - 5;
				int checkmarkY = top + (height - Minecraft.getInstance().font.lineHeight) / 2;
				guiGraphics.drawString(Minecraft.getInstance().font, checkmark, checkmarkX, checkmarkY, checkmarkColor);
			}
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			this.isSelected = !this.isSelected;
			MultiSelectDropdownList.this.onOptionSelected.accept(this.option, this.isSelected);
			return true;
		}
	}
}
