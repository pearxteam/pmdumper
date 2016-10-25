package ru.pearx.pmdumper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.fml.common.registry.*;
import ru.pearx.pmdumper.utils.Table;

import java.io.PrintWriter;
import java.util.*;

/**
 * Created by mrAppleXZ on 26.08.16.
 */
public class PartyUtils
{
    public static void dumpMeltingRecipes(String filePath)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("In Localized > Out Localized  |  In [Meta] xCount > Out [Meta] xCount \n");
        int count = 0;
        for (Map.Entry<ItemStack, ItemStack> rec : FurnaceRecipes.instance().getSmeltingList().entrySet())
        {
            count++;
            ItemStack in = rec.getKey();
            ItemStack out = rec.getValue();
            String idIn = Item.REGISTRY.getNameForObject(in.getItem()).toString();
            String idOut = Item.REGISTRY.getNameForObject(out.getItem()).toString();

            sb.append(in.getDisplayName() + " x" + in.stackSize);
            sb.append(" > ");
            sb.append(out.getDisplayName() + " x" + out.stackSize);
            sb.append("  |  ");
            sb.append(idIn + "[" + (in.getMetadata() == 32767 ? "any" : in.getMetadata())+ "]" + " x" + in.stackSize);
            sb.append(" > ");
            sb.append(idOut + "[" + out.getMetadata() + "]" + " x" + out.stackSize);

            sb.append("\n");
        }
        dump(filePath, sb.toString(), "Total: " + count);
    }

    public static void dumpSounds(String filePath)
    {
        StringBuilder sb = new StringBuilder();
        Map<String, Integer> m = new HashMap<String, Integer>();
        sb.append("Registry name\n");
        for(SoundEvent event : ForgeRegistries.SOUND_EVENTS)
        {
            String s = event.getRegistryName().getResourceDomain();
            sb.append(event.getRegistryName().toString() + "\n");
            if(m.containsKey(s))
                m.put(s, m.get(s) + 1);
            else
                m.put(s, 1);
        }
        dump(filePath, m, sb.toString());
    }

    public static void dumpModels(String filePath)
    {
        StringBuilder sb = new StringBuilder();
        IRegistry<ModelResourceLocation, IBakedModel> v = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager().modelRegistry;
        Map<String, Integer> m = new HashMap<String, Integer>();
        for (ResourceLocation loc : v.getKeys())
        {
            String s = loc.getResourceDomain();
            if (m.containsKey(s))
                m.put(s, m.get(s) + 1);
            else
                m.put(s, 1);
            sb.append(loc.toString() + "\n");
        }
        dump(filePath, m, sb.toString());
    }

    public static void dumpItems(String filePath)
    {
        Table t = new Table();
        t.add("ID", "Metadata", "Localized Name");

        Map<String, Integer> m = new HashMap<String, Integer>(); // model count for resource domain
        for (Item itm : ForgeRegistries.ITEMS)
        {
            ResourceLocation loc = Item.REGISTRY.getNameForObject(itm);
            List<ItemStack> l = new ArrayList<ItemStack>();
            if (itm.getHasSubtypes())
                itm.getSubItems(itm, null, l);
            else
                l.add(new ItemStack(itm, 1, 0));
            for (ItemStack stack : l)
            {
                String s = loc.getResourceDomain();
                if (m.containsKey(s))
                    m.put(s, m.get(s) + 1);
                else
                    m.put(s, 1);
                t.add(loc.toString(), Integer.toString(stack.getMetadata()), stack.getDisplayName());
            }
        }
        dump(filePath, m, t.print());
    }

    private static void dump(String filePath, Map<String, Integer> m, String str)
    {
        StringBuilder sb = new StringBuilder();
        List<Map.Entry<String, Integer>> lst = new ArrayList<Map.Entry<String, Integer>>(m.entrySet());
        Collections.sort(lst, new Comparator<Map.Entry<String, Integer>>()
        {
            @Override
            public int compare(Map.Entry<String, Integer> entr, Map.Entry<String, Integer> t1)
            {
                return t1.getValue().compareTo(entr.getValue());
            }
        });
        long total = 0;
        for (Map.Entry<String, Integer> entr : lst)
        {
            total += entr.getValue();
            sb.append(entr.getKey() + ": " + entr.getValue() + "\n");
        }
        sb.append("Total: " + total);
        dump(filePath, str, sb.toString());
    }

    private static void dump(String filePath, String str, String down)
    {
        PrintWriter wr = null;
        try
        {
            PartyCore.Log.info("[PartyMaker Dumper] Dump started!");
            wr = new PrintWriter(filePath);
            wr.println(str);
            wr.println("===================================");
            wr.println(down);

            PartyCore.Log.info("[PartyMaker Dumper] Dump finished!");
        } catch (Exception ex)
        {
            PartyCore.Log.error("[PartyMaker Dumper] " + ex.toString());
            PartyCore.Log.info("[PartyMaker Dumper] Dump error!");

        } finally
        {
            wr.flush();
            wr.close();
        }
    }
}