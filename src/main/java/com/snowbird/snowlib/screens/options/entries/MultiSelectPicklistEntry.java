package com.snowbird.snowlib.screens.options.entries;

import com.snowbird.snowlib.screens.options.dropdown.MultiSelectDropdownList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A picklist allowing multiple selections (generic Enum-based).
 */
public class MultiSelectPicklistEntry<T extends Enum<T>> extends AbstractDropdownEntry {
	private final MultiSelectDropdownList<T> dropdownList;
	private final Set<T> selectedOptions;

	public MultiSelectPicklistEntry(
		String name,
		String description,
		Set<T> selectedOptions,
		T[] options,
		Consumer<Set<T>> onChange
	) {
		super(name, description);
		this.selectedOptions = selectedOptions;

		this.dropdownList = new MultiSelectDropdownList<>(
			0, options, selectedOptions,
			(option, isSelected) -> {
				if (isSelected) {
					selectedOptions.add(option);
				} else {
					selectedOptions.remove(option);
				}
				onChange.accept(selectedOptions);
			}
		);
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

		// Pass the parent's color scheme
		if (this.parentList != null) {
			this.dropdownList.setColorScheme(this.parentList.getColorScheme());
		}

		int adjustedTop;
		if (!this.inTable) {
			adjustedTop = top + 5 + Minecraft.getInstance().font.lineHeight + 5;
		} else {
			adjustedTop = top + (height - Minecraft.getInstance().font.lineHeight) / 2;
		}

		String selectedText = selectedOptions.stream()
			.map(Enum::name)
			.sorted()
			.collect(Collectors.joining(", "));

		guiGraphics.drawString(
			Minecraft.getInstance().font,
			selectedText,
			left + 5,
			adjustedTop,
			0xFFFFFF
		);

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
