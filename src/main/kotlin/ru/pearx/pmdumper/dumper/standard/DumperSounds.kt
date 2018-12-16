@file:JvmMultifileClass
@file:JvmName("StandardDumpers")
package ru.pearx.pmdumper.dumper.standard

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper

val DumperSounds = dumper {
    registryName = ResourceLocation(ID, "sounds")
    header = listOf("ID")
    amounts {
        for (sound in ForgeRegistries.SOUND_EVENTS)
            this += registryName
    }
    iterator {
        for (sound in ForgeRegistries.SOUND_EVENTS) {
            with(ArrayList<String>(header.size)) {
                with(sound) {
                    add(registryName.toString())
                }
                yield(this)
            }
        }
    }
}