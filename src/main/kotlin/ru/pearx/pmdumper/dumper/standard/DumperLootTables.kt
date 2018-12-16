@file:JvmMultifileClass
@file:JvmName("StandardDumpers")
package ru.pearx.pmdumper.dumper.standard

import com.google.gson.GsonBuilder
import net.minecraft.util.ResourceLocation
import net.minecraft.world.storage.loot.*
import net.minecraft.world.storage.loot.conditions.LootCondition
import net.minecraft.world.storage.loot.conditions.LootConditionManager
import net.minecraft.world.storage.loot.functions.LootFunction
import net.minecraft.world.storage.loot.functions.LootFunctionManager
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper

val DumperLootTables = dumper {
    registryName = ResourceLocation(ID, "loot_tables")
    header = listOf("ID", "Loot Data")
    amounts {
        for (table in LootTableList.getAll())
            this += table
    }
    iterator {
        val manager = LootTableManager(null)
        val gs = GsonBuilder().registerTypeAdapter(RandomValueRange::class.java, RandomValueRange.Serializer()).registerTypeAdapter(LootPool::class.java, LootPool.Serializer()).registerTypeAdapter(LootTable::class.java, LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry::class.java, LootEntry.Serializer()).registerTypeHierarchyAdapter(LootFunction::class.java, LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(LootCondition::class.java, LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget::class.java, LootContext.EntityTarget.Serializer()).setPrettyPrinting().create()
        for (loc in LootTableList.getAll()) {
            with(ArrayList<String>(header.size)) {
                add(loc.toString())
                add(gs.toJson(manager.getLootTableFromLocation(loc)))
                yield(this)
            }
        }
    }
}