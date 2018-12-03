package ru.pearx.pmdumper.dumper

import moze_intel.projecte.utils.EMCHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EnumCreatureType
import net.minecraft.init.Items
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.oredict.OreDictionary
import ru.pearx.pmdumper.*
import org.apache.commons.lang3.reflect.FieldUtils
import net.minecraftforge.fml.common.registry.VillagerRegistry



val DumperBiomes = dumper {
    registryName = ResourceLocation(ID, "biomes")
    header = mutableListOf("ID", "Name", "Default Temperature", "Base Height", "Height Variation", "Class Name", "Is Snowy", "Can Rain", "Rainfall", "Base Biome", "Filler Block", "Top Block", "Water Color", "Water Color Multiplier", "Creature Spawning Chance").apply {
        for(type in EnumCreatureType.values()) {
            add("${type.toString().toLowerCase().capitalize()} Spawn List: Entity*(Min Group-Max Group):Weight")
    }
    iterator { amounts ->
        for (biome in ForgeRegistries.BIOMES) {
            with(ArrayList<String>(header.size)) {
                with(biome) {
                    amounts += registryName
                    add(registryName.toString())
                    add(biomeName)
                    add(defaultTemperature.toString())
                    add(baseHeight.toString())
                    add(heightVariation.toString())
                    add(this::class.java.name)
                    add(isSnowyBiome.toPlusMinusString())
                    add(canRain().toPlusMinusString())
                    add(rainfall.toString())
                    add(baseBiomeRegName ?: "")
                    add(fillerBlock.toString())
                    add(topBlock.toString())
                    add(waterColor.toHexColorString())
                    add(waterColorMultiplier.toString())
                    add(spawningChance.toString())
                    for (type in EnumCreatureType.values()) {
                        add(getSpawnableList(type).joinToString(separator = System.lineSeparator()))
                    }
                }
                yield(this)
            }
        }
    }
}
}

val DumperEnchantments = dumper {
    registryName = ResourceLocation(ID, "enchantments")
    header = listOf("ID", "Name", "Class Name", "Levels", "Rarity", "Is Curse", "Type", "Allowed on Books", "Is Treasure", "Localized Name [Min Enchantability - Max Enchantability]...")
    iterator { amounts ->
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
                    add(StringBuilder().apply {
                        for (lvl in minLevel..maxLevel) {
                            append("${getTranslatedName(lvl)} [${getMinEnchantability(lvl)} - ${getMaxEnchantability(lvl)}]")
                        }
                    }.toString())
                }
                yield(this)
            }
        }
    }
}

val DumperItemStacks = dumper {
    registryName = ResourceLocation(ID, "itemstacks")
    header = listOf("ID", "Metadata", "NBT Tag Compound", "Display Name", "Tooltip", "Translation Key", "Class Name", "Is ItemBlock", "OreDict Names", "Max Stack Size", "Max Damage", "EMC")
    iterator { amounts ->
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
                                if (start)
                                    start = false
                                else
                                    appendln()
                                append(OreDictionary.getOreName(id))
                            }

                        }.toString())
                        add(getItemStackLimit(stack).toString())
                        add(getMaxDamage(stack).toString())
                        if (Loader.isModLoaded(PROJECTE_ID))
                            add(if (EMCHelper.doesItemHaveEmc(stack)) EMCHelper.getEmcValue(stack).toString() else "")
                        else
                            add("N/A")
                    }
                    yield(this)
                }
            }
        }
    }
}

val DumperPotions = dumper {
    registryName = ResourceLocation(ID, "potions")
    header = listOf("ID", "Display Name", "Name", "Class Name", "Is Bad Effect", "Is Instant", "Is Beneficial", "Status Icon Index", "Liquid Color", "Curative Items", "Attribute Modifiers")
    iterator { amounts ->
        for (potion in ForgeRegistries.POTIONS) {
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
                    add(liquidColor.toHexColorString())
                    add(StringBuilder().apply {
                        var start = true
                        for (item in curativeItems) {
                            if (start)
                                start = false
                            else
                                appendln()
                            append(item.toFullString())
                        }
                    }.toString())
                    add(StringBuilder().apply {
                        var start = true
                        for ((attribute, modifier) in attributeModifierMap) {
                            if (start)
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

val DumperSounds = dumper {
    registryName = ResourceLocation(ID, "sounds")
    header = listOf("ID")
    iterator { amounts ->
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

val DumperVillagerProfessions = dumper {
    registryName = ResourceLocation(ID, "villagerprofessions")
    header = listOf("ID", "Skin", "Zombie Skin", "Career Names")
    iterator { amounts ->
        for(profession in ForgeRegistries.VILLAGER_PROFESSIONS) {
            with(ArrayList<String>(header.size)) {
                with(profession) {
                    amounts += registryName
                    add(registryName.toString())
                    add(skin.toString())
                    add(zombieSkin.toString())
                    add(StringBuilder().apply { // todo: dump trades
                        var start = true
                        for(career in profession.readField<List<VillagerRegistry.VillagerCareer>>("careers")) {
                            if(start)
                                start = false
                            else
                                appendln()
                            append(career.name)
                        }
                    }.toString())
                }
                yield(this)
            }
        }
    }
}

val DumperEntities = dumper {
    registryName = ResourceLocation(ID, "entities")
    header = listOf("ID", "Name", "Class Name", "Primary Egg Color", "Secondary Egg Color")
    iterator { amounts ->
        for(entity in ForgeRegistries.ENTITIES) {
            with(ArrayList<String>(header.size)) {
                with(entity) {
                    amounts += registryName
                    add(registryName.toString())
                    add(name)
                    add(entityClass.name)
                    add(egg?.primaryColor?.toHexColorString() ?: "")
                    add(egg?.secondaryColor?.toHexColorString() ?: "")
                }
                yield(this)
            }
        }
    }
}

val DumperModels = dumper {
    registryName = ResourceLocation(ID, "models")
    header = listOf("Variant", "Class Name")
    iterator { amounts ->
        val registry = Minecraft.getMinecraft().modelManager.modelRegistry
        for(key in registry.keys) {
            val model = registry.getObject(key)!!
            amounts += key
            with(ArrayList<String>(header.size)) {
                add(key.toString())
                add(model::class.java.name)
                yield(this)
            }
        }
    }
}