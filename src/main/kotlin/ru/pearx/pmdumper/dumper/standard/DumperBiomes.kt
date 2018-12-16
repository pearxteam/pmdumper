@file:JvmMultifileClass
@file:JvmName("StandardDumpers")
package ru.pearx.pmdumper.dumper.standard

import net.minecraft.entity.EnumCreatureType
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import ru.pearx.pmdumper.*
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.utils.client
import ru.pearx.pmdumper.utils.mutableListOfNotNull
import ru.pearx.pmdumper.utils.toHexColorString
import ru.pearx.pmdumper.utils.toPlusMinusString

val DumperBiomes = dumper {
    registryName = ResourceLocation(ID, "biomes")
    header = mutableListOfNotNull("ID", client("Name"), "Default Temperature", "Base Height", "Height Variation", "Class Name", "Is Snowy", "Can Rain", "Rainfall", "Base Biome", "Filler Block", "Top Block", client("Water Color"), "Water Color Multiplier", "Creature Spawning Chance").apply {
        for (type in EnumCreatureType.values())
            add("${type.toString().toLowerCase().capitalize()} Spawn List: Entity*(Min Group-Max Group):Weight")
    }
    amounts {
        for (biome in ForgeRegistries.BIOMES)
            this += biome.registryName
    }
    iterator {
        for (biome in ForgeRegistries.BIOMES) {
            with(ArrayList<String>(header.size)) {
                with(biome) {
                    add(registryName.toString())
                    client { add(biomeName) }
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
                    client { add(waterColor.toHexColorString()) }
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