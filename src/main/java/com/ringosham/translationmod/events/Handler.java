package com.ringosham.translationmod.events;

import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import com.ringosham.translationmod.gui.CommonGui;
import com.ringosham.translationmod.gui.TranslateGui;
import com.ringosham.translationmod.translate.SignTranslate;
import com.ringosham.translationmod.translate.Translator;
import com.ringosham.translationmod.translate.types.SignText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.Objects;

public class Handler {
	private boolean hintShown = false;

	@SubscribeEvent
    public void onGuiOpen(ScreenOpenEvent event) {
        if (event.getScreen() == null) {
            Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(false);
        }
    }

    @SubscribeEvent
    public void chatReceived(ClientChatReceivedEvent event) {
        if(!ConfigManager.config.modAble.get()) return;
        Component eventMessage = event.getMessage();
        String message = eventMessage.getString().replaceAll("§(.)", "");
        Thread translate = new Translator(message, null, LangManager.getInstance().findLanguageFromName(ConfigManager.config.targetLanguage.get()));
        translate.start();
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player == null)
            return;
        if (!hintShown) {
            hintShown = true;
            ChatUtil.printChatMessage(true, "Press [" + ChatFormatting.AQUA + KeyMapping.createNameSupplier(KeyBind.translateKey.getName()).get().getString() + ChatFormatting.WHITE + "] for translation settings", ChatFormatting.WHITE);
            if (ConfigManager.config.regexList.get().isEmpty()) {
                Log.logger.warn("No chat regex in the configurations");
                ChatUtil.printChatMessage(true, "The mod needs chat regex to function. Check the mod options to add one", ChatFormatting.RED);
            }
        }
    }

    //If config is somehow changed through other means
    @SubscribeEvent
    public void onConfigChanged(ModConfigEvent.Reloading event) {
        ConfigManager.saveConfig();
    }


    @SubscribeEvent
    public void onKeybind(InputEvent.KeyInputEvent event) {
        if (KeyBind.translateKey.isDown() && !(Minecraft.getInstance().screen instanceof CommonGui)){
            Minecraft.getInstance().setScreen(new TranslateGui());
            event.setResult(Event.Result.ALLOW);
        }
    }


    @SubscribeEvent
    public void processSign(PlayerInteractEvent.RightClickBlock event) {
        if(ConfigManager.config.translateSign.get() && ConfigManager.config.modAble.get()
                && event.getHand() == InteractionHand.MAIN_HAND
                && event.getSide().isClient())
        {
            BlockHitResult hitVec = event.getHitVec();
            if(hitVec != null){
                Level level = event.getPlayer().level;
                BlockPos blockPos = hitVec.getBlockPos();
                BlockState blockState = level.getBlockState(blockPos);
                if(blockState.getBlock() instanceof SignBlock){
                    SignTranslate signThread = getSignThread(level, blockPos);
                    if(signThread != null){
                        signThread.start();
                    }
                }
            }
        }
    }

    private SignTranslate getSignThread(Level world, BlockPos pos) {
        StringBuilder text = new StringBuilder();
        //Four lines of text in signs
        for (int i = 0; i < 4; i++) {
            Component line = ((SignBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos))).getMessage(i,false);
            //Combine each line of the sign with spaces.
            //Due to differences between languages, this may break asian languages. (Words don't separate with spaces)
            text.append(" ").append(line.getString().replaceAll("§(.)", ""));
        }
        text = new StringBuilder(text.toString().replaceAll("§(.)", ""));
        if (text.isEmpty())
            return null;
	    SignText lastSign = new SignText();
        lastSign.setSign(text.toString(), pos);
        return new SignTranslate(text.toString(), pos);
    }
}
