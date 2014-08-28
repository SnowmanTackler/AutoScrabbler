package autoscrabbler;

/**
 * @author Samuel Seifert
 * autoscrabbler@gmail.com
 */

import java.awt.Dimension;

/*
 * This class contains and initializes a scrabble game in either a applet or a j frame.
 */

public class mainParent
{
    // Saved games as strings.
    public static String games[] = new String[5];

    // Creator.  
    private static main parent;

    public static void myStart(main Papa)
    {        
        parent = Papa;
        // Size of window.
        Dimension d = new Dimension(600, 475);
        
        // Mainly for JFrame, because applets can't resize themselves.
        parent.setSize(d);
        
        // Start a scrabble class.
        ScrabbleGame runningScrabbleGame = new ScrabbleGame(parent.getContainer());
        WordFinder.myStart(runningScrabbleGame);

        String saved = parent.getSavedGames();

        // Intro message.
        if (saved == null || saved.length() != (15*15+2)*games.length)
        {
            saved = "00"
                    + "use9999999enter"
                    + "arrows9CAPITALS"
                    + "to9999999999for"
                    + "move999999wilds"
                    + "around999999999"
                    + "999999999999999"
                    + "9999999reset999"
                    + "999999clears999"
                    + "9999999board999"
                    + "999999999999999"
                    + "9type9hand99999"
                    + "9in9top9box9999"
                    + "999999999999999"
                    + "999999999999999"
                    + "999999999999999";
            for (int i = 0; i < (15*15+2)*(games.length-1); i++) saved += "9";
        }

        // Load saved games;
        for (int i = 0; i < games.length; i++)
        {
            games[i] = saved.substring(0, 15*15+2);
            saved = saved.substring(15*15+2);
        }

        // Fully initialize scrabble game.
        runningScrabbleGame.loadGame(games[0]);
        runningScrabbleGame.setGameText("Welcome to AutoScrabbler.com", "Click on a tile to get started!");
        runningScrabbleGame.startListening();
    }

    public static void save()
    {
        // Combines all games, tells the creator to save them in whatever method they do.
        String saved = "";
        for (int i = 0; i < games.length; i++) saved += games[i];
        parent.save(saved);
    }
}