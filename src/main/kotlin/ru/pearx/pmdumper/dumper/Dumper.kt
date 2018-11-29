package ru.pearx.pmdumper.dumper

import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistryEntry

typealias DumperTable = MutableList<MutableList<String>>
class DumperAmounts : MutableMap<String, Int> by hashMapOf() {
    operator fun plusAssign(value: String) {
        this[value] = (this[value] ?: 0) + 1
    }

    operator fun plusAssign(value: ResourceLocation?) = plusAssign(value?.namespace ?: "null")
}
typealias Filler = (table: DumperTable, amounts: DumperAmounts) -> Unit

interface IDumper : IForgeRegistryEntry<IDumper> {
    val header: List<String>
    fun fillData(table: DumperTable, amounts: DumperAmounts)

    override fun getRegistryType(): Class<IDumper> = IDumper::class.java
}

class Dumper : IDumper {
    lateinit var filler: Filler

    override lateinit var header: List<String>

    private var registryName: ResourceLocation? = null

    override fun getRegistryName(): ResourceLocation? = registryName

    override fun setRegistryName(name: ResourceLocation?): IDumper {
        registryName = name
        return this
    }

    override fun fillData(table: DumperTable, amounts: DumperAmounts) = filler(table, amounts)
}

inline fun buildDumper(init: Dumper.() -> Unit): IDumper {
    val dumper = Dumper()
    dumper.init()
    return dumper
}