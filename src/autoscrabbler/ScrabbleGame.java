package autoscrabbler;

/**
 * @author Samuel Seifert
 * autoscrabbler@gmail.com
 */

import SamuelSeifert.FontLoader;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ScrabbleGame extends Canvas implements ActionListener, MouseListener, KeyListener
{

    public static int GAP = 3, // Gap between rects.
                      RECT_DIM = 25; // Rect dim
    
    public static Color brickColor    = new Color(238, 217, 168), // Tile color of played bricks
                        textColor     = new Color(20, 20, 20), // Text color of normal bricks
                        textColorWild = Color.GRAY, // Text color of wild bricks
                        answerColor   = Color.WHITE, // Tile color of best answer bricks
                        tileColor     = new Color (220,220,220); // Tile color of non-bonus unused tiles

    private final String messageText[] = {"", ""}; // Displays "welcom" and elapsed time and best word shinannigans
    
    public char let[][]        = new char[15][15]; // current letters on board
    public Color letColors[][] = new Color[15][15]; // current color of letters on board
    private final Color baseColors[][] = new Color[15][15]; // base color of tile on a board (changes based on GAME type only)
    public Color tileColors[][] = new Color[15][15]; // current color of board tiles

    // User Interface
    public JTextField input        = new JTextField(); // user input for their hand
    private JTextField dInput      = new JTextField(); // user input to look up a word

    private final JCheckBox boardChecks[] = new JCheckBox[5], // Saved game selection check boxes.
                            gameTypeChecks[] = new JCheckBox[2], // Game type selection check boxes.
                            dictChecks[] = new JCheckBox[3]; // Dict select check boxes
    
    private final JButton reset = new JButton("Clear"), // Clears board of best answer, (if there is none) clears board completely
                          submit = new JButton("Play"); // Plays a found word.

    private final Color barColor = Color.WHITE; // Color of side bar.

    private int selectX = 7, selectY = 7; // Board cursor location (in tiles)
    boolean selectShowing = false; // board bool

    private boolean lastD = true; // Last written direction, horizontal is true, vertical is false

    // Game type labels, used to switch Boards
    public static final String gameTypes[] = {"Scrabble", "Words With Friends"};

    
    // Starting messages for user inputs.
    private static final String dInputStart = "",//Green is good, red is bad...",
                                inputStart = "Enter \"?\" for wilds!";


    public ScrabbleGame(Container contentPane)
    {
        // Create a canvas!
        super();

        // Load fonts
        if (!FontLoader.isMainFontSet()) FontLoader.setMainFont("FontBoard.ttf", 18);
        if (!FontLoader.isSecondaryFontSet()) FontLoader.setSecondaryFont("FontMessage.ttf", 13);

        // Set fonts on user inputs.
        dInput.setFont(FontLoader.getSecondaryFont());
        input.setFont(FontLoader.getSecondaryFont());
        JLabel tempLabel;

        // Create right side panel
            JPanel mainRight = new JPanel();
            mainRight.setLayout(new BoxLayout(mainRight, BoxLayout.Y_AXIS));
            mainRight.setBackground(barColor);

            tempLabel = new JLabel("Tiles in hand:");               tempLabel.setFont(FontLoader.getSecondaryFont());   mainRight.add(tempLabel);
            mainRight.add(input);
            input.setText(inputStart);

            JPanel tPanel = new JPanel();
            tPanel.add(submit);
            tPanel.add(reset);
            tPanel.setBackground(barColor);
            mainRight.add(tPanel);

            tempLabel = new JLabel("Board:");                       tempLabel.setFont(FontLoader.getSecondaryFont());   mainRight.add(tempLabel);
            for (int i = 0; i < boardChecks.length; i++)
            {
                boardChecks[i] = new JCheckBox(String.valueOf(i+1));
                boardChecks[i].setFont(FontLoader.getSecondaryFont());
                boardChecks[i].setBackground(barColor);
                mainRight.add(boardChecks[i]);
            }
            boardChecks[0].setSelected(true);
            tempLabel = new JLabel(" ");                            tempLabel.setFont(FontLoader.getSecondaryFont());   mainRight.add(tempLabel);

            tempLabel = new JLabel("Board Type:");                  tempLabel.setFont(FontLoader.getSecondaryFont());   mainRight.add(tempLabel);
            for (int i = 0; i < gameTypeChecks.length; i++)
            {
                gameTypeChecks[i] = new JCheckBox(gameTypes[i]);
                gameTypeChecks[i].setFont(FontLoader.getSecondaryFont());
                gameTypeChecks[i].setBackground(barColor);
                mainRight.add(gameTypeChecks[i]);
            }
            gameTypeChecks[0].setSelected(true);
            tempLabel = new JLabel(" ");                            tempLabel.setFont(FontLoader.getSecondaryFont());   mainRight.add(tempLabel);

            tempLabel = new JLabel("Dictionary:");                  tempLabel.setFont(FontLoader.getSecondaryFont());   mainRight.add(tempLabel);
            for (int i = 0; i < dictChecks.length; i++)
            {
                dictChecks[i] = new JCheckBox(Dictionary.getforint(i).toString());
                dictChecks[i].setFont(FontLoader.getSecondaryFont());
                dictChecks[i].setBackground(barColor);
                mainRight.add(dictChecks[i]);
            }
            dictChecks[0].setSelected(true);
            tempLabel = new JLabel(" ");                            tempLabel.setFont(FontLoader.getSecondaryFont());   mainRight.add(tempLabel);

            tempLabel = new JLabel("Look up a word here:");         tempLabel.setFont(FontLoader.getSecondaryFont());   mainRight.add(tempLabel);
            mainRight.add(dInput); dInput.setText(dInputStart);
            dInput.setSelectionColor(Color.WHITE);
            dInput.setSelectedTextColor(Color.RED);

            tempLabel = new JLabel(" ");                            tempLabel.setFont(FontLoader.getSecondaryFont());   mainRight.add(tempLabel);
            tempLabel = new JLabel(" ");                            tempLabel.setFont(FontLoader.getSecondaryFont());   mainRight.add(tempLabel);
            tempLabel = new JLabel(" ");                            tempLabel.setFont(FontLoader.getSecondaryFont());   mainRight.add(tempLabel);
            tempLabel = new JLabel(" ");                            tempLabel.setFont(FontLoader.getSecondaryFont());   mainRight.add(tempLabel);
            tempLabel = new JLabel(" ");                            tempLabel.setFont(FontLoader.getSecondaryFont());   mainRight.add(tempLabel);
            tempLabel = new JLabel(" ");                            tempLabel.setFont(FontLoader.getSecondaryFont());   mainRight.add(tempLabel);
            tempLabel = new JLabel(" ");                            tempLabel.setFont(FontLoader.getSecondaryFont());   mainRight.add(tempLabel);
            tempLabel = new JLabel(" ");                            tempLabel.setFont(FontLoader.getSecondaryFont());   mainRight.add(tempLabel);
            tempLabel = new JLabel(" ");                            tempLabel.setFont(FontLoader.getSecondaryFont());   mainRight.add(tempLabel);


        // DO THIS TO ALIGN PROPERLY
        Component comp[] = mainRight.getComponents();
        for (int i = 0; i < comp.length; i++)
        {
            ((JComponent)comp[i]).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        mainRight.validate();

        // Init our array variables.
        for(int i = 0; i<15; i++)
            for(int k = 0; k<15; k++)
            {
                baseColors[i][k] = tileColor;
                letColors[i][k] = textColor;
                let[i][k] = ' ';
            }
        
        // Add stuff to container!
        contentPane.setLayout(new BorderLayout());
        contentPane.add(this, BorderLayout.CENTER);
        contentPane.add(mainRight, BorderLayout.EAST);
        contentPane.validate();
        
        // Created double buffer image & graphics.
        offscreen = new BufferedImage(super.getWidth(), super.getHeight(), BufferedImage.TYPE_INT_RGB);
        bufferGraphics = offscreen.getGraphics(); 
    }
    
    // Used to predict font sizes (so stuff can be centered)
    private FontMetrics mainFontMetrics = null;
    private FontMetrics secondaryFontMetrics = null;
    
    // Stores height of fonts, couldn't use a mainFontMetric method because non fit properly.
    int mainTextHeightOffset;
    int secondaryTextHeightOffset;
    
    // Double Buffered Stuff
    private Graphics bufferGraphics = null; 
    private BufferedImage offscreen;     
    
    @Override
    public void paint(Graphics g)
    {
        // Because of init ordering, this is required to not through null exceptions during init.
        if (bufferGraphics == null) return;
        
        // Get font sizes
        else if (mainFontMetrics == null)
        {
            mainFontMetrics = bufferGraphics.getFontMetrics(FontLoader.getMainFont());
            mainTextHeightOffset = mainFontMetrics.getAscent() - mainFontMetrics.getDescent() - 1;
            secondaryFontMetrics = bufferGraphics.getFontMetrics(FontLoader.getSecondaryFont());
            secondaryTextHeightOffset = secondaryFontMetrics.getAscent() - secondaryFontMetrics.getDescent() - 1;
        }
        
        // Clear screen.
        bufferGraphics.setColor(Color.WHITE);
        bufferGraphics.fillRect(0, 0, super.getWidth(), super.getHeight());
        
        int tempX = (RECT_DIM + GAP)*selectX;
        int tempY = (RECT_DIM+GAP)*selectY;
        
        // Show cursor.
        if (selectShowing)
        {
            bufferGraphics.setColor(Color.DARK_GRAY);
            bufferGraphics.fillRect(tempX-3+GAP, tempY-3+GAP, RECT_DIM+6, RECT_DIM+6);            
        }
        
        // Show tile rectangles and texts
        bufferGraphics.setFont(FontLoader.getMainFont());
        for(int i = 0; i<15; i++)
            for(int k = 0; k<15; k++)
            {
                tempX = (RECT_DIM + GAP)*i+GAP;
                tempY = (RECT_DIM+GAP)*k+GAP;
                bufferGraphics.setColor(tileColors[i][k]);
                bufferGraphics.fillRect(tempX, tempY, RECT_DIM, RECT_DIM);
                bufferGraphics.setColor(letColors[i][k]);
                bufferGraphics.drawString(String.valueOf(let[i][k]), tempX + (RECT_DIM - mainFontMetrics.charWidth(let[i][k]))/2, tempY + (RECT_DIM + mainTextHeightOffset)/2 );
            }
        
        // Show main message texts
        bufferGraphics.setFont(FontLoader.getSecondaryFont());
        bufferGraphics.setColor(Color.BLACK);
        bufferGraphics.drawString(messageText[0], (super.getWidth() - secondaryFontMetrics.stringWidth(messageText[0]))/2, 18 + 15*(RECT_DIM + GAP));
        bufferGraphics.drawString(messageText[1], (super.getWidth() - secondaryFontMetrics.stringWidth(messageText[1]))/2, 38 + 15*(RECT_DIM + GAP));
        
        // After all are loaded, send them to the screen.
        g.drawImage(offscreen,0,0,this); 
    }
    
    @Override
    public void update(Graphics g)
    {
        // Looks redundant, but is needed!
        paint(g);
    }










    // If there is no "best word" on the board, this erases the board completely.
    // If there is a "best word" on the board, this just clears the best word.
    private void resetGame()
    {
        System.out.println("RESET GAME");
        WordFinder.kill();
        boolean noOldAnswer = true;

	for(int i = 0; i < 15; i++)
	{
            for(int k = 0; k < 15; k++)
            {
                if (tileColors[i][k].equals(answerColor))
                {
                    noOldAnswer = false;
                    tileColors[i][k] = baseColors[i][k];
                    let[i][k] = ' ';
                }
            }
	}

        if (noOldAnswer)
        {
            for(int i = 0; i < 15; i++)
            {
                for(int k = 0; k < 15; k++)
                {
                    tileColors[i][k] = (baseColors[i][k]);
                    let[i][k] = ' ';
                }
            }
        }
        if (!input.getText().equals(inputStart)) input.setText("");

        super.repaint();
    }

    // Erases the old answer.
    public void clearOldAnswer()
    {
        for(int i = 0; i < 15; i++)
	{
	    for(int k = 0; k < 15; k++)
            {
                if (tileColors[i][k].equals(answerColor))
                {
                    tileColors[i][k] = baseColors[i][k];
                    let[i][k] = ' ';
                }
            }
        }
        
        super.repaint();
    }

    // takes a "best answer" and finalizes it by chaning the tile colors.
    private void submitAnswer()
    {
        WordFinder.kill();
        for(int i = 0; i < 15; i++)
	{
	    for(int k = 0; k < 15; k++)
            {
                if (tileColors[i][k].equals(answerColor))
                {
                    tileColors[i][k] = brickColor;

                    if (letColors[i][k].equals(textColorWild)) input.setText(input.getText().replaceFirst("\\?", ""));
                    else input.setText(input.getText().replaceFirst(String.valueOf(let[i][k]), ""));
                }
            }
        }
    }

    // Erases old best answer and draws a new one.
    public synchronized void newBestAnswer(int x, int y, boolean isVertical, char[] word) 
    {            
        clearOldAnswer();

        int a = 0, b = 0;
        for (int p = 0; p < word.length; p++)
        {            
            if (isVertical)
                b = p;
            else
                a = p;

            if (let[x+a][y+b] == ' ')
            {
                tileColors[x+a][y+b] = answerColor;
                Character current = word[p];

                if (Character.isUpperCase(current))
                    letColors[x+a][y+b] = textColorWild;
                else
                    letColors[x+a][y+b] = textColor;

                let[x+a][y+b] = current;
            }
        }
        
        super.repaint();
    }












    // Set Board Bonus's (controlled with color)
    private void setBoardBaseColor()
    {
        for(int i = 0; i<15; i++)
            for(int k = 0; k<15; k++)
                baseColors[i][k] = tileColor;

        // Scrabble
        if (gameTypeChecks[0].isSelected())
        {
            // Orange: Triple Word
            baseColors[0][0] = Color.ORANGE;             baseColors[0][14] = Color.ORANGE;
            baseColors[14][14] = Color.ORANGE;           baseColors[14][0] = Color.ORANGE;
            baseColors[0][7] = Color.ORANGE;		baseColors[7][14] = Color.ORANGE;
            baseColors[7][0] = Color.ORANGE;		baseColors[14][7] = Color.ORANGE;

            // Red: Double Word
            baseColors[1][1] = Color.RED;		baseColors[2][2] = Color.RED;
            baseColors[3][3] = Color.RED;		baseColors[4][4] = Color.RED;
            baseColors[10][10] = Color.RED;		baseColors[11][11] = Color.RED;
            baseColors[12][12] = Color.RED;		baseColors[13][13] = Color.RED;
            baseColors[10][4] = Color.RED;		baseColors[11][3] = Color.RED;
            baseColors[12][2] = Color.RED;		baseColors[13][1] = Color.RED;
            baseColors[4][10] = Color.RED;		baseColors[3][11] = Color.RED;
            baseColors[2][12] = Color.RED;		baseColors[1][13] = Color.RED;
            baseColors[7][7] = Color.RED;

            // Blue: Double Letter
            baseColors[0][3] = Color.BLUE;		baseColors[14][11] = Color.BLUE;
            baseColors[3][0] = Color.BLUE;		baseColors[11][14] = Color.BLUE;
            baseColors[0][11] = Color.BLUE;		baseColors[3][14] = Color.BLUE;
            baseColors[11][0] = Color.BLUE;		baseColors[14][3] = Color.BLUE;
            baseColors[7][3] = Color.BLUE;		baseColors[7][11] = Color.BLUE;
            baseColors[3][7] = Color.BLUE;		baseColors[11][7] = Color.BLUE;
            baseColors[6][2] = Color.BLUE;		baseColors[8][2] = Color.BLUE;
            baseColors[2][6] = Color.BLUE;		baseColors[2][8] = Color.BLUE;
            baseColors[6][12] = Color.BLUE;		baseColors[8][12] = Color.BLUE;
            baseColors[12][6] = Color.BLUE;		baseColors[12][8] = Color.BLUE;
            baseColors[6][6] = Color.BLUE;		baseColors[6][8] = Color.BLUE;
            baseColors[8][8] = Color.BLUE;		baseColors[8][6] = Color.BLUE;

            // Green: Triple Letter
            baseColors[1][5] = Color.GREEN;		baseColors[1][9] = Color.GREEN;
            baseColors[5][1] = Color.GREEN;		baseColors[9][1] = Color.GREEN;
            baseColors[13][5] = Color.GREEN;		baseColors[13][9] = Color.GREEN;
            baseColors[5][13] = Color.GREEN;		baseColors[9][13] = Color.GREEN;
            baseColors[5][5] = Color.GREEN;		baseColors[5][9] = Color.GREEN;
            baseColors[9][9] = Color.GREEN;		baseColors[9][5] = Color.GREEN;
        }
        // Words with friends
        else if (gameTypeChecks[1].isSelected())
        {
            // Orange: Triple Word
            baseColors[0][3] = Color.ORANGE;             baseColors[3][0] = Color.ORANGE;
            baseColors[0][11] = Color.ORANGE;            baseColors[11][0] = Color.ORANGE;
            baseColors[14][3] = Color.ORANGE;		baseColors[3][14] = Color.ORANGE;
            baseColors[14][11] = Color.ORANGE;		baseColors[11][14] = Color.ORANGE;

            // Red: Double Word
            baseColors[7][3] = Color.RED;		baseColors[3][7] = Color.RED;
            baseColors[7][11] = Color.RED;		baseColors[11][7] = Color.RED;
            baseColors[1][5] = Color.RED;		baseColors[5][1] = Color.RED;
            baseColors[1][9] = Color.RED;		baseColors[9][1] = Color.RED;
            baseColors[13][5] = Color.RED;		baseColors[5][13] = Color.RED;
            baseColors[13][9] = Color.RED;		baseColors[9][13] = Color.RED;
            
            // MIDDLE IS NOT A DOUBLE WORD, so change it
            baseColors[7][7] = new Color(254 , 0 , 0);

            // Blue: Double Letter
            baseColors[2][1] = Color.BLUE;		baseColors[1][2] = Color.BLUE;
            baseColors[2][4] = Color.BLUE;		baseColors[4][2] = Color.BLUE;
            baseColors[1][12] = Color.BLUE;		baseColors[12][1] = Color.BLUE;
            baseColors[2][13] = Color.BLUE;		baseColors[13][2] = Color.BLUE;
            baseColors[2][10] = Color.BLUE;		baseColors[10][2] = Color.BLUE;
            baseColors[4][12] = Color.BLUE;		baseColors[12][4] = Color.BLUE;
            baseColors[4][6] = Color.BLUE;		baseColors[6][4] = Color.BLUE;
            baseColors[4][8] = Color.BLUE;		baseColors[8][4] = Color.BLUE;
            baseColors[8][10] = Color.BLUE;		baseColors[10][8] = Color.BLUE;
            baseColors[12][13] = Color.BLUE;		baseColors[13][12] = Color.BLUE;
            baseColors[12][10] = Color.BLUE;		baseColors[10][12] = Color.BLUE;
            baseColors[6][10] = Color.BLUE;		baseColors[10][6] = Color.BLUE;

            // Green: Triple Letter
            baseColors[0][6] = Color.GREEN;		baseColors[6][0] = Color.GREEN;
            baseColors[0][8] = Color.GREEN;		baseColors[8][0] = Color.GREEN;
            baseColors[3][3] = Color.GREEN;		baseColors[5][5] = Color.GREEN;
            baseColors[11][11] = Color.GREEN;		baseColors[9][9] = Color.GREEN;
            baseColors[3][11] = Color.GREEN;		baseColors[11][3] = Color.GREEN;
            baseColors[5][9] = Color.GREEN;		baseColors[9][5] = Color.GREEN;
            baseColors[14][6] = Color.GREEN;		baseColors[8][14] = Color.GREEN;
            baseColors[14][8] = Color.GREEN;		baseColors[6][14] = Color.GREEN;
        }
        else
            System.err.println(this.getClass().getSimpleName() + " – " + "can't find color scheme for selected game type");
    }

    // Sets game message text
    public void setGameText(String top, String bottom)
    {
        messageText[0] = top;
        messageText[1] = bottom;
        super.repaint();
    }









    
    
    
    
    
    
    
    
    
    // Saves current game in mainParent array
    private void saveCurrentGame(int index)
    {
        String temp = "";

        // Save board type
        for (int i = 0; i < gameTypeChecks.length; i++)
            if (gameTypeChecks[i].isSelected())
                temp += String.valueOf(i);
        
        if (temp.length() == 0) temp += "0";

        // Save dictionary type
        for (int i = 0; i < dictChecks.length; i++)
            if (dictChecks[i].isSelected()) temp += String.valueOf(i);
        
        if (temp.length() == 1) temp += "0";

        // Save the letters
        for(int i = 0; i < 15; i++)
            for(int k = 0; k < 15; k++)
		temp = temp + let[i][k];

        // Cookies don't store adjacent spaces, so use 9's instead
        temp = temp.replaceAll(" ", "9");

        mainParent.games[index] = temp;
    }
    
    // Can't just check checks!  Because of response orderings, both checks will be displayed during this method call
    private int getCurrentGameIndex(int newSelected)
    {
        // if a check box is checked and it isn't the new one, return it!
        for (int i = 0; i < boardChecks.length; i++) if (i != newSelected && boardChecks[i].isSelected()) return i;
        
        // else we selected a board that was already selected
        return newSelected;
    }

    // if called without an index, assume not changing check marks 
    private void saveCurrentGame()
    {
        saveCurrentGame(this.getCurrentGameIndex(-1));
    }
    
    // loads a board from a specific string type
    public void loadGame(String saved)
    {
        System.out.println("LOAD GAME");
        if (saved == null || saved.length() != 15*15+2) return;

        int zero = Integer.valueOf(String.valueOf(saved.charAt(0))),
            one  = Integer.valueOf(String.valueOf(saved.charAt(1)));

        saved = saved.substring(2).replaceAll("9", " ");

        for(int i = 0; i < 15; i++)
	{
            for(int k = 0; k < 15; k++)
            {
                char temp = saved.charAt(i*15 + k);
                if (!Character.isSpaceChar(temp))
                {
                    tileColors[i][k] = brickColor;
                    if (Character.isUpperCase(temp))
                        letColors[i][k] = textColorWild;
                    else 
                        letColors[i][k] = textColor;
                    let[i][k] = temp;
                }
                else
                {
                    tileColors[i][k] = baseColors[i][k];
                    letColors[i][k] = textColor;
                    let[i][k] = ' ';
                }
            }
	}

        // Avoid words with friends shinnanigans
        this.selectGameTypeCheck(zero);
        if (!gameTypeChecks[1].isSelected()) this.selectDictCheck(one);
        
        super.repaint();
    }


















    // GUI, initialize all listeners
    public void startListening()
    {
        reset.addActionListener(this);
        submit.addActionListener(this);

        super.addMouseListener(this);
        input.addMouseListener(this);
        dInput.addMouseListener(this);

        super.addKeyListener(this);
        dInput.addKeyListener(this);
        input.addKeyListener(this);

        for (int i = 0; i < boardChecks.length; i++)
            boardChecks[i].addActionListener(this);
        for (int i = 0; i < gameTypeChecks.length; i++)
            gameTypeChecks[i].addActionListener(this);
        for (int i = 0; i < dictChecks.length; i++)
            dictChecks[i].addActionListener(this);
    }

    
    
    
    
    
    
    
    
    
    // My own method to address text box input.
    // Clears invalid text as you write it.
    public void dInputAutoUpdate(Character typedChar)
    {
        String newInput = dInput.getText();

        // If its not a back space, add it to string
        if(!(typedChar == '\b')) { newInput = newInput + typedChar; }

        char newGuess[] = newInput.toLowerCase().toCharArray();
        newInput = "";

        // Make sure everything is a letter!
        for (int i = 0; i < newGuess.length; i++)
            if (Character.isLetter(newGuess[i]))
                newInput = newInput + newGuess[i];

        dInput.setText(newInput);

        // Color signifies a valid word
        if (newInput.length() < 2 || newInput.length() > 15)    { dInput.setSelectedTextColor(Color.RED); }
        else if (Dictionary.CheckWord(newInput))      { dInput.setSelectedTextColor(Color.GREEN); }
        else                                                    { dInput.setSelectedTextColor(Color.RED); }
    }

    // My own method to address text box input.
    // Clears invalid text as you write it.
    public void inputAutoUpdate(Character typedChar)
    {
        String newInput = input.getText();

        if(!(typedChar == '\b')) { newInput = newInput + typedChar; }

        char newGuess[] = newInput.toLowerCase().toCharArray();
        newInput = "";

        for (int i = 0; i < newGuess.length; i++)
            if (Character.isLetter(newGuess[i]) || newGuess[i] == '?')
                newInput = newInput + newGuess[i];

        input.setText(newInput);

        WordFinder.update();
    }

    // if the user wan't to enter something in the input text box, clear default message for them.
    public void conditionalClearInput()
    {
        if (input.getText().equals(inputStart)) input.setText("");
    }

    // Get dictionary type
    public Dictionary.DictType getDictType()
    {
        for (int i = 0; i < dictChecks.length; i++)
            if (dictChecks[i].isSelected())
                return Dictionary.getforint(i);
        
        return Dictionary.DictType.NotSet;
    }






    

    






    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();

        // If we submit an answer
        if (source == submit)
        {
            selectShowing = false;
            WordFinder.kill();
            this.submitAnswer();
            this.conditionalClearInput();
            this.saveCurrentGame();
            mainParent.save();
            WordFinder.update();
        }
        // if we reset/clear the board
        else if (source == reset)
        {
            this.resetGame();
            this.saveCurrentGame();
            mainParent.save();
        }
        // Change dict/type/board
        else
        {
            WordFinder.kill();
            this.clearOldAnswer();

            for (int i = 0; i < boardChecks.length; i++)
                if (source == boardChecks[i]) this.selectBoardCheck(i);
            for (int i = 0; i < gameTypeChecks.length; i++)
                if (source == gameTypeChecks[i]) this.selectGameTypeCheck(i);
            for (int i = 0; i < dictChecks.length; i++)
                if (source == dictChecks[i]) this.selectDictCheck(i);

            this.saveCurrentGame();
            mainParent.save();
        }
    }

    // Change board!
    public void selectBoardCheck(int select)
    {
        input.setText(inputStart);
        if (select >= boardChecks.length) select = 0;
        if (select != this.getCurrentGameIndex(select))
        {
            this.saveCurrentGame(this.getCurrentGameIndex(select));
            this.loadGame(mainParent.games[select]);
        }
        for (int j = 0; j < boardChecks.length; j++)
            boardChecks[j].setSelected(select == j);
    }

    // Change game type!
    public void selectGameTypeCheck(int select)
    {
        input.setText(inputStart);
        if (select >= gameTypeChecks.length) select = 0;
        for (int j = 0; j < gameTypeChecks.length; j++)
            gameTypeChecks[j].setSelected(select == j);
        if (select == 1)
        {
            this.selectDictCheck(2);
            for (int j = 0; j < dictChecks.length; j++)
                dictChecks[j].setEnabled(false);
        }
        else for (int j = 0; j < dictChecks.length; j++)
            dictChecks[j].setEnabled(true);

        this.setBoardBaseColor();
        
        for(int i = 0; i<15; i++)
            for(int k = 0; k<15; k++)
                if (!tileColors[i][k].equals(brickColor)) tileColors[i][k] = baseColors[i][k];
    }

    // Select dictionary!
    public void selectDictCheck(int select)
    {
        input.setText(inputStart);
        if (select >= dictChecks.length) select = 0;
        for (int j = 0; j < dictChecks.length; j++) dictChecks[j].setSelected(select == j);
        Dictionary.select(select);
    }












    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        int x = 0, y = 0;

        if (e.getKeyCode() == KeyEvent.VK_UP)
        {
            lastD = false;
            selectY = Math.max(0, selectY-1);
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN)
        {
            lastD = false;
            selectY = Math.min(14, selectY+1);
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT)
        {
            lastD = true;
            selectX = Math.max(0, selectX-1);
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            lastD = true;
            selectX = Math.min(14, selectX+1);
        }
        
        super.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
        Character typedChar = new Character(e.getKeyChar());

        if (e.getSource() == dInput)
        {
            // Make sure the dafault action doesn't change the text box.
            e.setKeyChar(KeyEvent.CHAR_UNDEFINED);
            
            // Now do my own thing.
            dInputAutoUpdate(typedChar);
            dInput.selectAll();
        }
        else if (e.getSource() == input)
        {
            this.conditionalClearInput();

            // Make sure the dafault action doesn't change the text box.
            e.setKeyChar(KeyEvent.CHAR_UNDEFINED);

            // Now do my own thing.
            inputAutoUpdate(typedChar);
        }
        else // if (e.getSource() == canvas)
        {
            for (int i = 14; i >= 0; i--)
            {
                for (int k = 14; k >= 0; k--)
                {
                    if (selectX == i && selectY == k)
                    {
                        if (Character.isLetter(typedChar))
                        {
                            tileColors[i][k] = brickColor;
                            let[i][k] = typedChar;
                            if (Character.isUpperCase(typedChar))
                                letColors[i][k] = textColorWild;
                            else
                                letColors[i][k] = textColor;
                        }
                        else
                        {
                            tileColors[i][k] = baseColors[i][k];
                            let[i][k] = ' ';
                        }
                    }
                }
            }

            if (typedChar == '\b')
            {
                if (!lastD && selectY > 0) selectY--;
                if ( lastD && selectX > 0) selectX--;
            }
            else if (!lastD && selectY < 14) selectY++;
            else if ( lastD && selectX < 14) selectX++;

            super.repaint();
        }
    }




    
    
    
    








    @Override
    public void mousePressed(MouseEvent me)
    {
        if (me.getSource() == dInput || me.getSource() == input)
        {
            if (me.getSource() == input) conditionalClearInput();
            if (me.getSource() == dInput) dInput.selectAll();
            if (selectShowing)
            {
                WordFinder.kill();
                this.clearOldAnswer();
                this.saveCurrentGame();
                mainParent.save();
                WordFinder.update();
                selectShowing = false;
                super.repaint();
            }
        }
        else
        {
            WordFinder.kill();
            this.clearOldAnswer();
            selectX = Math.max(0, Math.min(14, me.getX()/(RECT_DIM + GAP)));
            selectY = Math.max(0, Math.min(14, me.getY()/(RECT_DIM + GAP)));
            selectShowing = true;
            super.requestFocus();
            super.repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent me)
    {
    }

    @Override
    public void mouseReleased(MouseEvent me)
    {
    }

    @Override
    public void mouseEntered(MouseEvent me)
    {
    }

    @Override
    public void mouseExited(MouseEvent me)
    {
    }
}