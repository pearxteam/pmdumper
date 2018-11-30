package ru.pearx.pmdumper.dumper.standard

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper

val DumperEnchantments = dumper {
    registryName = ResourceLocation(ID, "enchantments")
    header = listOf("ID", "Name", "Class Name", "Levels", "Rarity", "Is Curse", "Type", "Allowed on books", "Is Treasure", "Localized Name [Min Enchantability - Max Enchantability]...")
    columnToSortBy = 0
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
                        add(isCurse.toString())
                        add(type.toString())
                        add(isAllowedOnBooks.toString())
                        add(isTreasureEnchantment.toString())
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
    header = listOf("ID", "Name")
    columnToSortBy = 0
    iteratorBuilder = { amounts ->
        iterator {
            for (sound in ForgeRegistries.SOUND_EVENTS) {
                with(ArrayList<String>(header.size)) {
                    with(sound) {
                        amounts += registryName
                        add(registryName.toString())
                        add(soundName.toString())
                    }
                    yield(this)
                }
            }
        }
    }
}