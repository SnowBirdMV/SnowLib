package com.snowbird.snowlib.screens;

import com.snowbird.snowlib.screens.options.OptionsList;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates how to create a two-column config screen:
 * one column for categories, another for various options.
 */
public class SnowLibNewExampleScreen extends SimpleTwoColumnConfigScreen {

	/**
	 * Basic constructor that builds categories and their content.
	 */
	public SnowLibNewExampleScreen() {
		super(buildCategories());
	}

	/**
	 * Creates a list of category definitions and their associated
	 * population functions.
	 */
	private static List<SimpleCategoryDefinition> buildCategories() {
		List<SimpleCategoryDefinition> list = new ArrayList<>();

		list.add(new SimpleCategoryDefinition("General", options -> {
			options.addCheckboxOption(
				"Enable Feature",
				"Toggle the main feature on/off",
				true,
				val -> {}
			);
			options.addTextFieldOption(
				"Username",
				"Enter your user name",
				"Steve",
				val -> {}
			);
			options.addNumberFieldOption(
				"Max Items",
				"Set the max item count",
				64L,
				val -> {}
			);
		}));

		list.add(new SimpleCategoryDefinition("Advanced", options -> {
			options.addCheckboxOption(
				"Extra Logging",
				"Enables verbose debug logs",
				false,
				val -> {}
			);
			options.addScrollableDropdownOption(
				"Difficulty",
				"Pick difficulty",
				"Normal",
				new String[] {"Easy", "Normal", "Hard", "Insane"},
				chosen -> {}
			);
		}));

		return list;
	}
}
