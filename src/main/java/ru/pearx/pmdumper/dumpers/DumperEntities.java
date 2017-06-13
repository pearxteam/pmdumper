package ru.pearx.pmdumper.dumpers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.pmdumper.dumpers.base.IDumper;
import ru.pearx.pmdumper.dumpers.base.PMDData;

import java.util.*;

/**
 * Created by mrAppleXZ on 13.06.17 22:16.
 */
@SideOnly(Side.CLIENT)
public class DumperEntities implements IDumper
{
    @Override
    public String getName()
    {
        return "entities";
    }

    @Override
    public PMDData getData()
    {
        List<List<String>> data = new ArrayList<>();
        Map<String, Integer> counts = new HashMap<>();
        data.add(Arrays.asList("ID", "Name", "Class Name"));
        for(Map.Entry<ResourceLocation, EntityEntry> entr : ForgeRegistries.ENTITIES.getEntries())
        {
            ResourceLocation loc = entr.getKey();
            EntityEntry ent = entr.getValue();
            data.add(Arrays.asList(loc.toString(), ent.getName(), ent.getEntityClass().getName()));
            PMDData.plusCounts(counts, loc.getResourceDomain(), 1);
            EntityVillager.GET_TRADES_DONT_USE()
        }
        return new PMDData(data, counts);
    }
}
