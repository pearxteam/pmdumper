package ru.pearx.pmdumper.utils

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.NonNullList
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.oredict.OreIngredient

fun ItemStack.appendTo(to: Appendable, wildcardMetaAsAny: Boolean = false) {
    to.apply {
        if (isEmpty) return

        if (count != 1) {
            append(count.toString())
            append("*")
        }

        append(item.registryName.toString())

        if (metadata != 0) {
            append(":")
            if (wildcardMetaAsAny && metadata == OreDictionary.WILDCARD_VALUE)
                append("*")
            else
                append(metadata.toString())
        }

        if (hasTagCompound()) {
            append(" ")
            append(tagCompound.toString())
        }
    }
}

fun ItemStack.toFullString(wildcardMetaAsAny: Boolean = false) = StringBuilder().apply { appendTo(this, wildcardMetaAsAny) }.toString()

inline fun <reified T : Item> eachStack(block: (item: T, stack: ItemStack) -> Unit) {
    for (item in ForgeRegistries.ITEMS) {
        if (item is T) {
            val stacks = NonNullList.create<ItemStack>().apply {
                item.getSubItems(this)
            }
            for (stack in stacks) {
                block(item, stack)
            }
        }
    }
}

fun Item.getSubItems(list: NonNullList<ItemStack>) {
    getSubItems(creativeTab ?: CreativeTabs.SEARCH, list)
}

fun Ingredient.appendTo(to: Appendable) {
    if (matchingStacks.isNotEmpty()) {
        if (this is OreIngredient) {
            to.append("ore:")
            to.append(run {
                val ores = readField<NonNullList<ItemStack>>("ores")
                for (oreId in OreDictionary.getOreIDs(matchingStacks[0])) {
                    val oreName = OreDictionary.getOreName(oreId)
                    val ores1 = OreDictionary.getOres(oreName)
                    if (ores == ores1)
                        return@run oreName
                }
                return@run ""
            })
        }
        else {
            val internalMatchingStacks = readField<Array<ItemStack>>("matchingStacks")
            if (!internalMatchingStacks.isEmpty())
                appendStackListOrSeparatedTo(to, internalMatchingStacks)
            else
                appendStackListOrSeparatedTo(to, matchingStacks)
        }
    }
}

private fun appendStackListOrSeparatedTo(to: Appendable, stacks: Array<ItemStack>) {
    var startStacks = true
    for(stack in stacks) {
        if (startStacks)
            startStacks = false
        else
            to.append(" | ")
        to.append(stack.toFullString(true))
    }
}

fun Ingredient.toFullString() = StringBuilder().apply { appendTo(this) }.toString()