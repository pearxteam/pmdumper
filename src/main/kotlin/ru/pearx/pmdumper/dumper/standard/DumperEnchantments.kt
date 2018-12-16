@file:JvmMultifileClass
@file:JvmName("StandardDumpers")
package ru.pearx.pmdumper.dumper.standard

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.utils.toPlusMinusString

val DumperEnchantments = dumper {
    registryName = ResourceLocation(ID, "enchantments")
    header = listOf("ID", "Name", "Class Name", "Levels", "Rarity", "Is Curse", "Type", "Allowed on Books", "Is Treasure", "Localized Name [Min Enchantability - Max Enchantability]")
    amounts {
        for (ench in ForgeRegistries.ENCHANTMENTS)
            this += ench.registryName
    }
    iterator {
        for (ench in ForgeRegistries.ENCHANTMENTS) {
            with(ArrayList<String>(header.size)) {
                with(ench) {
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
                        var start = true
                        for (lvl in minLevel..maxLevel) {
                            if (start)
                                start = false
                            else
                                appendln()
                            append("${getTranslatedName(lvl)} [${getMinEnchantability(lvl)} - ${getMaxEnchantability(lvl)}]")
                        }
                    }.toString())
                }
                yield(this)
            }
        }
    }
}