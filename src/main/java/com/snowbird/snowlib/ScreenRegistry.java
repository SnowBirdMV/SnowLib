package com.snowbird.snowlib;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Global registry for configuration screens.
 * Each registered screen is displayed as a tab in the MasterConfigScreen.
 */
public class ScreenRegistry {

	private static final List<ConfigScreenRegistration> REGISTERED_SCREENS = new ArrayList<>();

	/**
	 * Register a new config screen with a specified mod ID and title.
	 *
	 * @param modId          Unique identifier for the mod.
	 * @param title          The title to show on the config tab.
	 * @param screenSupplier Supplies an instance of the screen when needed.
	 */
	public static void registerConfigScreen(String modId, Component title, Supplier<Screen> screenSupplier) {
		REGISTERED_SCREENS.add(new ConfigScreenRegistration(modId, title, screenSupplier));
	}

	/**
	 * Retrieves all registered config screens.
	 *
	 * @return A list of config screen registrations.
	 */
	public static List<ConfigScreenRegistration> getRegisteredScreens() {
		return REGISTERED_SCREENS;
	}

	/**
	 * Data class containing information about a single
	 * registered configuration screen.
	 */
	public static class ConfigScreenRegistration {
		private final String modId;
		private final Component title;
		private final Supplier<Screen> screenSupplier;

		/**
		 * @param modId          Mod’s ID.
		 * @param title          Title for the tab in MasterConfigScreen.
		 * @param screenSupplier Creates the Screen instance to show.
		 */
		public ConfigScreenRegistration(String modId, Component title, Supplier<Screen> screenSupplier) {
			this.modId = modId;
			this.title = title;
			this.screenSupplier = screenSupplier;
		}

		public String getModId() {
			return modId;
		}

		public Component getTitle() {
			return title;
		}

		public Supplier<Screen> getScreenSupplier() {
			return screenSupplier;
		}
	}
}
