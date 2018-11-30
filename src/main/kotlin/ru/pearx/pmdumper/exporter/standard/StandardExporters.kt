package ru.pearx.pmdumper.exporter.standard

import net.minecraft.util.ResourceLocation
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.exporter.fileExporter
import java.io.File

val ExporterCsv = fileExporter {
    registryName = ResourceLocation(ID, "csv")
    exporter = { dumperIterator, amounts, directory, baseFilename ->
        File(directory, "$baseFilename.csv").printWriter().use { writer ->
            with(writer) {
                for (list in dumperIterator) {
                    var start = true
                    for (element in list) {
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
            }
        }
    }
}