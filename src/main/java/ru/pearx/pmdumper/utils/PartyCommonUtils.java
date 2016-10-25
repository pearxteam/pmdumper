package ru.pearx.pmdumper.utils;

/**
 * Created by me on 25.10.16.
 */
public class PartyCommonUtils
{
    public static String getMultichars(String s, int count)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < count; i++)
        {
            sb.append(s);
        }
        return sb.toString();
    }
}
