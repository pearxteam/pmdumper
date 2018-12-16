@file:JvmMultifileClass
@file:JvmName("StandardDumpers")

package ru.pearx.pmdumper.dumper.standard

import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.crafting.IShapedRecipe
import net.minecraftforge.fml.common.registry.ForgeRegistries
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.utils.toFullString

val DumperShapedRecipes = dumper {
    registryName = ResourceLocation(ID, "shaped_recipes")
    header = listOf("ID", "Group", "Input Pattern", "Input Ingredients", "Output Item", "Width", "Height")
    columnToSortBy = 4
    amounts {
        eachShaped { recipe ->
            this += recipe.registryName
        }
    }
    iterator {
        eachShaped { recipe ->
            with(ArrayList<String>(header.size)) {
                with(recipe) {
                    add(registryName.toString())
                    add(group)

                    val ingredientStrings = mutableListOf<String>().apply { ingredients.forEach { add(it.toFullString()) } }
                    val patternMap = hashMapOf<String, Char>().apply {
                        var lastChar = 'A'
                        for (dist in ingredientStrings.distinct()) {
                            if(!dist.isEmpty()) {
                                this[dist] = lastChar
                                lastChar++
                            }
                        }
                    }
                    add(StringBuilder().apply {
                        var start = true
                        for (row in 0 until recipeHeight) {
                            if(start)
                                start = false
                            else
                                appendln()

                            for (column in 0 until recipeWidth) {
                                val str = ingredientStrings[row * recipeWidth + column]
                                append(if(str.isEmpty()) "-" else patternMap[str])
                            }
                        }
                    }.toString())
                    add(StringBuilder().apply {
                        var start = true
                        for((str, char) in patternMap.entries.sortedBy { it.value }) {
                            if(start)
                                start = false
                            else
                                appendln()

                            append(char)
                            append(": ")
                            append(str)
                        }
                    }.toString())
                    add(recipeOutput.toFullString())
                    add(recipeWidth.toString())
                    add(recipeHeight.toString())
                }
                yield(this)
            }
        }
    }
}

private inline fun eachShaped(block: (recipe: IShapedRecipe) -> Unit) {
    for (recipe in ForgeRegistries.RECIPES)
        if (recipe is IShapedRecipe)
            block(recipe)
}