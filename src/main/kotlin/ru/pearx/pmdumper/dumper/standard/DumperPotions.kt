@file:JvmMultifileClass
@file:JvmName("StandardDumpers")
package ru.pearx.pmdumper.dumper.standard

import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.fml.common.registry.ForgeRegistries
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.utils.appendTo
import ru.pearx.pmdumper.utils.client
import ru.pearx.pmdumper.utils.toHexColorString
import ru.pearx.pmdumper.utils.toPlusMinusString

val DumperPotions = dumper {
    registryName = ResourceLocation(ID, "potions")
    header = listOfNotNull("ID", "Display Name", "Name", "Class Name", "Is Bad Effect", "Is Instant", client("Is Beneficial"), client("Status Icon Index"), client("Liquid Color"), "Curative Items", client("Attribute Modifiers"))
    amounts {
        for (potion in ForgeRegistries.POTIONS)
            this += potion.registryName
    }
    iterator {
        for (potion in ForgeRegistries.POTIONS) {
            with(ArrayList<String>(header.size)) {
                with(potion) {
                    add(registryName.toString())
                    add(I18n.translateToLocalFormatted(name))
                    add(name)
                    add(this::class.java.name)
                    add(isBadEffect.toPlusMinusString())
                    add(isInstant.toPlusMinusString())
                    client {
                        add(isBeneficial.toPlusMinusString())
                        add(statusIconIndex.toString())
                        add(liquidColor.toHexColorString())
                    }
                    add(StringBuilder().apply {
                        var start = true
                        for (item in curativeItems) {
                            if (start)
                                start = false
                            else
                                appendln()
                            item.appendTo(this)
                        }
                    }.toString())
                    client {
                        add(
                            StringBuilder().apply {
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
                            }.toString()
                        )
                    }
                }
                yield(this)
            }
        }
    }
}