package ru.pearx.pmdumper.dumper.standard

import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.fml.common.registry.ForgeRegistries
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.toFullString
import ru.pearx.pmdumper.toPlusMinusString

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