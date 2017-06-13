package ru.pearx.pmdumper.dumpers;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.pmdumper.dumpers.base.IDumper;
import ru.pearx.pmdumper.dumpers.base.PMDData;

import java.util.*;

/**
 * Created by mrAppleXZ on 12.06.17 12:02.
 */
@SideOnly(Side.CLIENT)
public class DumperSounds implements IDumper
{
    @Override
    public String getName()
    {
        return "sounds";
    }

    @Override
    public PMDData getData()
    {
        List<List<String>> lst = new ArrayList<>();
        Map<String, Integer> counts = new HashMap<>();
        lst.add(Arrays.asList("ID", "Name"));
        for(Map.Entry<ResourceLocation, SoundEvent> entr : ForgeRegistries.SOUND_EVENTS.getEntries())
        {
            ResourceLocation loc = entr.getKey();
            SoundEvent ev = entr.getValue();
            lst.add(Arrays.asList(loc.toString(), ev.getSoundName().toString()));
            PMDData.plusCounts(counts, loc.getResourceDomain(), 1);
        }
        return new PMDData(lst, counts);
    }
}
