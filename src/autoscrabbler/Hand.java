package autoscrabbler;

/**
 * @author Samuel Seifert
 * autoscrabbler@gmail.com
 */

public class Hand
{
    public char[] AvailableTiles;    
    public char[] WordSoFar = new char[0];
    
    private final int[] dupes = new int[27];
    
    public int TilesLeft = 0;
    public int TilesUsed = 0;
    
    public Hand(String s)
    {
        for (int i = 0; i < 27; i++) this.dupes[i] = -1;
        
        StringBuilder b = new StringBuilder();
        
        for (char c : s.toLowerCase().toCharArray())
        {
            if (WordInfo.CheckIfLower(c))
            {
                if (dupes[c - 97] < 0) b.append(c);
                dupes[c - 97]++;
                this.TilesLeft++;
            }            
            else if (c == '?')
            {
                if (dupes[26] < 0) b.append(c);
                dupes[26]++;                
                this.TilesLeft++;
            }                        
        }

        for (int i = 0; i < 27; i++) if(this.dupes[i] < 0) this.dupes[i] = 0;
        
        this.AvailableTiles = b.toString().toCharArray();
    }    
        
    private Hand()
    {
        
    }
        
    public Hand CloneAndRemove(char c)
    {
        if (WordInfo.CheckIfLower(c))
        {
            Hand h = new Hand();
            h.TilesLeft = this.TilesLeft - 1;
            h.TilesUsed = this.TilesUsed + 1;
            System.arraycopy(this.dupes, 0, h.dupes, 0, 27);         

            int i = c - 97; 
            if (h.dupes[i] > 0)
            {
                h.dupes[i]--; 
                h.AvailableTiles = this.AvailableTiles; // Don't need a new array
            }
            else
            {
                i = 0;
                h.AvailableTiles = new char[this.AvailableTiles.length - 1];                
                for (char cc : this.AvailableTiles) if (cc != c) h.AvailableTiles[i++] = cc; 
            }
            
            i = this.WordSoFar.length;
            h.WordSoFar = new char[i + 1];
            System.arraycopy(this.WordSoFar, 0, h.WordSoFar, 0, i); 
            h.WordSoFar[i] = c;
            
            return h;
        }
        else return null;        
    }

    Hand CloneAndRemoveWild(char c)
    {
        Hand h = new Hand();
        h.TilesLeft = this.TilesLeft - 1;
        h.TilesUsed = this.TilesUsed + 1;
        System.arraycopy(this.dupes, 0, h.dupes, 0, 27);         

        int i = 26; 
        if (h.dupes[i] > 0)
        {
            h.dupes[i]--; 
            h.AvailableTiles = this.AvailableTiles; // Don't need a new array
        }
        else
        {
            i = 0;
            h.AvailableTiles = new char[this.AvailableTiles.length - 1];                
            for (char cc : this.AvailableTiles) if (cc != '?') h.AvailableTiles[i++] = cc; 
        }

        i = this.WordSoFar.length;
        h.WordSoFar = new char[i + 1];
        System.arraycopy(this.WordSoFar, 0, h.WordSoFar, 0, i); 
        h.WordSoFar[i] = c;

        return h;
    }

    Hand CloneAndAddBoard(char c)
    {
        Hand h = new Hand();
        h.TilesLeft = this.TilesLeft;
        h.TilesUsed = this.TilesUsed;
        System.arraycopy(this.dupes, 0, h.dupes, 0, 27);         
        h.AvailableTiles = this.AvailableTiles; // Don't need a new array
        int i = this.WordSoFar.length;
        h.WordSoFar = new char[i + 1];
        System.arraycopy(this.WordSoFar, 0, h.WordSoFar, 0, i); 
        h.WordSoFar[i] = c;
        return h;
    }
}
