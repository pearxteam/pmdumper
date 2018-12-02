package ru.pearx.pmdumper.dumper.standard

import moze_intel.projecte.utils.EMCHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Items
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.oredict.OreDictionary
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.PROJECTE_ID
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.toFullString
import ru.pearx.pmdumper.toPlusMinusString

val DumperEnchantments = dumper {
    registryName = ResourceLocation(ID, "enchantments")
    header = listOf("ID", "Name", "Class Name", "Levels", "Rarity", "Is Curse", "Type", "Allowed on Books", "Is Treasure", "Localized Name [Min Enchantability - Max Enchantability]...")
    iteratorBuilder = { amounts ->
        iterator {
            for (ench in ForgeRegistries.ENCHANTMENTS) {
                with(ArrayList<String>(header.size)) {
                    with(ench) {
                        amounts += registryName
                        add(registryName.toString())
                        add(name)
                        add(this::class.java.name)
                        add("$minLevel - $maxLevel")
                        add(rarity.toString())
                        add(isCurse.toPlusMinusString())
                        add(type.toString())
                        add(isAllowedOnBooks.toPlusMinusString())
                        add(isTreasureEnchantment.toPlusMinusString())
                        for (lvl in minLevel..maxLevel) {
                            add("${getTranslatedName(lvl)} [${getMinEnchantability(lvl)} - ${getMaxEnchantability(lvl)}]")
                        }
                    }
                    yield(this)
                }
            }
        }
    }
}

val DumperSounds = dumper {
    registryName = ResourceLocation(ID, "sounds")
    header = listOf("ID")
    iteratorBuilder = { amounts ->
        iterator {
            for (sound in ForgeRegistries.SOUND_EVENTS) {
                with(ArrayList<String>(header.size)) {
                    with(sound) {
                        amounts += registryName
                        add(registryName.toString())
                    }
                    yield(this)
                }
            }
        }
    }
}

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

val DumperPotions = dumper {
    registryName = ResourceLocation(ID, "potions")
    header = listOf("ID", "Display Name", "Name", "Class Name", "Is Bad Effect", "Is Instant", "Is Beneficial", "Status Icon Index", "Liquid Color", "Curative Items", "Attribute Modifiers")
    iteratorBuilder = { amounts ->
         iterator {
            for(potion in ForgeRegistries.POTIONS) {
                with(ArrayList<String>(header.size)) {
                    with(potion) {
                        amounts += registryName
                        add(registryName.toString())
                        add(I18n.translateToLocalFormatted(name))
                        add(name)
                        add(this::class.java.name)
                        add(isBadEffect.toPlusMinusString())
                        add(isInstant.toPlusMinusString())
                        add(isBeneficial.toPlusMinusString())
                        add(statusIconIndex.toString())
                        add("#${Integer.toHexString(liquidColor).toUpperCase().padStart(6, '0')}")
                        add(StringBuilder().apply {
                            var start = true
                            for(item in curativeItems) {
                                if(start)
                                    start = false
                                else
                                    appendln()
                                append(item.toFullString())
                            }
                        }.toString())
                        add(StringBuilder().apply {
                            var start = true
                            for((attribute, modifier) in attributeModifierMap) {
                                if(start)
                                    start = true
                                else
                                    appendln()
                                append(attribute.name)
                                append(": ")
                                append(modifier)
                            }
                        }.toString())
                    }
                    yield(this)
                }
            }
        }
    }
}