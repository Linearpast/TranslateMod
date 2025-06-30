package com.ringosham.translationmod.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.lwjgl.opengl.GL11;

public class TextButton extends Button {

    private final int color;

    public TextButton(int x, int y, int width, Component text, Button.OnPress onPress, int color) {
        super(x, y, width, 10, text, onPress);
        this.color = color;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float tick) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        Minecraft.getInstance().font.draw(stack, this.getMessage().getString(), x, y, color);
    }
}
