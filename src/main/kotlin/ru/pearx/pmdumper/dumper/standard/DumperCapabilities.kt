@file:JvmMultifileClass
@file:JvmName("StandardDumpers")
package ru.pearx.pmdumper.dumper.standard

import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.utils.readField
import java.util.*

val DumperCapabilities = dumper {
    registryName = ResourceLocation(ID, "capabilities")
    header = listOf("Interface", "Default Instance Class", "Storage Class")
    iterator {
        for ((key, value) in CapabilityManager.INSTANCE.readField<IdentityHashMap<String, Capability<*>>>("providers")) {
            with(ArrayList<String>(header.size)) {
                add(key)
                val defaultInstance = value.defaultInstance
                add(if (defaultInstance == null) "" else defaultInstance::class.java.name)
                add(value.storage::class.java.name)
                yield(this)
            }
        }
    }
}