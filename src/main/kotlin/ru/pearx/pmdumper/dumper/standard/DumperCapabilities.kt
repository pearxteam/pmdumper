@file:JvmMultifileClass
@file:JvmName("StandardDumpers")

package ru.pearx.pmdumper.dumper.standard

import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.utils.add
import ru.pearx.pmdumper.utils.readField
import ru.pearx.pmdumper.utils.tryDump
import java.util.*

val DumperCapabilities = dumper {
    registryName = ResourceLocation(ID, "capabilities")
    header = listOf("Interface", "Default Instance Class", "Storage Class")
    iterator {
        for ((key, value) in CapabilityManager.INSTANCE.readField<IdentityHashMap<String, Capability<*>>>("providers")) {
            tryDump(ArrayList(header.size)) {
                add(key)
                add {
                    val defaultInstance = value.defaultInstance
                    if (defaultInstance == null) "" else defaultInstance::class.java.name
                }
                add(value.storage::class.java.name)
            }
        }
    }
}