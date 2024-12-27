package com.snowbird.snowlib.screens;

import com.snowbird.snowlib.GuiColorScheme;
import com.snowbird.snowlib.screens.options.OptionsList;
import com.snowbird.snowlib.screens.options.entries.CheckboxEntry;
import com.snowbird.snowlib.screens.options.entries.MultiSelectPicklistEntry;
import com.snowbird.snowlib.screens.options.entries.NumberFieldEntry;
import com.snowbird.snowlib.screens.options.entries.ScrollableDropdownEntry;
import com.snowbird.snowlib.screens.options.entries.TextFieldEntry;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A comprehensive test configuration screen demonstrating
 * the usage of all available entry types (checkbox, text field, number field,
 * dropdown, multi-select picklist, and even a table).
 */
public class AllEntriesTestScreen extends SimpleTwoColumnConfigScreen {

	private final GuiColorScheme customColorScheme;

	/**
	 * Initializes this screen with some custom color scheme
	 * and sets up multiple categories to showcase available entries.
	 */
	public AllEntriesTestScreen() {
		super(getCategoryDefinitions());

		// Example of a custom color scheme for demonstration
		this.customColorScheme = new GuiColorScheme()
			.setHoveredColor(0x8800AAFF)
			.setSelectedColor(0x88AAFF00)
			.setBorderShadowColor(0x88000000);
	}

	/**
	 * Called when the screen is being displayed or re-initialized.
	 * Applies the custom color scheme to the underlying OptionsList.
	 */
	@Override
	protected void init() {
		super.init();
		setOptionsListColorScheme(customColorScheme);
	}

	/**
	 * Provides the categories (left-side) and their associated
	 * population logic (right-side).
	 */
	private static List<SimpleCategoryDefinition> getCategoryDefinitions() {
		return List.of(
			new SimpleCategoryDefinition("General", AllEntriesTestScreen::populateGeneralOptions),
			new SimpleCategoryDefinition("Advanced", AllEntriesTestScreen::populateAdvancedOptions),
			new SimpleCategoryDefinition("Miscellaneous", AllEntriesTestScreen::populateMiscOptions),
			new SimpleCategoryDefinition("Table Test", AllEntriesTestScreen::showComprehensiveTable)
		);
	}

	/** Populates the "General" category. */
	private static void populateGeneralOptions(OptionsList options) {
		options.addCheckboxOption("Enable Feature",
			"Toggle the main feature on or off.",
			true,
			enabled -> { /* handle change */ }
		);

		options.addTextFieldOption("Username",
			"Set your username.",
			"Player1",
			username -> { /* handle text change */ }
		);

		options.addNumberFieldOption("Max Connections",
			"Set the maximum number of connections.",
			10L,
			max -> { /* handle number change */ }
		);

		options.addScrollableDropdownOption("Difficulty",
			"Select the game difficulty.",
			"Normal",
			new String[]{"Easy", "Normal", "Hard", "Extreme"},
			difficulty -> { /* handle selection */ }
		);

		options.addMultiSelectPicklistOption("Supported Modes",
			"Choose the modes supported.",
			EnumSet.of(Mode.MODE_A, Mode.MODE_C),
			Mode.values(),
			selectedModes -> { /* handle multi-select */ }
		);
	}

	/** Populates the "Advanced" category. */
	private static void populateAdvancedOptions(OptionsList options) {
		options.addCheckboxOption("Enable Logging",
			"Toggle detailed logging.",
			false,
			enabled -> { /* handle logging toggle */ }
		);

		options.addTextFieldOption("API Key",
			"Set your API key.",
			"ABC123XYZ",
			apiKey -> { /* handle API key change */ }
		);

		options.addNumberFieldOption("Timeout",
			"Network timeout in seconds.",
			30L,
			timeout -> { /* handle number change */ }
		);

		options.addScrollableDropdownOption("Theme",
			"Select a theme.",
			"Light",
			new String[]{"Light", "Dark", "Solarized"},
			theme -> { /* handle selection */ }
		);

		options.addMultiSelectPicklistOption("Enabled Plugins",
			"Select which plugins are enabled.",
			EnumSet.of(Plugin.PLUGIN_X, Plugin.PLUGIN_Z),
			Plugin.values(),
			selectedPlugins -> { /* handle multi-select */ }
		);
	}

	/** Populates the "Miscellaneous" category. */
	private static void populateMiscOptions(OptionsList options) {
		options.addCheckboxOption("Show Notifications",
			"Toggle notifications display.",
			true,
			enabled -> { /* handle change */ }
		);

		options.addTextFieldOption("Backup Directory",
			"Directory path for backups.",
			"/home/user/backups",
			path -> { /* handle path change */ }
		);

		options.addNumberFieldOption("Refresh Rate",
			"Set refresh rate in Hz.",
			60L,
			rate -> { /* handle refresh rate */ }
		);

		options.addScrollableDropdownOption("Language",
			"Select the application language.",
			"English",
			new String[]{"English", "Spanish", "French", "German"},
			language -> { /* handle selection */ }
		);

		options.addMultiSelectPicklistOption("Accessibility Options",
			"Choose accessibility features.",
			EnumSet.of(AccessibilityOption.SCREEN_READER, AccessibilityOption.HIGH_CONTRAST),
			AccessibilityOption.values(),
			selected -> { /* handle multi-select */ }
		);
	}

	/**
	 * Shows a comprehensive table containing all entry types (Checkbox, TextField,
	 * NumberField, Dropdown, MultiSelectPicklist).
	 */
	private static void showComprehensiveTable(OptionsList options) {
		List<String> columnHeaders = List.of("Toggle", "Input", "Number", "Dropdown", "Multi-Picklist");
		List<String> rowHeaders = List.of("Row 1", "Row 2", "Row 3");
		List<Integer> columnWidths = List.of(25, 40, 40, 40, 40);
		int cellHeight = 30;
		int headerWidth = 40;

		// Build table data
		List<List<OptionsList.Entry>> tableEntries = new ArrayList<>();

		// --- Row 1 ---
		List<OptionsList.Entry> row1 = new ArrayList<>();
		row1.add(new CheckboxEntry("Enable Row 1", "Enable features for Row 1", true, val -> {}));
		row1.add(new TextFieldEntry("Name Row 1", "Enter name for Row 1", "Alpha", val -> {}));
		row1.add(new NumberFieldEntry("Limit Row 1", "Set limit for Row 1", 50, val -> {}));
		String[] dropdownOptions = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
		row1.add(new ScrollableDropdownEntry("Type Row 1", "Select type for Row 1", "A", dropdownOptions, sel -> {}));

		enum TablePicklistEnum {
			PICK1, PICK2, PICK3, PICK4, PICK5,
			PICK6, PICK7, PICK8, PICK9, PICK10
		}
		Set<TablePicklistEnum> row1Selected = EnumSet.of(TablePicklistEnum.PICK1, TablePicklistEnum.PICK3);
		row1.add(new MultiSelectPicklistEntry<>("Options Row 1", "Select multiple options for Row 1",
			row1Selected, TablePicklistEnum.values(), s -> {}));
		tableEntries.add(row1);

		// --- Row 2 ---
		List<OptionsList.Entry> row2 = new ArrayList<>();
		row2.add(new CheckboxEntry("Enable Row 2", "Enable features for Row 2", false, val -> {}));
		row2.add(new TextFieldEntry("Name Row 2", "Enter name for Row 2", "Beta", val -> {}));
		row2.add(new NumberFieldEntry("Limit Row 2", "Set limit for Row 2", 75, val -> {}));
		row2.add(new ScrollableDropdownEntry("Type Row 2", "Select type for Row 2", "B", dropdownOptions, sel -> {}));
		Set<TablePicklistEnum> row2Selected = EnumSet.of(TablePicklistEnum.PICK2, TablePicklistEnum.PICK4, TablePicklistEnum.PICK6);
		row2.add(new MultiSelectPicklistEntry<>("Options Row 2", "Select multiple options for Row 2",
			row2Selected, TablePicklistEnum.values(), s -> {}));
		tableEntries.add(row2);

		// --- Row 3 ---
		List<OptionsList.Entry> row3 = new ArrayList<>();
		row3.add(new CheckboxEntry("Enable Row 3", "Enable features for Row 3", true, val -> {}));
		row3.add(new TextFieldEntry("Name Row 3", "Enter name for Row 3", "Gamma", val -> {}));
		row3.add(new NumberFieldEntry("Limit Row 3", "Set limit for Row 3", 120, val -> {}));
		row3.add(new ScrollableDropdownEntry("Type Row 3", "Select type for Row 3", "C", dropdownOptions, sel -> {}));
		Set<TablePicklistEnum> row3Selected = EnumSet.of(TablePicklistEnum.PICK5, TablePicklistEnum.PICK7, TablePicklistEnum.PICK9);
		row3.add(new MultiSelectPicklistEntry<>("Options Row 3", "Select multiple options for Row 3",
			row3Selected, TablePicklistEnum.values(), s -> {}));
		tableEntries.add(row3);

		options.addTableOption(
			"Table",
			"A table showcasing all entry types",
			tableEntries,
			columnHeaders,
			rowHeaders,
			cellHeight,
			columnWidths,
			headerWidth
		);
	}

	/**
	 * Enums used for certain multi-select picklist demos.
	 */
	private enum Mode {
		MODE_A, MODE_B, MODE_C, MODE_D
	}

	private enum Plugin {
		PLUGIN_X, PLUGIN_Y, PLUGIN_Z, PLUGIN_W
	}

	private enum AccessibilityOption {
		SCREEN_READER, HIGH_CONTRAST, SUBTITLES, DYSLEXIA_FRIENDLY
	}
}
