package ru.pearx.pmdumper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.registry.*;
import org.lwjgl.input.Mouse;
import ru.pearx.pmdumper.utils.Table;
import ru.pearx.pmdumper.utils.TableFormat;

import javax.annotation.Nonnull;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by mrAppleXZ on 26.08.16.
 */
public class PartyUtils
{
    public static void dumpRecipesSmelting(String filePath, TableFormat f)
    {
        Table t = new Table(f, -1);
        t.add("In Localized > Out Localized", "In > Out");
        int count = 0;
        for (Map.Entry<ItemStack, ItemStack> rec : FurnaceRecipes.instance().getSmeltingList().entrySet())
        {
            count++;
            ItemStack in = rec.getKey();
            ItemStack out = rec.getValue();
            String idIn = Item.REGISTRY.getNameForObject(in.getItem()).toString();
            String idOut = Item.REGISTRY.getNameForObject(out.getItem()).toString();

            t.add(
                    in.getDisplayName() + " x" + in.getCount()
                            + " > " +
                            out.getDisplayName() + " x" + out.getCount(),

                    idIn + "[" + (in.getMetadata() == 32767 ? "any" : in.getMetadata()) + "]" + " x" + in.getCount()
                            + " > " +
                            idOut + "[" + out.getMetadata() + "]" + " x" + out.getCount());
        }
        dump(filePath, t, "Total: " + count);
    }

    public static void dumpLootTables(String filePath)
    {
        //todo
    }

    public static void dumpSounds(String filePath, TableFormat f)
    {
        Table t = new Table(f, 0);
        Map<String, Integer> m = new HashMap<String, Integer>();
        t.add("Registry name");
        for(SoundEvent event : ForgeRegistries.SOUND_EVENTS)
        {
            addToMap(m, event.getRegistryName().getResourceDomain());
            t.add(event.getRegistryName().toString());
        }
        dump(filePath, t, m);
    }

    public static void dumpModels(String filePath, TableFormat f)
    {
        Table t = new Table(f, 0);
        t.add("Name");
        IRegistry<ModelResourceLocation, IBakedModel> v = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager().modelRegistry;
        Map<String, Integer> m = new HashMap<String, Integer>();
        for (ResourceLocation loc : v.getKeys())
        {
            t.add(loc.toString());
            addToMap(m, loc.getResourceDomain());
        }
        dump(filePath, t, m);
    }

    public static void dumpItems(String filePath, TableFormat f)
    {
        Table t = new Table(f, 0);
        t.add("ID", "Metadata", "Localized Name");

        Map<String, Integer> m = new HashMap<String, Integer>(); // model count for resource domain
        for (Item itm : ForgeRegistries.ITEMS)
        {
            ResourceLocation loc = Item.REGISTRY.getNameForObject(itm);
            @Nonnull
            NonNullList<ItemStack> l = NonNullList.create();
            if (itm.getHasSubtypes())
                itm.getSubItems(itm, null, l);
            else
                l.add(new ItemStack(itm, 1, 0));
            for (ItemStack stack : l)
            {
                t.add(loc.toString(), Integer.toString(stack.getMetadata()), stack.getDisplayName());
                addToMap(m, loc.getResourceDomain());
            }
        }
        dump(filePath, t, m);
    }

    public static void dumpBlocks(String filePath, TableFormat f)
    {
        Table t = new Table(f, 0);
        t.add("ID", "Localized Name");
        Map<String, Integer> m = new HashMap<String, Integer>();
        for(Block b : ForgeRegistries.BLOCKS)
        {
            ResourceLocation loc = Block.REGISTRY.getNameForObject(b);
            t.add(loc.toString(), b.getLocalizedName());
            addToMap(m, loc.getResourceDomain());
        }
        dump(filePath, t, m);
    }

    public static void dumpFluids(String filePath, TableFormat f)
    {
        Table t = new Table(f, 0);
        t.add("Fluid Name", "Fluid Localized Name", "Fluid Block ID", "Gaseous");
        Map<String, Integer> m = new HashMap<String, Integer>();
        for(Map.Entry<String, Fluid> e : FluidRegistry.getRegisteredFluids().entrySet())
        {
            Fluid fl = e.getValue();
            ResourceLocation loc = fl.getStill();
            t.add(
                    fl.getName(),
                    fl.getLocalizedName(new FluidStack(fl, 1)),
                    (fl.getBlock() != null ? Block.REGISTRY.getNameForObject(fl.getBlock()).toString() : "no"),
                    Boolean.toString(fl.isGaseous())
            );
            addToMap(m, loc.getResourceDomain());
        }
        dump(filePath, t, m);
    }

    private static void dump(String filePath, Table t, Map<String, Integer> m)
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
        dump(filePath, t, sb.toString());
    }

    private static void dump(String filePath, Table t, String down)
    {
        switch (t.Format)
        {
            case Csv:
                filePath += ".csv";
                break;
            case  Txt:
                filePath += ".txt";
                break;
        }
        PrintWriter wr = null;
        try
        {
            PartyCore.Log.info("[PartyMaker Dumper] Dump started!");
            wr = new PrintWriter(filePath);
            wr.println(t.print());
            if(t.Format == TableFormat.Txt)
            {
                wr.println("===================================");
                wr.println(down);
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

    private static void addToMap(Map<String, Integer> m, String modid)
    {
        if (m.containsKey(modid))
            m.put(modid, m.get(modid) + 1);
        else
            m.put(modid, 1);
    }
}