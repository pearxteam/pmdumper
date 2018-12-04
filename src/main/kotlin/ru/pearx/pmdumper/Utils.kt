package ru.pearx.pmdumper

import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import org.apache.commons.lang3.reflect.FieldUtils

internal fun <T : IForgeRegistryEntry<T>> lookupRegistryElements(registry: IForgeRegistry<T>, name: String): List<T> {
    val foundElements = mutableListOf<T>()

    for (element in registry) {
        if (element.registryName != null) {
            if (element.registryName.toString() == name)
                return listOf(element)
            if (element.registryName!!.path == name)
                foundElements.add(element)
        }
    }

    return foundElements
}

internal fun <T : IForgeRegistryEntry<T>> getRegistryElementNames(registry: IForgeRegistry<T>): List<String> {
    return ArrayList<String>(registry.entries.size).apply {
        for (element in registry) {

            if (element.registryName != null)
                add(getRegistryElementName(registry, element.registryName!!))
        }
    }
}

internal fun <T : IForgeRegistryEntry<T>> getRegistryElementName(registry: IForgeRegistry<T>, name: ResourceLocation): String {
    val found: Boolean = run {
        for (anotherElement in registry) {
            if (anotherElement.registryName != name &&
                anotherElement.registryName!!.path == name.path) {
                return@run true
            }
        }
        return@run false
    }

    return if (found)
        name.toString()
    else
        name.path
}

fun Boolean.toPlusMinusString() = if(this) "+" else "-"

fun ItemStack.toFullString() = StringBuilder().apply {
    append(item.registryName)

    if(metadata != 0) {
        append(":")
        append(metadata)
    }

    if(count != 1) {
        append("*")
        append(count)
    }

    if(hasTagCompound()) {
        append(" ")
        append(tagCompound.toString())
    }
}.toString()

fun Int.toHexColorString() = "#${Integer.toHexString(this).toUpperCase().padStart(6, '0')}"

inline fun <reified T> Any.readField(name: String) = FieldUtils.readField(this, name, true) as T