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
import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.client.types.Language;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public class ConfigGui extends CommonGui {
    private static final int guiWidth = 250;
    private static final int guiHeight = 206;
    private static final TextComponent targetTooltip = new TextComponent("The language your chat will be translated to");
    private static final List<Component> selfTooltip = new ArrayList<>();
    private static final List<Component> speakAsTooltip = new ArrayList<>();
    private static final List<Component> regexTooltip = new ArrayList<>();
    private static final List<Component> apiKeyTooltip = new ArrayList<>();
    private static final List<Component> colorTooltip = new ArrayList<>();
    private static final List<Component> boldTooltip = new ArrayList<>();
    private static final List<Component> underlineTooltip = new ArrayList<>();
    private static final List<Component> italicTooltip = new ArrayList<>();
    private static final List<Component> signTooltip = new ArrayList<>();
    private static final String title;

    static {
        selfTooltip.add(new TextComponent("The language you speak in game"));
        selfTooltip.add(new TextComponent("This will be utilised when you want to translationmod.mixins.json what you speak"));
        speakAsTooltip.add(new TextComponent("The language your messages will be translated to."));
        speakAsTooltip.add(new TextComponent("After you typed your messages through this mod,"));
        speakAsTooltip.add(new TextComponent("it will be translated to the language you specified"));
        regexTooltip.add(new TextComponent("Regex are patterns for the mod to detect chat messages."));
        regexTooltip.add(new TextComponent("If you notice the mod doesn't do anything on a server,"));
        regexTooltip.add(new TextComponent("chances are you need to add one here."));
        signTooltip.add(new TextComponent("Translates signs when you look at them"));
        apiKeyTooltip.add(new TextComponent("Change your translation engine options and enter your API key"));
        colorTooltip.add(new TextComponent("Changes the color of the translated message"));
        boldTooltip.add(new TextComponent("Bolds the translated message"));
        italicTooltip.add(new TextComponent("Italics the translated message"));
        underlineTooltip.add(new TextComponent("Underlines the translated message"));
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String modName = ModList.get().getModContainerById(TranslationMod.MODID).get().getModInfo().getDisplayName();
        title = modName + " - Settings";
    }

    //If this instance is between transition between other GUIs
    private boolean isTransition = false;
    private Language targetLang;
    private Language speakAsLang;
    private Language selfLang;
    private String color;
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private boolean translateSign;

    ConfigGui() {
        super(title, guiHeight, guiWidth);
    }

    //Use for passing unsaved configurations between GUIs
    ConfigGui(ConfigGui instance, int langSelect, Language lang) {
        super(title, guiHeight, guiWidth);
        this.targetLang = instance.targetLang;
        this.speakAsLang = instance.speakAsLang;
        this.selfLang = instance.selfLang;
        this.color = instance.color;
        this.bold = instance.bold;
        this.italic = instance.italic;
        this.underline = instance.underline;
        this.translateSign = instance.translateSign;
        this.isTransition = true;
        if (lang != null) {
            switch (langSelect) {
                case 0:
                    this.targetLang = lang;
                    break;
                case 1:
                    this.selfLang = lang;
                    break;
                case 2:
                    this.speakAsLang = lang;
                    break;
            }
        }
    }

    @Override
    public void render(PoseStack stack, int x, int y, float tick) {
        super.render(stack, x, y, tick);
        drawStringLine(stack, title, null, 0);
        font.draw(stack, "Regex list:", getLeftMargin(), getYOrigin() + 25, 0x555555);
        font.draw(stack, "Target language:", getLeftMargin(), getYOrigin() + 55, 0x555555);
        font.draw(stack, "Self language:", getLeftMargin(), getYOrigin() + 75, 0x555555);
        font.draw(stack, "Speak as language:", getLeftMargin(), getYOrigin() + 95, 0x555555);
        font.draw(stack, "Preview: ", getLeftMargin(), getYOrigin() + 115, 0x555555);

        Style previewStyle = Style.EMPTY.applyFormats(ChatFormatting.getByName(color))
                .withBold(bold).withItalic(italic).withUnderlined(underline);
        font.draw(stack, new TextComponent("Notch --> English: Hello!").setStyle(previewStyle), getLeftMargin() + 45, getYOrigin() + 115, 0);
        //func_243308_b(MatrixStack, List<ITextComponent>, int, int) -> renderTooltip(...)
        Button button2,button3,button4, button5, button6, button7, button8, button9, button10, button11;
        button10 = button11 = button9 = button8 = button7 = button6 = button5 = button4 = button3 = button2 = null;
        for (int i = 0, j = 0; i < renderables.size(); i++) {
            if(renderables.get(i) instanceof Button){
                if(j == 2) button2 = (Button)renderables.get(i);
                if(j == 3) button3 = (Button)renderables.get(i);
                if(j == 4) button4 = (Button)renderables.get(i);
                if(j == 5) button5 = (Button)renderables.get(i);
                if(j == 6) button6 = (Button)renderables.get(i);
                if(j == 7) button7 = (Button)renderables.get(i);
                if(j == 8) button8 = (Button)renderables.get(i);
                if(j == 9) button9 = (Button)renderables.get(i);
                if(j == 10) button10 = (Button)renderables.get(i);
                if(j == 11) {
                    button11 = (Button)renderables.get(i);
                    break;
                }
                j++;
            }
        }
        //Target language
        if (button2 != null && button2.isHoveredOrFocused())
            renderTooltip(stack, targetTooltip, x, y);
        //Self language
        if (button3 != null && button3.isHoveredOrFocused())
            renderComponentTooltip(stack, selfTooltip, x, y);
        //Speak as language
        if (button4 != null && button4.isHoveredOrFocused())
            renderComponentTooltip(stack, speakAsTooltip, x, y);
        //Regex list
        if (button11 != null && button11.isHoveredOrFocused())
            renderComponentTooltip(stack, regexTooltip, x, y);
        //API key
        if (button6 != null && button6.isHoveredOrFocused())
            renderComponentTooltip(stack, apiKeyTooltip, x, y);
        //Translate sign
        if (button5 != null && button5.isHoveredOrFocused())
            renderComponentTooltip(stack, signTooltip, x, y);
        //Color message
        if (button7 != null && button7.isHoveredOrFocused())
            renderComponentTooltip(stack, colorTooltip, x, y);
        //Bold
        if (button8 != null && button8.isHoveredOrFocused())
            renderComponentTooltip(stack, boldTooltip, x, y);
        //Italic
        if (button9 != null && button9.isHoveredOrFocused())
            renderComponentTooltip(stack, italicTooltip, x, y);
        //Underline
        if (button10 != null && button10.isHoveredOrFocused())
            renderComponentTooltip(stack, underlineTooltip, x, y);
    }

    @Override
    public void init() {
        if (!isTransition) {
            color = ConfigManager.config.color.get();
            bold = ConfigManager.config.bold.get();
            italic = ConfigManager.config.italic.get();
            underline = ConfigManager.config.underline.get();
            translateSign = ConfigManager.config.translateSign.get();
            targetLang = LangManager.getInstance().findLanguageFromName(ConfigManager.config.targetLanguage.get());
            selfLang = LangManager.getInstance().findLanguageFromName(ConfigManager.config.selfLanguage.get());
            speakAsLang = LangManager.getInstance().findLanguageFromName(ConfigManager.config.speakAsLanguage.get());
        }
        addRenderableWidget(new Button(getLeftMargin(), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, new TextComponent("Save and close"),
                (button) -> this.applySettings()));
        addRenderableWidget(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, new TextComponent("Reset to default"),
                (button) -> this.resetDefault()));
        addRenderableWidget(new Button(getRightMargin(regularButtonWidth), getYOrigin() + 50, regularButtonWidth, regularButtonHeight, new TextComponent(targetLang.getName()),
                (button) -> this.langSelect(0)));
        addRenderableWidget(new Button(getRightMargin(regularButtonWidth), getYOrigin() + 70, regularButtonWidth, regularButtonHeight, new TextComponent(selfLang.getName()),
                (button) -> this.langSelect(1)));
        addRenderableWidget(new Button(getRightMargin(regularButtonWidth), getYOrigin() + 90, regularButtonWidth, regularButtonHeight, new TextComponent(speakAsLang.getName()),
                (button) -> this.langSelect(2)));
        addRenderableWidget(new Button(getLeftMargin(), getYOrigin() + guiHeight - 15 - regularButtonHeight * 3, regularButtonWidth, regularButtonHeight, new TextComponent(translateSign ? ChatFormatting.GREEN + "Translate signs" : ChatFormatting.RED + "Translate signs"),
                (button) -> {
                    translateSign = !translateSign;
                    this.toggleButtonBool(translateSign, button);
                }));
        addRenderableWidget(new Button(getLeftMargin(), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, new TextComponent("Engine options"),
                (button) -> this.addKeyGui()));
        addRenderableWidget(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, new TextComponent(ChatFormatting.getByName(color) + "Message color"),
                this::rotateColor));
        addRenderableWidget(new Button(getLeftMargin() + regularButtonWidth + 10, getYOrigin() + guiHeight - 15 - regularButtonHeight * 3, smallButtonLength, smallButtonLength, new TextComponent(bold ? "\u00a7a" + ChatFormatting.BOLD + "B" : "\u00a7c" + ChatFormatting.BOLD + "B"),
                (button) -> {
                    bold = !bold;
                    this.toggleButtonBool(bold, button, ChatFormatting.BOLD);
                }));
        addRenderableWidget(new Button(getLeftMargin() + regularButtonWidth + 10, getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, smallButtonLength, smallButtonLength, new TextComponent(italic ? "\u00a7a" + ChatFormatting.ITALIC + "I" : "\u00a7c" + ChatFormatting.ITALIC + "I"),
                (button) -> {
                    italic = !italic;
                    this.toggleButtonBool(italic, button, ChatFormatting.ITALIC);
                }));
        addRenderableWidget(new Button(getLeftMargin() + regularButtonWidth + 10, getYOrigin() + guiHeight - 5 - regularButtonHeight, smallButtonLength, smallButtonLength, new TextComponent(underline ? "\u00a7a" + ChatFormatting.UNDERLINE + "U" : "\u00a7c" + ChatFormatting.UNDERLINE + "U"),
                (button) -> {
                    underline = !underline;
                    this.toggleButtonBool(underline, button, ChatFormatting.UNDERLINE);
                }));
        addRenderableWidget(new Button(getRightMargin(regularButtonWidth), getYOrigin() + 20, regularButtonWidth, regularButtonHeight, new TextComponent("View / Add"),
                (button) -> this.regexGui()));
    }

    private void applySettings() {
        getMinecraft().keyboardHandler.setSendRepeatsToGui(false);
        ConfigManager.config.targetLanguage.set(targetLang.getName());
        ConfigManager.config.selfLanguage.set(selfLang.getName());
        ConfigManager.config.speakAsLanguage.set(speakAsLang.getName());
        ConfigManager.config.color.set(color);
        ConfigManager.config.bold.set(bold);
        ConfigManager.config.italic.set(italic);
        ConfigManager.config.underline.set(underline);
        ConfigManager.config.translateSign.set(translateSign);
        ConfigManager.saveConfig();
        ChatUtil.printChatMessage(true, "Settings applied.", ChatFormatting.WHITE);
        exitGui();
    }

    private void exitGui() {
        getMinecraft().keyboardHandler.setSendRepeatsToGui(false);
        getMinecraft().setScreen(null);
    }

    private void langSelect(int id) {
        getMinecraft().pushGuiLayer(new LanguageSelectGui(this, id));
    }

    private void toggleButtonBool(boolean state, Button button, ChatFormatting prefixFormat) {
        String rawString = button.getMessage().getString().replaceAll("\u00a7(.)", "");
        TextComponent buttonText;
        ChatFormatting color = state ? ChatFormatting.GREEN : ChatFormatting.RED;
        if (prefixFormat == null) {
            buttonText = new TextComponent(color + rawString);
        } else {
            buttonText = new TextComponent(prefixFormat + "" + color + rawString);
        }
        button.setMessage(buttonText);
    }

    private void toggleButtonBool(boolean state, Button button) {
        toggleButtonBool(state, button, null);
    }

    @SuppressWarnings("ConstantConditions")
    private void rotateColor(Button button) {
        ChatFormatting formatColor = ChatFormatting.getByName(color);
        int colorCode = formatColor.getColor();
        colorCode++;
        colorCode = colorCode & 0xf;
        ChatFormatting newColor = ChatFormatting.getById(colorCode);
        color = newColor.getName();
        TextComponent buttonText = new TextComponent(newColor + button.getMessage().getString().replaceAll("\u00a7(.)", ""));
        button.setMessage(buttonText);
    }

    private void regexGui() {
        getMinecraft().keyboardHandler.setSendRepeatsToGui(true);
        getMinecraft().setScreen(new RegexGui());
    }

    private void addKeyGui() {
        getMinecraft().keyboardHandler.setSendRepeatsToGui(true);
        getMinecraft().setScreen(new EngineGui());
    }

    private void resetDefault() {
        color = "gray";
        bold = false;
        italic = false;
        underline = false;
        translateSign = true;
        targetLang = LangManager.getInstance().findLanguageFromName("English");
        selfLang = targetLang;
        speakAsLang = LangManager.getInstance().findLanguageFromName("Japanese");
        Button button2,button3,button4, button5, button7, button8, button9, button10;
        button10 = button9 = button8 = button7 = button5 = button4 = button3 = button2 = null;
        for (int i = 0, j = 0; i < renderables.size(); i++) {
            if(renderables.get(i) instanceof Button){
                if(j == 2) button2 = (Button)renderables.get(i);
                if(j == 3) button3 = (Button)renderables.get(i);
                if(j == 4) button4 = (Button)renderables.get(i);
                if(j == 5) button5 = (Button)renderables.get(i);
                if(j == 7) button7 = (Button)renderables.get(i);
                if(j == 8) button8 = (Button)renderables.get(i);
                if(j == 9) button9 = (Button)renderables.get(i);
                if(j == 10) {
                    button10 = (Button)renderables.get(i);
                    break;
                }
                j++;
            }
        }
        if(button2 != null) {
            button2.setMessage(new TextComponent("English"));
        }
        if(button3 != null) {
            button3.setMessage(new TextComponent("English"));
        }
        if(button4 != null) {
            button4.setMessage(new TextComponent("Japanese"));
        }
        if(button5 != null) {
            button5.setMessage(new TextComponent(ChatFormatting.GREEN + "Translate signs"));
        }
        if(button7 != null) {
            button7.setMessage(new TextComponent(ChatFormatting.getByName(color) + "Message color"));
        }
        if(button8 != null) {
            button8.setMessage(new TextComponent(bold ? "\u00a7a" : "\u00a7c" + ChatFormatting.BOLD + "B"));
        }
        if(button9 != null) {
            button9.setMessage(new TextComponent(italic ? "\u00a7a" : "\u00a7c" + ChatFormatting.ITALIC + "I"));
        }
        if(button10 != null) {
            button10.setMessage(new TextComponent(underline ? "\u00a7a" : "\u00a7c" + ChatFormatting.UNDERLINE + "U"));
        }
    }
}
