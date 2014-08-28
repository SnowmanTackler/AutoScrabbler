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
    // Displayed Dictionary Names
    public static final String TYPETWL = "TWL",
                               TYPESOWPODS = "SOWPODS",
                               TYPEWWF = "WWF";
    
    // Displayed Dictionary Names in an ARRAY!
    public static final String[] dictNames = {TYPETWL, TYPESOWPODS, TYPEWWF};

    // Dictionaries will be loaded into here!
    // Dimensions: word length, starting letter, index#
    private static String allDictWords[][][];
    
    // Remembers which dictionary is loaded. Number represents index in dictNames array.
    private static int currentType = -1;


    // Loads a dictionary to RAM if it isn't already loaded.  Forgets old dictionaries.
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
        // Create new pointer
        allDictWords = new String[14][][];
        try
        {
            String word;
            int size = 0;

            // will store length of the specific word lists.
            int lengthSize[][] = new int[14][];

            for (int i = 0; i < allDictWords.length; i++)
            {
                lengthSize[i] = new int[26];
                allDictWords[i] = new String[26][];
                for (int k = 0; k < 26; k++) lengthSize[i][k] = 0;
            }

            BufferedReader dict;

            // Pick the dictionary.
            if (dictType.equals(TYPESOWPODS))
                dict =  new BufferedReader(new InputStreamReader(
                        Dictionary.class.getResourceAsStream("/resources/SOWPODS")));
            else if (dictType.equals(TYPEWWF))
                dict =  new BufferedReader(new InputStreamReader(
                        Dictionary.class.getResourceAsStream("/resources/WWF")));
            else if (dictType.equals(TYPETWL)) 
                dict =  new BufferedReader(new InputStreamReader(
                    Dictionary.class.getResourceAsStream("/resources/TWL")));
            else
            {
                dict = new BufferedReader(new InputStreamReader(
                    Dictionary.class.getResourceAsStream("/resources/TWL")));
                System.out.println("Dictionary - Can't find dict: " + dictType + ".  Using TWL");
            }

            // Read it lines, add 1 to corresponding lengthSize[][]
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
            
            // Initialize final dimension of dictionary array
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

            // Reopen file
            if (dictType.equals(TYPESOWPODS))
                dict =  new BufferedReader(new InputStreamReader(
                        Dictionary.class.getResourceAsStream("/resources/SOWPODS")));
            else if (dictType.equals(TYPEWWF))
                dict =  new BufferedReader(new InputStreamReader(
                        Dictionary.class.getResourceAsStream("/resources/WWF")));
            else if (dictType.equals(TYPETWL)) 
                dict =  new BufferedReader(new InputStreamReader(
                    Dictionary.class.getResourceAsStream("/resources/TWL")));
            else
                dict = new BufferedReader(new InputStreamReader(
                    Dictionary.class.getResourceAsStream("/resources/TWL")));

            // Start loading into memory
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
    // Uses quick search
    public static boolean b(String word)
    {
        int firstSpaceIndex = word.indexOf(" ");
        if (firstSpaceIndex == 0) return true;
        else if (firstSpaceIndex < 0)  return isValid(word);
        else
        {
            int startingLetter = Character.getNumericValue(word.charAt(0)) - 10;
            String adjustedWord = word.substring(0, firstSpaceIndex).toLowerCase();
            boolean found = false;
            int length = word.length() - 2;
            int min = 0, max = allDictWords[length][startingLetter].length - 1, mid;
            while (max >= min && !found)
            {
                mid = (min + max) / 2;
                if (adjustedWord.compareTo(allDictWords[length][startingLetter][mid].substring(0, firstSpaceIndex)) < 0) max = mid - 1;
                else if (adjustedWord.compareTo(allDictWords[length][startingLetter][mid].substring(0, firstSpaceIndex)) > 0) min = mid + 1;
                else found = true;
            }
            return found;
        }
    }

    // Returns true if the word exists
    // Uses quick search algorithm
    public static boolean isValid(String word)
    {
        int startingLetter = Character.getNumericValue(word.charAt(0)) - 10;
        String adjustedWord = word.toLowerCase();
        boolean found = false;
        int length = word.length() - 2;
        int min = 0, max = allDictWords[length][startingLetter].length - 1, mid;
        while (max >= min && !found)
        {
            mid = (min + max) / 2;
            if (adjustedWord.compareTo(allDictWords[length][startingLetter][mid]) < 0) max = mid - 1;
            else if (adjustedWord.compareTo(allDictWords[length][startingLetter][mid]) > 0) min = mid + 1;
            else found = true;
        }
        return found;
    }
}