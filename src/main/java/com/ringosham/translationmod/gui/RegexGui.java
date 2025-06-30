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

import com.google.common.primitives.Ints;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexGui extends CommonGui {
    //Regex must not be in conflict of the translated message. Otherwise the mod will stuck in a loop spamming the server.
    private static final String testMessage = "Notch --> English: Hello!";
    private static final int guiWidth = 400;
    private static final int guiHeight = 230;
    private static final List<Component> cheatsheet;
    private static final List<List<Component>> cheatsheetDesc;
    private static final String regexTest = "https://regexr.com";
    private static final String title;

    static {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String modName = ModList.get().getModContainerById(TranslationMod.MODID).get().getModInfo().getDisplayName();
        title = modName + " - Regex list";
    }

    static {
        cheatsheet = new ArrayList<>();
        cheatsheetDesc = new ArrayList<>();
        for (int i = 0; i < 12; i++)
            cheatsheetDesc.add(new ArrayList<>());
        cheatsheet.add(new TextComponent(". - Matches any character"));
        cheatsheetDesc.get(0).add(new TextComponent("Matches any character"));
        cheatsheetDesc.get(0).add(new TextComponent("The only exception is the newline character (\\n)"));
        cheatsheetDesc.get(0).add(new TextComponent("Newlines are not used in chat so it doesn't matter"));

        cheatsheet.add(new TextComponent("\\w - Matches word"));
        cheatsheetDesc.get(1).add(new TextComponent("Matches all alphabets (Both capital and small), numbers and underscore"));
        cheatsheetDesc.get(1).add(new TextComponent("Minecraft usernames are based on words. They are perfect to detect player names"));

        cheatsheet.add(new TextComponent("\\d - Digit"));
        cheatsheetDesc.get(2).add(new TextComponent("Matches all numbers"));

        cheatsheet.add(new TextComponent("[a-g] - Match character in range"));
        cheatsheetDesc.get(3).add(new TextComponent("Matches any characters in tis specific range"));
        cheatsheetDesc.get(3).add(new TextComponent("Example: [a-g]"));
        cheatsheetDesc.get(3).add(new TextComponent("\u2713 " + ChatFormatting.GREEN + "a"));
        cheatsheetDesc.get(3).add(new TextComponent("\u2713 " + ChatFormatting.GREEN + "b"));
        cheatsheetDesc.get(3).add(new TextComponent("\u2717 " + ChatFormatting.RED + "z"));

        cheatsheet.add(new TextComponent("* - Matches 0 or more"));
        cheatsheetDesc.get(4).add(new TextComponent("Matches 0 or more of its character class"));
        cheatsheetDesc.get(4).add(new TextComponent("Example: N\\w*"));
        cheatsheetDesc.get(4).add(new TextComponent("\u2713 " + ChatFormatting.GREEN + "N"));
        cheatsheetDesc.get(4).add(new TextComponent("\u2713 " + ChatFormatting.GREEN + "No"));
        cheatsheetDesc.get(4).add(new TextComponent("\u2713 " + ChatFormatting.GREEN + "Notch"));

        cheatsheet.add(new TextComponent("+ - Matches 1 or more"));
        cheatsheetDesc.get(5).add(new TextComponent("Matches 1 or more of a character/group"));
        cheatsheetDesc.get(5).add(new TextComponent("Example: N\\w+"));
        cheatsheetDesc.get(5).add(new TextComponent("\u2717 " + ChatFormatting.RED + "N"));
        cheatsheetDesc.get(5).add(new TextComponent("\u2713 " + ChatFormatting.GREEN + "No"));
        cheatsheetDesc.get(5).add(new TextComponent("\u2713 " + ChatFormatting.GREEN + "Notch"));

        cheatsheet.add(new TextComponent("? - Optional"));
        cheatsheetDesc.get(6).add(new TextComponent("Exactly as the name suggests"));
        cheatsheetDesc.get(6).add(new TextComponent("Example: (VIP )?\\w+"));
        cheatsheetDesc.get(6).add(new TextComponent("\u2713 " + ChatFormatting.GREEN + "VIP PlayerName"));
        cheatsheetDesc.get(6).add(new TextComponent("\u2713 " + ChatFormatting.GREEN + "PlayerName"));

        cheatsheet.add(new TextComponent("{2,} - Matches n or more"));
        cheatsheetDesc.get(7).add(new TextComponent("Matches a group/character n times or more"));
        cheatsheetDesc.get(7).add(new TextComponent("Add a number after the comma if you want the it match x to y times"));
        cheatsheetDesc.get(7).add(new TextComponent("Or omit the comma if you want the it match exactly n times"));
        cheatsheetDesc.get(7).add(new TextComponent("Example: Level \\d{1,3}"));
        cheatsheetDesc.get(7).add(new TextComponent("\u2713 " + ChatFormatting.GREEN + "Level 1"));
        cheatsheetDesc.get(7).add(new TextComponent("\u2713 " + ChatFormatting.GREEN + "Level 420"));
        cheatsheetDesc.get(7).add(new TextComponent("\u2717 " + ChatFormatting.RED + "Level 42069"));


        cheatsheet.add(new TextComponent("| - Either"));
        cheatsheetDesc.get(8).add(new TextComponent("Must match either of them, but not both."));
        cheatsheetDesc.get(8).add(new TextComponent("Example: (Dead)|(Alive) (\\w+)"));
        cheatsheetDesc.get(8).add(new TextComponent("\u2713 " + ChatFormatting.GREEN + "Dead PlayerName"));
        cheatsheetDesc.get(8).add(new TextComponent("\u2713 " + ChatFormatting.GREEN + "Alive PlayerName"));
        cheatsheetDesc.get(8).add(new TextComponent("\u2717 " + ChatFormatting.RED + "DeadAlive PlayerName"));

        cheatsheet.add(new TextComponent("() - Group"));
        cheatsheetDesc.get(9).add(new TextComponent("Think of groups as parentheses like in mathematics"));
        cheatsheetDesc.get(9).add(new TextComponent("They also have a second function. Capture groups."));
        cheatsheetDesc.get(9).add(new TextComponent("By specifying the group number below, the mod can know which group"));
        cheatsheetDesc.get(9).add(new TextComponent(" contains the player's username"));

        cheatsheet.add(new TextComponent("\\ - Escape character"));
        cheatsheetDesc.get(10).add(new TextComponent("If you need to capture special characters mentioned in this list,"));
        cheatsheetDesc.get(10).add(new TextComponent(" you will need to add an extra backslash to escape them."));
        cheatsheetDesc.get(10).add(new TextComponent("Correct:" + ChatFormatting.GREEN + " \\(VIP\\) \\w+"));
        cheatsheetDesc.get(10).add(new TextComponent("Wrong:" + ChatFormatting.RED + " (VIP) \\w+"));
    }

    private int index;
    private final LinkedList<String> regexes = new LinkedList<>();
    private final LinkedList<Integer> groups = new LinkedList<>();
    private EditBox regexTextBox;
    private EditBox groupTextBox;

    {
        regexes.addAll(ConfigManager.config.regexList.get());
        groups.addAll(ConfigManager.config.groupList.get());
        index = regexes.size() - 1;
    }

    RegexGui() {
        super(title, guiHeight, guiWidth);
    }

    @Override
    public void render(PoseStack stack, int x, int y, float tick) {
        super.render(stack, x, y, tick);
        drawStringLine(stack, title, new String[]{
                "Regex(Regular expression) are search patterns used to detect messages.",
                "You can use this website to test your regex.",
                "Cheatsheet: (Hover your mouse to see explanation)",
        }, 0);
        font.draw(stack, "TIP: Combine classes and quantifiers together to match several characters", getLeftMargin(), getYOrigin() + guiHeight - 40, 0x555555);
        font.draw(stack, (index + 1) + " of " + Math.max(index + 1, regexes.size()), getLeftMargin() + 15 + smallButtonLength * 2, getYOrigin() + guiHeight - regularButtonHeight, 0x555555);
        String regex = regexTextBox.getValue();
        int group = groupTextBox.getValue().isEmpty() ? -1 : Integer.parseInt(groupTextBox.getValue());
        if (validateRegex(regex)) {
            if (!isRegexConflict(regex)) {
                int groupCount = countGroups(regex);
                if (groupCount == 0)
                    font.draw(stack, ChatFormatting.YELLOW + "Regex valid, but it needs at least 1 group to detect player names", getLeftMargin(), getYOrigin() + guiHeight - 120, 0x555555);
                else
                    font.draw(stack, ChatFormatting.GREEN + "Regex valid! The regex should stop at before the message content", getLeftMargin(), getYOrigin() + guiHeight - 120, 0x555555);
                font.draw(stack, "Possible match: " + findMatch(getChatLog(), regex), getLeftMargin(), getYOrigin() + guiHeight - 110, 0x555555);
                if (groupCount > 0)
                    font.draw(stack, "Group number: (1 - " + groupCount + ")", getLeftMargin(), getYOrigin() + guiHeight - 80, 0x555555);
                else
                    font.draw(stack, "Group number: (?)", getLeftMargin(), getYOrigin() + guiHeight - 80, 0x555555);
                font.draw(stack, "Matching username: " + matchUsername(findMatch(getChatLog(), regex), regex, group), getLeftMargin(), getYOrigin() + guiHeight - 70, 0x555555);
            } else {
                font.draw(stack, ChatFormatting.RED + "Regex conflict with the mod messages! Please be more specific", getLeftMargin(), getYOrigin() + guiHeight - 120, 0x555555);
                font.draw(stack, "Possible match: ---", getLeftMargin(), getYOrigin() + guiHeight - 110, 0x555555);
                font.draw(stack, "Matching username: ---", getLeftMargin(), getYOrigin() + guiHeight - 70, 0x555555);
                font.draw(stack, "Group number: (?)", getLeftMargin(), getYOrigin() + guiHeight - 80, 0x555555);
            }
        } else {
            font.draw(stack, ChatFormatting.RED + "Regex invalid! Please check your syntax", getLeftMargin(), getYOrigin() + guiHeight - 120, 0x555555);
            font.draw(stack, "Possible match: ---", getLeftMargin(), getYOrigin() + guiHeight - 110, 0x555555);
            font.draw(stack, "Matching username: ---", getLeftMargin(), getYOrigin() + guiHeight - 70, 0x555555);
            font.draw(stack, "Group number: (?)", getLeftMargin(), getYOrigin() + guiHeight - 80, 0x555555);
        }
        regexTextBox.render(stack, x, y, tick);
        groupTextBox.render(stack, x, y, tick);
        //Draw tooltips
        for (int i = 0, j = 0; i < renderables.size(); i++) {
            if(renderables.get(i) instanceof Button) {
                if(j >= 5){
                    HoveringText button = (HoveringText) renderables.get(i);
                    if (button.isHoveredOrFocused())
                        //func_243308_b(PoseStack, List<Component>, int, int) -> renderTooltip(...)
                        renderComponentTooltip(stack, button.getHoverText(), x, y);
                }
                j++;
            }
        }
    }

    @Override
    public void init() {
        regexTextBox = new EditBox(this.font, getLeftMargin(), getYOrigin() + guiHeight - 100, guiWidth - 10, 15, new TextComponent(""));
        regexTextBox.setCanLoseFocus(true);
        regexTextBox.setMaxLength(200);
        regexTextBox.setBordered(true);
        regexTextBox.setValue(regexes.get(index));
        groupTextBox = new EditBox(this.font, getLeftMargin(), getYOrigin() + guiHeight - 60, guiWidth - 10, 15, new TextComponent(""));
        groupTextBox.setCanLoseFocus(true);
        groupTextBox.setMaxLength(10);
        groupTextBox.setBordered(true);
        groupTextBox.setValue(Integer.toString(groups.get(index)));
        addRenderableWidget(groupTextBox);
        addRenderableWidget(regexTextBox);
        getMinecraft().keyboardHandler.setSendRepeatsToGui(false);
        addRenderableWidget(new TextButton(getRightMargin(150), getYOrigin() + 25, getTextWidth(regexTest), new TextComponent(regexTest),
                (button) -> this.openLink(), 0x0000aa));
        addRenderableWidget(new Button(getLeftMargin() + 5 + smallButtonLength, getYOrigin() + guiHeight - 5 - regularButtonHeight, smallButtonLength, smallButtonLength, new TextComponent("+"),
                this::nextPage));
        addRenderableWidget(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, new TextComponent("Save and close"),
                (button) -> this.applySettings()));
        addRenderableWidget(new Button(getLeftMargin(), getYOrigin() + guiHeight - 5 - regularButtonHeight, smallButtonLength, smallButtonLength, new TextComponent("<"),
                this::previousPage));
        addRenderableWidget(new Button(getRightMargin(regularButtonWidth) - 5 - regularButtonWidth, getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, new TextComponent("Reset to default"),
                (button) -> this.resetDefault()));
        //Needs to be cleared since resizing the window calls initGui() again
        addRenderableWidget(new HoveringText(getLeftMargin(), getYOrigin() + 45, cheatsheet.get(0), cheatsheetDesc.get(0)));
        addRenderableWidget(new HoveringText(getLeftMargin(), getYOrigin() + 55, cheatsheet.get(1), cheatsheetDesc.get(1)));
        addRenderableWidget(new HoveringText(getLeftMargin(), getYOrigin() + 65, cheatsheet.get(2), cheatsheetDesc.get(2)));
        addRenderableWidget(new HoveringText(getLeftMargin(), getYOrigin() + 75, cheatsheet.get(3), cheatsheetDesc.get(3)));
        addRenderableWidget(new HoveringText(getLeftMargin(), getYOrigin() + 85, cheatsheet.get(4), cheatsheetDesc.get(4)));
        addRenderableWidget(new HoveringText(getLeftMargin(), getYOrigin() + 95, cheatsheet.get(5), cheatsheetDesc.get(5)));
        addRenderableWidget(new HoveringText(getLeftMargin() + 210, getYOrigin() + 45, cheatsheet.get(6), cheatsheetDesc.get(6)));
        addRenderableWidget(new HoveringText(getLeftMargin() + 210, getYOrigin() + 55, cheatsheet.get(7), cheatsheetDesc.get(7)));
        addRenderableWidget(new HoveringText(getLeftMargin() + 210, getYOrigin() + 65, cheatsheet.get(8), cheatsheetDesc.get(8)));
        addRenderableWidget(new HoveringText(getLeftMargin() + 210, getYOrigin() + 75, cheatsheet.get(9), cheatsheetDesc.get(9)));
        addRenderableWidget(new HoveringText(getLeftMargin() + 210, getYOrigin() + 85, cheatsheet.get(10), cheatsheetDesc.get(10)));
    }

    private void openLink() {
        getMinecraft().keyboardHandler.setSendRepeatsToGui(false);
        getMinecraft().setScreen(new ConfirmLinkScreen((ConfirmOpen) -> {
            if (ConfirmOpen)
                Util.getPlatform().openUri(regexTest);
            getMinecraft().setScreen(this);
        }, regexTest, false));
        getMinecraft().keyboardHandler.setSendRepeatsToGui(true);
    }

    private void nextPage(Button button) {
        regexes.set(index, regexTextBox.getValue());
        if (groupTextBox.getValue().trim().isEmpty())
            groups.set(index, 0);
        else
            groups.set(index, Integer.parseInt(groupTextBox.getValue()));
        index++;
        if (index == regexes.size()) {
            button.active = false;
            regexes.add("");
            groups.add(1);
            regexTextBox.setValue("");
            groupTextBox.setValue("1");
        } else {
            regexTextBox.setValue(regexes.get(index));
            groupTextBox.setValue(groups.get(index).toString());
            button.setMessage(new TextComponent(">"));
            button.active = true;
        }
        if (index >= regexes.size() - 1)
            button.setMessage(new TextComponent("+"));
        for (int i = 0, j = 0; i < renderables.size(); i++) {
            if(renderables.get(i) instanceof Button button1){
                if(j == 3){
                    button1.active = true;
                    break;
                }
                j++;
            }
        }
        regexTextBox.moveCursorToEnd();
        getMinecraft().keyboardHandler.setSendRepeatsToGui(true);
    }

    private void previousPage(Button button) {
        //Discard changes if the textboxes are empty.
        if (regexTextBox.getValue().trim().isEmpty() || groupTextBox.getValue().isEmpty()) {
            regexes.remove(index);
            groups.remove(index);
        } else {
            regexes.set(index, regexTextBox.getValue());
            groups.set(index, Integer.parseInt(groupTextBox.getValue()));
        }
        index--;
        if (index == 0)
            button.active = false;
        Button button1 = null;
        for (int i = 0, j = 0; i < renderables.size(); i++) {
            if(renderables.get(i) instanceof Button){
                if(j == 1) {
                    button1 = (Button)renderables.get(i);
                    button1.active = true;
                    break;
                }
                j++;
            }
        }
        if(button1 != null){
            if (regexes.size() - 1 == index)
                button1.setMessage(new TextComponent("+"));
            else
                button1.setMessage(new TextComponent(">"));
            button1.active = true;
        }

        regexTextBox.setValue(regexes.get(index));
        groupTextBox.setValue(groups.get(index).toString());
        regexTextBox.moveCursorToEnd();
        getMinecraft().keyboardHandler.setSendRepeatsToGui(true);
    }

    private void resetDefault() {
        Button button1 = null;
        Button button3 = null;
        for (int i = 0, j = 0; i < renderables.size(); i++) {
            if(renderables.get(i) instanceof Button){
                if(j == 1) button1 = (Button)renderables.get(i);
                if(j == 3) {
                    button3 = (Button)renderables.get(i);
                    break;
                }
                j++;
            }
        }
        if(button3 != null) button3.active = true;
        if(button1 != null) button1.setMessage(new TextComponent("+"));
        regexes.clear();
        regexes.addAll(Arrays.asList(ConfigManager.defaultRegex));
        groups.clear();
        groups.addAll(Ints.asList(ConfigManager.defaultGroups));
        index = regexes.size() - 1;
        regexTextBox.setValue(regexes.get(index));
        groupTextBox.setValue(groups.get(index).toString());
        regexTextBox.moveCursorToEnd();
        getMinecraft().keyboardHandler.setSendRepeatsToGui(true);
    }

    private void exitGui() {
        getMinecraft().keyboardHandler.setSendRepeatsToGui(false);
        getMinecraft().setScreen(null);
    }

    @Override
    public boolean charTyped(char typedchar, int keyCode) {
        if (this.groupTextBox.isFocused()) {
            if (typedchar >= '0' && typedchar <= '9')
                //No group 0 allowed.
                if (this.groupTextBox.getValue().isEmpty() && typedchar != 48)
                    return super.charTyped(typedchar, keyCode);
                else if (!this.groupTextBox.getValue().isEmpty())
                    return super.charTyped(typedchar, keyCode);
            return false;
        }
        return super.charTyped(typedchar, keyCode);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
        if (keyCode == GLFW.GLFW_KEY_E && !this.regexTextBox.isFocused()) {
            getMinecraft().setScreen(null);
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifier);
    }

    private boolean validateRegex(String regex) {
        if (regex == null)
            return false;
        if (regex.trim().isEmpty())
            return false;
        try {
            Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            return false;
        }
        return true;
    }

    private int countGroups(String regex) {
        Pattern pattern = Pattern.compile(regex);
        //Why is matching even needed... This is stupid.
        Matcher matcher = pattern.matcher("Reality is a shitty game! -Katsuragi Keima");
        return matcher.groupCount();
    }

    //Ensure the regex does not conflict with the translated chat output.
    private boolean isRegexConflict(String regex) {
        if (!regex.contains("^"))
            regex = "^" + regex;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(testMessage);
        return matcher.find();
    }

    //Gets the chat log of 20 messages for testing regex
    @SuppressWarnings("ConstantConditions")
    private List<String> getChatLog() {
        //Chat log is a private field.
        List<GuiMessage<Component>> fullChatLog = ObfuscationReflectionHelper.getPrivateValue(ChatComponent.class, Minecraft.getInstance().gui.getChat(), "allMessages");
        //For 1.7.10 debug use.
        //List<ChatLine> fullChatLog = ObfuscationReflectionHelper.getPrivateValue(GuiNewChat.class, Minecraft.getInstance().ingameGUI.getChatGUI(), "chatLines");
        List<String> chatLog = new ArrayList<>();
        for (int i = 0; i < Math.min(fullChatLog.size(), 20); i++) {
            //func_238169_a_() --> getComponent()
            Component chatLine = fullChatLog.get(i).getMessage();
            if (chatLine instanceof TranslatableComponent ttc) {
	            chatLog.add(ttc.getString().replaceAll("§(.)", ""));
            } else {
                chatLog.add(fullChatLog.get(i).getMessage().getString().replaceAll("§(.)", ""));
            }
        }
        return chatLog;
    }

    //An indicator to see how much the regex matches the chat message
    private String findMatch(List<String> chatLog, String regex) {
        if (!regex.contains("^"))
            regex = "^" + regex;
        Pattern pattern = Pattern.compile(regex);
        for (String message : chatLog) {
            Matcher matcher = pattern.matcher(message);
            if (!matcher.find())
                continue;
            String matchMessage = ChatFormatting.GREEN + matcher.group(0) + ChatFormatting.DARK_GRAY + message.replace(matcher.group(0), "");
            String shorten = matchMessage;
            for (int i = getTextWidth(matchMessage); i > 120; i--) {
                shorten = shorten.substring(0, matchMessage.length() - 1);
            }
            shorten = shorten + "...";
            return matchMessage.length() < shorten.length() ? matchMessage : shorten;
        }
        return ChatFormatting.RED + "No match from chat log :(";
    }

    private String matchUsername(String message, String regex, int group) {
        if (group == -1 || group > countGroups(regex) || message.equals(ChatFormatting.RED + "No match from chat log :("))
            return "---";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        if (!matcher.find())
            return ChatFormatting.RED + "Can't find player username :(";
        return matcher.group(group);
    }

    private void applySettings() {
        regexes.set(index, regexTextBox.getValue());
        if (groupTextBox.getValue().trim().isEmpty())
            groups.set(index, 0);
        else
            groups.set(index, Integer.parseInt(groupTextBox.getValue()));
        for (int i = 0; i < regexes.size(); i++) {
            if (!validateRegex(regexes.get(i)) || isRegexConflict(regexes.get(i))) {
                regexes.remove(i);
                groups.remove(i);
                i--;
                continue;
            }
            int groupCount = countGroups(regexes.get(i));
            if (groupCount < groups.get(i)) {
                regexes.remove(i);
                groups.remove(i);
                i--;
            }
        }
        ConfigManager.config.regexList.set(regexes);
        ConfigManager.config.groupList.set(groups);
        //Let the manager do all the validation
        ConfigManager.validateConfig();
        ChatUtil.printChatMessage(true, "Regex list applied", ChatFormatting.WHITE);
        exitGui();
    }

    //Must be inner class due to protected access to renderTooltip in GuiScreen
    public class HoveringText extends Button {
        private final List<Component> hoverText;

        HoveringText(int x, int y, Component text, List<Component> hoverText) {
            super(x, y, getTextWidth(text.getString()), 10, text, (button) -> {
            });
            this.hoverText = hoverText;
        }

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float tick) {
            RenderSystem.setShaderColor(1, 1, 1, 1);
            font.draw(stack, this.getMessage().getString(), x, y, 0xFF555555);
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        }

        List<Component> getHoverText() {
            return hoverText;
        }
    }
}
