package ru.pearx.pmdumper

import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.event.ClickEvent
import ru.pearx.pmdumper.dumper.getDumperNames
import ru.pearx.pmdumper.dumper.lookupDumperRegistry
import ru.pearx.pmdumper.exporter.getExporterNames
import ru.pearx.pmdumper.exporter.lookupExporterRegistry

class PMDumperCommand : CommandBase() {
    override fun getName() = "pmdumper"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        if (args.size < 2) {
            throw createWrongUsageException(sender)
        }

        val dumper = args[0]
        val exporter = args[1]

        val dumpers = lookupDumperRegistry(dumper)
        if (dumpers.size != 1)
            throw createWrongUsageException(sender)

        val exporters = lookupExporterRegistry(exporter)
        if (exporters.size != 1)
            throw createWrongUsageException(sender)

        val outputs = exporters[0].export(dumpers[0])
        sender.sendMessage(TextComponentTranslation("commands.pmdumper.success").apply {
            var start = true
            for (output in outputs) {
                if (start) {
                    start = false
                    appendText(" ")
                }
                else
                    appendText(", ")
                val style = Style().setClickEvent(ClickEvent(ClickEvent.Action.OPEN_FILE, output.path.toString())).setUnderlined(true).setColor(TextFormatting.BLUE)
                appendSibling(TextComponentTranslation(output.translationKey).setStyle(style))
            }
            appendText(".")
        })
    }

    override fun getUsage(sender: ICommandSender) = "commands.pmdumper.usage"

    override fun getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array<String>, targetPos: BlockPos?): List<String> {
        return when (args.size) {
            1 -> getDumperNames()
            2 -> getExporterNames()
            else -> listOf()
        }
    }

    private fun createWrongUsageException(sender: ICommandSender) = WrongUsageException(getUsage(sender), getExporterNames().joinToString(separator = "|"), getDumperNames().joinToString(", "))
}