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

package com.ringosham.translationmod.common;

import com.ringosham.translationmod.TranslationMod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public class ChatUtil {
    private static final String prefix = ChatFormatting.GREEN + "[" + ChatFormatting.WHITE + "RTTM" + ChatFormatting.GREEN + "] " + ChatFormatting.RESET;

    public static void printChatMessage(boolean addPrefix, String message, ChatFormatting color) {
        //Color.func_240774_a_(ChatFormatting) -> Color.fromChatFormatting(...)
        Style style = Style.EMPTY.withColor(color);
        Minecraft.getInstance().gui.getChat().addMessage((new TextComponent((addPrefix ? prefix : "") + color + message).withStyle(style)));
    }

    public static void printChatMessageAdvanced(String message, String hoverText, boolean bold, boolean italic, boolean underline, ChatFormatting color) {
        //Styles are immutable in 1.16. So we have that...
        Style style = Style.EMPTY.withColor(color);
        if (bold)
            style = style.withBold(true);
        if (italic)
            style = style.withItalic(true);
        if (underline)
            style = style.withUnderlined(true);

        if (hoverText != null)
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(hoverText)));
        Minecraft.getInstance().gui.getChat().addMessage(new TextComponent(message).withStyle(style));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void printCredits() {
        String version = ModList.get().getModContainerById(TranslationMod.MODID).get().getModInfo().getVersion().toString();
        ChatUtil.printChatMessage(false, "Real-time translation mod by Ringosham. Version " + version, ChatFormatting.AQUA);
        ChatUtil.printChatMessage(false, "Online translation services powered by Google", ChatFormatting.AQUA);
        ChatUtil.printChatMessage(false, "Translation results may not be 100% accurate", ChatFormatting.AQUA);
    }
}
