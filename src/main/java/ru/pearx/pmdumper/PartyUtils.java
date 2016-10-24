package ru.pearx.pmdumper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IntegerCache;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.fml.common.registry.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by mrAppleXZ on 26.08.16.
 */
public class PartyUtils
{
    public static void dumpModels(String filePath)
    {
        StringBuilder sb = new StringBuilder();
        IRegistry<ModelResourceLocation, IBakedModel> v = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager().modelRegistry;
        Map<String, Integer> m = new HashMap<String, Integer>();
        for (ResourceLocation loc : v.getKeys())
        {
            String s = loc.getResourceDomain();
            if(m.containsKey(s))
                m.put(s, m.get(s) + 1);
            else
                m.put(s, 1);
            sb.append(loc.toString() + "\n");
        }
        dump(filePath, m, sb.toString());
    }

    public static void dumpItems(String filePath)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ID | Metadata | Localized Name\n");

        Map<String, Integer> m = new HashMap<String, Integer>(); // model count for resource domain
        for (Item itm : ForgeRegistries.ITEMS)
        {
            ResourceLocation loc = Item.REGISTRY.getNameForObject(itm);
            List<ItemStack> l = new ArrayList<ItemStack>();
            if(itm.getHasSubtypes())
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
                sb.append(loc.toString() + " | " + stack.getMetadata() + " | " + stack.getDisplayName() + "\n");
            }
        }
        dump(filePath, m, sb.toString());
    }

    private static void dump(String filePath, Map<String, Integer> m, String str)
    {
        PrintWriter wr = null;
        //start dumping
        try
        {
            PartyCore.Log.info("[PartyMaker Dumper] Dump started!");
            wr = new PrintWriter(filePath);
            wr.println(str);
            wr.println("===================================");
            List<Map.Entry<String, Integer>> lst = new ArrayList<Map.Entry<String, Integer>>(m.entrySet());
            Collections.sort(lst, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> entr, Map.Entry<String, Integer> t1) {
                    return t1.getValue().compareTo(entr.getValue());
                }
            });
            for(Map.Entry<String, Integer> entr : lst)
            {
                wr.print(entr.getKey() + ": " + entr.getValue() + "\n");
            }

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