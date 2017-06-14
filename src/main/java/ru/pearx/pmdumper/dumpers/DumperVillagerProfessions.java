package ru.pearx.pmdumper.dumpers;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.reflect.FieldUtils;
import ru.pearx.pmdumper.dumpers.base.IDumper;
import ru.pearx.pmdumper.dumpers.base.PMDData;

import java.util.*;

/**
 * Created by mrAppleXZ on 13.06.17 23:49.
 */
@SideOnly(Side.CLIENT)
public class DumperVillagerProfessions implements IDumper
{
    @Override
    public String getName()
    {
        return "villager_professions";
    }

    @Override
    public PMDData getData()
    {
        Map<String, Integer> counts = new HashMap<>();
        List<List<String>> lst = new ArrayList<>();
        lst.add(Arrays.asList("ID", "Skin", "Zombie Skin", "Career Names"));
        for(Map.Entry<ResourceLocation, VillagerRegistry.VillagerProfession> entr : ForgeRegistries.VILLAGER_PROFESSIONS.getEntries())
        {
            ResourceLocation loc = entr.getKey();
            VillagerRegistry.VillagerProfession prof = entr.getValue();
            try
            {
                List<String> profLst = new ArrayList<>();
                List<VillagerRegistry.VillagerCareer> careers = (List<VillagerRegistry.VillagerCareer>) FieldUtils.readField(prof, "careers", true);

                profLst.add(loc.toString());
                profLst.add(prof.getSkin().toString());
                profLst.add(prof.getZombieSkin().toString());

                StringBuilder profs = new StringBuilder();
                for(VillagerRegistry.VillagerCareer car : careers)
                {
                    profs.append(car.getName());
                    profs.append(System.lineSeparator());
                }
                if(careers.size() > 0)
                    profs.delete(profs.length() - System.lineSeparator().length(), profs.length());
                profLst.add(profs.toString());

                lst.add(profLst);
                PMDData.plusCounts(counts, loc.getResourceDomain(), 1);
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        return new PMDData(lst, counts);
    }
}
