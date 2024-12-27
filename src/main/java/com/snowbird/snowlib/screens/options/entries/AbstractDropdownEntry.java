package com.snowbird.snowlib.screens.options.entries;

import com.snowbird.snowlib.screens.options.OptionsList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * A base entry that shows a "dropdown" button on the right side.
 * Subclasses handle the actual dropdown list rendering/logic.
 */
public abstract class AbstractDropdownEntry extends OptionsList.Entry {
	protected final Button dropdownButton;
	protected boolean dropdownOpen = false;

	public AbstractDropdownEntry(String name, String description) {
		super(name, description);
		this.dropdownButton = Button.builder(
			Component.literal("V"),
			button -> {
				if (this.parentList != null) {
					this.parentList.closeAllDropdownsExcept(this);
				}
				this.dropdownOpen = !this.dropdownOpen;
			}
		).bounds(0, 0, 20, 20).build();
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
			// Draw background & name
			super.render(guiGraphics, index, top, left, width, height, mouseX, mouseY, hovered, partialTicks);
		}

		// Position & render the dropdown toggle button
		this.dropdownButton.setX(left + width - 25);
		this.dropdownButton.setY(top + (height - 20) / 2);
		this.dropdownButton.setWidth(20);
		this.dropdownButton.setHeight(20);
		this.dropdownButton.render(guiGraphics, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.dropdownButton.isMouseOver(mouseX, mouseY)) {
			this.dropdownButton.onPress();
			return true;
		}
		return false;
	}

	public boolean isDropdownOpen() {
		return this.dropdownOpen;
	}

	public void setDropdownVisibility(boolean dropdownOpen) {
		this.dropdownOpen = dropdownOpen;
	}
}
