package ru.pearx.pmdumper.dumper.standard

import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.fml.relauncher.Side
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper
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
    header = listOf("ID", "Metadata", "NBT Tag Compound", "Display Name", "Tooltip", "Translation Key", "Class Name", "Is ItemBlock", "Max Stack Size", "Max Damage")
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
                            add(getItemStackLimit(stack).toString())
                            add(getMaxDamage(stack).toString())
                        }
                        yield(this)
                    }
                }
            }
        }
    }
}