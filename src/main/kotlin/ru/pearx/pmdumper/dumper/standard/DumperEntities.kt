@file:JvmMultifileClass
@file:JvmName("StandardDumpers")
package ru.pearx.pmdumper.dumper.standard

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.utils.toHexColorString

val DumperEntities = dumper {
    registryName = ResourceLocation(ID, "entities")
    header = listOf("ID", "Name", "Class Name", "Primary Egg Color", "Secondary Egg Color")
    amounts {
        for (entity in ForgeRegistries.ENTITIES)
            this += entity.registryName
    }
    iterator {
        for (entity in ForgeRegistries.ENTITIES) {
            with(ArrayList<String>(header.size)) {
                with(entity) {
                    add(registryName.toString())
                    add(name)
                    add(entityClass.name)
                    add(egg?.primaryColor?.toHexColorString() ?: "")
                    add(egg?.secondaryColor?.toHexColorString() ?: "")
                }
                yield(this)
            }
        }
    }
}