package com.twostinkysocks.activitychecker;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.util.logging.LogManager;
import java.util.logging.Logger;


@Mod(modid = ActivityChecker.MODID, name = ActivityChecker.MOD_NAME, version = ActivityChecker.VERSION, clientSideOnly = true)
public class ActivityChecker {
    public static final String MODID = "activitychecker";
    public static final String MOD_NAME = "Activity Checker";
    public static final String VERSION = "1.0";

    private static Configuration config = new Configuration(new File(Loader.instance().getConfigDir(), "ActivityChecker.cfg"));

    SimpleCommand activity = new SimpleCommand("activity", new ActivityCommand());

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        System.out.println("Hello from ActivityChecker! :D");
        ClientCommandHandler.instance.registerCommand(activity);
        MinecraftForge.EVENT_BUS.register(new Events());
    }


    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        config.load();
        config.get("api", "key", "");
        if(config.hasChanged()) {
            config.save();
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {

    }

    public static Configuration getConfig() {
        return config;
    }

    public static String cleanColor(String in) {
        return in.replaceAll("(?i)\\u00A7.", "");
    }
}
