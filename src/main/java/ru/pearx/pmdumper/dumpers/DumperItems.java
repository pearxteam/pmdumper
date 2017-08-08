package ru.pearx.pmdumper.dumpers;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.pmdumper.dumpers.base.IDumper;
import ru.pearx.pmdumper.dumpers.base.PMDData;

import java.util.*;

/**
 * Created by mrAppleXZ on 11.06.17 12:54.
 */
@SideOnly(Side.CLIENT)
public class DumperItems implements IDumper
{
    @Override
    public String getName()
    {
        return "items";
    }

    @Override
    public PMDData getData()
    {
        List<List<String>> data = new ArrayList<>();
        Map<String, Integer> counts = new HashMap<>();
        data.add(Arrays.asList("ID", "Metadata", "Display Name", "Is ItemBlock", "Class Name", "NBT Tag Compound"));
        for(Map.Entry<ResourceLocation, Item> entr : ForgeRegistries.ITEMS.getEntries())
        {
            ResourceLocation loc = entr.getKey();
            Item itm = entr.getValue();
            NonNullList<ItemStack> stacks = NonNullList.create();
            itm.getSubItems(itm.getCreativeTab() == null ? CreativeTabs.MISC : itm.getCreativeTab() , stacks);
            for(ItemStack stack : stacks)
            {
                data.add(Arrays.asList(loc.toString(), Integer.toString(stack.getMetadata()), stack.getDisplayName(), Boolean.toString(stack.getItem() instanceof ItemBlock), stack.getItem().getClass().getName(), stack.hasTagCompound() ? stack.getTagCompound().toString() : "no"));
                PMDData.plusCounts(counts, loc.getResourceDomain() + " (ItemStack)", 1);
            }
            PMDData.plusCounts(counts, loc.getResourceDomain() + " (Item)", 1);
        }

        return new PMDData(data, counts);
    }
}
