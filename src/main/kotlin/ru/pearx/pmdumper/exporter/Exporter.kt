package ru.pearx.pmdumper.exporter

import net.minecraftforge.registries.IForgeRegistryEntry
import ru.pearx.pmdumper.dumper.DumperAmounts

interface IExporter : IForgeRegistryEntry<IExporter> {

    override fun getRegistryType(): Class<IExporter> = IExporter::class.java
}