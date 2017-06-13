package ru.pearx.pmdumper;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.pmdumper.dumpers.base.IDumper;
import ru.pearx.pmdumper.dumpers.base.PMDData;

import javax.annotation.Nullable;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by mrAppleXZ on 11.06.17 12:44.
 */
@SideOnly(Side.CLIENT)
public class PMDCommand extends CommandBase
{
    @Override
    public String getName()
    {
        return "pmdumper";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        StringBuilder dumpl = new StringBuilder();
        for(IDumper dump : PMDumper.INSTANCE.dumps)
        {
            dumpl.append(dump.getName());
            dumpl.append("|");
        }
        dumpl.deleteCharAt(dumpl.length() - 1);
        return "/pmdumper <" + dumpl.toString() + ">";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        List<String> lst = new ArrayList<>();
        for (IDumper dump : PMDumper.INSTANCE.dumps)
            lst.add(dump.getName());
        lst.add("all");
        return lst;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if(args.length < 1)
            throw new WrongUsageException(getUsage(sender));
        for(IDumper dump : PMDumper.INSTANCE.dumps)
        {
            if(dump.getName().equals(args[0]) || "all".equals(args[0]))
            {
                PMDData data = dump.getData();

                StringBuilder csv = new StringBuilder();
                for (List<String> row : data.data)
                {
                    for (String s : row)
                    {
                        csv.append("\"" + s.replace("\"", "\"\"") + "\"");
                        csv.append(",");
                    }
                    csv.deleteCharAt(csv.length() - 1);
                    csv.append(System.lineSeparator());
                }
                File dir = new File(Minecraft.getMinecraft().mcDataDir, "pmdumper");
                dir.mkdirs();

                try
                {
                    PrintWriter pw = new PrintWriter(new File(dir, dump.getName() + "_" + PMDumper.getCurrentDate() + ".csv"));
                    pw.write(csv.toString());
                    pw.close();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

                if (data.counts != null)
                {
                    try
                    {
                        PrintWriter pw = new PrintWriter(new File(dir, dump.getName() + "_" + PMDumper.getCurrentDate() + "_counts.txt"));
                        List<Map.Entry<String, Integer>> entrLst = new ArrayList<>(data.counts.entrySet());
                        entrLst.sort((e1, e2) -> {
                            if(e1.getValue() < e2.getValue())
                                return -1;
                            if(e1.getValue() > e2.getValue())
                                return 1;
                            return 0;
                        });
                        for (Map.Entry<String, Integer> entr : entrLst)
                        {
                            pw.println(entr.getKey() + ": " + entr.getValue());
                        }
                        pw.close();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                notifyCommandListener(sender, this, "command.pmdumper.success");
                return;
            }
        }
        throw new CommandException("command.pmdumper.notFound");
    }
}
