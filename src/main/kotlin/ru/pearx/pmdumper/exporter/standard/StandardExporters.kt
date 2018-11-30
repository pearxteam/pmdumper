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
        if(start)
            start = false
        else
            append(',')

        val shouldBeQuoted = element.any { it == ',' || it == '"' }
        if(shouldBeQuoted)
            append('"')

        print(element.replace("\"", "\"\""))

        if(shouldBeQuoted)
            append('"')
    }
    appendln()
}

val ExporterCsv = fileExporter {
    registryName = ResourceLocation(ID, "csv")
    exporter = { header, dumperIterator, amounts, directory, baseFilename ->
        val table = File(directory, "$baseFilename.csv")
        table.printWriter().use { writer ->
            with(writer) {
                appendRow(header)
                for (row in dumperIterator) {
                    appendRow(row)
                }
            }
        }
        listOf(ExporterOutput("exporterOutput.csv.name", table))
    }
}