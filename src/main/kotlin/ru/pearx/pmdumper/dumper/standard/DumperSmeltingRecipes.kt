@file:JvmMultifileClass
@file:JvmName("StandardDumpers")
package ru.pearx.pmdumper.dumper.standard

import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.util.ResourceLocation
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.utils.toFullString

val DumperSmeltingRecipes = dumper {
    registryName = ResourceLocation(ID, "smelting_recipes")
    header = listOf("Input", "Output", "XP")
    columnToSortBy = 1
    iterator {
        val recipes = FurnaceRecipes.instance()
        for ((input, output) in recipes.smeltingList.entries) {
            with(ArrayList<String>(header.size)) {
                add(input.toFullString(true))
                add(output.toFullString())
                add(recipes.getSmeltingExperience(output).toString())
                yield(this)
            }
        }
    }
}