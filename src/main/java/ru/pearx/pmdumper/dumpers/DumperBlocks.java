package ru.pearx.pmdumper.dumpers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.pmdumper.dumpers.base.IDumper;
import ru.pearx.pmdumper.dumpers.base.PMDData;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by mrAppleXZ on 13.06.17 19:53.
 */
@SideOnly(Side.CLIENT)
public class DumperBlocks implements IDumper
{
    @Override
    public String getName()
    {
        return "blocks";
    }

    @Override
    public PMDData getData()
    {
        Map<String, Integer> counts = new HashMap<>();
        List<List<String>> lst = new ArrayList<>();
        lst.add(Arrays.asList("ID", "Properties", "Class Name"));
        for(Map.Entry<ResourceLocation, Block> entr : ForgeRegistries.BLOCKS.getEntries())
        {
            ResourceLocation loc = entr.getKey();
            Block block = entr.getValue();
            lst.add(Arrays.asList(loc.toString(),
                    block.getBlockState().getProperties().toString(),
                    block.getClass().getName()));
            PMDData.plusCounts(counts, loc.getResourceDomain(), 1);
        }
        return new PMDData(lst, counts);
    }
}
