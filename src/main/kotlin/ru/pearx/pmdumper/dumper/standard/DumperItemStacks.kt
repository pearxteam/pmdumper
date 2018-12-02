package ru.pearx.pmdumper.dumper.standard

import moze_intel.projecte.utils.EMCHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.oredict.OreDictionary
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.PROJECTE_ID
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.toPlusMinusString

val DumperItemStacks = dumper {
    registryName = ResourceLocation(ID, "itemstacks")
    header = listOf("ID", "Metadata", "NBT Tag Compound", "Display Name", "Tooltip", "Translation Key", "Class Name", "Is ItemBlock", "OreDict Names", "Max Stack Size", "Max Damage", "EMC")
    iteratorBuilder = { amounts ->
        iterator {
            for (item in ForgeRegistries.ITEMS) {
                val stacks = NonNullList.create<ItemStack>().apply {
                    item.getSubItems(item.creativeTab ?: CreativeTabs.SEARCH, this)
                }
                for (stack in stacks) {
                    with(ArrayList<String>(header.size)) {
                        with(item) {
                            amounts += registryName
                            add(registryName.toString())
                            add(stack.metadata.toString())
                            add(stack.tagCompound?.toString() ?: "")
                            add(getItemStackDisplayName(stack))
                            if (FMLCommonHandler.instance().side == Side.CLIENT)
                                add(mutableListOf<String>().apply { addInformation(stack, Minecraft.getMinecraft().world, this, ITooltipFlag.TooltipFlags.NORMAL) }.joinToString(separator = System.lineSeparator()))
                            else
                                add("N/A")
                            add(getTranslationKey(stack))
                            add(this::class.java.name)
                            add((this is ItemBlock).toPlusMinusString())
                            add(StringBuilder().apply {
                                var start = true
                                for (id in OreDictionary.getOreIDs(stack)) {
                                    if(start)
                                        start = false
                                    else
                                        appendln()
                                    append(OreDictionary.getOreName(id))
                                }

                            }.toString())
                            add(getItemStackLimit(stack).toString())
                            add(getMaxDamage(stack).toString())
                            if(Loader.isModLoaded(PROJECTE_ID))
                                add(if(EMCHelper.doesItemHaveEmc(stack)) EMCHelper.getEmcValue(stack).toString() else "")
                            else
                                add("N/A")
                        }
                        yield(this)
                    }
                }
            }
        }
    }
}