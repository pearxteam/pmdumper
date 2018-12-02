package ru.pearx.pmdumper.dumper.standard

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries
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
}