@file:JvmMultifileClass
@file:JvmName("StandardDumpers")
package ru.pearx.pmdumper.dumper.standard

import net.minecraft.advancements.Advancement
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.DimensionManager
import ru.pearx.pmdumper.*
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.utils.*

val DumperAdvancements = dumper {
    registryName = ResourceLocation(ID, "advancements")
    header = listOfNotNull("ID", "Display Text", "Title", "Description", client("Icon"), client("X"), client("Y"), client("Background"), "Frame", "Is Hidden", "Should Announce", client("Should Show Toast"), "Parent", "Children", "Reward Experience", "Reward Loot", "Reward Recipes", "Reward Function")
    amounts {
        for (adv in collectAdvancements())
            this += adv.id
    }
    iterator {
        for (adv in collectAdvancements()) {
            with(ArrayList<String>(header.size)) {
                with(adv) {
                    add(id.toString())
                    add(displayText.formattedText)
                    display?.run {
                        add(title.formattedText)
                        add(description.formattedText)
                        client {
                            add(icon.toFullString())
                            add(x.toString())
                            add(y.toString())
                            add(background?.toPath() ?: "")
                        }
                        add(frame.toString())
                        add(isHidden.toPlusMinusString())
                        add(shouldAnnounceToChat().toPlusMinusString())
                        client { add(shouldShowToast().toPlusMinusString()) }
                    } ?: repeat(if (isClient) 10 else 5) { add("") }
                    add(parent?.id?.toString() ?: "")
                    add(StringBuilder().apply {
                        var start = true
                        for (child in children) {
                            if (start)
                                start = false
                            else
                                appendln()
                            append(child.id.toString())
                        }
                    }.toString())
                    rewards.apply {
                        add(experience.toString())
                        add(loot.joinToString(separator = System.lineSeparator()))
                        add(recipes.joinToString(separator = System.lineSeparator()))
                        add(function?.toString() ?: "")
                    }
                }
                yield(this)
            }
        }
    }
}

private fun collectAdvancements(): List<Advancement> {
    val advancements = mutableListOf<Advancement>()
    for (w in DimensionManager.getWorlds())
        for (adv in w.advancementManager.advancements)
            if (adv !in advancements)
                advancements.add(adv)
    return advancements
}