package ru.pearx.pmdumper.dumper

import com.google.common.base.Predicate
import com.google.common.collect.ImmutableMap
import com.google.gson.GsonBuilder
import moze_intel.projecte.utils.EMCHelper
import net.minecraft.advancements.Advancement
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.model.WeightedBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EnumCreatureType
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ITickable
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.translation.I18n
import net.minecraft.world.storage.loot.*
import net.minecraft.world.storage.loot.conditions.LootCondition
import net.minecraft.world.storage.loot.conditions.LootConditionManager
import net.minecraft.world.storage.loot.functions.LootFunction
import net.minecraft.world.storage.loot.functions.LootFunctionManager
import net.minecraftforge.client.ItemModelMesherForge
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.fml.common.registry.VillagerRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.oredict.OreDictionary
import ru.pearx.pmdumper.*
import java.util.IdentityHashMap
import kotlin.collections.ArrayList
import kotlin.collections.component1
import kotlin.collections.component2


val DumperBiomes = dumper {
    registryName = ResourceLocation(ID, "biomes")
    header = mutableListOfNotNull("ID", client("Name"), "Default Temperature", "Base Height", "Height Variation", "Class Name", "Is Snowy", "Can Rain", "Rainfall", "Base Biome", "Filler Block", "Top Block", client("Water Color"), "Water Color Multiplier", "Creature Spawning Chance").apply {
        for (type in EnumCreatureType.values())
            add("${type.toString().toLowerCase().capitalize()} Spawn List: Entity*(Min Group-Max Group):Weight")
    }
    iterator { amounts ->
        for (biome in ForgeRegistries.BIOMES) {
            with(ArrayList<String>(header.size)) {
                with(biome) {
                    amounts += registryName
                    add(registryName.toString())
                    client { add(biomeName) }
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
                    client { add(waterColor.toHexColorString()) }
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
    header = listOfNotNull("ID", "Metadata", "NBT Tag Compound", "Display Name", client("Tooltip"), "Translation Key", "Class Name", "Is ItemBlock", "OreDict Names", "Max Stack Size", "Max Damage", client("Model Name"), ifOrNull(Loader.isModLoaded(PROJECTE_ID), "EMC"))
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
                        client { add(mutableListOf<String>().apply { addInformation(stack, Minecraft.getMinecraft().world, this, ITooltipFlag.TooltipFlags.NORMAL) }.joinToString(separator = System.lineSeparator())) }
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
                        client { add((Minecraft.getMinecraft().renderItem.itemModelMesher as ItemModelMesherForge).getLocation(stack).toString()) }
                        if (Loader.isModLoaded(PROJECTE_ID))
                            add(if (EMCHelper.doesItemHaveEmc(stack)) EMCHelper.getEmcValue(stack).toString() else "")
                    }
                    yield(this)
                }
            }
        }
    }
}

val DumperPotions = dumper {
    registryName = ResourceLocation(ID, "potions")
    header = listOfNotNull("ID", "Display Name", "Name", "Class Name", "Is Bad Effect", "Is Instant", client("Is Beneficial"), client("Status Icon Index"), client("Liquid Color"), "Curative Items", client("Attribute Modifiers"))
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
                    client {
                        add(isBeneficial.toPlusMinusString())
                        add(statusIconIndex.toString())
                        add(liquidColor.toHexColorString())
                    }
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
                    client {
                        add(
                            StringBuilder().apply {
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
                            }.toString()
                        )
                    }
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
                    add(skin.toPath())
                    add(zombieSkin.toPath())
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

val DumperModels = clientDumper {
    registryName = ResourceLocation(ID, "models")
    header = listOf("Variant", "Particle Texture", "Model Textures", "Model Path", "Class Name", "Is Ambient Occlusion", "Is GUI 3D", "Is Built In Renderer")
    iterator { amounts ->
        val registry = Minecraft.getMinecraft().modelManager.modelRegistry
        val classes = mutableListOf<Class<*>>()
        for (key in registry.keys) {
            val model = registry.getObject(key)!!
            amounts += key
            with(ArrayList<String>(header.size)) {
                add(key.toString())
                with(model) {
                    add(particleTexture?.let { ResourceLocation(it.iconName) }?.toTexturesPath() ?: "")
                    val textures = mutableListOf<TextureAtlasSprite>()
                    for (quad in getQuads(null, null, 0)) {
                        if (quad.sprite !in textures)
                            textures.add(quad.sprite)
                    }
                    add(textures.joinToString(separator = System.lineSeparator()) { it -> ResourceLocation(it.iconName).toTexturesPath() })
                    add(getModelPath(key))
                    add(this::class.java.name)
                    add(isAmbientOcclusion.toPlusMinusString())
                    add(isGui3d.toPlusMinusString())
                    add(isBuiltInRenderer.toPlusMinusString())
                }
                yield(this)
            }
        }
        println(classes)
    }
}

// hack \/
@SideOnly(Side.CLIENT)
private fun IBakedModel.getModelPath(location: ModelResourceLocation): String {
    return when (this::class.java.name) {
        "net.minecraftforge.client.model.ModelLoader\$VanillaModelWrapper\$1" -> this.readField<Any>("this$1").readField<ResourceLocation>("location").toPath("", ".json")
        "net.minecraft.client.renderer.block.model.MultipartBakedModel" -> {
            val lst = mutableListOf<String>()
            for(subModel in readField<Map<Predicate<IBlockState>, IBakedModel>>("selectors").values) {
                val subPath = subModel.getModelPath(location)
                if(subPath !in lst)
                    lst.add(subPath)
            }
            lst.joinToString(separator = System.lineSeparator())
        }
        "net.minecraftforge.client.model.MultiModel\$Baked" -> {
            val lst = mutableListOf<String>()
            lst.add(readField<ResourceLocation>("location").toPath("", ".json"))
            for(subModel in readField<ImmutableMap<String, IBakedModel>>("parts").values) {
                val subPath = subModel.getModelPath(location)
                if(subPath !in lst)
                    lst.add(subPath)
            }
            lst.joinToString(separator = System.lineSeparator())
        }
        "net.minecraft.client.renderer.block.model.WeightedBakedModel" -> {
            val lst = mutableListOf<String>()
            for(model in readField<List<Any>>("models")) {
                val path = model.readField<IBakedModel>("model").getModelPath(location)
                if(path !in lst)
                    lst.add(path)
            }
            lst.joinToString(separator = System.lineSeparator())
        }
        "net.minecraftforge.client.model.BakedItemModel" -> location.toPath("models/item", ".json")
        else -> ""
    }
}

val DumperCapabilities = dumper {
    registryName = ResourceLocation(ID, "capabilities")
    header = listOf("Interface", "Default Instance Class", "Storage Class")
    iterator {
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
    header = listOfNotNull("ID", "Display Text", "Title", "Description", client("Icon"), client("X"), client("Y"), client("Background"), "Frame", "Is Hidden", "Should Announce", client("Should Show Toast"), "Parent", "Children", "Reward Experience", "Reward Loot", "Reward Recipes", "Reward Function")
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
                        client {
                            add(icon.toFullString())
                            add(x.toString())
                            add(y.toString())
                            add(background?.toPath() ?: "")
                        }
                        add(frame.toString())
                        add(isHidden.toPlusMinusString())
                        add(shouldAnnounceToChat().toPlusMinusString())
                        client { add(shouldShowToast().toPlusMinusString()) }
                    } ?: repeat(if (isClient) 10 else 5) { add("") }
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

val DumperSmeltingRecipes = dumper {
    registryName = ResourceLocation(ID, "smelting_recipes")
    header = listOf("Input", "Output", "XP")
    columnToSortBy = 1
    iterator {
        val recipes = FurnaceRecipes.instance()
        for ((input, output) in recipes.smeltingList.entries) {
            with(ArrayList<String>(header.size)) {
                add(input.toFullString(true))
                add(output.toFullString())
                add(recipes.getSmeltingExperience(output).toString())
                yield(this)
            }
        }
    }
}

val DumperFluids = dumper {
    registryName = ResourceLocation(ID, "fluids")
    header = listOf("ID", "Unlocalized Name", "Display Name", "Still Texture", "Flowing Texture", "Overlay Texture", "Fill Sound", "Empty Sound", "Luminosity", "Density", "Temperature", "Viscosity", "Is Gaseous", "Rarity", "Block", "Color", "Is Lighter than Air", "Can be Placed in World")
    iterator { amounts ->
        for (fluid in FluidRegistry.getRegisteredFluids().values) {
            with(ArrayList<String>(header.size)) {
                val stack = FluidStack(fluid, 1)
                val modId = FluidRegistry.getModId(stack) ?: ""
                amounts += modId
                with(fluid) {
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
                yield(this)
            }
        }
    }
}

val DumperTileEntities = dumper {
    registryName = ResourceLocation(ID, "tile_entities")
    header = listOf("ID", "Class Name", "Is Tickable")
    iterator { amounts ->
        for (id in TileEntity.REGISTRY.keys) {
            amounts += id
            with(ArrayList<String>(header.size)) {
                add(id.toString())
                val tileClass = TileEntity.REGISTRY.getObject(id)!!
                add(tileClass.name)
                add(ITickable::class.java.isAssignableFrom(tileClass).toPlusMinusString())
                yield(this)
            }
        }
    }
}

val DumperFood = dumper {
    registryName = ResourceLocation(ID, "food")
    header = listOf("Item", "Heal Amount", "Saturation Modifier", "Is Wolfs Favorite Meal", "Is Always Edible", "Item Use Duration", "Potion Effect", "Potion Effect Probability")
    iterator { amounts ->
        for (item in ForgeRegistries.ITEMS) {
            if (item is ItemFood) {
                val stacks = NonNullList.create<ItemStack>().apply {
                    item.getSubItems(item.creativeTab ?: CreativeTabs.SEARCH, this)
                }
                for (stack in stacks) {
                    with(ArrayList<String>(header.size)) {
                        with(item) {
                            amounts += registryName
                            add(stack.toFullString())
                            add(getHealAmount(stack).toString())
                            add(getSaturationModifier(stack).toString())
                            add(isWolfsFavoriteMeat.toPlusMinusString())
                            add(alwaysEdible.toPlusMinusString())
                            add(itemUseDuration.toString())
                            if (potionId != null) {
                                add(potionId.toString())
                                add(potionEffectProbability.toString())
                            }
                            else repeat(2) { add("") }
                        }
                        yield(this)
                    }
                }
            }
        }
    }
}