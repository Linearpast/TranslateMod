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

import com.google.common.primitives.Ints;
import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.client.types.Language;
import net.minecraft.ChatFormatting;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class ConfigManager {
    public static final ClientConfig config;
    public static final ForgeConfigSpec configSpec;
    public static final String[] defaultRegex = {
            "<(\\w+)> (?!\\[UseTranslateMod\\])"//Default
    };
    public static final int[] defaultGroups = {
            1
    };
    private static final String[] engines = {"google", "baidu"};
    //In case there are future updates that drastically change how the mod works. This variable would be here to check if the configs are out of date.
    private static final int configMinVersion = 1;

    static {
        final Pair<ClientConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        configSpec = pair.getRight();
        config = pair.getLeft();
    }

    //Sync all config values to the mod. As well as error checks
    public static void validateConfig() {
        versionCheck();
        //Validations to prevent dumbasses messing with the mod config through notepad
        //Regex validation
        boolean valid = true;
        List<String> regexList = config.regexList.get();
        List<Integer> groupList = config.groupList.get();
        Iterator<String> regexIt = regexList.iterator();
        int index = 0;
        while (regexIt.hasNext()) {
            String regex = regexIt.next();
            try {
                //Apparently Java does not have a method to check if a regex is valid. The only to do this is to catch exceptions.
                Pattern.compile(regex);
            } catch (PatternSyntaxException e) {
                valid = false;
                regexIt.remove();
                groupList.remove(index);
                //-1 is needed as everything in the array is shifted left
                index--;
            }
            //Make sure the group number is not less than or equal to 0
            if (groupList.get(index) <= 0) {
                valid = false;
                regexIt.remove();
                groupList.remove(index);
                index--;
            }
            index++;
        }

        //Validates the color.
        ArrayList<String> colors = new ArrayList<>(ChatFormatting.getNames(true, false));
        if (!colors.contains(config.color.get())) {
            config.color.set("gray");
            valid = false;
        }
        if (!valid) {
            config.regexList.set(regexList);
            config.groupList.set(groupList);
            saveConfig();
        }
    }

    private static void versionCheck() {
        int configVersion = config.configMinVersion.get();
        //In case there might be any major updates that would break under existing configs, this is here to reset everything.
        if (configMinVersion > configVersion) {
            config.targetLanguage.set("English");
            config.selfLanguage.set("English");
            config.speakAsLanguage.set("Japanese");
            config.bold.set(false);
            config.italic.set(false);
            config.underline.set(false);
            config.translateSign.set(true);
            config.modAble.set(true);
            config.color.set("gray");
            config.regexList.set(Arrays.asList(defaultRegex));
            config.groupList.set(Ints.asList(defaultGroups));
            config.configMinVersion.set(configMinVersion);
            config.translationEngine.set(engines[0]);
        }
    }

    public static void saveConfig() {
        configSpec.save();
        config.selfLanguage.save();
        config.targetLanguage.save();
        config.speakAsLanguage.save();
        config.groupList.save();
        config.regexList.save();
        config.bold.save();
        config.italic.save();
        config.underline.save();
        config.color.save();
        config.configMinVersion.save();
        config.googleKey.save();
        config.translateSign.save();
        config.baiduKey.save();
        config.baiduAppId.save();
        config.translationEngine.save();
        config.modAble.save();
        validateConfig();
    }

    //1.13+ no longer requires long property definition. Yay!
    public static class ClientConfig {
        public final ConfigValue<String> targetLanguage;
        public final ConfigValue<String> selfLanguage;
        public final ConfigValue<String> speakAsLanguage;
        public final BooleanValue bold;
        public final BooleanValue italic;
        public final BooleanValue underline;
        public final ConfigValue<String> color;
        public final BooleanValue translateSign;
        public final ConfigValue<String> googleKey;
        public final ConfigValue<List<String>> regexList;
        public final ConfigValue<List<Integer>> groupList;
        public final ConfigValue<String> baiduAppId;
        public final ConfigValue<String> baiduKey;
        public final ConfigValue<String> translationEngine;
        public final BooleanValue modAble;
        final IntValue configMinVersion;


        ClientConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Real time translation mod configs").push(TranslationMod.MODID);
            modAble = builder.comment("switch").define("modAble", true);
            configMinVersion = builder.comment("Config version. DO NOT CHANGE.").defineInRange("configMinVersion", 1, 0, Integer.MAX_VALUE);
            targetLanguage = builder.comment("Target language to translationmod.mixins.json for the chat").define("targetLanguage", "English", lang -> validateLang((String) lang));
            selfLanguage = builder.comment("The language the user types").define("selfLanguage", "English", lang -> validateLang((String) lang));
            speakAsLanguage = builder.comment("The language the user wants their message to translationmod.mixins.json to").define("speakAsLanguage", "Japanese", lang -> validateLang((String) lang));
            bold = builder.comment("Bold the translated message").define("bold", false);
            italic = builder.comment("Italic the translated message").define("italic", false);
            underline = builder.comment("Underline the translated message").define("underline", false);
            color = builder.comment("Changes the color of the translated message").define("color", "gray", color -> {
                List<String> colors = new ArrayList<>(ChatFormatting.getNames(true, false));
                String c = (String) color;
                return colors.contains(c);
            });
            translateSign = builder.comment("Allows translating texts in sign by looking").define("translateSign", true);
            googleKey = builder.comment("Your Google Cloud translation API key").define("googleKey", "");
            //Not using forge to correct. It will just replace the entire list with the default.
            regexList = builder.comment("Your regex list").define("regexList", Arrays.asList(defaultRegex), o -> true);
            groupList = builder.comment("Your match group number to detect player names").define("groupList", Ints.asList(defaultGroups), o -> true);
            baiduAppId = builder.comment("Your Baidu developer App ID").define("baiduAppId", "");
            baiduKey = builder.comment("Your Baidu API key").define("baiduKey", "");
            translationEngine = builder.comment("Translation engine used").define("translationEngine", "google", o -> {
                String value = (String) o;
                return Arrays.asList(engines).contains(value);
            });
        }

        private boolean validateLang(String lang) {
            if (lang == null)
                return false;
            List<Language> languages = LangManager.getInstance().getAllLanguages();
            for (Language language : languages) {
                if (lang.equals(language.getName()))
                    return true;
            }
            return false;
        }
    }
}
