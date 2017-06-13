package ru.pearx.pmdumper.dumpers.base;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;

/**
 * Created by mrAppleXZ on 11.06.17 12:38.
 */
@SideOnly(Side.CLIENT)
public class PMDData
{
    public List<List<String>> data;
    public Map<String, Integer> counts;

    public PMDData(List<List<String>> data, Map<String, Integer> counts)
    {
        this.data = data;
        this.counts = counts;
    }

    public static void plusCounts(Map<String, Integer> map, String key, int pl)
    {
        if(map.containsKey(key))
        {
            map.put(key, map.get(key) + pl);
        }
        else
            map.put(key, pl);
    }
}
