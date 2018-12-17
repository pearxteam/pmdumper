@file:JvmMultifileClass
@file:JvmName("StandardDumpers")

package ru.pearx.pmdumper.dumper.standard

import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.crafting.IShapedRecipe
import net.minecraftforge.fml.common.registry.ForgeRegistries
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.utils.appendTo
import ru.pearx.pmdumper.utils.toFullString
import ru.pearx.pmdumper.utils.tryDump

val DumperShapelessRecipes = dumper {
    registryName = ResourceLocation(ID, "shapeless_recipes")
    header = listOf("ID", "Group", "Input Ingredients", "Output Item")
    columnToSortBy = 3
    amounts {
        eachShapeless { recipe ->
            this += recipe.registryName
        }
    }
    iterator {
        eachShapeless { recipe ->
            tryDump(ArrayList(header.size)) {
                with(recipe) {
                    add(registryName.toString())
                    add(group)
                    add(StringBuilder().apply {
                        var start = true
                        for (ing in ingredients) {
                            if (start)
                                start = false
                            else
                                appendln()

                            ing.appendTo(this)
                        }
                    }.toString())
                    add(recipeOutput.toFullString())
                }
            }
        }
    }
}

private inline fun eachShapeless(block: (recipe: IRecipe) -> Unit) {
    for (recipe in ForgeRegistries.RECIPES)
        if (recipe !is IShapedRecipe)
            block(recipe)
}