package com.snowbird.snowlib.screens.options.dropdown;

import com.snowbird.snowlib.GuiColorScheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Consumer;

/**
 * A simple dropdown list that manages String options with scrolling capability.
 */
public class DropdownList extends AbstractDropdownList<String, DropdownList.Entry> {

	private final Consumer<String> onOptionSelected;
	private String selectedOption;

	/**
	 * We’ll allow the user to optionally pass in a color scheme if needed,
	 * or you can store it in a field. For simplicity, we do not show a separate
	 * constructor param here. If you want a fully custom color scheme for each
	 * dropdown, you’d pass it in similarly.
	 */
	private GuiColorScheme colorScheme = new GuiColorScheme();

	public DropdownList(int width, String initialValue, String[] options, Consumer<String> onOptionSelected) {
		super(width, 20);
		this.onOptionSelected = onOptionSelected;
		this.selectedOption = initialValue;

		for (String option : options) {
			this.addEntry(new Entry(option));
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

	public class Entry extends AbstractDropdownList<String, Entry>.Entry {
		public Entry(String option) {
			super(option);
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
				? DropdownList.this.colorScheme.getHoveredColor()
				: 0xFF000000; // idle color

			guiGraphics.fill(left, top, left + width, top + height, backgroundColor);

			// Draw option text
			guiGraphics.drawString(
				Minecraft.getInstance().font,
				this.option,
				left + 5,
				top + (height - Minecraft.getInstance().font.lineHeight) / 2,
				0xFFFFFFFF
			);

			// Draw checkmark if selected
			if (this.option.equals(DropdownList.this.selectedOption)) {
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
			DropdownList.this.selectedOption = this.option;
			DropdownList.this.onOptionSelected.accept(this.option);
			return true;
		}
	}
}
