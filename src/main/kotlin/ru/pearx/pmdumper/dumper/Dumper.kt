package ru.pearx.pmdumper.dumper

import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistryEntry

class DumperAmounts : MutableMap<String, Int> by hashMapOf() {
    operator fun plusAssign(value: String) {
        this[value] = (this[value] ?: 0) + 1
    }

    operator fun plusAssign(value: ResourceLocation?) = plusAssign(value?.namespace ?: "null")
}

interface IDumper : IForgeRegistryEntry<IDumper> {
    val header: List<String>
    fun createIterator(amounts: DumperAmounts): Iterator<List<String>>

    override fun getRegistryType(): Class<IDumper> = IDumper::class.java
}

class Dumper : IDumper {
    lateinit var iteratorBuilder: (amounts: DumperAmounts) -> Iterator<List<String>>

    override lateinit var header: List<String>

    private var registryName: ResourceLocation? = null

    override fun getRegistryName(): ResourceLocation? = registryName

    override fun setRegistryName(name: ResourceLocation?): IDumper {
        registryName = name
        return this
    }

    override fun createIterator(amounts: DumperAmounts) = iteratorBuilder(amounts)
}

inline fun dumper(init: Dumper.() -> Unit): IDumper {
    val dumper = Dumper()
    dumper.init()
    return dumper
}