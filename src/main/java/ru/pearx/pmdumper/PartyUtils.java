package ru.pearx.pmdumper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
        PrintWriter wr = null;
        //start dumping<
        try
        {
            PartyCore.Log.info("[PartyMaker Dumper] Dump started!");
            wr = new PrintWriter(filePath);
            IRegistry<ModelResourceLocation, IBakedModel> v = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager().modelRegistry;
            Map<String, Integer> m = new HashMap<String, Integer>();
            for (ResourceLocation loc : v.getKeys())
            {
                String s = loc.getResourceDomain();
                if(m.containsKey(s))
                    m.put(s, m.get(s) + 1);
                else
                    m.put(s, 1);
                wr.println(loc.toString());
            }
            wr.println("===================================");
            for(String s : m.keySet())
            {
                wr.print(s + ": " + m.get(s) + ", ");
            }

            PartyCore.Log.info("[PartyMaker Dumper] Dump finished!");
        }
        catch (IOException ex) {}
        finally
        {
            wr.flush();
            wr.close();
        }
    }

    public static void dumpItems(String filePath)
    {
        PrintWriter wr = null;
        //start dumping
        try
        {
            PartyCore.Log.info("[PartyMaker Dumper] Dump started!");
            wr = new PrintWriter(filePath);

            wr.println("ID | Metadata | Localized Name");

            Map<String, Integer> m = new HashMap<String, Integer>();
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
                    wr.println(loc.toString() + " | " + stack.getMetadata() + " | " + stack.getDisplayName());
                }

            }
            wr.println("===================================");
            for(String s : m.keySet())
            {
                wr.print(s + ": " + m.get(s) + ", ");
            }

            PartyCore.Log.info("[PartyMaker Dumper] Dump finished!");
        } catch (IOException ex)
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