package autoscrabbler;

/**
 * @author Samuel Seifert
 * autoscrabbler@gmail.com
 */

public class WordInfo
{    
    public WordInfo()
    {
        for (int i = 0; i < this.Children.length; i++) 
            this.Children[i] = null;
    }
    
    private char Char = ' ';
    private int Depth = 0;
    public int getDepth() { return this.Depth; }

    public boolean CanContinue = false;
    public boolean IsWord = false;
    public final WordInfo[] Children = new WordInfo[26];  
    public WordInfo Parent = null;
    
    /// Must Be Called With All Lower Case
    public void add(String s)
    {
        char cs[] = s.trim().toLowerCase().toCharArray();
        
        for (char c : cs)
        {
            int test = c - 97;
            if ((test < 0) || (test > 25))
            {
                System.out.println("Error Adding Word: " + s);
                return;
            }
        }
        
        this.add(cs, 0);
        
    }
    
    public void add(char[] dat, int dex)
    {
        if (dex == dat.length)
        {
            this.IsWord = true;
        }
        else
        {
            this.CanContinue = true; 
            
            int adex = dat[dex] - 97; // 97 is offset for a in ascii (or utf)?            
            WordInfo child = this.Children[adex];            
            if (child == null)
            {
                child = new WordInfo();
                child.Parent = this;
                child.Depth = this.Depth + 1;
                child.Char = dat[dex];
                this.Children[adex] = child;
            }
            
            child.add(dat, dex + 1);
        }        
    }
    
    public String GetWord()
    {
        StringBuilder b = new StringBuilder(this.Depth);
        this.GetWordParent(b);
        return b.toString();
    }
        
    private void GetWordParent(StringBuilder b)
    {
        if (this.Parent != null)
        {
            this.Parent.GetWordParent(b);
            b.append(this.Char);
        }
    }    
    
    public char[] GetWordAsCharArray()
    {
        char[] c = new char[this.Depth];
        this.GetWordAsCharArrayParent(c);
        return c;
    }
    
    private void GetWordAsCharArrayParent(char[] c)
    {
        if (this.Parent != null)
        {
            c[this.Depth - 1] = this.Char;
            this.Parent.GetWordAsCharArray();
        }
    }

    public boolean CheckWord(String s)
    {
        return this.CheckWord(s.toCharArray());
    }
    
    public boolean CheckWord(char[] s)
    {
        WordInfo cur = this;
        
        for (char c : s)
        {
            cur = cur.CheckChar(c);
            if (cur == null) return false;            
        }
        
        return cur.IsWord;
    }
    
    public WordInfo CheckChar(char c)
    {
        // Should be ranged from -12.5 to 12.5
        if (WordInfo.CheckIfLower(c)) return this.Children[c - 97];
        else if (WordInfo.CheckIfUpper(c)) return this.Children[c - 65];
        return null;
    }
    
    public static boolean CheckIfUpper(float f)
    {
        return Math.abs(f - (65.0f + 12.5f)) < 13;
    }
    
    public static boolean CheckIfLower(float f)
    {
        return Math.abs(f - (97.0f + 12.5f)) < 13;
    }
}
