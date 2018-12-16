package ru.pearx.pmdumper.exporter

import net.minecraft.util.ResourceLocation
import ru.pearx.pmdumper.ID
import java.io.File

private fun Appendable.appendRow(row: List<String>) {
    var start = true
    for (element in row) {
        if (start)
            start = false
        else
            append(',')

        val shouldBeQuoted = element.any { it == ',' || it == '"' || it == '\r' || it == '\n'}
        if (shouldBeQuoted)
            append('"')

        append(element.replace("\"", "\"\""))

        if (shouldBeQuoted)
            append('"')
    }
}

val ExporterCsv = fileExporter {
    registryName = ResourceLocation(ID, "csv")
    exporter { header, table, amounts, directory, baseFilename ->
        val outputs = mutableListOf<ExporterOutput>()
        val tableFile = File(directory, "$baseFilename.csv")
        val amountsFile = File(directory, "${baseFilename}_amounts.csv")
        tableFile.printWriter().use { writer ->
            with(writer) {
                appendRow(header)
                for (row in table) {
                    appendln()
                    appendRow(row)
                }
            }
            outputs.add(ExporterOutput("exporterOutput.csv.name", tableFile))
        }
        if(amounts != null) {
            if (!amounts.isEmpty()) {
                amountsFile.printWriter().use { writer ->
                    with(writer) {
                        var start = true
                        for (entry in amounts) {
                            if (start)
                                start = false
                            else
                                appendln()
                            appendRow(listOf(entry.first, entry.second.toString()))
                        }
                    }
                }
                outputs.add(ExporterOutput("exporterOutput.amounts.name", amountsFile))
            }
        }
        outputs
    }
}