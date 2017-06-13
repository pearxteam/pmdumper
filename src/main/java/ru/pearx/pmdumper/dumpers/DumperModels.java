package ru.pearx.pmdumper.dumpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.pmdumper.dumpers.base.IDumper;
import ru.pearx.pmdumper.dumpers.base.PMDData;

import java.util.*;

/**
 * Created by mrAppleXZ on 12.06.17 18:01.
 */
@SideOnly(Side.CLIENT)
public class DumperModels implements IDumper
{
    @Override
    public String getName()
    {
        return "models";
    }

    @Override
    public PMDData getData()
    {
        Map<String, Integer> counts = new HashMap<>();
        List<List<String>> lst = new ArrayList<>();
        lst.add(Arrays.asList("Variant", "Class"));
        for(ModelResourceLocation loc : Minecraft.getMinecraft().modelManager.modelRegistry.getKeys())
        {
            IBakedModel mdl = Minecraft.getMinecraft().modelManager.getModel(loc);
            lst.add(Arrays.asList(loc.toString(), mdl.getClass().toString()));
            PMDData.plusCounts(counts, loc.getResourceDomain(), 1);
        }
        return new PMDData(lst, counts);
    }
}
