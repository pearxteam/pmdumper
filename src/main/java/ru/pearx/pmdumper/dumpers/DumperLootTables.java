package ru.pearx.pmdumper.dumpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.pmdumper.dumpers.base.IDumper;
import ru.pearx.pmdumper.dumpers.base.PMDData;

import java.util.*;

/**
 * Created by mrAppleXZ on 13.06.17 22:50.
 */
@SideOnly(Side.CLIENT)
public class DumperLootTables implements IDumper
{
    @Override
    public String getName()
    {
        return "loot_tables";
    }

    @Override
    public PMDData getData()
    {
        Map<String, Integer> counts = new HashMap<>();
        List<List<String>> lst = new ArrayList<>();
        lst.add(Arrays.asList("ID", "Loot Data"));
        LootTableManager manager = new LootTableManager(null);
        Gson gs = (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntry.Serializer()).registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).setPrettyPrinting().create();
        for(ResourceLocation loc : LootTableList.getAll())
        {
            LootTable table = manager.getLootTableFromLocation(loc);
            lst.add(Arrays.asList(loc.toString(), gs.toJson(table)));
            PMDData.plusCounts(counts, loc.getResourceDomain(), 1);
        }
        return new PMDData(lst, counts);
    }
}
