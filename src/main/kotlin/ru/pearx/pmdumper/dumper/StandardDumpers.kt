package ru.pearx.pmdumper.dumper

import com.google.gson.GsonBuilder
import moze_intel.projecte.utils.EMCHelper
import net.minecraft.advancements.Advancement
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EnumCreatureType
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.translation.I18n
import net.minecraft.world.storage.loot.*
import net.minecraft.world.storage.loot.conditions.LootCondition
import net.minecraft.world.storage.loot.conditions.LootConditionManager
import net.minecraft.world.storage.loot.functions.LootFunction
import net.minecraft.world.storage.loot.functions.LootFunctionManager
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.fml.common.registry.VillagerRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.oredict.OreDictionary
import ru.pearx.pmdumper.*
import java.util.IdentityHashMap
import kotlin.collections.ArrayList
import kotlin.collections.component1
import kotlin.collections.component2


val DumperBiomes = dumper {
    registryName = ResourceLocation(ID, "biomes")
    header = mutableListOf("ID", "Name", "Default Temperature", "Base Height", "Height Variation", "Class Name", "Is Snowy", "Can Rain", "Rainfall", "Base Biome", "Filler Block", "Top Block", "Water Color", "Water Color Multiplier", "Creature Spawning Chance").apply {
        for (type in EnumCreatureType.values()) {
            add("${type.toString().toLowerCase().capitalize()} Spawn List: Entity*(Min Group-Max Group):Weight")
        }
        iterator { amounts ->
            for (biome in ForgeRegistries.BIOMES) {
                with(ArrayList<String>(header.size)) {
                    with(biome) {
                        amounts += registryName
                        add(registryName.toString())
                        add(biomeName)
                        add(defaultTemperature.toString())
                        add(baseHeight.toString())
                        add(heightVariation.toString())
                        add(this::class.java.name)
                        add(isSnowyBiome.toPlusMinusString())
                        add(canRain().toPlusMinusString())
                        add(rainfall.toString())
                        add(baseBiomeRegName ?: "")
                        add(fillerBlock.toString())
                        add(topBlock.toString())
                        add(waterColor.toHexColorString())
                        add(waterColorMultiplier.toString())
                        add(spawningChance.toString())
                        for (type in EnumCreatureType.values()) {
                            add(getSpawnableList(type).joinToString(separator = System.lineSeparator()))
                        }
                    }
                    yield(this)
                }
            }
        }
    }
}

val DumperEnchantments = dumper {
    registryName = ResourceLocation(ID, "enchantments")
    header = listOf("ID", "Name", "Class Name", "Levels", "Rarity", "Is Curse", "Type", "Allowed on Books", "Is Treasure", "Localized Name [Min Enchantability - Max Enchantability]")
    iterator { amounts ->
        for (ench in ForgeRegistries.ENCHANTMENTS) {
            with(ArrayList<String>(header.size)) {
                with(ench) {
                    amounts += registryName
                    add(registryName.toString())
                    add(name)
                    add(this::class.java.name)
                    add("$minLevel - $maxLevel")
                    add(rarity.toString())
                    add(isCurse.toPlusMinusString())
                    add(type.toString())
                    add(isAllowedOnBooks.toPlusMinusString())
                    add(isTreasureEnchantment.toPlusMinusString())
                    add(StringBuilder().apply {
                        var start = true
                        for (lvl in minLevel..maxLevel) {
                            if (start)
                                start = false
                            else
                                appendln()
                            append("${getTranslatedName(lvl)} [${getMinEnchantability(lvl)} - ${getMaxEnchantability(lvl)}]")
                        }
                    }.toString())
                }
                yield(this)
            }
        }
    }
}

val DumperItemStacks = dumper {
    registryName = ResourceLocation(ID, "itemstacks")
    header = listOf("ID", "Metadata", "NBT Tag Compound", "Display Name", "Tooltip", "Translation Key", "Class Name", "Is ItemBlock", "OreDict Names", "Max Stack Size", "Max Damage", "EMC")
    iterator { amounts ->
        for (item in ForgeRegistries.ITEMS) {
            val stacks = NonNullList.create<ItemStack>().apply {
                item.getSubItems(item.creativeTab ?: CreativeTabs.SEARCH, this)
            }
            for (stack in stacks) {
                with(ArrayList<String>(header.size)) {
                    with(item) {
                        amounts += registryName
                        add(registryName.toString())
                        add(stack.metadata.toString())
                        add(stack.tagCompound?.toString() ?: "")
                        add(getItemStackDisplayName(stack))
                        if (FMLCommonHandler.instance().side == Side.CLIENT)
                            add(mutableListOf<String>().apply { addInformation(stack, Minecraft.getMinecraft().world, this, ITooltipFlag.TooltipFlags.NORMAL) }.joinToString(separator = System.lineSeparator()))
                        else
                            add("N/A")
                        add(getTranslationKey(stack))
                        add(this::class.java.name)
                        add((this is ItemBlock).toPlusMinusString())
                        add(StringBuilder().apply {
                            var start = true
                            for (id in OreDictionary.getOreIDs(stack)) {
                                if (start)
                                    start = false
                                else
                                    appendln()
                                append(OreDictionary.getOreName(id))
                            }

                        }.toString())
                        add(getItemStackLimit(stack).toString())
                        add(getMaxDamage(stack).toString())
                        if (FMLCommonHandler.instance().side == Side.CLIENT)
                            add(Minecraft.getMinecraft().renderItem.itemModelMesher.getItemModel(stack).let {
                                val reg = Minecraft.getMinecraft().modelManager.modelRegistry
                                for (k in reg.keys) {
                                    if (reg.getObject(k) == it)
                                        return@let k.toString()
                                }
                                return@let ""
                            })
                        else
                            add("N/A")
                        if (Loader.isModLoaded(PROJECTE_ID))
                            add(if (EMCHelper.doesItemHaveEmc(stack)) EMCHelper.getEmcValue(stack).toString() else "")
                        else
                            add("N/A")
                    }
                    yield(this)
                }
            }
        }
    }
}

val DumperPotions = dumper {
    registryName = ResourceLocation(ID, "potions")
    header = listOf("ID", "Display Name", "Name", "Class Name", "Is Bad Effect", "Is Instant", "Is Beneficial", "Status Icon Index", "Liquid Color", "Curative Items", "Attribute Modifiers")
    iterator { amounts ->
        for (potion in ForgeRegistries.POTIONS) {
            with(ArrayList<String>(header.size)) {
                with(potion) {
                    amounts += registryName
                    add(registryName.toString())
                    add(I18n.translateToLocalFormatted(name))
                    add(name)
                    add(this::class.java.name)
                    add(isBadEffect.toPlusMinusString())
                    add(isInstant.toPlusMinusString())
                    add(isBeneficial.toPlusMinusString())
                    add(statusIconIndex.toString())
                    add(liquidColor.toHexColorString())
                    add(StringBuilder().apply {
                        var start = true
                        for (item in curativeItems) {
                            if (start)
                                start = false
                            else
                                appendln()
                            append(item.toFullString())
                        }
                    }.toString())
                    add(StringBuilder().apply {
                        var start = true
                        for ((attribute, modifier) in attributeModifierMap) {
                            if (start)
                                start = true
                            else
                                appendln()
                            append(attribute.name)
                            append(": ")
                            append(modifier)
                        }
                    }.toString())
                }
                yield(this)
            }
        }
    }
}

val DumperSounds = dumper {
    registryName = ResourceLocation(ID, "sounds")
    header = listOf("ID")
    iterator { amounts ->
        for (sound in ForgeRegistries.SOUND_EVENTS) {
            with(ArrayList<String>(header.size)) {
                with(sound) {
                    amounts += registryName
                    add(registryName.toString())
                }
                yield(this)
            }
        }
    }
}

val DumperVillagerProfessions = dumper {
    registryName = ResourceLocation(ID, "villager_professions")
    header = listOf("ID", "Skin", "Zombie Skin", "Career Names")
    iterator { amounts ->
        for (profession in ForgeRegistries.VILLAGER_PROFESSIONS) {
            with(ArrayList<String>(header.size)) {
                with(profession) {
                    amounts += registryName
                    add(registryName.toString())
                    add(skin.toString())
                    add(zombieSkin.toString())
                    add(StringBuilder().apply {
                        var start = true
                        for (career in profession.readField<List<VillagerRegistry.VillagerCareer>>("careers")) {
                            if (start)
                                start = false
                            else
                                appendln()
                            append(career.name)
                        }
                    }.toString())
                }
                yield(this)
            }
        }
    }
}

val DumperEntities = dumper {
    registryName = ResourceLocation(ID, "entities")
    header = listOf("ID", "Name", "Class Name", "Primary Egg Color", "Secondary Egg Color")
    iterator { amounts ->
        for (entity in ForgeRegistries.ENTITIES) {
            with(ArrayList<String>(header.size)) {
                with(entity) {
                    amounts += registryName
                    add(registryName.toString())
                    add(name)
                    add(entityClass.name)
                    add(egg?.primaryColor?.toHexColorString() ?: "")
                    add(egg?.secondaryColor?.toHexColorString() ?: "")
                }
                yield(this)
            }
        }
    }
}

val DumperModels = dumper {
    registryName = ResourceLocation(ID, "models")
    header = listOf("Variant", "Class Name", "Is Ambient Occlusion", "Is GUI 3D", "Is Built In Renderer", "Particle Texture", "Model Textures")
    iterator { amounts ->
        val registry = Minecraft.getMinecraft().modelManager.modelRegistry
        for (key in registry.keys) {
            val model = registry.getObject(key)!!
            amounts += key
            with(ArrayList<String>(header.size)) {
                add(key.toString())
                with(model) {
                    add(this::class.java.name)
                    add(isAmbientOcclusion.toPlusMinusString())
                    add(isGui3d.toPlusMinusString())
                    add(isBuiltInRenderer.toPlusMinusString())
                    add(particleTexture?.iconName ?: "")
                    val textures = mutableListOf<TextureAtlasSprite>()
                    for(quad in getQuads(null, null, 0)) {
                        if(quad.sprite !in textures)
                            textures.add(quad.sprite)
                    }
                    add(textures.joinToString(separator = System.lineSeparator()) { it -> it.iconName })
                }
                yield(this)
            }
        }
    }
}

val DumperCapabilities = dumper {
    registryName = ResourceLocation(ID, "capabilities")
    header = listOf("Interface", "Default Instance Class", "Storage Class")
    iterator { amounts ->
        for ((key, value) in CapabilityManager.INSTANCE.readField<IdentityHashMap<String, Capability<*>>>("providers")) {
            with(ArrayList<String>(header.size)) {
                add(key)
                val defaultInstance = value.defaultInstance
                add(if (defaultInstance == null) "" else defaultInstance::class.java.name)
                add(value.storage::class.java.name)
                yield(this)
            }
        }
    }
}

val DumperBlocks = dumper {
    registryName = ResourceLocation(ID, "blocks")
    header = listOf("ID", "Class Name", "BlockState Properties", "BlockState Class Name")
    iterator { amounts ->
        for (block in ForgeRegistries.BLOCKS) {
            with(ArrayList<String>(header.size)) {
                with(block) {
                    amounts += registryName
                    add(registryName.toString())
                    add(this::class.java.name)
                    add(blockState.properties.toString())
                    add(blockState::class.java.name)
                }
                yield(this)
            }
        }
    }
}

val DumperAdvancements = dumper {
    registryName = ResourceLocation(ID, "advancements")
    header = listOf("ID", "Display Text", "Title", "Description", "Icon", "X", "Y", "Background", "Frame", "Is Hidden", "Should Announce", "Should Show Toast", "Parent", "Children", "Reward Experience", "Reward Loot", "Reward Recipes", "Reward Function")
    iterator { amounts ->
        val advancements = mutableListOf<Advancement>()
        for (w in DimensionManager.getWorlds())
            for (adv in w.advancementManager.advancements)
                if (adv !in advancements)
                    advancements.add(adv)
        for (adv in advancements) {
            with(ArrayList<String>(header.size)) {
                with(adv) {
                    amounts += id
                    add(id.toString())
                    add(displayText.formattedText)
                    display?.run {
                        add(title.formattedText)
                        add(description.formattedText)
                        add(icon.toFullString())
                        add(x.toString())
                        add(y.toString())
                        add(background?.toString() ?: "")
                        add(frame.toString())
                        add(isHidden.toPlusMinusString())
                        add(shouldAnnounceToChat().toPlusMinusString())
                        add(shouldShowToast().toPlusMinusString())
                    } ?: repeat(10) { add("") }
                    add(parent?.id?.toString() ?: "")
                    add(StringBuilder().apply {
                        var start = true
                        for (child in children) {
                            if (start)
                                start = false
                            else
                                appendln()
                            append(child.id.toString())
                        }
                    }.toString())
                    rewards.apply {
                        add(experience.toString())
                        add(loot.joinToString(separator = System.lineSeparator()))
                        add(recipes.joinToString(separator = System.lineSeparator()))
                        add(function?.toString() ?: "")
                    }
                }
                yield(this)
            }
        }
    }
}

val DumperLootTables = dumper {
    registryName = ResourceLocation(ID, "loot_tables")
    header = listOf("ID", "Loot Data")
    iterator { amounts ->
        val manager = LootTableManager(null)
        val gs = GsonBuilder().registerTypeAdapter(RandomValueRange::class.java, RandomValueRange.Serializer()).registerTypeAdapter(LootPool::class.java, LootPool.Serializer()).registerTypeAdapter(LootTable::class.java, LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry::class.java, LootEntry.Serializer()).registerTypeHierarchyAdapter(LootFunction::class.java, LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(LootCondition::class.java, LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget::class.java, LootContext.EntityTarget.Serializer()).setPrettyPrinting().create()
        for (loc in LootTableList.getAll()) {
            with(ArrayList<String>(header.size)) {
                amounts += loc
                add(loc.toString())
                add(gs.toJson(manager.getLootTableFromLocation(loc)))
                yield(this)
            }
        }
    }
}