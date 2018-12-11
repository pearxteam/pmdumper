package ru.pearx.pmdumper

import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side
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

fun Boolean.toPlusMinusString() = if (this) "+" else "-"

fun ItemStack.toFullString(wildcardMetaAsAny: Boolean = false) = StringBuilder().apply {
    append(item.registryName)

    if (metadata != 0) {
        append(":")
        if (wildcardMetaAsAny && metadata == 32767)
            append("any")
        else
            append(metadata)
    }

    if (count != 1) {
        append("*")
        append(count)
    }

    if (hasTagCompound()) {
        append(" ")
        append(tagCompound.toString())
    }
}.toString()

fun <V : IForgeRegistryEntry<V>> IForgeRegistry<V>.registerNonNull(v: V?) {
    if (v != null)
        register(v)
}

fun Int.toHexColorString() = "#${Integer.toHexString(this).toUpperCase().padStart(6, '0')}"

fun ResourceLocation.toPath(topPath: String = "", postfix: String = "") = "assets/$namespace/$topPath${if(topPath.isEmpty()) "" else "/"}$path$postfix"

fun ResourceLocation.toTexturesPath(pngPostfix: Boolean = true) = toPath("textures", ".png")

fun <T> mutableListOfNotNull(vararg elements: T?): MutableList<T> {
    val lst = ArrayList<T>(elements.size)
    for (element in elements)
        if (element != null)
            lst.add(element)
    return lst
}

inline fun <reified T> Any.readField(name: String) = FieldUtils.readField(this, name, true) as T

val isClient = FMLCommonHandler.instance().side == Side.CLIENT


fun <T> ifOrNull(bool: Boolean, value: T) = if (bool) value else null
inline fun <T> ifOrNull(bool: Boolean, func: () -> T) = if (bool) func() else null

fun <T> client(value: T) = ifOrNull(isClient, value)
inline fun <T> client(func: () -> T) = ifOrNull(isClient, func)

inline fun client(func: () -> Unit) {
    if (isClient)
        func()
}