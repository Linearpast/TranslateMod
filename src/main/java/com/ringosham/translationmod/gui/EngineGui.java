/*
 * Copyright (C) 2021 Ringosham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ringosham.translationmod.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fml.ModList;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class EngineGui extends CommonGui {
    private static final int guiWidth = 300;
    private static final int guiHeight = 150;
    private static final String title;

    private static final List<Component> googleTooltip = new ArrayList<>();
    private static final List<Component> baiduTooltip = new ArrayList<>();

    static {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String modName = ModList.get().getModContainerById(TranslationMod.MODID).get().getModInfo().getDisplayName();
        title = modName + " - Engine options";
        googleTooltip.add(new TextComponent("By default, you are using the \"free\" version of Google translation"));
        googleTooltip.add(new TextComponent("This is the same API the Google translationmod.mixins.json website is using"));
        googleTooltip.add(new TextComponent("However, too many requests and Google will block you for a few minutes"));
        googleTooltip.add(new TextComponent("Cloud translation API is the paid version of Google translationmod.mixins.json"));
        googleTooltip.add(new TextComponent("Please check the mod page for details"));
        baiduTooltip.add(new TextComponent("If you cannot use Google due to country restrictions,"));
        baiduTooltip.add(new TextComponent("Baidu is your second option"));
        baiduTooltip.add(new TextComponent("An account is needed to use this API (Phone verification required)"));
        baiduTooltip.add(new TextComponent("Free tier only allows 1 request per second"));
        baiduTooltip.add(new TextComponent("Paying allows for more requests per second"));
        baiduTooltip.add(new TextComponent("Please check the mod page for details"));
    }

    private String engine;
    private EditBox googleKeyBox;
    private EditBox baiduKeyBox;
    private EditBox baiduAppIdBox;

    EngineGui() {
        super(title, guiHeight, guiWidth);
        engine = ConfigManager.config.translationEngine.get();
    }

    @Override
    public void render(PoseStack stack, int x, int y, float tick) {
        super.render(stack, x, y, tick);
        drawStringLine(stack, title, new String[]{
                "Please choose your translation engine",
                "The mod can only use either of them"
        }, 5);
        switch (engine) {
            case "google":
                googleKeyBox.render(stack, x, y, tick);
                baiduKeyBox.active = false;
                baiduAppIdBox.active = false;
                baiduKeyBox.visible = false;
                baiduAppIdBox.visible = false;
                googleKeyBox.active = true;
                googleKeyBox.visible = true;
                font.draw(stack, "Cloud platform API key", getLeftMargin(), getYOrigin() + 75, 0);
                font.draw(stack, "Delete/Leave empty to use the free API", getLeftMargin(), getYOrigin() + 110, 0);
                break;
            case "baidu":
                baiduKeyBox.active = true;
                baiduAppIdBox.active = true;
                baiduKeyBox.visible = true;
                baiduAppIdBox.visible = true;
                googleKeyBox.active = false;
                googleKeyBox.visible = false;
                font.draw(stack, "Baidu developer App ID", getLeftMargin(), getYOrigin() + 65, 0);
                baiduAppIdBox.render(stack, x, y, tick);
                font.draw(stack, "Baidu API key", getLeftMargin(), getYOrigin() + 95, 0);
                baiduKeyBox.render(stack, x, y, tick);
                break;
        }
        Button button0 = null;
        Button button1 = null;
        for (int i = 0, j = 0; i < renderables.size(); i++) {
            if(renderables.get(i) instanceof Button){
                if(j == 0) button0 = (Button)renderables.get(i);
                if(j == 1) {
                    button1 = (Button)renderables.get(i);
                    break;
                }
                j++;
            }
        }
        if (button0 != null && button0.isHoveredOrFocused())
            renderComponentTooltip(stack, googleTooltip, x, y);
        if (button1 != null && button1.isHoveredOrFocused())
            renderComponentTooltip(stack, baiduTooltip, x, y);

    }

    @Override
    public void init() {
        getMinecraft().keyboardHandler.setSendRepeatsToGui(true);
        this.googleKeyBox = new EditBox(this.font, getLeftMargin(), getYOrigin() + 90, guiWidth - 10, 15, null);
        googleKeyBox.setCanLoseFocus(true);
        googleKeyBox.setMaxLength(84);
        googleKeyBox.setBordered(true);
        googleKeyBox.setValue(ConfigManager.config.googleKey.get());
        addRenderableWidget(googleKeyBox);
        this.baiduAppIdBox = new EditBox(this.font, getLeftMargin(), getYOrigin() + 75, guiWidth - 10, 15, null) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                baiduKeyBox.setFocus(false);
                super.clicked(mouseX, mouseY);
            }
        };
        baiduAppIdBox.setCanLoseFocus(true);
        baiduAppIdBox.setMaxLength(20);
        baiduAppIdBox.setBordered(true);
        baiduAppIdBox.setValue(ConfigManager.config.baiduAppId.get());
        addRenderableWidget(baiduAppIdBox);
        this.baiduKeyBox = new EditBox(this.font, getLeftMargin(), getYOrigin() + 105, guiWidth - 10, 15, null) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                baiduAppIdBox.setFocus(false);
            }
        };
        baiduKeyBox.setCanLoseFocus(true);
        baiduKeyBox.setBordered(true);
        baiduKeyBox.setMaxLength(24);
        baiduKeyBox.setValue(ConfigManager.config.baiduKey.get());
        addRenderableWidget(baiduKeyBox);

        addRenderableWidget(new Button(getLeftMargin(), getYOrigin() + 40, guiWidth / 2 - 10, regularButtonHeight, new TextComponent("Google"), (button) -> {
            button.active = false;
            Button button1 = null;
            for (int i = 0, j = 0; i < renderables.size(); i++) {
                if(renderables.get(i) instanceof Button){
                    if(j == 1) {
                        button1 = (Button)renderables.get(i);
                        break;
                    }
                    j++;
                }
            }
            if(button1 != null) button1.active = true;
            engine = "google";
        }));
        addRenderableWidget(new Button(getRightMargin(guiWidth / 2 - 5), getYOrigin() + 40, guiWidth / 2 - 10, regularButtonHeight, new TextComponent("Baidu"), (button) -> {
            button.active = false;
            Button button0 = null;
	        for (net.minecraft.client.gui.components.Widget renderable : renderables) {
		        if (renderable instanceof Button) {
			        button0 = (Button) renderable;
			        break;
		        }
	        }
            if(button0 != null) button0.active = true;
            engine = "baidu";
        }));
        addRenderableWidget(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, new TextComponent("Apply and close"),
                (button) -> this.applyKey()));
        addRenderableWidget(new Button(getRightMargin(regularButtonWidth) - regularButtonWidth - 5, getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, new TextComponent("Back"),
                (button) -> this.configGui()));
        Button button0 = null;
        Button button1 = null;
        for (int i = 0, j = 0; i < renderables.size(); i++) {
            if(renderables.get(i) instanceof Button){
                if(j == 0) button0 = (Button)renderables.get(i);
                if(j == 1) {
                    button1 = (Button)renderables.get(i);
                    break;
                }
                j++;
            }
        }
        switch (engine) {
            case "google":
                if(button0 != null) button0.active = true;
                break;
            case "baidu":
                if(button1 != null) button1.active = true;
                break;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
        if (keyCode == GLFW.GLFW_KEY_E && !this.googleKeyBox.isFocused()) {
            getMinecraft().keyboardHandler.setSendRepeatsToGui(false);
            getMinecraft().setScreen(null);
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifier);
    }

    private void configGui() {
        getMinecraft().keyboardHandler.setSendRepeatsToGui(false);
        getMinecraft().setScreen(new ConfigGui());
    }

    private void applyKey() {
        getMinecraft().keyboardHandler.setSendRepeatsToGui(true);
        ConfigManager.config.googleKey.set(googleKeyBox.getValue());
        ConfigManager.config.baiduAppId.set(baiduAppIdBox.getValue());
        ConfigManager.config.baiduKey.set(baiduKeyBox.getValue());
        ConfigManager.config.translationEngine.set(engine);
        ConfigManager.saveConfig();
        Log.logger.info("Saved engine options");
        ChatUtil.printChatMessage(true, "New translation engine options have been applied.", ChatFormatting.WHITE);
        getMinecraft().setScreen(null);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 点击 headerField 区域时转移焦点
        if (googleKeyBox.isMouseOver(mouseX, mouseY)) {
            googleKeyBox.setFocus(true);
            googleKeyBox.setFocus(false); // 移除其他框的焦点
        }

        // 点击 messageField 区域时同理
        if (baiduKeyBox.isMouseOver(mouseX, mouseY)) {
            baiduKeyBox.setFocus(true);
            baiduKeyBox.setFocus(false);
        }

        if (baiduAppIdBox.isMouseOver(mouseX, mouseY)) {
            baiduAppIdBox.setFocus(true);
            baiduAppIdBox.setFocus(false);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
