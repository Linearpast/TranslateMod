package com.ringosham.translationmod.events;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class KeyBind {
    static KeyMapping translateKey;

    public static void keyInit() {
        translateKey = new KeyMapping("key.translation.settings", GLFW.GLFW_KEY_Y, "Translate mod");
        ClientRegistry.registerKeyBinding(translateKey);
    }
}
