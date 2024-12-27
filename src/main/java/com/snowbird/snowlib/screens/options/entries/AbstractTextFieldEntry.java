package com.snowbird.snowlib.screens.options.entries;

import com.snowbird.snowlib.screens.options.OptionsList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * Base class for text field entries (String, number, etc.).
 * Subclasses can override onTextChanged to handle validation.
 */
public abstract class AbstractTextFieldEntry<T> extends OptionsList.Entry {
	protected final EditBox textField;
	protected final Consumer<T> onChange;

	public AbstractTextFieldEntry(
		String name,
		String description,
		String initialValue,
		Consumer<T> onChange
	) {
		super(name, description);
		this.textField = new EditBox(
			Minecraft.getInstance().font,
			0, 0,
			200, 20,
			Component.literal("")
		);
		this.textField.setMaxLength(1024);
		this.textField.setValue(initialValue);
		this.onChange = onChange;
		this.textField.setResponder(this::onTextChanged);

		// Start cursor at position 0
		this.textField.setCursorPosition(0);
		this.textField.setHighlightPos(0);
	}

	protected abstract void onTextChanged(String value);

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.textField.isMouseOver(mouseX, mouseY)) {
			int relativeX = (int) mouseX - this.textField.getX();
			Minecraft mc = Minecraft.getInstance();
			int clickedIndex = mc.font.plainSubstrByWidth(this.textField.getValue(), relativeX).length();

			this.textField.setFocused(true);
			this.textField.setCursorPosition(clickedIndex);
			this.textField.setHighlightPos(clickedIndex);
			return true;
		} else {
			this.textField.setFocused(false);
			this.textField.setHighlightPos(this.textField.getCursorPosition());
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.textField.isFocused()) {
			int relativeX = (int) mouseX - this.textField.getX();
			Minecraft mc = Minecraft.getInstance();
			int draggedIndex = mc.font.plainSubstrByWidth(this.textField.getValue(), relativeX).length();

			this.textField.setHighlightPos(draggedIndex);
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return super.mouseReleased(mouseX, mouseY, button);
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
			super.render(guiGraphics, index, top, left, width, height, mouseX, mouseY, hovered, partialTicks);
			int adjustedTop = top + 5 + Minecraft.getInstance().font.lineHeight + 5;

			this.textField.setX(left + 5);
			this.textField.setY(adjustedTop);
			this.textField.setWidth(width - 10);
		} else {
			this.textField.setX(left + 2);
			this.textField.setY(top + (height - 20) / 2);
			this.textField.setWidth(width - 4);
		}

		this.textField.render(guiGraphics, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.textField.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		return this.textField.charTyped(codePoint, modifiers);
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		this.textField.setFocused(focused);
		if (!focused) {
			this.textField.setHighlightPos(this.textField.getCursorPosition());
		}
	}
}
