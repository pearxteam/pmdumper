package ru.pearx.pmdumper.dumper.standard

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper

val DumperSounds = dumper {
    registryName = ResourceLocation(ID, "sounds")
    header = listOf("ID")
    iteratorBuilder = { amounts ->
        iterator {
            for (sound in ForgeRegistries.SOUND_EVENTS) {
                with(ArrayList<String>(header.size)) {
                    with(sound) {
                        amounts += registryName
                        add(registryName.toString())
                    }
                    yield(this)
                }
            }
        }
    }
}