package autoscrabbler;

/**
 * @author Samuel Seifert
 * autoscrabbler@gmail.com
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Dictionary
{
    public static final String TYPETWL = "TWL",
                               TYPESOWPODS = "SOWPODS",
                               TYPEWWF = "WWF";
    public static final String[] dictNames = {TYPETWL, TYPESOWPODS, TYPEWWF};

    private static String allDictWords[][][];
    private static int currentType = -1;

    public static void select(int type)
    {
        type = Math.abs(type) % dictNames.length;
        if (type != currentType)
        {
            Dictionary.load(dictNames[type]);
            currentType = type;
        }
    }

    public static void load(String dictType)
    {
        allDictWords = new String[14][][];
        try
        {
            String word;
            int size = 0;

            int lengthSize[][] = new int[14][];

            for (int i = 0; i < allDictWords.length; i++)
            {
                lengthSize[i] = new int[26];
                allDictWords[i] = new String[26][];
                for (int k = 0; k < 26; k++) lengthSize[i][k] = 0;
            }

            BufferedReader dict;

            if (dictType.equals(TYPESOWPODS)) dict =  new BufferedReader(new InputStreamReader(Dictionary.class.getResourceAsStream("/resources/SOWPODS")));
            else if (dictType.equals(TYPEWWF)) dict =  new BufferedReader(new InputStreamReader(Dictionary.class.getResourceAsStream("/resources/WWF")));
            else if (dictType.equals(TYPETWL)) dict =  new BufferedReader(new InputStreamReader(Dictionary.class.getResourceAsStream("/resources/TWL")));
            else { dict = new BufferedReader(new InputStreamReader(Dictionary.class.getResourceAsStream("/resources/TWL"))); System.out.println("Dictionary - Can't find dict: " + dictType + ".  Using TWL"); }

            word = dict.readLine();
            while(word != null)
            {
                size++;
                int wLength = word.length();
                if (wLength > 1 && wLength < 16)
                {
                    int startLet = Character.getNumericValue(Character.toLowerCase(word.charAt(0))) - 10;
                    if (startLet > -1 && startLet < 26) lengthSize[wLength-2][startLet]++;
                }

                word = dict.readLine();
            }
            dict.close();

            if (dictType.equals(TYPESOWPODS)) dict =  new BufferedReader(new InputStreamReader(Dictionary.class.getResourceAsStream("/resources/SOWPODS")));
            else if (dictType.equals(TYPEWWF)) dict =  new BufferedReader(new InputStreamReader(Dictionary.class.getResourceAsStream("/resources/WWF")));
            else if (dictType.equals(TYPETWL)) dict =  new BufferedReader(new InputStreamReader(Dictionary.class.getResourceAsStream("/resources/TWL")));
            else { dict = new BufferedReader(new InputStreamReader(Dictionary.class.getResourceAsStream("/resources/TWL"))); System.out.println("Dictionary - Can't find dict: " + dictType + ".  Using TWL"); }

            int indexOfWordLengths[][] = new int[14][];
            for (int i = 0; i < allDictWords.length ; i++)
            {
                indexOfWordLengths[i] = new int[26];
                for (int k = 0; k < 26; k++)
                {
                    indexOfWordLengths[i][k] = 0;
                    allDictWords[i][k] = new String[lengthSize[i][k]];
                }
            }

            for(int i = 0; i < size; i++)
            {
                word = dict.readLine().toLowerCase();

                int wLength = word.length();
                if (wLength > 1 && wLength < 16)
                {
                    int startLet = Character.getNumericValue(word.charAt(0)) - 10;
                    allDictWords[wLength-2][startLet][indexOfWordLengths[wLength-2][startLet]] = word;
                    indexOfWordLengths[wLength-2][startLet]++;
                }
            }

            dict.close();
        }
        catch (IOException ex) { System.out.println(ex); }
    }


    // Returns true if the first part of the string (before the first space) can be made into a valid word of appropriate length
    public static boolean b(String aaab) {
        int aaaa = aaab.indexOf(" ");
        if (aaaa == 0) return true;
        else if (aaaa < 0)  return isValid(aaab);
        else
        {
            int aaar = Character.getNumericValue(aaab.charAt(0)) - 10;
            String aaac = aaab.substring(0, aaaa);
            aaac = aaac.toLowerCase();
            boolean aaae = false;
            int aaad = aaab.length() - 2;
            int aaa = 0, aaaf = allDictWords[aaad][aaar].length - 1, aaag;
            while (aaaf >= aaa && !aaae)
            {
                aaag = (aaa + aaaf) / 2;
                if (aaac.compareTo(allDictWords[aaad][aaar][aaag].substring(0, aaaa)) < 0) aaaf = aaag - 1;
                else if (aaac.compareTo(allDictWords[aaad][aaar][aaag].substring(0, aaaa)) > 0) aaa = aaag + 1;
                else aaae = true;
            }
            return aaae;
        }
    }

    // Returns true if the word exists
    public static boolean isValid(String input)
    {
        String guess = input.toLowerCase();
        boolean found = false;
        int size = guess.length()-2;
        int startLet = Character.getNumericValue(input.charAt(0)) - 10;

        int first = 0, last = allDictWords[size][startLet].length - 1, middle;

        while (last >= first && !found)
        {
            middle = (first + last) / 2;

            if (guess.compareTo(allDictWords[size][startLet][middle]) < 0)      { last = middle - 1; }
            else if (guess.compareTo(allDictWords[size][startLet][middle]) > 0)	{ first = middle + 1; }
            else                                                                { found = true;}
        }

        return found;
    }
}