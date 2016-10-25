package ru.pearx.pmdumper;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

/**
 * Created by mrAppleXZ on 26.08.16.
 */
public class PartyCommand extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "pmdumper";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/pmdumper <models|items|sounds|smelting>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if(args.length >= 1)
        {
            String s = Minecraft.getMinecraft().mcDataDir.toString() + "/pmdumper/";
            String dn = "";
            if(args[0].equals("models"))
            {
                dn = "models.txt";
                PartyUtils.dumpModels(s + dn);
            }
            else if(args[0].equals("items"))
            {
                dn = "items.txt";
                PartyUtils.dumpItems(s + dn);
            }
            else if(args[0].equals("sounds"))
            {
                dn = "sounds.txt";
                PartyUtils.dumpSounds(s + dn);
            }
            else if(args[0].equals("smelting"))
            {
                dn = "recipesMelting.txt";
                PartyUtils.dumpMeltingRecipes(s + dn);
            }
            if(dn != "")
            {
                sender.addChatMessage(new TextComponentString("OK! Dump saved in your .minecraft directory."));
                return;
            }
        }
        sender.addChatMessage(new TextComponentString(getCommandUsage(sender)));
    }
}
