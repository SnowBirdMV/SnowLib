package com.snowbird.snowlib;

public class GuiColorScheme {

	// Default Colors
	private static final int DEFAULT_HOVERED_COLOR  = 0xCC333333; // 80% dark gray
	private static final int DEFAULT_SELECTED_COLOR = 0xCC666666; // 80% lighter gray
	private static final int DEFAULT_BORDER_SHADOW  = 0x88000000; // 50% black

	// Color Fields (use Integer to allow null for defaults)
	private Integer hoveredColor;
	private Integer selectedColor;
	private Integer borderShadowColor; // Example of an expandable color field

	// Constructor
	public GuiColorScheme() {
		// Defaults are handled in getters
	}

	// Static Factory Method for Default Scheme
	public static GuiColorScheme defaultScheme() {
		return new GuiColorScheme();
	}

	// Getters and Setters
	public int getHoveredColor() {
		return hoveredColor != null ? hoveredColor : DEFAULT_HOVERED_COLOR;
	}

	public GuiColorScheme setHoveredColor(int hoveredColor) {
		this.hoveredColor = hoveredColor;
		return this;
	}

	public int getSelectedColor() {
		return selectedColor != null ? selectedColor : DEFAULT_SELECTED_COLOR;
	}

	public GuiColorScheme setSelectedColor(int selectedColor) {
		this.selectedColor = selectedColor;
		return this;
	}

	public int getBorderShadowColor() {
		return borderShadowColor != null ? borderShadowColor : DEFAULT_BORDER_SHADOW;
	}

	public GuiColorScheme setBorderShadowColor(int borderShadowColor) {
		this.borderShadowColor = borderShadowColor;
		return this;
	}

	// Future color fields can be added here with similar getters and setters
}
