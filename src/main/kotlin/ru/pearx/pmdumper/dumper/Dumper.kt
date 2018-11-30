package ru.pearx.pmdumper.dumper

import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistryEntry

class DumperAmounts : MutableMap<String, Int> by hashMapOf() {
    operator fun plusAssign(value: String) {
        this[value] = (this[value] ?: 0) + 1
    }

    operator fun plusAssign(value: ResourceLocation?) = plusAssign(value?.namespace ?: "null")

    fun sort(): List<Pair<String, Int>> = toList().sortedByDescending { (k, v) -> v }
}

interface IDumper : IForgeRegistryEntry<IDumper> {
    val header: List<String>
    val columnToSortBy: Int
    fun dump(amounts: DumperAmounts): Iterable<List<String>>

    override fun getRegistryType(): Class<IDumper> = IDumper::class.java
}

class Dumper : IDumper {
    lateinit var iteratorBuilder: (amounts: DumperAmounts) -> Iterator<List<String>>

    override lateinit var header: List<String>

    override var columnToSortBy = 0

    private var registryName: ResourceLocation? = null

    override fun getRegistryName(): ResourceLocation? = registryName

    override fun setRegistryName(name: ResourceLocation?): IDumper {
        registryName = name
        return this
    }

    override fun dump(amounts: DumperAmounts): Iterable<List<String>> = Iterable { iteratorBuilder(amounts) }
}

inline fun dumper(init: Dumper.() -> Unit): IDumper {
    val dumper = Dumper()
    dumper.init()
    return dumper
}