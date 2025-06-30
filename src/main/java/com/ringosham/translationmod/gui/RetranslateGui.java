package com.ringosham.translationmod.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.translate.Translator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public class RetranslateGui extends CommonGui {
    private static final String title;
    private static final int guiHeight;
    private static final int guiWidth;

    static {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String modName = ModList.get().getModContainerById(TranslationMod.MODID).get().getModInfo().getDisplayName();
        title = modName + " - Retranslate";
        guiHeight = 200;
        guiWidth = 350;
    }

    private final List<Translator.TranslationLog> logs;

    public RetranslateGui() {
        super(title, guiHeight, guiWidth);
        //Cache the log within this gui instance. As the chat will overwrite the log.
        logs = Translator.getTranslationLog(15);
    }

    @Override
    public void render(PoseStack stack, int x, int y, float tick) {
        super.render(stack, x, y, tick);
        drawStringLine(stack, title, new String[]{
                "Translations are in the incorrect language?",
                "Select the messages below to retranslate.",
        }, 0);
        for (int i = 0; i < renderables.size(); i++) {
            TextButton button = (TextButton) renderables.get(i);
            if (button.isHoveredOrFocused()) {
                List<Component> hoverText = new ArrayList<>();
                hoverText.add(new TextComponent("Sender: " + logs.get(i).getSender()));
                hoverText.add(new TextComponent("Message: " + logs.get(i).getMessage()));
                //func_243308_b(MatrixStack, List<ITextComponent>, int, int) -> renderTooltip(...)
                renderComponentTooltip(stack, hoverText, x, y);
            }
        }
    }

    @Override
    public void init() {
        int offset = 0;
        for (Translator.TranslationLog log : logs) {
            String buttonText = log.getMessage();
            if (getTextWidth(buttonText) > guiWidth - 15) {
                buttonText = buttonText + "...";
                while (getTextWidth(buttonText) > guiWidth - 15)
                    buttonText = buttonText.substring(0, buttonText.length() - 4) + "...";
            }
            addRenderableWidget(new TextButton(getLeftMargin(), getTopMargin() + 40 + offset, getTextWidth(buttonText), new TextComponent(buttonText), (button) -> selectLanguage(log.getSender(), log.getMessage()), 0));
            offset += 10;
        }
    }

    private void selectLanguage(String sender, String message) {
        getMinecraft().pushGuiLayer(new LanguageSelectGui(sender, message));
    }
}
