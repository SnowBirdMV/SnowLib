package com.snowbird.snowlib.screens.options.entries;

import com.snowbird.snowlib.GuiColorScheme;
import com.snowbird.snowlib.screens.options.OptionsList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * A simple checkbox entry.
 */
public class CheckboxEntry extends OptionsList.Entry {
	private final Checkbox checkbox;
	private final Consumer<Boolean> onChange;

	public CheckboxEntry(String name, String description, boolean initialValue, Consumer<Boolean> onChange) {
		super(name, description);
		this.checkbox = new Checkbox(0, 0, 20, 20, Component.literal(""), initialValue);
		this.onChange = onChange;
	}

	@Override
	public int getHeight() {
		return this.inTable ? this.height : 45;
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

		GuiColorScheme scheme = (this.parentList != null)
			? this.parentList.getColorScheme()
			: new GuiColorScheme(); // fallback

		if (!this.inTable) {
			super.render(guiGraphics, index, top, left, width, height, mouseX, mouseY, hovered, partialTicks);
			if (this.isFocused()) {
				guiGraphics.fill(left - 2, top - 2, left + width + 2, top + height + 2, scheme.getSelectedColor());
			} else if (hovered) {
				guiGraphics.fill(left, top, left + width, top + height, scheme.getHoveredColor());
			}
			int adjustedTop = top + 5;

			guiGraphics.drawString(
				Minecraft.getInstance().font,
				this.getName(),
				left + 5,
				adjustedTop,
				0xFFFFFF
			);
			adjustedTop += 15;

			this.checkbox.setX(left + 5);
			this.checkbox.setY(adjustedTop);
		} else {
			this.checkbox.setX(left + (width - 20) / 2);
			this.checkbox.setY(top + (height - 20) / 2);
		}

		this.checkbox.render(guiGraphics, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.checkbox.isMouseOver(mouseX, mouseY)) {
			this.checkbox.onPress();
			this.onChange.accept(this.checkbox.selected());
			return true;
		}
		return false;
	}
}
