package ru.pearx.pmdumper;

import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.pmdumper.dumpers.*;
import ru.pearx.pmdumper.dumpers.base.IDumper;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mrAppleXZ on 11.06.17 12:27.
 */
@Mod(modid = PMDumper.ID, name = PMDumper.NAME, version = PMDumper.VERSION, acceptedMinecraftVersions = PMDumper.MCVERSION, clientSideOnly = true)
public class PMDumper
{
    public static PMDumper INSTANCE;

    public static final String ID = "pmdumper";
    public static final String NAME = "PM Dumper";
    public static final String VERSION = "@VERSION@";
    public static final String MCVERSION = "@MCVERSION@";

    @SideOnly(Side.CLIENT)
    public List<IDumper> dumps = new ArrayList<>();

    public PMDumper()
    {
        INSTANCE = this;
    }

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void serverStart(FMLServerStartingEvent e)
    {
        dumps.add(new DumperItems());
        dumps.add(new DumperCapabilities());
        dumps.add(new DumperSounds());
        dumps.add(new DumperEnchantments());
        dumps.add(new DumperModels());
        dumps.add(new DumperBlocks());
        dumps.add(new DumperEntities());
        dumps.add(new DumperLootTables());
        dumps.add(new DumperVillagerProfessions());
        dumps.add(new DumperBiomes());
        e.registerServerCommand(new PMDCommand());
    }

    public static String getCurrentDate()
    {
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd-kk-mm");
        return form.format(new Date());
    }
}
