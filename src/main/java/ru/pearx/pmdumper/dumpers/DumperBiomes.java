package ru.pearx.pmdumper.dumpers;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.pmdumper.dumpers.base.IDumper;
import ru.pearx.pmdumper.dumpers.base.PMDData;

import java.util.*;

/**
 * Created by mrAppleXZ on 14.06.17 9:36.
 */
@SideOnly(Side.CLIENT)
public class DumperBiomes implements IDumper
{
    @Override
    public String getName()
    {
        return "biomes";
    }

    @Override
    public PMDData getData()
    {
        Map<String, Integer> counts = new HashMap<>();
        List<List<String>> lst = new ArrayList<>();
        List<String> header = new ArrayList<>();
        header.addAll(Arrays.asList("ID", "Name", "Temperature", "Base Height", "Class Name", "Is Snowy", "Can Rain", "Rainfall", "Base Biome", "Filler Block", "Top Block"));
        for(EnumCreatureType t : EnumCreatureType.values())
        {
            header.add("Spawn List (" + t.toString() + ")");
        }
        lst.add(header);


        for(Map.Entry<ResourceLocation, Biome> entr : ForgeRegistries.BIOMES.getEntries())
        {
            ResourceLocation loc = entr.getKey();
            Biome b = entr.getValue();
            List<String> bLst = new ArrayList<>();
            bLst.add(loc.toString());
            bLst.add(b.getBiomeName());
            bLst.add(b.getTemperature() + " (" + b.getTempCategory().toString() + ")");
            bLst.add(Float.toString(b.getBaseHeight()));
            bLst.add(b.getBiomeClass().getName());
            bLst.add(Boolean.toString(b.isSnowyBiome()));
            bLst.add(Boolean.toString(b.canRain()));
            bLst.add(Float.toString(b.getRainfall()));
            bLst.add(b.baseBiomeRegName == null ? "no" : b.baseBiomeRegName);
            bLst.add(b.fillerBlock.toString());
            bLst.add(b.topBlock.toString());
            for(EnumCreatureType t : EnumCreatureType.values())
            {
                bLst.add(spawnableListToString(b.getSpawnableList(t)));
            }
            lst.add(bLst);

            PMDData.plusCounts(counts, loc.getResourceDomain(), 1);
        }

        return new PMDData(lst, counts);
    }

    public String spawnableListToString(List<Biome.SpawnListEntry> lst)
    {
        StringBuilder bld = new StringBuilder();
        for(Biome.SpawnListEntry entr : lst)
        {
            bld.append(entr.toString());
            bld.append(System.lineSeparator());
        }
        if(lst.size() > 0)
            bld.delete(bld.length() - System.lineSeparator().length(), bld.length());
        return bld.toString();
    }
}
