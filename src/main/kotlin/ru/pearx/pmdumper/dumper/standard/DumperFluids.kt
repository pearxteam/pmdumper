@file:JvmMultifileClass
@file:JvmName("StandardDumpers")

package ru.pearx.pmdumper.dumper.standard

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import ru.pearx.pmdumper.ID
import ru.pearx.pmdumper.dumper.dumper
import ru.pearx.pmdumper.utils.toHexColorString
import ru.pearx.pmdumper.utils.toPlusMinusString
import ru.pearx.pmdumper.utils.toTexturesPath
import ru.pearx.pmdumper.utils.tryDump

val DumperFluids = dumper {
    registryName = ResourceLocation(ID, "fluids")
    header = listOf("ID", "Unlocalized Name", "Display Name", "Still Texture", "Flowing Texture", "Overlay Texture", "Fill Sound", "Empty Sound", "Luminosity", "Density", "Temperature", "Viscosity", "Is Gaseous", "Rarity", "Block", "Color", "Is Lighter than Air", "Can be Placed in World")
    amounts {
        for (fluid in FluidRegistry.getRegisteredFluids().values)
            this += FluidRegistry.getModId(FluidStack(fluid, 1)) ?: ""
    }
    iterator {
        for (fluid in FluidRegistry.getRegisteredFluids().values) {
            tryDump(ArrayList(header.size)) {
                with(fluid) {
                    val stack = FluidStack(fluid, 1)
                    val modId = FluidRegistry.getModId(stack) ?: ""
                    add(ResourceLocation(modId, name).toString())
                    add(getUnlocalizedName(stack))
                    add(getLocalizedName(stack))
                    add(getStill(stack).toTexturesPath())
                    add(getFlowing(stack).toTexturesPath())
                    add(overlay?.toTexturesPath() ?: "")
                    add(getFillSound(stack).registryName.toString())
                    add(getEmptySound(stack).registryName.toString())
                    add(getLuminosity(stack).toString())
                    add(getDensity(stack).toString())
                    add(getTemperature(stack).toString())
                    add(getViscosity(stack).toString())
                    add(isGaseous(stack).toPlusMinusString())
                    add(getRarity(stack).toString())
                    add(block?.registryName?.toString() ?: "")
                    add(getColor(stack).toHexColorString())
                    add(isLighterThanAir.toPlusMinusString())
                    add(canBePlacedInWorld().toPlusMinusString())
                }
            }
        }
    }
}
