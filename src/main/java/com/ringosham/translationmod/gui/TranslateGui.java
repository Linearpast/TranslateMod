package com.ringosham.translationmod.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.translate.SelfTranslate;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class TranslateGui extends CommonGui {
    private static final int guiHeight = 125;
    private static final int guiWidth = 225;
    private static final String title;

    static {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String modName = ModList.get().getModContainerById(TranslationMod.MODID).get().getModInfo().getDisplayName();
        title = modName + " - by Ringosham";
    }

    private EditBox headerField;
    private EditBox messageField;

    public TranslateGui() {
        super(title, guiHeight, guiWidth);
    }

    @Override
    public void render(PoseStack stack, int x, int y, float tick) {
        super.render(stack, x, y, tick);
        font.draw(stack, title, getLeftMargin(), getTopMargin(), 0x555555);
        font.draw(stack, "Enter the command/prefix here (Optional)", getLeftMargin(), getTopMargin() + 10, 0x555555);
        font.draw(stack, "Enter your message here (Enter to send)", getLeftMargin(), getTopMargin() + 40, 0x555555);
        headerField.render(stack, x, y, tick);
        messageField.render(stack, x, y, tick);
    }

    @Override
    public void init() {
        this.headerField = new EditBox(font, getLeftMargin(), getYOrigin() + 25, guiWidth - 10, 15, new TextComponent(""));
        this.messageField = new EditBox(font, getLeftMargin(), getYOrigin() + 55, guiWidth - 10, 15, new TextComponent(""));
        headerField.setMaxLength(25);
        headerField.setCanLoseFocus(true);
        headerField.setBordered(true);
        messageField.setMaxLength(75);
        messageField.setCanLoseFocus(true);
        messageField.setBordered(true);
        addWidget(headerField);
        addWidget(messageField);
        getMinecraft().keyboardHandler.setSendRepeatsToGui(true);
        addRenderableWidget(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, new TextComponent("Settings"),
                (button) -> this.configGui()));
        addRenderableWidget(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, new TextComponent("Close"),
                (button) -> this.exitGui()));
        addRenderableWidget(new Button(getLeftMargin(), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, new TextComponent("Retranslate"), (button) -> this.retranslateGui()));
        addRenderableWidget(new Button(getLeftMargin(), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, new TextComponent("Credits"),
                (button) -> {
                    ChatUtil.printCredits();
                    this.exitGui();
                }));
    }

    private void retranslateGui() {
        getMinecraft().keyboardHandler.setSendRepeatsToGui(false);
        getMinecraft().setScreen(new RetranslateGui());
    }

    private void configGui() {
        getMinecraft().keyboardHandler.setSendRepeatsToGui(false);
        getMinecraft().setScreen(new ConfigGui());
    }

    private void exitGui() {
        getMinecraft().keyboardHandler.setSendRepeatsToGui(false);
        getMinecraft().setScreen(null);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER && (this.messageField.isFocused() || this.headerField.isFocused())) {
            exitGui();
            Thread translate = new SelfTranslate(this.messageField.getValue(), this.headerField.getValue());
            translate.start();
            return false;
        }
        if (keyCode == GLFW.GLFW_KEY_E && !this.messageField.isFocused() && !this.headerField.isFocused()) {
            exitGui();
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 点击 headerField 区域时转移焦点
        if (headerField.isMouseOver(mouseX, mouseY)) {
            headerField.setFocus(true);
            messageField.setFocus(false); // 移除其他框的焦点
        }

        // 点击 messageField 区域时同理
        if (messageField.isMouseOver(mouseX, mouseY)) {
            messageField.setFocus(true);
            headerField.setFocus(false);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
