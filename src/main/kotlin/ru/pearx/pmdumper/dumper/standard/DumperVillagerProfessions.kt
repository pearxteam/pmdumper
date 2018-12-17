@file:JvmMultifileClass
@file:JvmName("StandardDumpers")

package ru.pearx.pmdumper.dumper.standard

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.fml.common.registry.VillagerRegistry
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.utils.readField
import ru.pearx.pmdumper.utils.toPath
import ru.pearx.pmdumper.utils.tryDump

val DumperVillagerProfessions = dumper {
    registryName = ResourceLocation(ID, "villager_professions")
    header = listOf("ID", "Skin", "Zombie Skin", "Career Names")
    amounts {
        for (prof in ForgeRegistries.VILLAGER_PROFESSIONS)
            this += prof.registryName
    }
    iterator {
        for (profession in ForgeRegistries.VILLAGER_PROFESSIONS) {
            tryDump(ArrayList(header.size)) {
                with(profession) {
                    add(registryName.toString())
                    add(skin.toPath())
                    add(zombieSkin.toPath())
                    add(StringBuilder().apply {
                        var start = true
                        for (career in profession.readField<List<VillagerRegistry.VillagerCareer>>("careers")) {
                            if (start)
                                start = false
                            else
                                appendln()
                            append(career.name)
                        }
                    }.toString())
                }
            }
        }
    }
}