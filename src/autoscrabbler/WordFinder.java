package autoscrabbler;

/**
 * @author Samuel Seifert
 * autoscrabbler@gmail.com
 */

import java.awt.Color;

public class WordFinder extends Thread
{
    private static ScrabbleGame game;
    
    public static void myStart(ScrabbleGame runningScrabbleGame)
    {
        // Static thread rememebers parent and children.
        game = runningScrabbleGame;
        
        // The threads need to be started 
        workers[0] = new WordFinder(diesImmediately);
        workers[1] = new WordFinder(diesImmediately);
        workers[2] = new WordFinder(diesImmediately);
    }

    private static WordFinder workers[] = new WordFinder[3];

    private static long startTime; // Start time of a word
    
    private static boolean isWWF; // Whether or not the game type is WWF, matters for storing.
    
    private static final String SearchHorizontal = "0xD5", // Stores type of thread, should be int but I don't care
                               SearchVertical = "0xEE",
                               SearchNewGame = "0x9A",
                               diesImmediately = "0x05",
                               haventFoundWordsMessage = "I haven't found any words yet!", // Beginning message
                               didntFindWordsMessage = "No valid words."; // other beginning message

    private static String input; // Users hand

    private static char let[][] = new char[15][15]; // threads store their own board chars
                                                    // so they don't change during "live score update"
    
    private static Color tile[][] = new Color[15][15]; // same with colors.
    
    private static volatile int bestScore = -1; // stores best scored word score
    private static volatile String bestWord = ""; // stores best scored word
    
    private static volatile WordFinder currentWritingHighScore = null; // stores current thread editing high score.
                                                                       // only one thread can edit at a time 
    
    private static boolean killAllNow = false; // stops active threads when pulled high

    private boolean alive = true; // is the thread alive?
    private String type; // what type is the thread? (either vert, horiz or NG)
    private String scoreSurround[]; // Surround words
    private int    cLoc[] = new int[2]; // Stores location of starting point of word.
    private int    bonus; // bonus for using all letters.  50 for scrabble, 35 for WWF

    private static final int scrabbleBonus = 50, wwfBonus = 35;

    private static boolean isBusy()
    {
        // Is the word finder class busy?
        return (workers[0].alive || workers[1].alive || workers[2].alive);
    }

    // Statically updates all three threads.
    public static void update()
    {
        kill();
        game.clearOldAnswer();
        boolean isNewGame = true;
        
        for(int i = 0; i<15; i++)
        {
            for(int k = 0; k<15; k++)
            {                
                tile[i][k] = game.tileColors[i][k];
                let[i][k] = game.let[i][k];
                if (let[i][k] != ' ') isNewGame = false;
            }
        }

        bestScore = -1;
        input = game.input.getText();
        isWWF = game.getDictType().equals(Dictionary.TYPEWWF);

        game.setGameText(haventFoundWordsMessage, "Thinking");
        startTime = System.currentTimeMillis();

        if (isNewGame) workers[2] = new WordFinder(SearchNewGame);
        else
        {
            workers[1] = new WordFinder(SearchHorizontal);
            workers[0] = new WordFinder(SearchVertical);
        }
    }

    // Stops all threads.
    public static void kill()
    {
        killAllNow = true;

        try { workers[0].join(); } catch (Exception ex) { System.out.println("V"); }
        try { workers[1].join(); } catch (Exception ex) { System.out.println("H"); }
        try { workers[2].join(); } catch (Exception ex) { System.out.println("N"); }

        killAllNow = false;

        game.setGameText("...","");
    }

    private void newBestWord(int score, String word, int x, int y)
    {
        while (currentWritingHighScore != this)
        {
            if (score <= bestScore) return;
            if (currentWritingHighScore == null && score > bestScore) currentWritingHighScore = this;
        }
        bestScore = score;
        bestWord = word;
        game.newBestAnswer(x, y, !type.equals(SearchHorizontal), word);
        game.setGameText("Best word: " + bestWord.toLowerCase() + " " + bestScore, "Thinking");
        currentWritingHighScore = null;
    }


    private WordFinder(String t)
    {
        type = t;
        this.setPriority(Thread.MIN_PRIORITY);
        this.start();
    }


    @Override
    public void run()
    {
        if (type.equals(SearchHorizontal)) cycleHorizontal();
        else if (type.equals(SearchVertical)) cycleVertical();
        else if (type.equals(SearchNewGame)) cycleNewGame();

        alive = false;

        if (!type.equals(diesImmediately) && !isBusy())
        {
            String top = "Best word: " + bestWord.toLowerCase() + " " + bestScore;
            if (bestScore == -1) top = didntFindWordsMessage;
            game.setGameText(top, "Search Complete: " + ((System.currentTimeMillis() - startTime)/1000.0) + " seconds");
        }
    }


    // If game is new, find set up (surroud and base word)
    private void cycleNewGame() {
	int i = 7;
	cLoc[0] = i;

	for(int k = 0; k < 8; k++)
	{
            cLoc[1] = k;

            for(int tiles = input.length(); tiles > 1; tiles--)
            {
                bonus = tiles == 7 ? isWWF ? wwfBonus : scrabbleBonus : 0;

                int wordLength = tiles;

                String baseWord = "";
                String surround[] = new String[wordLength];

                for(int l = 0; l<wordLength; l++)
                {
                    baseWord = baseWord + " ";
                    surround[l] = " ";
                }

                // first part is if it contains center tile, second part is if its contained by board)
                boolean connected = ((k<=7 && k+wordLength-1 >= 7) && (k+wordLength-1<15));


                scoreSurround = new String[surround.length];
                System.arraycopy(surround, 0, scoreSurround, 0, surround.length);

                if (connected) { findBases(input, baseWord, surround); }
            }
        }
    }

    // Find Vertical set ups (surround and base word)
    private void cycleVertical()
    {
        for (int i = 0; i < 15; i++)
        {
            cLoc[0] = i;
            for (int k = 0; k < 14; k++)
            {
                cLoc[1] = k;
                if (k == 0 || let[i][k - 1] == ' ')
                {
                    for (int tiles = input.length(); tiles > 0; tiles--)
                    {
                        boolean connected = false;

                        bonus = tiles == 7 ? isWWF ? wwfBonus : scrabbleBonus : 0;

                        int tilesUsed = 0;
                        int wordLength = 0;

                        while (k + wordLength - 1 < 14 && tilesUsed < tiles)
                        {
                            if (let[i][k + wordLength] == ' ')
                                tilesUsed++;
                            wordLength++;
                        }

                        while (k + wordLength - 1 < 14 && let[i][k + wordLength] != ' ')
                        {
                            wordLength++;
                            connected = true;
                        }

                        if (tilesUsed == tiles)
                        {
                            String baseWord = "";
                            String surround[] = new String[wordLength];

                            for (int l = wordLength-1; l > -1; l--)
                            {
                                baseWord = let[i][k + l] + baseWord;
                                if (let[i][k + l] != ' ')
                                {
                                    surround[l] = String.valueOf(let[i][k + l]);
                                    connected = true;
                                }
                                else
                                {
                                    String crossWord = "";

                                    for (int newI = 0; newI < 15; newI++)
                                    {
                                        if (newI == i || let[newI][k + l] != ' ')
                                            crossWord = crossWord + let[newI][k + l];
                                        else
                                        {
                                            if (newI < i) { crossWord = ""; }
                                            else          { newI = 99999999; }
                                        }
                                    }

                                    surround[l] = crossWord;
                                    if (crossWord.length() > 1)
                                        connected = true;
                                }
                            }

                            if (connected && baseWord.length() > 1)
                            {
                                scoreSurround = new String[surround.length];
                                System.arraycopy(surround, 0, scoreSurround, 0, surround.length);
                                findBases(input, baseWord, surround);
                            }
                        }
                    }
                }
            }
        }
    }


    // Find Horizontal set ups (surround and base word)
    private void cycleHorizontal()
    {
        for (int i = 0; i < 14; i++)
        {
            cLoc[0] = i;
            for (int k = 0; k < 15; k++)
            {
                cLoc[1] = k;
                if (i == 0 || let[i - 1][k] == ' ')
                {
                    for (int tiles = input.length(); tiles > 0; tiles--)
                    {
                        boolean connected = false;

                        bonus = tiles == 7 ? isWWF ? wwfBonus : scrabbleBonus : 0;

                        int tilesUsed = 0;
                        int wordLength = 0;

                        while (i + wordLength - 1 < 14 && tilesUsed < tiles)
                        {
                            if (let[i + wordLength][k] == ' ') { tilesUsed++; }
                            wordLength++;
                        }

                        while (i + wordLength - 1 < 14 && let[i + wordLength][k] != ' ')
                        {
                            wordLength++;
                            connected = true;
                        }

                        if (tilesUsed == tiles)
                        {
                            String baseWord = "";
                            String surround[] = new String[wordLength];

                            for (int l = wordLength-1; l>-1; l--)
                            {
                                baseWord = let[i+l][k] + baseWord;
                                if (let[i + l][k] != ' ')
                                {
                                    surround[l] = String.valueOf(let[i + l][k]);
                                    connected = true;
                                }
                                else
                                {
                                    String crossWord = "";

                                    for (int newK = 0; newK < 15; newK++)
                                    {
                                        if (newK == k || let[i + l][newK] != ' ')
                                            crossWord = crossWord + let[i + l][newK];
                                        else
                                        {
                                            if (newK < k) { crossWord = ""; }
                                            else          { newK = 99999999; }
                                        }
                                    }

                                    surround[l] = crossWord;
                                    if (crossWord.length() > 1)
                                        connected = true;
                                }
                            }

                            if (connected && baseWord.length() > 1)
                            {
                                scoreSurround = new String[surround.length];
                                System.arraycopy(surround, 0, scoreSurround, 0, surround.length);
                                findBases(input, baseWord, surround);
                            }
                        }
                    }
                }
            }
        }
    }



    // Finds bases for a given surround and base word + letters
    private void findBases(String letters, String baseWord, String surround[]) {

        if (killAllNow) return;

        int i = 0;

        while (i < baseWord.length() && surround[i].length() == 1) { i++; }

        if (i == baseWord.length()) simplify(baseWord, letters);

        else
	{
            String repeatLetters = "";
            for(int p = letters.length()-1; p>-1; p--)
            {
                char current = letters.charAt(p);
                if (repeatLetters.indexOf(current) < 0)
                {
                    repeatLetters = repeatLetters + current;

                    if (current != '?')
                    {
                        if(Dictionary.isValid(surround[i].replaceFirst(" ", "" + current)))
                        {
                            String newSurround[] = new String[surround.length];
                            System.arraycopy(surround, 0, newSurround, 0, surround.length);
                            newSurround[i] = "" + current;

                            String newLetters = letters.replaceFirst("" + current, "");

                            char newBase[] = baseWord.toCharArray();
                            newBase[i] = current;
                            String newBaseWord = String.copyValueOf(newBase);

                            findBases(newLetters, newBaseWord, newSurround);
                        }
                    }
                    else
                    {
                        String newLetters = letters.replaceFirst("\\?", "");
                        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                        char alpha[] = alphabet.toCharArray();

                        for (int al=0; al < alpha.length; al++)
                        {
                            current = alpha[al];

                            if (Dictionary.isValid(surround[i].replaceFirst(" ", "" + current)))
                            {
                                String newSurround[] = new String[surround.length];
                                System.arraycopy(surround, 0, newSurround, 0, surround.length);
                                newSurround[i] = "" + current;

                                char newBase[] = baseWord.toCharArray();
                                newBase[i] = current;
                                String newBaseWord = String.copyValueOf(newBase);

                                findBases(newLetters, newBaseWord, newSurround);
                            }
                        }
                    }
                }
            }
        }
    }

    // Takes a word setup and finds all words
    private void simplify(String word, String letters) {

        if (killAllNow || !Dictionary.b(word)) return;
        if (word.indexOf(" ") < 0)  score(word);
        else
        {
            String repeatLetters = "";
            for (int p = 0; p < letters.length(); p++)
            {
                char current = letters.charAt(p);
                if (repeatLetters.indexOf(current) < 0)
                {
                    repeatLetters = repeatLetters + current;

                    if (current != '?')
                    {
                        String newWord = word.replaceFirst(" ", "" + current);
                        String newLetters = letters.replaceFirst("" + current, "");
                        simplify(newWord, newLetters);
                    }
                    else
                    {
                        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                        char alpha[] = alphabet.toCharArray();

                        String newLetters = letters.replaceFirst("\\?", "");

                        for (int al=0; al < alpha.length; al++)
                        {
                            String newWord = word.replaceFirst(" ", "" + alpha[al]);
                            simplify(newWord, newLetters);
                        }
                    }
                }
            }
        }
    }

    // Scores a word
    private void score(String baseWord)
    {
        int baseScore = 0; int multBase = 1;

        for (int p = 0; p < scoreSurround.length; p++)
        {
            int a = 0, b = 0; if (!type.equals(SearchHorizontal)) b = p; else a = p;
            char current = baseWord.charAt(p);

            if      (tile[cLoc[0]+a][cLoc[1]+b].equals(Color.BLUE)) baseScore += CS(current)    *1;
            else if (tile[cLoc[0]+a][cLoc[1]+b].equals(Color.GREEN)) baseScore += CS(current)*2;
            else if (tile[cLoc[0]+a][cLoc[1]+b].equals(Color.RED)) multBase = multBase*2;
            else if (tile[cLoc[0]+a][cLoc[1]+b].equals(Color.ORANGE)) multBase = multBase*3;

            baseScore += CS(current);
        }
        int currentScore = bonus + baseScore*multBase;

        for (int p = 0; p < scoreSurround.length; p++)
            if (scoreSurround[p].length() > 1)
            {
                int crossScore = 0; int multCross = 1;

                int a = 0, b = 0; if (type.equals(SearchHorizontal)) a = p; else b = p;
                char current = baseWord.charAt(p);

                char crossWord[] = scoreSurround[p].toCharArray();
		for (int i = 0; i < crossWord.length; i++) { crossScore += CS(crossWord[i]); }
                crossScore += CS(current);

                if      (tile[cLoc[0]+a][cLoc[1]+b].equals(Color.BLUE)) crossScore += CS(current)*1;
                else if (tile[cLoc[0]+a][cLoc[1]+b].equals(Color.GREEN)) crossScore += CS(current)*2;
                else if (tile[cLoc[0]+a][cLoc[1]+b].equals(Color.RED)) multCross = multCross*2;
                else if (tile[cLoc[0]+a][cLoc[1]+b].equals(Color.ORANGE)) multCross = multCross*3;

                currentScore += crossScore*multCross;
            }
        System.out.println(this.type + " : " + currentScore);
        newBestWord(currentScore, baseWord, cLoc[0], cLoc[1]);
    }

    
    
    // Finds base score of a given letter
    private static int CS(char y)
    {
        if (isWWF)
        {
            if      (y=='a' || y=='e' || y=='i' || y=='o' || y=='r' || y=='s' || y=='t')	return 1;
            else if (y=='b' || y=='c' || y=='f' || y=='m' || y=='p' || y=='w')                  return 4;
            else if (y=='d' || y=='n' || y=='u' || y=='l')                                      return 2;
            else if (y=='g' || y=='h' || y=='y')                                                return 3;
            else if (y=='j' || y=='q' || y=='z')						return 10;
            else if (y=='k' || y=='v')								return 5;
            else if (y=='x') 									return 8;
            else                                                                                return 0;
        }
        else
        {
            if      (y=='e' || y=='a' || y=='i' || y=='o' || y=='n' || y=='r' || y=='t' || y=='l' || y=='s' || y=='u')	return 1;
            else if (y=='f' || y=='h' || y=='v' || y=='w' || y=='y')							return 4;
            else if (y=='b' || y=='c' || y=='m' || y=='p')                                                              return 3;
            else if (y=='d' || y=='g') 											return 2;
            else if (y=='j' || y=='x')											return 8;
            else if (y=='q' || y=='z') 											return 10;
            else if (y=='k')												return 5;
            else                                                                                                        return 0;
        }
    }
}