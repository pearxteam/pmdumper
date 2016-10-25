package ru.pearx.pmdumper.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by me on 25.10.16.
 */
public class Table
{
    public List<List<String>> data = new ArrayList<List<String>>();

    public Table()
    {

    }

    public void add(String... strs)
    {
        data.add(Arrays.asList(strs));
    }

    public String print()
    {
        StringBuilder sb = new StringBuilder();
        if(data.size() > 0)
        {
            int[] maxes = new int[data.get(0).size()];
            for(List<String> l : data)
            {
                for(int i = 0; i < maxes.length; i++)
                {
                    if(maxes[i] < l.get(i).length())
                        maxes[i] = l.get(i).length();
                }
            }
            int allSize = 0;
            for(int i : maxes)
            {
                allSize += i; //add max
                allSize += 1; //add |
            }
            allSize += 1; //remove | and add 2 =.

            //+---+---+-----+
            sb.append("+");
            for(int i = 0; i < maxes.length; i++)
            {
                sb.append(PartyCommonUtils.getMultichars("-", maxes[i]));
                sb.append("+");
            }
            sb.append("\n");
            for(List<String> l : data)
            {
                //|one|two|three|
                sb.append("|");
                for(int i = 0; i < maxes.length; i++)
                {
                    sb.append(l.get(i));
                    sb.append(getSpaces(l.get(i), maxes[i]));
                    if(i < maxes.length - 1)
                    {
                        sb.append("|");
                    }
                }
                sb.append("|");
                sb.append("\n");
                //+---+---+-----+
                sb.append("+");
                for(int i = 0; i < maxes.length; i++)
                {
                    sb.append(PartyCommonUtils.getMultichars("-", maxes[i]));
                    sb.append("+");
                }
                sb.append("\n");
            }
        }
        else
            sb.append(" ");
        return sb.toString();
    }

    public static String getSpaces(String word, int max)
    {
        int spaces = max - word.length();
        return PartyCommonUtils.getMultichars(" ", spaces);
    }

}
