package ru.pearx.pmdumper.dumpers.base;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by mrAppleXZ on 11.06.17 12:35.
 */
@SideOnly(Side.CLIENT)
public interface IDumper
{
    String getName();
    PMDData getData();
}
