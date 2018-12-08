package ru.pearx.pmdumper.dumper

import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistryEntry
import ru.pearx.pmdumper.isClient

class DumperAmounts : MutableMap<String, Int> by hashMapOf() {
    operator fun plusAssign(value: String) {
        this[value] = (this[value] ?: 0) + 1
    }

    operator fun plusAssign(value: ResourceLocation?) = plusAssign(value?.namespace ?: "null")

    fun sort(): List<Pair<String, Int>> = toList().sortedByDescending { (k, v) -> v }
}

typealias DumperIteratorCreator = suspend SequenceScope<List<String>>.(amounts: DumperAmounts) -> Unit

interface IDumper : IForgeRegistryEntry<IDumper> {
    val header: List<String>
    val columnToSortBy: Int
    fun dump(amounts: DumperAmounts): Iterable<List<String>>

    override fun getRegistryType(): Class<IDumper> = IDumper::class.java
}

class Dumper : IDumper {
    private var registryName: ResourceLocation? = null
    private lateinit var iterator: DumperIteratorCreator

    override lateinit var header: List<String>

    override var columnToSortBy = 0

    override fun getRegistryName(): ResourceLocation? = registryName

    override fun setRegistryName(name: ResourceLocation?): IDumper {
        registryName = name
        return this
    }

    override fun dump(amounts: DumperAmounts): Iterable<List<String>> = Iterable { iterator<List<String>> { iterator(amounts) } }

    fun iterator(block: DumperIteratorCreator) {
        iterator = block
    }
}

inline fun dumper(init: Dumper.() -> Unit): IDumper = Dumper().apply { init() }

inline fun clientDumper(init: Dumper.() -> Unit): IDumper? {
    return if(isClient)
        Dumper().apply { init() }
    else
        null
}