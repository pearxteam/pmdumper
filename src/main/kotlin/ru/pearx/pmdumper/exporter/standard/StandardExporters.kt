package ru.pearx.pmdumper.exporter.standard

import net.minecraft.util.ResourceLocation
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.exporter.ExporterOutput
import ru.pearx.pmdumper.exporter.fileExporter
import java.io.File
import java.io.PrintWriter

private fun PrintWriter.appendRow(row: List<String>) {
    var start = true
    for (element in row) {
        if (start)
            start = false
        else
            append(',')

        val shouldBeQuoted = element.any { it == ',' || it == '"' || it == '\r' || it == '\n'}
        if (shouldBeQuoted)
            append('"')

        print(element.replace("\"", "\"\""))

        if (shouldBeQuoted)
            append('"')
    }
    appendln()
}

val ExporterCsv = fileExporter {
    registryName = ResourceLocation(ID, "csv")
    exporter = { header, table, amounts, directory, baseFilename ->
        val tableFile = File(directory, "$baseFilename.csv")
        val amountsFile = File(directory, "${baseFilename}_amounts.csv")
        tableFile.printWriter().use { writer ->
            with(writer) {
                appendRow(header)
                for (row in table) {
                    appendRow(row)
                }
            }
        }
        amountsFile.printWriter().use { writer ->
            with(writer) {
                for(entry in amounts) {
                    appendRow(listOf(entry.first, entry.second.toString()))
                }
            }
        }
        listOf(ExporterOutput("exporterOutput.csv.name", tableFile), ExporterOutput("exporterOutput.amounts.name", amountsFile))
    }
}