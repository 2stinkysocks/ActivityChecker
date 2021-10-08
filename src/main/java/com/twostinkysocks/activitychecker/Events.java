package com.twostinkysocks.activitychecker;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Events {
    @SubscribeEvent
    public void onChatRecieved(ClientChatReceivedEvent e) {
        String message = e.message.getFormattedText();
        String unformatted = ActivityChecker.cleanColor(e.message.getUnformattedText());
        if(unformatted.startsWith("Your new API key is ")) {
            System.out.println("Found new API key in chat");
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN+"ActivityChecker automatically updated your API key"));
            ActivityChecker.getConfig().load();
            ActivityChecker.getConfig().getCategory("api").get("key").set(unformatted.substring("Your new API key is ".length()));
            ActivityChecker.getConfig().save();
        }
    }
}
