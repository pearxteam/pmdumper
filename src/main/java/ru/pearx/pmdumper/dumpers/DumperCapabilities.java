package ru.pearx.pmdumper.dumpers;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.reflect.FieldUtils;
import ru.pearx.pmdumper.PMDumper;
import ru.pearx.pmdumper.dumpers.base.IDumper;
import ru.pearx.pmdumper.dumpers.base.PMDData;

import java.util.*;

/**
 * Created by mrAppleXZ on 12.06.17 11:23.
 */
@SideOnly(Side.CLIENT)
public class DumperCapabilities implements IDumper
{

    @Override
    public String getName()
    {
        return "capabilities";
    }

    @Override
    public PMDData getData()
    {
        List<List<String>> lst = new ArrayList<>();
        lst.add(Arrays.asList("Interface", "Default Instance Class", "Storage Class"));
        try
        {
            IdentityHashMap<String, Capability<?>> map = (IdentityHashMap<String, Capability<?>>) FieldUtils.readField(CapabilityManager.INSTANCE, "providers", true);
            for (Map.Entry<String, Capability<?>> entr : map.entrySet())
            {
                     lst.add(Arrays.asList(entr.getKey(), entr.getValue().getDefaultInstance().getClass().getName(), entr.getValue().getStorage().getClass().getName()));
            }
        } catch (IllegalAccessException e)
        {
            PMDumper.INSTANCE.log.error(e);
        }
        return new PMDData(lst, null);
    }
}
