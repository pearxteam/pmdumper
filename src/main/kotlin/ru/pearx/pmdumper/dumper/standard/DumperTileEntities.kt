@file:JvmMultifileClass
@file:JvmName("StandardDumpers")
package ru.pearx.pmdumper.dumper.standard

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ITickable
import net.minecraft.util.ResourceLocation
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.utils.toPlusMinusString

val DumperTileEntities = dumper {
    registryName = ResourceLocation(ID, "tile_entities")
    header = listOf("ID", "Class Name", "Is Tickable")
    amounts {
        for (tileId in TileEntity.REGISTRY.keys)
            this += tileId
    }
    iterator {
        for (id in TileEntity.REGISTRY.keys) {
            with(ArrayList<String>(header.size)) {
                add(id.toString())
                val tileClass = TileEntity.REGISTRY.getObject(id)!!
                add(tileClass.name)
                add(ITickable::class.java.isAssignableFrom(tileClass).toPlusMinusString())
                yield(this)
            }
        }
    }
}