package com.snowbird.snowlib.screens;

import com.snowbird.snowlib.GuiColorScheme;
import com.snowbird.snowlib.screens.categories.Category;
import com.snowbird.snowlib.screens.categories.CategoryList;
import com.snowbird.snowlib.screens.options.OptionsList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A simple two-column config screen:
 *
 * - Draws a custom grey background behind the options (no Minecraft dirt background).
 * - Left column: CategoryList with user-provided category definitions.
 * - Right column: OptionsList that refreshes when a category is selected.
 *
 * Features:
 * - Dynamic left column width based on the longest category name plus offsets.
 * - No grey overlay or dirt background over the options; background is behind them.
 */
public class SimpleTwoColumnConfigScreen extends Screen {

	/**
	 * A simple container describing one category:
	 * - The category name (e.g., "General"),
	 * - A function that populates the OptionsList with its entries.
	 */
	public static class SimpleCategoryDefinition {
		private final String name;
		private final Consumer<OptionsList> populateOptions;

		public SimpleCategoryDefinition(String name, Consumer<OptionsList> populateOptions) {
			this.name = name;
			this.populateOptions = populateOptions;
		}

		public String name() {
			return name;
		}

		public Consumer<OptionsList> populateOptions() {
			return populateOptions;
		}
	}

	// Constants for layout
	private static final int LEFT_OFFSET = 5; // pixels from the left edge
	private static final int BUFFER = 30;      // additional buffer

	private final List<SimpleCategoryDefinition> categoryDefinitions;
	private CategoryList categoryList;
	private OptionsList optionsList;
	private final List<Category> categories = new ArrayList<>();

	private Category selectedCategory;
	private String lastSelectedCategory = ""; // Changed from static to instance variable

	// Dynamic widths
	private int leftWidth;
	private int rightWidth;

	// Store the GuiColorScheme
	protected GuiColorScheme colorScheme;

	/**
	 * Creates a two-column config screen with a custom grey background.
	 *
	 * @param categoryDefs List of category definitions (name + populate lambda).
	 */
	public SimpleTwoColumnConfigScreen(
		List<SimpleCategoryDefinition> categoryDefs
	) {
		super(Component.empty()); // Internally set to empty
		this.categoryDefinitions = categoryDefs;
	}

	/**
	 * Set the color scheme and store it in the field.
	 *
	 * @param colorScheme The GuiColorScheme to apply.
	 */
	public void setOptionsListColorScheme(GuiColorScheme colorScheme) {
		this.colorScheme = colorScheme;
		if (this.optionsList != null) {
			this.optionsList.setColorScheme(colorScheme);
		}
	}

	@Override
	protected void init() {
		// Clear existing widgets and categories
		this.clearWidgets();
		this.categories.clear();

		// Convert user definitions into Category objects
		for (SimpleCategoryDefinition def : categoryDefinitions) {
			Category cat = new Category(def.name(), () -> def.populateOptions().accept(this.optionsList));
			this.categories.add(cat);
		}

		// Calculate leftWidth based on the longest category name + LEFT_OFFSET + BUFFER
		int maxCategoryWidth = 0;
		for (SimpleCategoryDefinition def : categoryDefinitions) {
			int catWidth = this.font.width(def.name());
			if (catWidth > maxCategoryWidth) {
				maxCategoryWidth = catWidth;
			}
		}
		this.leftWidth = LEFT_OFFSET + maxCategoryWidth + BUFFER;
		this.rightWidth = this.width - this.leftWidth;

		// Margins for top and bottom
		int top = 20;
		int bottom = this.height - 20;

		// Create the CategoryList (left column)
		this.categoryList = new CategoryList(
			this.minecraft,
			leftWidth,
			this.height,  // total screen height
			top,
			bottom,
			20, // Each category item's height
			this.categories,
			this::onCategorySelected
		);
		// Use addRenderableWidget so it actually renders in 1.20.2
		this.addRenderableWidget(this.categoryList);

		// Create the OptionsList (right column)
		this.optionsList = new OptionsList(
			Minecraft.getInstance(),
			rightWidth,
			this.height,
			top,
			bottom,
			45 // Each option item's height
		);
		this.optionsList.setLeftPos(leftWidth);
		this.addRenderableWidget(this.optionsList);

		// Ensure colorScheme is initialized before applying
		if (this.colorScheme == null) {
			this.colorScheme = new GuiColorScheme(); // Default color scheme
		}
		this.optionsList.setColorScheme(this.colorScheme);

		// Default category selection
		if (!categories.isEmpty()) {
			Category defaultCat = this.categories.stream()
				.filter(cat -> cat.getName().equals(this.lastSelectedCategory)) // Use instance variable
				.findFirst()
				.orElse(this.categories.get(0));

			// Select the default category in the CategoryList
			this.categoryList.setSelected(
				this.categoryList.children().get(this.categories.indexOf(defaultCat))
			);
			setSelectedCategory(defaultCat);
			populateRightColumn(defaultCat.getName());
		}
	}

	/**
	 * Called when the user selects a category from the left column.
	 */
	private void onCategorySelected(Category category) {
		setSelectedCategory(category);

		// Re-create the OptionsList for the right column
		this.removeWidget(this.optionsList);
		this.optionsList = new OptionsList(
			Minecraft.getInstance(),
			rightWidth,
			this.height,
			20,
			this.height - 20,
			45
		);
		this.optionsList.setLeftPos(leftWidth);
		// Apply the stored colorScheme to the new OptionsList
		this.optionsList.setColorScheme(this.colorScheme);
		this.addRenderableWidget(this.optionsList);

		// Populate the OptionsList based on the selected category
		populateRightColumn(category.getName());
	}

	/**
	 * Updates which category is selected and remembers it for next time.
	 */
	private void setSelectedCategory(Category category) {
		if (this.selectedCategory != null) {
			this.lastSelectedCategory = this.selectedCategory.getName(); // Use instance variable
		}
		this.selectedCategory = category;
	}

	/**
	 * Looks up the matching definition and calls its populate function.
	 */
	private void populateRightColumn(String categoryName) {
		SimpleCategoryDefinition def = categoryDefinitions.stream()
			.filter(d -> d.name().equals(categoryName))
			.findFirst()
			.orElse(null);

		if (def != null && def.populateOptions() != null) {
			def.populateOptions().accept(this.optionsList);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.optionsList != null && this.optionsList.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		}
		if (this.categoryList != null && this.categoryList.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		if (this.optionsList != null && this.optionsList.charTyped(codePoint, modifiers)) {
			return true;
		}
		if (this.categoryList != null && this.categoryList.charTyped(codePoint, modifiers)) {
			return true;
		}
		return super.charTyped(codePoint, modifiers);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

		// Draw a custom grey background
		guiGraphics.fill(0, 0, this.width, this.height, 0xFF2D2D2D); // Dark grey background

		// Render child widgets
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		// Draw a vertical separator between columns
		guiGraphics.fill(leftWidth, 20, leftWidth + 1, this.height - 20, 0xFF808080);

		// **Title rendering removed**
	}
}
