package autoscrabbler;

/**
 * @author Samuel Seifert
 * autoscrabbler@gmail.com
 */

import java.awt.Dimension;


public class mainParent
{
    public static String games[] = new String[5];

    private static main parent;

    public static void myStart(main Papa)
    {
        parent = Papa;
        Dimension d = new Dimension(600, 475);
        parent.setSize(d);
        ScrabbleGame runningScrabbleGame = new ScrabbleGame(parent.getContainer());
        WordFinder.myStart(runningScrabbleGame);

        String saved = parent.getSavedGames();

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
            for (int i = 0; i < 15*15+2; i++) saved += "9999";
        }

        for (int i = 0; i < games.length; i++)
        {
            games[i] = saved.substring(0, 15*15+2);
            saved = saved.substring(15*15+2);
        }

        runningScrabbleGame.setGameState(games[0]);
        runningScrabbleGame.setGameText("Welcome to AutoScrabbler.com", "Click on a tile to get started!");
        runningScrabbleGame.startListening();
    }

    public static void save()
    {
        String saved = "";
        for (int i = 0; i < games.length; i++) saved += games[i];
        parent.save(saved);
    }
}