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
        return "/pmdumper <models|items>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if(args.length >= 1)
        {
            String s = Minecraft.getMinecraft().mcDataDir.toString() + "/pmdumper/";
            if(args[0].equals("models"))
            {
                PartyUtils.dumpModels(s + "models.txt");
                sender.addChatMessage(new TextComponentString("OK!"));
                return;
            }
            else if(args[0].equals("items"))
            {
                PartyUtils.dumpItems(s + "items.txt");
                sender.addChatMessage(new TextComponentString("OK!"));
                return;
            }
        }
        sender.addChatMessage(new TextComponentString(getCommandUsage(sender)));
    }
}
