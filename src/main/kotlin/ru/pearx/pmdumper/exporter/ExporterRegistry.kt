package ru.pearx.pmdumper.exporter

import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.RegistryBuilder
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.exporter.standard.ExporterCsv
import ru.pearx.pmdumper.getRegistryElementNames
import ru.pearx.pmdumper.lookupRegistryElements

lateinit var ExporterRegistry: IForgeRegistry<IExporter> private set

fun lookupExporterRegistry(name: String) = lookupRegistryElements(ExporterRegistry, name)

fun getExporterNames(): List<String> = getRegistryElementNames(ExporterRegistry)

@Mod.EventBusSubscriber(modid = ID)
object Events {
    @JvmStatic
    @SubscribeEvent
    fun onNewRegistry(event: RegistryEvent.NewRegistry) {
        ExporterRegistry = RegistryBuilder<IExporter>().setName(ResourceLocation(ID, "exporters")).setType(IExporter::class.java).disableSaving().create()
    }

    @JvmStatic
    @SubscribeEvent
    fun onRegisterDumpers(event: RegistryEvent.Register<IExporter>) {
        with(event.registry) {
            register(ExporterCsv)
        }
    }
}