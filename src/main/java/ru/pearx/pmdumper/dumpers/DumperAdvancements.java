package ru.pearx.pmdumper.dumpers;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.pearx.pmdumper.dumpers.base.IDumper;
import ru.pearx.pmdumper.dumpers.base.PMDData;

import java.util.*;

/**
 * Created by mrAppleXZ on 14.06.17 12:11.
 */
@SideOnly(Side.CLIENT)
public class DumperAdvancements implements IDumper
{

    @Override
    public String getName()
    {
        return "advancements";
    }

    @Override
    public PMDData getData()
    {
        List<Advancement> advs = new ArrayList<>();
        for(WorldServer wrld : DimensionManager.getWorlds())
        {
            for(Advancement adv : wrld.getAdvancementManager().getAdvancements())
            {
                if(!advs.contains(adv))
                    advs.add(adv);
            }
        }

        List<List<String>> data = new ArrayList<>();
        Map<String, Integer> counts = new HashMap<>();
        data.add(Arrays.asList("ID", "Display Text", "Display Info", "Parent", "Children", "Rewards"));
        for(Advancement adv : advs)
        {
            StringBuilder children = new StringBuilder();
            for(Advancement ch : adv.getChildren())
            {
                children.append(ch.getId());
                children.append(System.lineSeparator());
            }
            if(children.length() >= System.lineSeparator().length())
                children.delete(children.length() - System.lineSeparator().length(), children.length());
            data.add(Arrays.asList(adv.getId().toString(),
                    adv.getDisplayText().getFormattedText(),
                    adv.getDisplay() == null ? "no" : displayInfoToString(adv.getDisplay()),
                    adv.getParent() == null ? "no" : adv.getParent().getId().toString(),
                    children.toString(),
                    adv.getRewards().toString()
            ));
            PMDData.plusCounts(counts, adv.getId().getResourceDomain(), 1);
        }
        return new PMDData(data, counts);
    }

    public String displayInfoToString(DisplayInfo inf)
    {
        StringBuilder bld = new StringBuilder();
        bld.append("{");
        bld.append("title=").append(inf.getTitle().getFormattedText()).append(", ").append(System.lineSeparator());
        bld.append("description=").append(inf.getDescription().getFormattedText()).append(", ").append(System.lineSeparator());
        bld.append("icon=").append(inf.getIcon().toString()).append(", ").append(System.lineSeparator());
        bld.append("x=").append(inf.getX()).append(", ").append(System.lineSeparator());
        bld.append("y=").append(inf.getX()).append(", ").append(System.lineSeparator());
        bld.append("background=").append(inf.getBackground() == null ? "no" : inf.getBackground().toString()).append(", ").append(System.lineSeparator());
        bld.append("frame=").append(inf.getFrame().toString()).append(", ").append(System.lineSeparator());
        bld.append("hidden=").append(inf.isHidden()).append(", ").append(System.lineSeparator());
        bld.append("announce=").append(inf.shouldAnnounceToChat()).append(", ").append(System.lineSeparator());
        bld.append("toast=").append(inf.shouldShowToast());
        bld.append("}");
        return bld.toString();
    }
}
