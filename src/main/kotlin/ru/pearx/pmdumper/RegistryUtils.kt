package ru.pearx.pmdumper

import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry

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

            if (element.registryName != null) {

                val found: Boolean = run {
                    for (anotherElement in registry) {
                        if (anotherElement != element && anotherElement.registryName != null &&
                            anotherElement.registryName!!.path == element.registryName!!.path) {
                            return@run true
                        }
                    }
                    return@run false
                }

                if (found)
                    add(element.registryName.toString())
                else
                    add(element.registryName!!.path)
            }
        }
    }
}