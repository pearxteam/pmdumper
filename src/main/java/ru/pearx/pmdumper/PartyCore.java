package ru.pearx.pmdumper;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Created by mrAppleXZ on 07.08.16.
 */
@Mod(modid = PartyCore.ID, version = "1.11.2-1.5.1", name = PartyCore.Name, acceptedMinecraftVersions = "1.11,1.11.2")
public class PartyCore
{
    public static final String ID = "pmdumper";
    public static final String Name = "PM Dumper";
    public static Logger Log;

    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent e)
    {
        new File(Minecraft.getMinecraft().mcDataDir.toString() + "/pmdumper").mkdirs();
        Log = e.getModLog();
    }

    @Mod.EventHandler
    public void ServerLoad(FMLServerStartingEvent e)
    {
        e.registerServerCommand(new PartyCommand());
    }
}