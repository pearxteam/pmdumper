package ru.pearx.pmdumper;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.*;
import org.apache.commons.lang3.reflect.FieldUtils;

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
            IRegistry<ResourceLocation, IBakedModel> v = (IRegistry<ResourceLocation, IBakedModel>) FieldUtils.readField(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager(), "modelRegistry", true);

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

    public static void dumpItems(String filePath)
    {
        PrintWriter wr = null;
        //start dumping
        try
        {
            PartyCore.Log.info("[PartyMaker Dumper] Dump started!");
            wr = new PrintWriter(filePath);

            wr.println("ID | Metadata | Localized Name");

            IForgeRegistry<Item> v = ForgeRegistries.ITEMS;
            Map<String, Integer> m = new HashMap<String, Integer>();
            for (ResourceLocation loc : v.getKeys())
            {
                Item itm = v.getValue(loc);
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