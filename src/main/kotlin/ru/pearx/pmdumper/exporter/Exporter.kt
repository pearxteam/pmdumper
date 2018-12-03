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

data class ExporterOutput(val translationKey: String, val path: File)

interface IExporter : IForgeRegistryEntry<IExporter> {
    override fun getRegistryType(): Class<IExporter> = IExporter::class.java

    fun export(dumper: IDumper): List<ExporterOutput>
}


interface IFileExporter : IExporter {
    override fun export(dumper: IDumper): List<ExporterOutput> {
        val amounts = DumperAmounts()
        return exportToFile(dumper.header, dumper.dump(amounts).sortedBy { it[dumper.columnToSortBy] }, amounts.sort(), File(Minecraft.getMinecraft().gameDir, "pmdumper"), "${getRegistryElementName(DumperRegistry, dumper.registryName!!)}_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))}")
    }

    fun exportToFile(header: List<String>, table: List<List<String>>, amounts: List<Pair<String, Int>>, directory: File, baseFilename: String): List<ExporterOutput>
}

typealias FileExporterExporter = (header: List<String>, table: List<List<String>>, amounts: List<Pair<String, Int>>, directory: File, baseFilename: String) -> List<ExporterOutput>

class FileExporter : IFileExporter {
    private lateinit var exporter: FileExporterExporter
    private var registryName: ResourceLocation? = null

    override fun getRegistryName(): ResourceLocation? = registryName

    override fun setRegistryName(name: ResourceLocation?): IFileExporter {
        registryName = name
        return this
    }

    override fun exportToFile(header: List<String>, table: List<List<String>>, amounts: List<Pair<String, Int>>, directory: File, baseFilename: String): List<ExporterOutput> {
        directory.mkdirs()
        return exporter(header, table, amounts, directory, baseFilename)
    }

    fun exporter(block: FileExporterExporter) {
        exporter = block
    }
}

inline fun fileExporter(init: FileExporter.() -> Unit): IFileExporter {
    val exporter = FileExporter()
    exporter.init()
    return exporter
}