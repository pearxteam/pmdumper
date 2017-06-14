package ru.pearx.pmdumper.dumpers;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.pmdumper.dumpers.base.IDumper;
import ru.pearx.pmdumper.dumpers.base.PMDData;

import java.util.*;

/**
 * Created by mrAppleXZ on 14.06.17 11:39.
 */
@SideOnly(Side.CLIENT)
public class DumperPotions implements IDumper
{
    @Override
    public String getName()
    {
        return "potions";
    }

    @Override
    public PMDData getData()
    {
        List<List<String>> lst = new ArrayList<>();
        Map<String, Integer> counts = new HashMap<>();
        lst.add(Arrays.asList("ID", "Name", "Class Name", "Is Bad Effect", "Is Instant", "Is Beneficial", "Curative Items"));

        for(Map.Entry<ResourceLocation, Potion> entr : ForgeRegistries.POTIONS.getEntries())
        {
            ResourceLocation loc = entr.getKey();
            Potion p = entr.getValue();
            StringBuilder curative = new StringBuilder();
            for(ItemStack stack : p.getCurativeItems())
            {
                curative.append(stack.toString());
                curative.append(System.lineSeparator());
            }
            if(p.getCurativeItems().size() > 0)
                curative.delete(curative.length() - System.lineSeparator().length(), curative.length());
            lst.add(Arrays.asList(loc.toString(), p.getName(), p.getClass().getName(), Boolean.toString(p.isBadEffect()), Boolean.toString(p.isInstant()), Boolean.toString(p.isBeneficial()), curative.toString()));
            PMDData.plusCounts(counts, loc.getResourceDomain(), 1);
        }

        return new PMDData(lst, counts);
    }
}
