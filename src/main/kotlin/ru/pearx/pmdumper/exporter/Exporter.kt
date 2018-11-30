package ru.pearx.pmdumper.exporter

import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistryEntry
import ru.pearx.pmdumper.dumper.DumperAmounts
import ru.pearx.pmdumper.dumper.DumperRegistry
import ru.pearx.pmdumper.dumper.IDumper
import ru.pearx.pmdumper.getRegistryElementName
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface IExporter : IForgeRegistryEntry<IExporter> {
    override fun getRegistryType(): Class<IExporter> = IExporter::class.java

    fun export(dumper: IDumper)
}


interface IFileExporter : IExporter {
    override fun export(dumper: IDumper) {
        val amounts = DumperAmounts()
        exportToFile(dumper.header, dumper.createIterator(amounts), amounts, File(Minecraft.getMinecraft().gameDir, "pmdumper"), "${getRegistryElementName(DumperRegistry, dumper.registryName!!)}_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))}")
    }

    fun exportToFile(header: List<String>, dumperIterator: Iterator<List<String>>, amounts: DumperAmounts, directory: File, baseFilename: String)
}

class FileExporter : IFileExporter {
    lateinit var exporter: (header: List<String>, dumperIterator: Iterator<List<String>>, amounts: DumperAmounts, directory: File, baseFilename: String) -> Unit

    private var registryName: ResourceLocation? = null

    override fun getRegistryName(): ResourceLocation? = registryName

    override fun setRegistryName(name: ResourceLocation?): IFileExporter {
        registryName = name
        return this
    }

    override fun exportToFile(header: List<String>, dumperIterator: Iterator<List<String>>, amounts: DumperAmounts, directory: File, baseFilename: String) {
        directory.mkdirs()
        exporter(header, dumperIterator, amounts, directory, baseFilename)
    }
}

inline fun fileExporter(init: FileExporter.() -> Unit): IFileExporter {
    val exporter = FileExporter()
    exporter.init()
    return exporter
}