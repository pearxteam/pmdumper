package ru.pearx.pmdumper.utils

import net.minecraft.util.ResourceLocation
import org.apache.commons.lang3.reflect.FieldUtils

fun Boolean.toPlusMinusString() = if (this) "+" else "-"

fun Int.toHexColorString() = "#${Integer.toHexString(this).toUpperCase().padStart(6, '0')}"

fun ResourceLocation.toPath(topPath: String = "", postfix: String = "") = "assets/$namespace/$topPath${if(topPath.isEmpty()) "" else "/"}$path$postfix"

fun ResourceLocation.toTexturesPath(pngPostfix: Boolean = true) = toPath("textures", if(pngPostfix) ".png" else "")

fun <T> mutableListOfNotNull(vararg elements: T?): MutableList<T> {
    val lst = ArrayList<T>(elements.size)
    for (element in elements)
        if (element != null)
            lst.add(element)
    return lst
}

inline fun <reified T> Any.readField(name: String) = FieldUtils.readField(this, name, true) as T