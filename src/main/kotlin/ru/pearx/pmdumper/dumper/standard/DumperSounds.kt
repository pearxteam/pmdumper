@file:JvmMultifileClass
@file:JvmName("StandardDumpers")

package ru.pearx.pmdumper.dumper.standard

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.utils.tryDump

val DumperSounds = dumper {
    registryName = ResourceLocation(ID, "sounds")
    header = listOf("ID")
    amounts {
        for (sound in ForgeRegistries.SOUND_EVENTS)
            this += registryName
    }
    iterator {
        for (sound in ForgeRegistries.SOUND_EVENTS) {
            tryDump(ArrayList(header.size)) {
                with(sound) {
                    add(registryName.toString())
                }
            }
        }
    }
}