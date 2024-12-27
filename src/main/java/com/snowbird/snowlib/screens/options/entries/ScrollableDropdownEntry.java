package com.snowbird.snowlib.screens.options.entries;

import com.snowbird.snowlib.screens.options.dropdown.DropdownList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Consumer;

/**
 * A single-select dropdown for Strings.
 */
public class ScrollableDropdownEntry extends AbstractDropdownEntry {
	private final DropdownList dropdownList;
	private String selectedValue;

	public ScrollableDropdownEntry(
		String name,
		String description,
		String initialValue,
		String[] options,
		Consumer<String> onChange
	) {
		super(name, description);
		this.selectedValue = initialValue;
		this.dropdownList = new DropdownList(0, initialValue, options, option -> {
			this.selectedValue = option;
			this.dropdownOpen = false;
			onChange.accept(option);
		});
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
		super.render(guiGraphics, index, top, left, width, height, mouseX, mouseY, hovered, partialTicks);

		// Pass the parent list's color scheme to the underlying dropdown
		if (this.parentList != null) {
			this.dropdownList.setColorScheme(this.parentList.getColorScheme());
		}

		int adjustedTop;
		if (!this.inTable) {
			adjustedTop = top + 5 + Minecraft.getInstance().font.lineHeight + 5;
		} else {
			adjustedTop = top + (height - Minecraft.getInstance().font.lineHeight) / 2;
		}

		guiGraphics.drawString(
			Minecraft.getInstance().font,
			this.selectedValue,
			left + 5,
			adjustedTop,
			0xFFFFFF
		);

		// If open, recalc dropdown position
		if (this.isDropdownOpen()) {
			int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
			int dropdownItemHeight = 20;
			int maxDropdownHeight = this.dropdownList.children().size() * dropdownItemHeight;
			int dropdownHeight = Math.min(maxDropdownHeight, 100);
			int dropdownY0 = top + height;

			if (dropdownY0 + dropdownHeight > screenHeight) {
				dropdownY0 = top - dropdownHeight;
			}
			this.dropdownList.setPosition(left, dropdownY0, width, dropdownHeight);
		}
	}

	public void renderDropdown(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		if (this.isDropdownOpen()) {
			this.dropdownList.render(guiGraphics, mouseX, mouseY, partialTicks);
		}
	}

	public boolean handleDropdownClick(double mouseX, double mouseY, int button) {
		return this.dropdownList.mouseClicked(mouseX, mouseY, button);
	}

	public boolean handleDropdownScroll(double mouseX, double mouseY, double scrollDelta, double scrollAmount) {
		return this.dropdownList.mouseScrolled(mouseX, mouseY, scrollDelta, scrollAmount);
	}
}
