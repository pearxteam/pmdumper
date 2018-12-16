@file:JvmMultifileClass
@file:JvmName("StandardDumpers")
package ru.pearx.pmdumper.dumper.standard

import moze_intel.projecte.utils.EMCHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.ItemModelMesherForge
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.oredict.OreDictionary
import ru.pearx.pmdumper.*
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.utils.client
import ru.pearx.pmdumper.utils.eachStack
import ru.pearx.pmdumper.utils.ifOrNull
import ru.pearx.pmdumper.utils.toPlusMinusString

val DumperItemStacks = dumper {
    registryName = ResourceLocation(ID, "itemstacks")
    header = listOfNotNull("ID", "Metadata", "NBT Tag Compound", "Display Name", client("Tooltip"), "Translation Key", "Class Name", "Is ItemBlock", "OreDict Names", "Max Stack Size", "Max Damage", client("Model Name"), ifOrNull(Loader.isModLoaded(PROJECTE_ID), "EMC"))
    amounts {
        eachStack<Item> { item, _ ->
            this += item.registryName
        }
    }
    iterator {
        eachStack<Item> { item, stack ->
            with(ArrayList<String>(header.size)) {
                with(item) {
                    add(registryName.toString())
                    add(stack.metadata.toString())
                    add(stack.tagCompound?.toString() ?: "")
                    add(getItemStackDisplayName(stack))
                    client { add(mutableListOf<String>().apply { addInformation(stack, Minecraft.getMinecraft().world, this, ITooltipFlag.TooltipFlags.NORMAL) }.joinToString(separator = System.lineSeparator())) }
                    add(getTranslationKey(stack))
                    add(this::class.java.name)
                    add((this is ItemBlock).toPlusMinusString())
                    add(StringBuilder().apply {
                        var start = true
                        for (id in OreDictionary.getOreIDs(stack)) {
                            if (start)
                                start = false
                            else
                                appendln()
                            append(OreDictionary.getOreName(id))
                        }
                    }.toString())
                    add(getItemStackLimit(stack).toString())
                    add(getMaxDamage(stack).toString())
                    client { add((Minecraft.getMinecraft().renderItem.itemModelMesher as ItemModelMesherForge).getLocation(stack).toString()) }
                    if (Loader.isModLoaded(PROJECTE_ID))
                        add(if (EMCHelper.doesItemHaveEmc(stack)) EMCHelper.getEmcValue(stack).toString() else "")
                }
                yield(this)
            }
        }
    }
}