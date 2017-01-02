package ru.pearx.pmdumper.utils;

import java.util.*;

/**
 * Created by me on 25.10.16.
 */
public class Table
{
    public TableFormat Format;
    public int ColumnToSort;
    public List<List<String>> data = new ArrayList<List<String>>();

    public Table(TableFormat f, int columnToSort)
    {
        Format = f;
        ColumnToSort = columnToSort;
    }

    public void add(String... strs)
    {
        data.add(Arrays.asList(strs));
    }

    public void sort()
    {
        if(ColumnToSort < 0)
            return;
        Collections.sort(data, new Comparator<List<String>>()
        {
            @Override
            public int compare(List<String> e1, List<String> e2)
            {

                String val1 = e1.get(ColumnToSort);
                String val2 = e2.get(ColumnToSort);

                return val1.compareTo(val2);
            }
        });
    }

    public String print()
    {
        sort();
        StringBuilder sb = new StringBuilder();
        if(data.size() > 0)
        {
            if(Format == TableFormat.Txt)
            {
                int[] maxes = new int[data.get(0).size()];
                for (List<String> l : data)
                {
                    for (int i = 0; i < maxes.length; i++)
                    {
                        if (maxes[i] < l.get(i).length())
                            maxes[i] = l.get(i).length();
                    }
                }
                int allSize = 0;
                for (int i : maxes)
                {
                    allSize += i; //add max
                    allSize += 1; //add |
                }
                allSize += 1; //remove | and add 2 =.

                //+---+---+-----+
                sb.append("+");
                for (int i = 0; i < maxes.length; i++)
                {
                    sb.append(PartyCommonUtils.getMultichars("-", maxes[i]));
                    sb.append("+");
                }
                sb.append("\n");
                for (List<String> l : data)
                {
                    //|one|two|three|
                    sb.append("|");
                    for (int i = 0; i < maxes.length; i++)
                    {
                        sb.append(l.get(i));
                        sb.append(getSpaces(l.get(i), maxes[i]));
                        if (i < maxes.length - 1)
                        {
                            sb.append("|");
                        }
                    }
                    sb.append("|");
                    sb.append("\n");
                    //+---+---+-----+
                    sb.append("+");
                    for (int i = 0; i < maxes.length; i++)
                    {
                        sb.append(PartyCommonUtils.getMultichars("-", maxes[i]));
                        sb.append("+");
                    }
                    sb.append("\n");
                }
            }
            else if(Format == TableFormat.Csv)
            {
                for(List<String> row : data)
                {
                    for(String s : row)
                    {
                        sb.append("\"" + s.replace("\"", "\"\"") + "\"" + ",");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    sb.append("\n");
                }
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
