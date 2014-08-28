package autoscrabbler;

/**
 * @author Samuel Seifert
 * autoscrabbler@gmail.com
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Dictionary
{
    // Displayed Dictionary Names
    public enum DictType {
        TWL,
        SOWPODS,
        WWF,
        NotSet
    }
    
    // Dictionaries will be loaded into here!
    // Dimensions: word length, starting letter, index#
    public static WordInfo _WordInfo;
    
    // Remembers which dictionary is loaded. Number represents index in dictNames array.
    private static DictType currentType = DictType.NotSet;

    
    public static DictType getforint(int i)
    {
        switch (i)
        {
            case 0: return DictType.TWL;
            case 1: return DictType.SOWPODS;
            case 2: return DictType.WWF;
            default: return DictType.NotSet;                
        }
    }

    // Loads a dictionary to RAM if it isn't already loaded.  Forgets old dictionaries.
    public static void select(int type)
    {
        DictType next = Dictionary.getforint(type);
        if (next != currentType)
        {
            Dictionary.load(next);
            currentType = next;
        }
    }

    public static void load(DictType next)
    {
        // Create new pointer
        Dictionary._WordInfo = new WordInfo();
        try
        {
            InputStream is = null;
            
            switch (next)
            {
                case TWL:
                    is =  Dictionary.class.getResourceAsStream("/resources/TWL");
                    break;
                case SOWPODS:
                    is =  Dictionary.class.getResourceAsStream("/resources/SOWPODS");
                    break;
                case WWF:
                    is =  Dictionary.class.getResourceAsStream("/resources/WWF");
                    break;
            }
            
            BufferedReader dict;
            
            if (is == null)
            {
                System.out.println("Dictionary - Can't find dict: " + next + ".  Using TWL");
                is =  Dictionary.class.getResourceAsStream("/resources/TWL");
            }

            dict = new BufferedReader(new InputStreamReader(is));

            String word = dict.readLine();
            while (word != null)
            {
                _WordInfo.add(word);
                word = dict.readLine();
            }
            dict.close();
        }
        catch (IOException ex) { System.out.println(ex); }
    }

    // Returns true if the word exists
    // Uses quick search algorithm

    static boolean CheckWord(String s)
    {
        return Dictionary._WordInfo.CheckWord(s);
    }
}