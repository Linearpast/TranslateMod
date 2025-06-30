package com.ringosham.translationmod.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.client.types.Language;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.translate.Retranslate;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fml.ModList;

public class LanguageSelectGui extends CommonGui {
    private static final int guiWidth = 400;
    private static final int guiHeight = 200;
    private static final String title;

    static {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String modName = ModList.get().getModContainerById(TranslationMod.MODID).get().getModInfo().getDisplayName();
        title = modName + " - Language select";
    }

    private final ConfigGui config;
    private final int langSelect;
    private final String sender;
    private final String message;
    private LangList langList;

    //Calling from ConfigGui
    LanguageSelectGui(ConfigGui config, int langSelect) {
        super(title, guiHeight, guiWidth);
        this.config = config;
        this.langSelect = langSelect;
        this.message = null;
        this.sender = null;
    }

    //Calling from RetranslateGui
    LanguageSelectGui(String sender, String message) {
        super(title, guiHeight, guiWidth);
        this.message = message;
        this.sender = sender;
        this.config = null;
        this.langSelect = -1;
    }

    @Override
    public void render(PoseStack stack, int x, int y, float tick) {
        super.render(stack, x, y, tick);
        font.draw(stack, title, getLeftMargin(), getTopMargin(), 0x555555);
        langList.render(stack, x, y, tick);
    }

    @Override
    public void init() {
        langList = new LangList(getMinecraft(), font, guiWidth - 18, guiHeight - 48, getYOrigin() + 15, getYOrigin() + guiHeight - 10 - regularButtonHeight, 18);
        langList.setLeftPos(getLeftMargin());
        addRenderableWidget(langList);
        addRenderableWidget(new Button(getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, new TextComponent("Select language"),
                (button) -> {
                    if (langList.getSelected() != null) {
                        if (config != null)
                            this.selectLanguage(langList.getSelected().getLang());
                        else
                            this.retranslate(langList.getSelected().getLang());
                    }
                }));
        addRenderableWidget(new Button(getLeftMargin(), getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight, new TextComponent("Back"),
                (button) -> {
                    if (config != null)
                        this.selectLanguage(null);
                    else
                        getMinecraft().setScreen(new RetranslateGui());
                }));
    }

    @SuppressWarnings("ConstantConditions")
    private void selectLanguage(Language lang) {
        getMinecraft().setScreen(new ConfigGui(config, langSelect, lang));
    }

    private void retranslate(Language source) {
        Thread retranslate = new Retranslate(sender, message, source, LangManager.getInstance().findLanguageFromName(ConfigManager.config.targetLanguage.get()));
        retranslate.start();
        getMinecraft().setScreen(null);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (langList.isMouseOver(pMouseX, pMouseY)) {
            return langList.mouseScrolled(pMouseX, pMouseY, pDelta);
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }
}
