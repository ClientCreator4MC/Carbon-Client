package com.example.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class ExampleClientMixin {
    
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (client.player != null) {
            // Display custom HUD text
            String customText = "Example Mod Active - Health: " + (int)client.player.getHealth();
            client.textRenderer.drawWithShadow(
                matrices,
                Text.of(customText),
                10, // x position
                10, // y position
                0xFFFFFF // white color
            );
            
            // Display coordinates
            String coords = String.format("XYZ: %.1f / %.1f / %.1f", 
                client.player.getX(), 
                client.player.getY(), 
                client.player.getZ()
            );
            client.textRenderer.drawWithShadow(
                matrices,
                Text.of(coords),
                10,
                25,
                0x00FF00 // green color
            );
        }
    }
}
