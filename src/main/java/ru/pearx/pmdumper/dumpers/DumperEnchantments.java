package ru.pearx.pmdumper.dumpers;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.pmdumper.dumpers.base.IDumper;
import ru.pearx.pmdumper.dumpers.base.PMDData;

import java.util.*;

/**
 * Created by mrAppleXZ on 12.06.17 12:20.
 */
@SideOnly(Side.CLIENT)
public class DumperEnchantments implements IDumper
{

    @Override
    public String getName()
    {
        return "enchantments";
    }

    @Override
    public PMDData getData()
    {
        List<List<String>> lst = new ArrayList<>();
        Map<String, Integer> counts = new HashMap<>();
        lst.add(Arrays.asList("ID", "Name", "Class Name", "Levels", "Rarity", "Is Curse", "Type", "Allowed on books", "Is Treasure", "Level (Min Enchantability - Max Enchantability)..."));
        for(Map.Entry<ResourceLocation, Enchantment> entr : ForgeRegistries.ENCHANTMENTS.getEntries())
        {
            ResourceLocation loc = entr.getKey();
            Enchantment ench = entr.getValue();
            List<String> row = new ArrayList<>();
            row.add(loc.toString());
            row.add(ench.getName());
            row.add(ench.getClass().getName());
            row.add(ench.getMinLevel() + " - " + ench.getMaxLevel());
            row.add(ench.getRarity().toString());
            row.add(Boolean.toString(ench.func_190936_d()));
            row.add(ench.type == null ? "null" : ench.type.toString());
            row.add(Boolean.toString(ench.isAllowedOnBooks()));
            row.add(Boolean.toString(ench.isTreasureEnchantment()));
            for(int i = ench.getMinLevel(); i <= ench.getMaxLevel(); i++)
            {
                row.add(ench.getTranslatedName(i) + " (" + ench.getMinEnchantability(i) + " - " + ench.getMaxEnchantability(i) + ")");
            }
            PMDData.plusCounts(counts, loc.getResourceDomain(), 1);
            lst.add(row);
        }
        return new PMDData(lst, counts);
    }
}
