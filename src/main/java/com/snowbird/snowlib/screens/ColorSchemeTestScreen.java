package com.snowbird.snowlib.screens;

import com.snowbird.snowlib.GuiColorScheme;
import com.snowbird.snowlib.screens.categories.Category;
import com.snowbird.snowlib.screens.categories.CategoryList;
import com.snowbird.snowlib.screens.options.OptionsList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates a custom color scheme for hovered/selected entries,
 * replacing the old SecondDummyConfigScreen.
 */
public class ColorSchemeTestScreen extends Screen {

	private static final int LEFT_COLUMN_WIDTH_PERCENT = 25;

	private CategoryList categoryList;
	private OptionsList optionsList;

	private final List<Category> categories = new ArrayList<>();
	private Category selectedCategory;
	private static String lastSelectedCategory = "General";

	// Example custom color scheme just for this screen
	private final GuiColorScheme customColors;

	public ColorSchemeTestScreen() {
		super(Component.literal("Color Scheme Test Screen"));

		// Create a custom scheme
		this.customColors = new GuiColorScheme()
			.setHoveredColor(0x8800AAFF)  // bluish
			.setSelectedColor(0x88AAFF00) // greenish
			.setBorderShadowColor(0x44000000); // optional new field usage
	}

	@Override
	protected void init() {
		int leftWidth = Mth.ceil(this.width * (LEFT_COLUMN_WIDTH_PERCENT / 100.0f));
		int rightWidth = this.width - leftWidth;

		this.children().clear();
		this.categories.clear();

		// Define two dummy categories
		this.categories.add(new Category("General", this::showGeneralOptions));
		this.categories.add(new Category("Advanced", this::showAdvancedOptions));

		// Build the category list on the left
		this.categoryList = new CategoryList(
			this.minecraft,
			leftWidth,
			this.height,
			20,
			this.height - 20,
			20,
			this.categories,
			this::selectCategory
		);
		this.addWidget(this.categoryList);

		// Build the options list on the right
		this.optionsList = new OptionsList(this.minecraft, rightWidth, this.height, 20, this.height - 20, 45);
		// Use our custom color scheme:
		this.optionsList.setColorScheme(this.customColors);

		this.optionsList.setLeftPos(leftWidth);
		this.addWidget(this.optionsList);

		Category defaultCat = this.categories.stream()
			.filter(cat -> cat.getName().equals(lastSelectedCategory))
			.findFirst()
			.orElse(this.categories.get(0));
		this.categoryList.setSelected(
			this.categoryList.children().get(this.categories.indexOf(defaultCat))
		);
		setSelectedCategory(defaultCat);
	}

	private void selectCategory(Category category) {
		setSelectedCategory(category);

		int leftWidth = Mth.ceil(this.width * (LEFT_COLUMN_WIDTH_PERCENT / 100.0f));
		int rightWidth = this.width - leftWidth;

		// Re-init the options list for new options
		this.removeWidget(this.optionsList);
		this.optionsList = new OptionsList(this.minecraft, rightWidth, this.height, 20, this.height - 20, 45);
		// Apply custom color scheme again
		this.optionsList.setColorScheme(this.customColors);

		this.optionsList.setLeftPos(leftWidth);
		this.addWidget(this.optionsList);

		category.getShowOptions().run(); // Populate
	}

	private void setSelectedCategory(Category category) {
		if (this.selectedCategory != null) {
			lastSelectedCategory = this.selectedCategory.getName();
		}
		this.selectedCategory = category;
	}

	private void showGeneralOptions() {
		this.optionsList.addCheckboxOption(
			"Enable Feature",
			"Whether or not to enable this feature",
			true,
			val -> {}
		);
		this.optionsList.addTextFieldOption(
			"Username",
			"Enter your username here",
			"DummyUser",
			newVal -> {}
		);
		this.optionsList.addScrollableDropdownOption(
			"Difficulty",
			"Pick a difficulty",
			"Normal",
			new String[] { "Easy", "Normal", "Hard", "Expert" },
			chosen -> {}
		);
	}

	private void showAdvancedOptions() {
		this.optionsList.addNumberFieldOption(
			"Max Limit",
			"Set a maximum limit value",
			100L,
			num -> {}
		);
		this.optionsList.addCheckboxOption(
			"Show Debug Logs",
			"Toggle verbose debug logging",
			false,
			val -> {}
		);
		this.optionsList.addTextFieldOption(
			"Secret Key",
			"Enter a secret key",
			"ABC123",
			val -> {}
		);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);

		// Right side
		this.optionsList.render(guiGraphics, mouseX, mouseY, partialTicks);
		// Left side
		this.categoryList.render(guiGraphics, mouseX, mouseY, partialTicks);

		// Separator
		int separatorX = Mth.ceil(this.width * (LEFT_COLUMN_WIDTH_PERCENT / 100.0f));
		guiGraphics.fill(separatorX, 20, separatorX + 1, this.height - 20, 0xFF808080);

		// Title
		guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);

		super.render(guiGraphics, mouseX, mouseY, partialTicks);
	}
}
