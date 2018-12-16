@file:JvmMultifileClass
@file:JvmName("StandardDumpers")
package ru.pearx.pmdumper.dumper.standard

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper

val DumperBlocks = dumper {
    registryName = ResourceLocation(ID, "blocks")
    header = listOf("ID", "Class Name", "BlockState Properties", "BlockState Class Name")
    amounts {
        for (block in ForgeRegistries.BLOCKS)
            this += block.registryName
    }
    iterator {
        for (block in ForgeRegistries.BLOCKS) {
            with(ArrayList<String>(header.size)) {
                with(block) {
                    add(registryName.toString())
                    add(this::class.java.name)
                    add(blockState.properties.toString())
                    add(blockState::class.java.name)
                }
                yield(this)
            }
        }
    }
}