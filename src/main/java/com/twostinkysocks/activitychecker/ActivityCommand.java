package com.twostinkysocks.activitychecker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActivityCommand extends SimpleCommand.ProcessCommandRunnable {
    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(args != null && args.length <= 0) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /activity <player>"));
        } else {
            try {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + args[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("accept", "application/json");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                if(connection.getResponseCode() != 200) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid username. Usage: /activity <player>"));
                } else {
                    StringBuilder response = new StringBuilder("");
                    String inputLine;
                    while((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    connection.disconnect();
                    JsonParser parser = new JsonParser();
                    JsonObject element = (JsonObject) parser.parse(response.toString());
                    String uuid = element.get("id").getAsString();

                    // hypixel api
                    if(ActivityChecker.getConfig().get("api", "key", "" ) == null) {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid request! (Try running /api new)"));
                        return;
                    }
                    url = new URL("https://api.hypixel.net/status?uuid=" + uuid + "&key=" + ActivityChecker.getConfig().get("api", "key", "").getString());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("accept", "application/json");

                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    if(connection.getResponseCode() != 200) {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid request! (Try running /api new)"));
                    } else {
                        response = new StringBuilder("");
                        while((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        connection.disconnect();

                        parser = new JsonParser();
                        element = (JsonObject) parser.parse(response.toString());
                        boolean online = element.getAsJsonObject("session").get("online").getAsBoolean();
                        String gameType = "";
                        String mode = "";
                        String map = "";
                        if(online) {
                            if(element.getAsJsonObject("session").get("gameType") != null) gameType = element.getAsJsonObject("session").get("gameType").getAsString();
                            if(element.getAsJsonObject("session").get("mode") != null) mode = element.getAsJsonObject("session").get("mode").getAsString();
                            if(element.getAsJsonObject("session").get("map") != null) map = element.getAsJsonObject("session").get("map").getAsString();
                        }

                        StringBuilder message = new StringBuilder("");
                        message.append(EnumChatFormatting.GREEN + "---------------------------------\n\n");
                        message.append("  " + args[0] + " is " + (online ? EnumChatFormatting.GREEN + "" + EnumChatFormatting.BOLD + "ONLINE\n\n" :  EnumChatFormatting.RED + "" + EnumChatFormatting.BOLD + "OFFLINE\n\n"));
                        if(online) {
                            message.append(EnumChatFormatting.AQUA + "  Playing: " + gameType + "\n\n");
                            message.append(EnumChatFormatting.AQUA + "  Mode: " + mode + "\n\n");
                            message.append(EnumChatFormatting.AQUA + "  Map: " + map + "\n\n");
                        }
                        message.append(EnumChatFormatting.GREEN + "---------------------------------");
                        sender.addChatMessage(new ChatComponentText(message.toString()));
                    }

                }
            } catch(Exception e) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "An unexpected error has occured."));
                sender.addChatMessage(new ChatComponentText(e.getMessage()));
                sender.addChatMessage(new ChatComponentText(e.getStackTrace()[0].toString()));
            }

        }
    }
}
