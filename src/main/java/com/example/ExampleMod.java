package com.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
    public static final String MOD_ID = "examplemod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    // Key binding for mod functionality
    private static KeyBinding keyBinding;
    
    @Override
    public void onInitialize() {
        LOGGER.info("Example Mod is initializing!");
        
        // Register key binding
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.examplemod.toggle", // Translation key
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G, // Default key: G
            "category.examplemod.general" // Category
        ));
        
        // Register client tick event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                if (client.player != null) {
                    client.player.sendMessage(
                        Text.of("Example Mod key pressed! Health: " + (int)client.player.getHealth()),
                        false
                    );
                }
            }
        });
        
        LOGGER.info("Example Mod initialized successfully!");
    }
    
    public static String getModId() {
        return MOD_ID;
    }
}
