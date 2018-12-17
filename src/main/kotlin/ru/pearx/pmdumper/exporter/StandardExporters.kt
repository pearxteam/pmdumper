package ru.pearx.pmdumper.exporter

import net.minecraft.util.ResourceLocation
import ru.pearx.pmdumper.ID
import java.io.File

val ExporterCsvAmounts = fileExporter {
    registryName = ResourceLocation(ID, "csv_amounts")
    exporter { header, tableGetter, amountsGetter, directory, baseFilename ->
        listOf(dumpTable(header, tableGetter(), directory, baseFilename), dumpAmounts(amountsGetter(), directory, baseFilename))
    }
}

val ExporterCsv = fileExporter {
    registryName = ResourceLocation(ID, "csv")
    exporter { header, tableGetter, _, directory, baseFilename -> listOf(dumpTable(header, tableGetter(), directory, baseFilename)) }
}

val ExporterAmounts = fileExporter {
    registryName = ResourceLocation(ID, "amounts")
    exporter { _, _, amountsGetter, directory, baseFilename -> listOf(dumpAmounts(amountsGetter(), directory, baseFilename)) }
}

private fun dumpTable(header: List<String>, table: List<List<String>>, directory: File, baseFilename: String): ExporterOutput {
    val tableFile = File(directory, "$baseFilename.csv")
    tableFile.printWriter().use { writer ->
        with(writer) {
            appendRow(header)
            for (row in table) {
                appendln()
                appendRow(row)
            }
        }
        return ExporterOutput("exporterOutput.csv.name", tableFile)
    }
}

private fun Appendable.appendRow(row: List<String>) {
    var start = true
    for (element in row) {
        if (start)
            start = false
        else
            append(',')

        val shouldBeQuoted = element.any { it == ',' || it == '"' || it == '\r' || it == '\n' }
        if (shouldBeQuoted)
            append('"')

        append(element.replace("\"", "\"\""))

        if (shouldBeQuoted)
            append('"')
    }
}

private fun dumpAmounts(amounts: List<Pair<String, Int>>?, directory: File, baseFilename: String): ExporterOutput {
    val amountsFile = File(directory, "${baseFilename}_amounts.csv")
    if (amounts != null) {
        if (!amounts.isEmpty()) {
            amountsFile.printWriter().use { writer ->
                with(writer) {
                    var start = true
                    for (entry in amounts) {
                        if (start)
                            start = false
                        else
                            appendln()
                        appendRow(kotlin.collections.listOf(entry.first, entry.second.toString()))
                    }
                }
            }
        }
    }
    return ExporterOutput("exporterOutput.amounts.name", amountsFile)
}