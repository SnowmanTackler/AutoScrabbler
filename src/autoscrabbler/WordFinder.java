package autoscrabbler;

/**
 * @author Samuel Seifert
 * autoscrabbler@gmail.com
 */

import java.awt.Color;

public class WordFinder extends Thread
{
    private static ScrabbleGame _ScrabbleGame;
    
    public static void myStart(ScrabbleGame runningScrabbleGame)
    {
        WordFinder._ScrabbleGame = runningScrabbleGame;
        WordFinder.workers[0] = new WordFinder(SearchType.DiesImmediately);
        WordFinder.workers[1] = new WordFinder(SearchType.DiesImmediately);
        WordFinder.workers[2] = new WordFinder(SearchType.DiesImmediately);
    }

    private static final WordFinder workers[] = new WordFinder[3];
    private static volatile int bestScore = -1; // stores best scored word score
    private static volatile int bestCount = -1; // stores best scored word score
    private static char[] bestWord = new char[0]; // stores best scored word
    private static long startTime; // Start time of a word    
    
    private static final String haventFoundWordsMessage = "I haven't found any words yet!", // Beginning message
                                didntFindWordsMessage = "No valid words."; // other beginning message

        
    private static volatile boolean killAllNow = false; // stops active threads when pulled high

    private enum SearchType
    {
        Vertical,
        Horizontal, 
        NewGame,
        DiesImmediately        
    }
    
    private enum TileType
    {
        LetterDouble,
        LetterTriple, 
        WordDouble,
        WordTriple,
        None
    }
    
    private enum cycle3
    {
        Hand, 
        Wild,
        Board
    }
    
    private volatile boolean IsThreadThreading = false; // is the thread alive?
    private final SearchType _SearchType; // what type is the thread? (either vert, horiz or NG)
    private final boolean isWWF; // Whether or not the game type is WWF, matters for storing.
    private final int[] MinConnectedLengths = new int[15];
    private final int SurroundStart[] = new int[15];
    private final int SurroundEnd[] = new int[15];
    private int StartI = 0;
    private int StartK = 0;    
    private final String HandInput;
    private final char BoardLetters[][] = new char[15][15];   
    private final TileType[][] Tiles = new TileType[15][15]; // same with colors.
    private final char[] PossibleWilds = new char[]
    {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };


    private static final int 
            BonusScrabble = 50,
            BonusWWF = 35;

    // Statically updates all three threads.
    public static void update()
    {
        WordFinder.kill();        
        WordFinder._ScrabbleGame.clearOldAnswer();
        WordFinder._ScrabbleGame.setGameText(haventFoundWordsMessage, "Thinking");
        
        boolean isNewGame = true;
        
        for (int i = 0; i < 15 && isNewGame; i++)
        {
            for (int k = 0; k < 15 && isNewGame; k++)
            {                
                if (WordFinder._ScrabbleGame.let[i][k] != ' ')
                {
                    isNewGame = false;
                }
            }
        }        

        WordFinder.bestScore = -1;
        WordFinder.bestCount = 0;
        WordFinder.startTime = System.currentTimeMillis();

        if (isNewGame)
        {
            WordFinder.workers[2] = new WordFinder(SearchType.NewGame);
            WordFinder.workers[2].start();
        }
        else
        {
            WordFinder.workers[1] = new WordFinder(SearchType.Vertical);
            WordFinder.workers[0] = new WordFinder(SearchType.Horizontal);
            WordFinder.workers[1].start();
            WordFinder.workers[0].start();
        }
    }

    // Stops all threads.
    public static void kill()
    {
        killAllNow = true;

        try { workers[0].join(); } catch (InterruptedException ex) { System.out.println("V"); }
        try { workers[1].join(); } catch (InterruptedException ex) { System.out.println("H"); }
        try { workers[2].join(); } catch (InterruptedException ex) { System.out.println("N"); }

        killAllNow = false;

        _ScrabbleGame.setGameText("...","");
    }
    
    private static synchronized void threadDone()
    {
        if (workers[0].IsThreadThreading || workers[1].IsThreadThreading || workers[2].IsThreadThreading)
        {
            // Still Working
        }
        else
        {
            String top = "Best word: " + new String(bestWord).toLowerCase() + " " + bestScore;
            if (bestScore == -1) top = didntFindWordsMessage;
            _ScrabbleGame.setGameText(
                    top,
                    "Found " + WordFinder.bestCount + " words in " + 
                    ((System.currentTimeMillis() - startTime)/1000.0) + " seconds");        
        }
    }
    
    private static synchronized void newBestWord(
            int score, 
            char[] word, 
            int i, 
            int k,
            SearchType t)
    {
        WordFinder.bestCount++;
        
        if (score <= bestScore) return;
        
        bestScore = score;
        bestWord = word;
       
        if (t == SearchType.Vertical)
        {
            WordFinder._ScrabbleGame.newBestAnswer(k, i, true, word);            
        }
        else
        {
            WordFinder._ScrabbleGame.newBestAnswer(i, k, false, word);            
        }
        
        WordFinder._ScrabbleGame.setGameText("Best word: " + new String(bestWord).toLowerCase() + " " + bestScore, "Thinking");
    }
    




    private WordFinder(SearchType t)
    {            
        this._SearchType = t;
        this.setPriority(Thread.MIN_PRIORITY);
        
        for (int i = 0; i < 15; i++)
        {
            for (int k = 0; k < 15; k++)
            {                     
                Color c;
                
                switch (this._SearchType)
                {
                    case Horizontal:
                        this.BoardLetters[i][k] = WordFinder._ScrabbleGame.let[i][k];
                        c = WordFinder._ScrabbleGame.tileColors[i][k];
                        break;
                    default:
                        this.BoardLetters[i][k] = WordFinder._ScrabbleGame.let[k][i];
                        c = WordFinder._ScrabbleGame.tileColors[k][i];
                        break;
                }
                
                if (c == null) this.Tiles[i][k] = TileType.None;
                else if (c.equals(Color.BLUE)) this.Tiles[i][k] = TileType.LetterDouble;
                else if (c.equals(Color.GREEN)) this.Tiles[i][k] = TileType.LetterTriple;
                else if (c.equals(Color.RED)) this.Tiles[i][k] = TileType.WordDouble;
                else if (c.equals(Color.ORANGE)) this.Tiles[i][k] = TileType.WordTriple;
                else this.Tiles[i][k] = TileType.None;                
                
            }
        }
        
        this.HandInput = WordFinder._ScrabbleGame.input.getText();
        this.isWWF = WordFinder._ScrabbleGame.getDictType() == Dictionary.DictType.WWF;
    }
    
    @Override
    public void run()
    {
        this.IsThreadThreading = true;
        this.runMethod();
        this.IsThreadThreading = false;
        if (SearchType.DiesImmediately != this._SearchType) WordFinder.threadDone();
    }
    
    public void runMethod()
    {
        switch (this._SearchType)
        {
            case NewGame: this.cycleNewGame(); break;
            case DiesImmediately: break;
            default: this.cycle(); break;
        }
    }
 

    // If game is new, find set up (surroud and base word)
    private void cycleNewGame()
    {
        final int notconlength = 30;
        
        Hand h = new Hand(this.HandInput);
        
        int k = 7;
        
        for (int i = 0; i < 15; i++)
        {
            this.MinConnectedLengths[i] = notconlength;            
        }
        for (int i = 0; i < 15; i++)
        {
            this.SurroundStart[i] = 0;
            this.SurroundEnd[i] = 0;

            boolean connected = i == 7;
            // This means that this row is connected to puzzel, so we change the connect length requirement
            if (connected)
            {
                for (int i2 = 0; i2 <= i; i2++)
                {
                    this.MinConnectedLengths[i - i2] = Math.min(this.MinConnectedLengths[i - i2], i2 + 1);                        
                }
            }
        }
        for (int i = 0; i < 14; i++)
        {
            if (
                    (this.MinConnectedLengths[i] < notconlength) &&
                    ((i == 0) || (this.BoardLetters[i - 1][k] == ' '))
               )
            {
                this.StartI = i;
                this.StartK = k;
                this.cycle2(
                        i,
                        k, 
                        Dictionary._WordInfo, 
                        h, 
                        0, 
                        0, 
                        1);                    
            }
        }
    }

    // Find Horizontal set ups (surround and base word)
    private void cycle()
    {            
        final int notconlength = 30;
        
        Hand h = new Hand(this.HandInput);
        
        for (int k = 0; k < 15; k++)
        {                        
            for (int i = 0; i < 15; i++)
            {
                this.MinConnectedLengths[i] = notconlength;            
            }
            for (int i = 0; i < 15; i++)
            {
                this.SurroundStart[i] = 0;
                this.SurroundEnd[i] = 0;
                
                boolean connected;
                if (this.BoardLetters[i][k] == ' ')
                {
                    for (int newK = k - 1; newK >= 0; newK--)
                    {
                        if (this.BoardLetters[i][newK] == ' ') break;
                        else this.SurroundStart[i] ++;
                    }
                    for (int newK = k + 1; newK < 15; newK++)
                    {
                        if (this.BoardLetters[i][newK] == ' ') break;
                        else this.SurroundEnd[i] ++;
                    }                    
                    
                    connected = (this.SurroundStart[i] + this.SurroundEnd[i]) > 0;                   
                }
                else connected = true;
                                
                // This means that this row is connected to puzzel, so we change the connect length requirement
                if (connected)
                {
                    for (int i2 = 0; i2 <= i; i2++)
                    {
                        this.MinConnectedLengths[i - i2] = Math.min(this.MinConnectedLengths[i - i2], i2 + 1);                        
                    }
                }
            }
            for (int i = 0; i < 14; i++)
            {
                if (
                        (this.MinConnectedLengths[i] < notconlength) &&
                        ((i == 0) || (this.BoardLetters[i - 1][k] == ' '))
                   )
                {
                    this.StartI = i;
                    this.StartK = k;
                    this.cycle2(
                            i,
                            k, 
                            Dictionary._WordInfo, 
                            h, 
                            0, 
                            0, 
                            1);                    
                }
            }
        }
    }    
    
    private void cycle2(
            int i,
            int k, 
            WordInfo wf,
            Hand h,
            int cross_score,
            int word_score,
            int word_mult
        )
    {          
        if (WordFinder.killAllNow)
        {
            // Kill It
        } 
        else if (this.BoardLetters[i][k] == ' ') // If open space
        {            
            for (char cc : h.AvailableTiles) // Iterate through hand
            {                
                if (cc == '?') // If hand is wild
                {                    
                    for (char c : PossibleWilds) // Iterate through letters
                    {
                        this.cycle3(
                                i,
                                k, 
                                wf, 
                                h, 
                                cross_score, 
                                word_score, 
                                word_mult, 
                                cycle3.Wild, 
                                c);
                    }           
                }
                else // If hand not wild
                {
                    this.cycle3(
                            i,
                            k, 
                            wf, 
                            h, 
                            cross_score, 
                            word_score, 
                            word_mult, 
                            cycle3.Hand, 
                            cc);                
                }            
            }     
        }
        else // If Not Open Space
        {
            this.cycle3(
                    i,
                    k, 
                    wf, 
                    h, 
                    cross_score, 
                    word_score, 
                    word_mult, 
                    cycle3.Board, 
                    this.BoardLetters[i][k]);                            
        }
    }    
    
    private void cycle3(
            int i,
            int k, 
            WordInfo wf,
            Hand h,
            int cross_score,
            int word_score,
            int word_mult,
//////////////////////////////////////
            cycle3 cyc,
            char board_char            
        )
    {           
        WordInfo next = wf.CheckChar(board_char);           
        if (next != null)
        {
            int cross_mult = 1;
            int letter_score = this.CS(board_char);

            switch (this.Tiles[i][k])
            {
                case LetterDouble: letter_score *= 2; break;
                case LetterTriple: letter_score *= 3; break;
                case WordDouble: cross_mult = 2; break;
                case WordTriple: cross_mult = 3; break;
                default: break;
            }
                    
            boolean surroundGood = false;
            int sstart = this.SurroundStart[i];
            int lens = sstart + this.SurroundEnd[i]; 

            if (lens++ != 0)
            {
                char srnd[] = new char[lens];   
                
                for (int k2 = 0; k2 < lens; k2++)
                {
                    if (k2 == sstart) srnd[k2] = board_char;
                    else srnd[k2] = this.BoardLetters[i][k + k2 - sstart];                        
                }
                if (Dictionary._WordInfo.CheckWord(srnd))
                {
                    surroundGood = true;
                    int cross_score_instant = 0;

                    for (int k2 = 0; k2 < lens; k2++)
                        if (k2 != sstart) 
                            cross_score_instant += this.CS(srnd[k2]);                        

                    cross_score += (letter_score + cross_score_instant) * cross_mult;
                }
            }
            else surroundGood = true; 
            
            if (surroundGood)
            {   
                word_score += letter_score;
                word_mult *= cross_mult;

                int wordlength = next.getDepth();
                
                if (next.IsWord)
                {
                    if (wordlength >= this.MinConnectedLengths[this.StartI])
                    {
                        if ((i == 14) || this.BoardLetters[i+1][k] == ' ')
                        {                                    
                            int tiles_used = h.TilesUsed + (cyc == cycle3.Board ? 0 : 1);
                            
                            if (tiles_used > 0)
                            {
                                int score = cross_score + word_score * word_mult;
                                if (tiles_used == 7) score += this.isWWF 
                                        ? WordFinder.BonusWWF 
                                        : WordFinder.BonusScrabble;
                                
                                lens = h.WordSoFar.length;
                                char[] wrd = new char[lens + 1];
                                System.arraycopy(h.WordSoFar, 0, wrd, 0, lens); 
                                wrd[lens] = board_char;

                                WordFinder.newBestWord(
                                        score, 
                                        wrd, 
                                        this.StartI, 
                                        this.StartK, 
                                        this._SearchType);
                            }
                        }
                    }
                }
                if (next.CanContinue && i < 14)
                {
                    switch (cyc)
                    {
                        case Hand:
                            this.cycle2(
                                    i + 1, 
                                    k, 
                                    next, 
                                    h.CloneAndRemove(board_char), 
                                    cross_score,
                                    word_score,
                                    word_mult);   
                            break;
                        case Wild:
                            this.cycle2(
                                    i + 1, 
                                    k, 
                                    next, 
                                    h.CloneAndRemoveWild(board_char), 
                                    cross_score,
                                    word_score,
                                    word_mult);                   
                            break;
                        case Board:
                            this.cycle2(
                                    i + 1, 
                                    k, 
                                    next, 
                                    h.CloneAndAddBoard(board_char), 
                                    cross_score,
                                    word_score,
                                    word_mult);                   
                            break;
                        
                    }
                }
            }
        }
    }
        
    // Finds base score of a given letter
    private int CS(char y)
    {
        if (this.isWWF)
        {
            switch (y - 97)
            {
                case  0: return 1; // a
                case  1: return 4; // b
                case  2: return 4; // c
                case  3: return 2; // d
                case  4: return 1; // e
                case  5: return 4; // f
                case  6: return 3; // g
                case  7: return 3; // h
                case  8: return 1; // i
                case  9: return 10; // j
                case 10: return 5; // k
                case 11: return 2; // l
                case 12: return 4; // m
                case 13: return 2; // n
                case 14: return 1; // o
                case 15: return 4; // p
                case 16: return 10; // q
                case 17: return 1; // r
                case 18: return 1; // s
                case 19: return 1; // t
                case 20: return 2; // u
                case 21: return 5; // v
                case 22: return 4; // w
                case 23: return 8; // x
                case 24: return 3; // y
                case 25: return 10; // z
                default: return 0;
            }
        }
        else
        {
            switch (y - 97)
            {
                case  0: return 1; // a
                case  1: return 3; // b
                case  2: return 3; // c
                case  3: return 2; // d
                case  4: return 1; // e
                case  5: return 4; // f
                case  6: return 2; // g
                case  7: return 4; // h
                case  8: return 1; // i
                case  9: return 8; // j
                case 10: return 5; // k
                case 11: return 1; // l
                case 12: return 3; // m
                case 13: return 1; // n
                case 14: return 1; // o
                case 15: return 3; // p
                case 16: return 10; // q
                case 17: return 1; // r
                case 18: return 1; // s
                case 19: return 1; // t
                case 20: return 1; // u
                case 21: return 4; // v
                case 22: return 4; // w
                case 23: return 8; // x
                case 24: return 4; // y
                case 25: return 10; // z
                default: return 0;
            }
        }
    }
}