package ru.pearx.pmdumper.dumper

import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistryEntry
import ru.pearx.pmdumper.utils.isClient

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
    fun dump(): Iterable<List<String>>
    fun getAmounts(): DumperAmounts?

    override fun getRegistryType(): Class<IDumper> = IDumper::class.java
}

typealias DumperIteratorCreator = suspend SequenceScope<List<String>>.() -> Unit
typealias DumperAmountsCreator = DumperAmounts.() -> Unit

class Dumper : IDumper {
    private var registryName: ResourceLocation? = null
    private lateinit var iteratorBuilder: DumperIteratorCreator
    private var amountsBuilder: DumperAmountsCreator? = null

    override lateinit var header: List<String>

    override var columnToSortBy = 0

    override fun getRegistryName(): ResourceLocation? = registryName

    override fun setRegistryName(name: ResourceLocation?): IDumper {
        registryName = name
        return this
    }

    override fun dump(): Iterable<List<String>> = Iterable { iterator<List<String>>(iteratorBuilder) }

    override fun getAmounts(): DumperAmounts? = if (amountsBuilder == null) null else DumperAmounts().apply { amountsBuilder!!() }

    fun iterator(block: DumperIteratorCreator) {
        iteratorBuilder = block
    }

    fun amounts(block: DumperAmountsCreator) {
        amountsBuilder = block
    }
}

inline fun dumper(init: Dumper.() -> Unit): IDumper = Dumper().apply { init() }

inline fun clientDumper(init: Dumper.() -> Unit): IDumper? {
    return if (isClient)
        Dumper().apply { init() }
    else
        null
}