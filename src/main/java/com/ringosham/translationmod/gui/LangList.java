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

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.client.types.Language;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class LangList extends ObjectSelectionList<LangList.LangEntry> {
    private final List<Language> langList;
    private final Font font;

    {
        langList = LangManager.getInstance().getAllLanguages();
        //Exclude auto
        langList.remove(LangManager.getInstance().getAutoLang());
        //Sort alphabetically.
        langList.sort(Comparator.comparing(Language::getName));
    }

    public LangList(Minecraft client, Font font, int width, int height, int top, int bottom, int entryHeight) {
        // 修复：使用正确的构造函数参数顺序和方法
        super(client, width, height, top, bottom, entryHeight);
        this.font = font;
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);

        // 修复：确保在正确时机添加条目
        for (Language lang : langList) {
            this.addEntry(new LangEntry(lang));
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return getRight();
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    //The dirt background does not render correctly and it covers UI elements, needs to be disabled
    //Had to extract the entire rendering method as by default it renders the dirt background and there is no way to override it. This is so dumb.
    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int i = this.getScrollbarPosition();
        int j = i + 6;
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        this.minecraft.getTextureManager().bindForSetup(Gui.BACKGROUND_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(matrixStack.last().pose(), getLeft(), getBottom(), 0).uv((float) getLeft() / 32.0F, (float) (getBottom() + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        bufferbuilder.vertex(matrixStack.last().pose(),getRight(), getBottom(), 0).uv((float) getRight() / 32.0F, (float) (getBottom() + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        bufferbuilder.vertex(matrixStack.last().pose(),getRight(), getTop(), 0).uv((float) getRight() / 32.0F, (float) (getTop() + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        bufferbuilder.vertex(matrixStack.last().pose(),getLeft(), getTop(), 0).uv((float) getLeft() / 32.0F, (float) (getTop() + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
        int k = this.getRowLeft();
        int l = getTop() + 4 - (int) this.getScrollAmount();

        this.renderList(matrixStack, k, l, mouseX, mouseY, partialTicks);

        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.disableTexture();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(matrixStack.last().pose(),getLeft(), getTop() + 4, 0).uv(0.0F, 1.0F).color(0, 0, 0, 0).endVertex();
        bufferbuilder.vertex(matrixStack.last().pose(),getRight(), getTop() + 4, 0).uv(1.0F, 1.0F).color(0, 0, 0, 0).endVertex();
        bufferbuilder.vertex(matrixStack.last().pose(),getRight(), getTop(), 0).uv(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex(matrixStack.last().pose(),getLeft(), getTop(), 0).uv(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex(matrixStack.last().pose(),getLeft(), getBottom(), 0).uv(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex(matrixStack.last().pose(),getRight(), getBottom(), 0).uv(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex(matrixStack.last().pose(),getRight(), getBottom() - 4, 0).uv(1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
        bufferbuilder.vertex(matrixStack.last().pose(),getLeft(), getBottom() - 4, 0).uv(0.0F, 0.0F).color(0, 0, 0, 0).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
        int k1 = Math.max(0, this.getMaxPosition() - (getBottom() - getTop() - 4));
        if (k1 > 0) {
            int l1 = (int) ((float) ((getBottom() - getTop()) * (getBottom() - getTop())) / (float) this.getMaxPosition());
            l1 = Mth.clamp(l1, 32, getBottom() - getTop() - 8);
            int i2 = (int) this.getScrollAmount() * (getBottom() - getTop() - l1) / k1 + getTop();
            if (i2 < getTop()) {
                i2 = getTop();
            }

            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferbuilder.vertex(matrixStack.last().pose(), i, getBottom(), 0).uv(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(matrixStack.last().pose(), j, getBottom(), 0).uv(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(matrixStack.last().pose(), j, getTop(), 0).uv(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(matrixStack.last().pose(), i, getTop(), 0).uv(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(matrixStack.last().pose(), i, i2 + l1, 0).uv(0.0F, 1.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(matrixStack.last().pose(), j, i2 + l1, 0).uv(1.0F, 1.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(matrixStack.last().pose(), j, i2, 0).uv(1.0F, 0.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(matrixStack.last().pose(), i, i2, 0).uv(0.0F, 0.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(matrixStack.last().pose(), i, i2 + l1 - 1, 0).uv(0.0F, 1.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex(matrixStack.last().pose(), j - 1, i2 + l1 - 1, 0).uv(1.0F, 1.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex(matrixStack.last().pose(), j - 1, i2, 0).uv(1.0F, 0.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex(matrixStack.last().pose(), i, i2, 0).uv(0.0F, 0.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.end();
            BufferUploader.end(bufferbuilder);
        }

        this.renderDecorations(matrixStack, mouseX, mouseY);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    @Override
    public void updateNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
    @Override
    public boolean isFocused() {
        return this.getFocused() != null;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener listener) {
        super.setFocused(listener);
    }

    public class LangEntry extends ObjectSelectionList.Entry<LangEntry> {
        private final Language lang;
        private final String langName;

        public LangEntry(Language lang) {
            this.lang = lang;
            if (lang.getNameUnicode() != null)
                this.langName = lang.getName() + " (" + lang.getNameUnicode() + ")";
            else
                this.langName = lang.getName();
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public void render(PoseStack stack, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
            //font.setBidiFlag(true);
//            if (top + 1 < getBottom() && getTop() < top + 1)
                drawString(stack, font, this.langName, left + 5, top + 1, 16777215);
        }

        //Undocumented parameter names are fun!
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                LangList.this.setSelected(this);
                return true;
            }
            return false;
        }

        public Language getLang() {
            return lang;
        }

        @Override
        public Component getNarration() {
            return new TextComponent(langName);
        }
    }
}
