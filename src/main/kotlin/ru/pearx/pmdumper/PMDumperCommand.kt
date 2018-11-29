package ru.pearx.pmdumper

import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import ru.pearx.pmdumper.dumper.DumperRegistry
import ru.pearx.pmdumper.dumper.IDumper
import ru.pearx.pmdumper.dumper.getDumperNames
import ru.pearx.pmdumper.dumper.lookupDumperRegistry

class PMDumperCommand : CommandBase() {
    override fun getName() = "pmdumper"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        if(args.size < 2) {
            throw createWrongUsageException(sender)
        }

        val name = args[0]
        val exporter = args[1]

        val dumpers = lookupDumperRegistry(name)
        if(dumpers.size != 1)
            throw createWrongUsageException(sender)
    }

    override fun getUsage(sender: ICommandSender) = "commands.pmdumper.usage"

    override fun getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array<String>, targetPos: BlockPos?): List<String> {
        return when(args.size) {
            1 -> getDumperNames()
            2 -> listOf("") //todo exporters
            else -> listOf()
        }
    }

    private fun createWrongUsageException(sender: ICommandSender) = WrongUsageException(getUsage(sender), "", getDumperNames().joinToString(", "))
}