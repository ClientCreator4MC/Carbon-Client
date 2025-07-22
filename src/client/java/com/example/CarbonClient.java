package com.clientcreator4mc.carbonclient;

import com.clientcreator4mc.carbonclient.gui.ModuleScreen;
import com.clientcreator4mc.carbonclient.managers.ModuleManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarbonClient implements ModInitializer {
    public static final String MOD_ID = "carbonclient";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static CarbonClient INSTANCE;
    private ModuleManager moduleManager;
    private KeyBinding openGuiKey;

    @Override
    public void onInitialize() {
        INSTANCE = this;
        LOGGER.info("Initializing Carbon-Client...");

        // 1) Init modules
        moduleManager = new ModuleManager();
        moduleManager.initModules();

        // 2) Register R-Shift keybinding
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.carbonclient.opengui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.carbonclient"
        ));

        // 3) Tick loop: open GUI & update modules
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // open GUI screen
            while (openGuiKey.wasPressed()) {
                client.setScreen(new ModuleScreen());
            }
            // run module logic
            for (var m : moduleManager.getModules()) {
                if (m.isToggled()) m.onUpdate();
            }
        });

        LOGGER.info("Loaded {} modules", moduleManager.getModules().size());
    }

    public static CarbonClient getInstance() {
        return INSTANCE;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }
}
