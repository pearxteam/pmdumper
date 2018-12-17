@file:JvmMultifileClass
@file:JvmName("StandardDumpers")

package ru.pearx.pmdumper.dumper.standard

import net.minecraft.item.ItemFood
import net.minecraft.util.ResourceLocation
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.utils.eachStack
import ru.pearx.pmdumper.utils.toFullString
import ru.pearx.pmdumper.utils.toPlusMinusString
import ru.pearx.pmdumper.utils.tryDump

val DumperFood = dumper {
    registryName = ResourceLocation(ID, "food")
    header = listOf("Item", "Heal Amount", "Saturation Modifier", "Is Wolfs Favorite Meal", "Is Always Edible", "Item Use Duration", "Potion Effect", "Potion Effect Probability")
    amounts {
        eachStack<ItemFood> { item, _ ->
            this += item.registryName
        }
    }
    iterator {
        eachStack<ItemFood> { item, stack ->
            tryDump(ArrayList(header.size)) {
                with(item) {
                    add(stack.toFullString())
                    add(getHealAmount(stack).toString())
                    add(getSaturationModifier(stack).toString())
                    add(isWolfsFavoriteMeat.toPlusMinusString())
                    add(alwaysEdible.toPlusMinusString())
                    add(itemUseDuration.toString())
                    if (potionId != null) {
                        add(potionId.toString())
                        add(potionEffectProbability.toString())
                    }
                    else repeat(2) { add("") }
                }
            }
        }
    }
}