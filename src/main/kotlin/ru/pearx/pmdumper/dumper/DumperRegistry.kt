package ru.pearx.pmdumper.dumper

import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.RegistryBuilder
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.standard.DumperEnchantments
import ru.pearx.pmdumper.getRegistryElementNames
import ru.pearx.pmdumper.lookupRegistryElements

lateinit var DumperRegistry: IForgeRegistry<IDumper> private set

fun lookupDumperRegistry(name: String) = lookupRegistryElements(DumperRegistry, name)

fun getDumperNames(): List<String> = getRegistryElementNames(DumperRegistry)

@Mod.EventBusSubscriber(modid = ID)
object Events {
    @JvmStatic
    @SubscribeEvent
    fun onNewRegistry(event: RegistryEvent.NewRegistry) {
        DumperRegistry = RegistryBuilder<IDumper>().setName(ResourceLocation(ID, "dumpers")).setType(IDumper::class.java).disableSaving().create()
    }

    @JvmStatic
    @SubscribeEvent
    fun onRegisterDumpers(event: RegistryEvent.Register<IDumper>) {
        with(event.registry) {
            register(DumperEnchantments)
        }
    }
}