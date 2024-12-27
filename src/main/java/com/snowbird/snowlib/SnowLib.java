package com.snowbird.snowlib;

import com.mojang.blaze3d.platform.InputConstants;
import com.snowbird.snowlib.screens.AllEntriesTestScreen;
import com.snowbird.snowlib.screens.MasterConfigScreen;
import com.snowbird.snowlib.screens.SnowLibNewExampleScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 * Main entry point for the SnowLib mod.
 *
 * Registers a key binding, listens for key input, and
 * opens a master config screen when the key is pressed.
 */
@Mod(SnowLib.MODID)
public class SnowLib {

	public static final String MODID = "snowlib";
	private static KeyMapping openGuiKey;

	/**
	 * Constructor registers itself on the mod event bus and also
	 * registers example config screens.
	 */
	public SnowLib() {
		net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get()
			.getModEventBus()
			.register(this);
//
//		// Example config screens for demonstration
//		ScreenRegistry.registerConfigScreen(
//			"snowlibnewexample",
//			Component.literal("New Example"),
//			SnowLibNewExampleScreen::new
//		);
//
//		// Shown as a separate “All Test” screen
//		ScreenRegistry.registerConfigScreen(
//			"All Test",
//			Component.literal("Color"),
//			AllEntriesTestScreen::new
//		);
	}

	/**
	 * Registers the key mapping that opens the MasterConfigScreen.
	 */
	@SubscribeEvent
	public void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
		openGuiKey = new KeyMapping(
			"key.snowlib.open_gui",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_J,
			"key.categories.snowlib"
		);
		event.register(openGuiKey);
	}

	/**
	 * Handles key presses on the client side. If the user presses
	 * the assigned key, it opens the MasterConfigScreen.
	 */
	@Mod.EventBusSubscriber(modid = SnowLib.MODID, value = Dist.CLIENT)
	public static class ClientKeyHandler {

		@SubscribeEvent
		public static void onKeyInput(InputEvent.Key event) {
			if (openGuiKey != null && openGuiKey.consumeClick()) {
				Minecraft mc = Minecraft.getInstance();
				mc.setScreen(new MasterConfigScreen());
			}
		}
	}
}
