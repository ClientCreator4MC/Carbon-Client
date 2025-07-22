package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ExampleModClient implements ClientModInitializer {
    
    // Key binding for mod functionality
    private static KeyBinding toggleKeyBinding;
    private static KeyBinding infoKeyBinding;
    
    @Override
    public void onInitializeClient() {
        ExampleMod.LOGGER.info("Example Mod Client is initializing!");
        
        // Register key bindings
        toggleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.examplemod.toggle", // Translation key
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G, // Default key: G
            "category.examplemod.general" // Category
        ));
        
        infoKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.examplemod.info", // Translation key
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H, // Default key: H
            "category.examplemod.general" // Category
        ));
        
        // Register client tick event for key handling
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Handle toggle key
            while (toggleKeyBinding.wasPressed()) {
                if (client.player != null) {
                    client.player.sendMessage(
                        Text.of("§6Example Mod Toggle! Health: " + (int)client.player.getHealth()),
                        false
                    );
                }
            }
            
            // Handle info key
            while (infoKeyBinding.wasPressed()) {
                if (client.player != null && client.world != null) {
                    String worldInfo = String.format(
                        "§bWorld: %s | Time: %d | Dimension: %s",
                        client.world.getRegistryKey().getValue().getPath(),
                        client.world.getTimeOfDay(),
                        client.world.getDimensionKey().getValue().getPath()
                    );
                    client.player.sendMessage(Text.of(worldInfo), false);
                }
            }
        });
        
        ExampleMod.LOGGER.info("Example Mod Client initialized successfully!");
    }
    
    public static KeyBinding getToggleKeyBinding() {
        return toggleKeyBinding;
    }
    
    public static KeyBinding getInfoKeyBinding() {
        return infoKeyBinding;
    }
}
