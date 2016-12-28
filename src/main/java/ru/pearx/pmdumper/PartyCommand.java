package ru.pearx.pmdumper;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import ru.pearx.pmdumper.utils.TableFormat;

import java.io.File;
import java.io.IOException;

/**
 * Created by mrAppleXZ on 26.08.16.
 */
public class PartyCommand extends CommandBase
{
    @Override
    public String getName()
    {
        return "pmdumper";
    }

    @Override
    public String getUsage(ICommandSender iCommandSender)
    {
        return "Usage: /pmdumper <models|items|sounds|recipes_smelting|blocks|fluids> <csv|txt>. Format ";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        try
        {
            if (args.length >= 2)
            {
                String s = new File(Minecraft.getMinecraft().mcDataDir, "pmdumper").getCanonicalPath() + File.separator;
                TableFormat format;
                switch (args[1])
                {
                    case "txt":
                        format = TableFormat.Txt;
                        break;
                    case "csv":
                        format = TableFormat.Csv;
                        break;
                    default:
                        sender.sendMessage(new TextComponentString(getUsage(sender)));
                        return;
                }
                if (args[0].equals("models"))
                {
                    PartyUtils.dumpModels(s + args[0], format);
                } else if (args[0].equals("items"))
                {
                    PartyUtils.dumpItems(s + args[0], format);
                } else if (args[0].equals("sounds"))
                {
                    PartyUtils.dumpSounds(s + args[0], format);
                } else if (args[0].equals("recipes_smelting"))
                {
                    PartyUtils.dumpRecipesSmelting(s + args[0], format);
                } else if (args[0].equals("blocks"))
                {
                    sender.sendMessage(new TextComponentString("Block dumping is currently WIP!"));
                    PartyUtils.dumpBlocks(s + args[0], format);
                } else if (args[0].equals("fluids"))
                {
                    PartyUtils.dumpFluids(s + args[0], format);
                } else
                {
                    return;
                }
                sender.sendMessage(new TextComponentString("OK! Dump saved in \"" + s + "\" directory."));
                return;
            }
            sender.sendMessage(new TextComponentString(getUsage(sender)));
        }
        catch(IOException e)
        {
            
        }
    }
}
