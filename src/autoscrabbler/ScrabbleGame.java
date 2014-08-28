package autoscrabbler;

/**
 * @author Samuel Seifert
 * autoscrabbler@gmail.com
 */

import SamuelSeifert.FontLoader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import objectdraw.FilledRect;
import objectdraw.FramedRect;
import objectdraw.JDrawingCanvas;
import objectdraw.Location;
import objectdraw.Text;

public class ScrabbleGame implements ActionListener, MouseListener, KeyListener {

    // Scrabble Board
    public static int VOFFSET=0, HOFFSET=0, GAP=3, RECT_DIM=25;
    public static Color brickColor    = new Color(238, 217, 168),
                        textColor     = new Color(20, 20, 20),
                        textColorWild = Color.GRAY,
                        answerColor   = Color.WHITE,
                        tileColor     = new Color (220,220,220);

    private Text messageText[] = new Text[2];
    public FilledRect tile[][] = new FilledRect[15][15];
    public Text let[][]        = new Text[15][15];
    private Color baseColor[][] = new Color[15][15];

    // User Interface
    public JTextField input        = new JTextField();
    private JTextField dInput      = new JTextField();
    private  JDrawingCanvas canvas = new JDrawingCanvas();
    private JCheckBox boardChecks[] = new JCheckBox[5],
                      gameTypeChecks[] = new JCheckBox[2],
                      dictChecks[] = new JCheckBox[Dictionary.dictNames.length];
    private JButton reset = new JButton("Clear"),
                    submit = new JButton("Play");
    private Color barColor = Color.WHITE;


    // Writing
    private FramedRect select1, select2, select3;
    private boolean lastD = true; // Horizontal is true, vertical is false

    // Switch Boards
    public static final String gameTypes[] = {"Classic Scrabble", "Words With Friends"};

    private static final String dInputStart = "",//Green is good, red is bad...",
                                inputStart = "Enter \"?\" for wilds!";


    public ScrabbleGame(Container contentPane)
    {
        if (!FontLoader.isMainFontSet()) FontLoader.setMainFont("FontBoard.ttf", 18);
        if (!FontLoader.isSecondaryFontSet()) FontLoader.setSecondaryFont("FontMessage.ttf", 13);

        dInput.setFont(FontLoader.getSecondaryFont());
        input.setFont(FontLoader.getSecondaryFont());
        JLabel tempLabel;

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
            dictChecks[i] = new JCheckBox(Dictionary.dictNames[i]);
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

        Component comp[] = mainRight.getComponents();
        for (int i = 0; i < comp.length; i++)
        {
            ((JComponent)comp[i]).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        mainRight.validate();

        contentPane.setLayout(new BorderLayout());
        contentPane.add(canvas, BorderLayout.CENTER);
        contentPane.add(mainRight, BorderLayout.EAST);
        contentPane.validate();

        for(int i = 0; i<15; i++)
            for(int k = 0; k<15; k++)
            {
                baseColor[i][k] = tileColor;
                tile[i][k] = new FilledRect(HOFFSET + (RECT_DIM + GAP)*k, VOFFSET + (RECT_DIM+GAP)*i, RECT_DIM, RECT_DIM, canvas);
                let[i][k] = new Text("", 0, 0, canvas);
                let[i][k].setFont(FontLoader.getMainFont());
                let[i][k].setColor(Color.WHITE);
                center(i, k);
            }

        messageText[0] = new Text("", 0, 0, canvas);
        messageText[0].setFont(FontLoader.getSecondaryFont());
        messageText[0].moveTo(0, 5 + 15*(RECT_DIM + GAP));
        messageText[1] = new Text("", 0, 0, canvas);
        messageText[1].setFont(FontLoader.getSecondaryFont());
        messageText[1].moveTo(0, messageText[0].getY() + messageText[0].getHeight() + GAP*2);

        select1 = new FramedRect(tile[7][7].getX() + 0, tile[7][7].getY() + 0, RECT_DIM - 1, RECT_DIM - 1, canvas);
        select2 = new FramedRect(tile[7][7].getX() + 1, tile[7][7].getY() + 1, RECT_DIM - 3, RECT_DIM - 3, canvas);
        select3 = new FramedRect(tile[7][7].getX() + 2, tile[7][7].getY() + 2, RECT_DIM - 5, RECT_DIM - 5, canvas);

        select1.hide(); select2.hide(); select3.hide();

        this.centerGameText();
    }











    private void resetGame()
    {
        WordFinder.killWithoutClear();
        boolean noOldAnswer = true;

	for(int i = 0; i < 15; i++)
	{
            for(int k = 0; k < 15; k++)
            {
                if (tile[i][k].getColor().equals(answerColor))
                {
                    noOldAnswer = false;
                    tile[i][k].setColor(baseColor[i][k]);
                    let[i][k].setText(" ");
                    center(i, k);
                }
            }
	}

        if (noOldAnswer)
        {
            for(int i = 0; i < 15; i++)
            {
                for(int k = 0; k < 15; k++)
                {
                    tile[i][k].setColor(baseColor[i][k]);
                    let[i][k].setText(" ");
                    center(i, k);
                }
            }
        }
        if (!input.getText().equals(inputStart)) input.setText("");
    }

    public void clearOldAnswer() {
        for(int i = 0; i < 15; i++)
	{
	    for(int k = 0; k < 15; k++)
            {
                if (tile[i][k].getColor().equals(answerColor))
                {
                    tile[i][k].setColor(baseColor[i][k]);
                    let[i][k].setText(" ");
                    center(i, k);
                }
            }
        }
    }

    private void submitAnswer()
    {
        WordFinder.killWithoutClear();
        for(int i = 0; i < 15; i++)
	{
	    for(int k = 0; k < 15; k++)
            {
                if (tile[i][k].getColor().equals(answerColor))
                {
                    tile[i][k].setColor(brickColor);

                    if (let[i][k].getColor().equals(textColorWild)) input.setText(input.getText().replaceFirst("\\?", ""));
                    else input.setText(input.getText().replaceFirst(let[i][k].getText(), ""));
                }
            }
        }
    }

    public void newBestAnswer(int x, int y, boolean isHorizontal, String word) {
        clearOldAnswer();

        for (int p = 0; p < word.length(); p++)
        {
            int a = 0, b = 0;
            if (isHorizontal) {b = p; }
            else              {a = p; }

            if (let[x+a][y+b].getText().equals(" "))
            {
                tile[x+a][y+b].setColor(answerColor);
                Character current = new Character(word.charAt(p));

                if (Character.isUpperCase(current)) { let[x+a][y+b].setColor(textColorWild); }
                else                                { let[x+a][y+b].setColor(textColor); }

                let[x+a][y+b].setText(""+current);
                center(x+a, y+b);
            }
        }
    }












    // Set Board Bonus's
    private void setBoardBaseColor()
    {
        for(int i = 0; i<15; i++)
            for(int k = 0; k<15; k++)
                baseColor[i][k] = tileColor;

        if (gameTypeChecks[0].isSelected())
        {
            // Orange: Triple Word
            baseColor[0][0] = Color.ORANGE;             baseColor[0][14] = Color.ORANGE;
            baseColor[14][14] = Color.ORANGE;           baseColor[14][0] = Color.ORANGE;
            baseColor[0][7] = Color.ORANGE;		baseColor[7][14] = Color.ORANGE;
            baseColor[7][0] = Color.ORANGE;		baseColor[14][7] = Color.ORANGE;

            // Red: Double Word
            baseColor[1][1] = Color.RED;		baseColor[2][2] = Color.RED;
            baseColor[3][3] = Color.RED;		baseColor[4][4] = Color.RED;
            baseColor[10][10] = Color.RED;		baseColor[11][11] = Color.RED;
            baseColor[12][12] = Color.RED;		baseColor[13][13] = Color.RED;
            baseColor[10][4] = Color.RED;		baseColor[11][3] = Color.RED;
            baseColor[12][2] = Color.RED;		baseColor[13][1] = Color.RED;
            baseColor[4][10] = Color.RED;		baseColor[3][11] = Color.RED;
            baseColor[2][12] = Color.RED;		baseColor[1][13] = Color.RED;
            baseColor[7][7] = Color.RED;

            // Blue: Double Letter
            baseColor[0][3] = Color.BLUE;		baseColor[14][11] = Color.BLUE;
            baseColor[3][0] = Color.BLUE;		baseColor[11][14] = Color.BLUE;
            baseColor[0][11] = Color.BLUE;		baseColor[3][14] = Color.BLUE;
            baseColor[11][0] = Color.BLUE;		baseColor[14][3] = Color.BLUE;
            baseColor[7][3] = Color.BLUE;		baseColor[7][11] = Color.BLUE;
            baseColor[3][7] = Color.BLUE;		baseColor[11][7] = Color.BLUE;
            baseColor[6][2] = Color.BLUE;		baseColor[8][2] = Color.BLUE;
            baseColor[2][6] = Color.BLUE;		baseColor[2][8] = Color.BLUE;
            baseColor[6][12] = Color.BLUE;		baseColor[8][12] = Color.BLUE;
            baseColor[12][6] = Color.BLUE;		baseColor[12][8] = Color.BLUE;
            baseColor[6][6] = Color.BLUE;		baseColor[6][8] = Color.BLUE;
            baseColor[8][8] = Color.BLUE;		baseColor[8][6] = Color.BLUE;

            // Green: Triple Letter
            baseColor[1][5] = Color.GREEN;		baseColor[1][9] = Color.GREEN;
            baseColor[5][1] = Color.GREEN;		baseColor[9][1] = Color.GREEN;
            baseColor[13][5] = Color.GREEN;		baseColor[13][9] = Color.GREEN;
            baseColor[5][13] = Color.GREEN;		baseColor[9][13] = Color.GREEN;
            baseColor[5][5] = Color.GREEN;		baseColor[5][9] = Color.GREEN;
            baseColor[9][9] = Color.GREEN;		baseColor[9][5] = Color.GREEN;
        }
        else if (gameTypeChecks[1].isSelected())
        {
            // Orange: Triple Word
            baseColor[0][3] = Color.ORANGE;             baseColor[3][0] = Color.ORANGE;
            baseColor[0][11] = Color.ORANGE;            baseColor[11][0] = Color.ORANGE;
            baseColor[14][3] = Color.ORANGE;		baseColor[3][14] = Color.ORANGE;
            baseColor[14][11] = Color.ORANGE;		baseColor[11][14] = Color.ORANGE;

            // Red: Double Word
            baseColor[7][3] = Color.RED;		baseColor[3][7] = Color.RED;
            baseColor[7][11] = Color.RED;		baseColor[11][7] = Color.RED;
            baseColor[1][5] = Color.RED;		baseColor[5][1] = Color.RED;
            baseColor[1][9] = Color.RED;		baseColor[9][1] = Color.RED;
            baseColor[13][5] = Color.RED;		baseColor[5][13] = Color.RED;
            baseColor[13][9] = Color.RED;		baseColor[9][13] = Color.RED;
            baseColor[7][7] = new Color(254 , 0 , 0);

            // Blue: Double Letter
            baseColor[2][1] = Color.BLUE;		baseColor[1][2] = Color.BLUE;
            baseColor[2][4] = Color.BLUE;		baseColor[4][2] = Color.BLUE;
            baseColor[1][12] = Color.BLUE;		baseColor[12][1] = Color.BLUE;
            baseColor[2][13] = Color.BLUE;		baseColor[13][2] = Color.BLUE;
            baseColor[2][10] = Color.BLUE;		baseColor[10][2] = Color.BLUE;
            baseColor[4][12] = Color.BLUE;		baseColor[12][4] = Color.BLUE;
            baseColor[4][6] = Color.BLUE;		baseColor[6][4] = Color.BLUE;
            baseColor[4][8] = Color.BLUE;		baseColor[8][4] = Color.BLUE;
            baseColor[8][10] = Color.BLUE;		baseColor[10][8] = Color.BLUE;
            baseColor[12][13] = Color.BLUE;		baseColor[13][12] = Color.BLUE;
            baseColor[12][10] = Color.BLUE;		baseColor[10][12] = Color.BLUE;
            baseColor[6][10] = Color.BLUE;		baseColor[10][6] = Color.BLUE;

            // Green: Triple Letter
            baseColor[0][6] = Color.GREEN;		baseColor[6][0] = Color.GREEN;
            baseColor[0][8] = Color.GREEN;		baseColor[8][0] = Color.GREEN;
            baseColor[3][3] = Color.GREEN;		baseColor[5][5] = Color.GREEN;
            baseColor[11][11] = Color.GREEN;		baseColor[9][9] = Color.GREEN;
            baseColor[3][11] = Color.GREEN;		baseColor[11][3] = Color.GREEN;
            baseColor[5][9] = Color.GREEN;		baseColor[9][5] = Color.GREEN;
            baseColor[14][6] = Color.GREEN;		baseColor[8][14] = Color.GREEN;
            baseColor[14][8] = Color.GREEN;		baseColor[6][14] = Color.GREEN;
        }
        else
            System.err.println(this.getClass().getSimpleName() + " – " + "can't find color scheme for selected game type");
    }

    // Centers a given letter
    private void center(int i, int k) {
	let[i][k].moveTo(tile[i][k].getX() + RECT_DIM/2 - let[i][k].getWidth()/2,
                         tile[i][k].getY() + RECT_DIM/2 - let[i][k].getHeight()/2);
    }

    public void setGameText(String top, String bottom) {
        messageText[0].setText(top);
        messageText[1].setText(bottom);
        centerGameText();
    }

    private void centerGameText()
    {
        messageText[0].moveTo(messageText[0].getCanvas().getWidth()/2 - messageText[0].getWidth()/2, messageText[0].getY());
        messageText[1].moveTo(messageText[1].getCanvas().getWidth()/2 - messageText[1].getWidth()/2, messageText[1].getY());
    }










    private void saveCurrentGame()
    {
        saveCurrentGame(this.getCurrentGameIndex(-1));
    }

    private void saveCurrentGame(int index)
    {
        String temp = "";

        // Save board type
        for (int i = 0; i < gameTypeChecks.length; i++) if (gameTypeChecks[i].isSelected()) temp += String.valueOf(i);
        if (temp.length() == 0) temp += "0";

        // Save dictionary type
        for (int i = 0; i < dictChecks.length; i++) if (dictChecks[i].isSelected()) temp += String.valueOf(i);
        if (temp.length() == 1) temp += "0";

        for(int i = 0; i < 15; i++)
            for(int k = 0; k < 15; k++)
		temp = temp + let[i][k].getText();

        temp = temp.replaceAll(" ", "9");

        mainParent.games[index] = temp;
    }

    public void setGameState(String saved)
    {
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
                    tile[i][k].setColor(brickColor);
                    if (Character.isUpperCase(temp)) { let[i][k].setColor(textColorWild); } else { let[i][k].setColor(textColor); }
                    let[i][k].setText(temp);
                }
                else
                {
                    tile[i][k].setColor(baseColor[i][k]);
                    let[i][k].setColor(textColor);
                    let[i][k].setText(" ");
                }
		center(i, k);
            }
	}

        this.selectGameTypeCheck(zero);
        if (!gameTypeChecks[1].isSelected()) this.selectDictCheck(one);
    }


















    // GUI
    public void startListening()
    {
        reset.addActionListener(this);
        submit.addActionListener(this);

        canvas.addMouseListener(this);
        input.addMouseListener(this);
        dInput.addMouseListener(this);

        canvas.addKeyListener(this);
        dInput.addKeyListener(this);
        input.addKeyListener(this);

        for (int i = 0; i < boardChecks.length; i++)
            boardChecks[i].addActionListener(this);
        for (int i = 0; i < gameTypeChecks.length; i++)
            gameTypeChecks[i].addActionListener(this);
        for (int i = 0; i < dictChecks.length; i++)
            dictChecks[i].addActionListener(this);
    }

    public void dInputAutoUpdate(Character typedChar)
    {
        String newInput = dInput.getText();

        if(!(typedChar == '\b')) { newInput = newInput + typedChar; }

        char newGuess[] = newInput.toLowerCase().toCharArray();
        newInput = "";

        for (int i = 0; i < newGuess.length; i++)
            if (Character.isLetter(newGuess[i]) || newGuess[i] == '?')
                newInput = newInput + newGuess[i];

        dInput.setText(newInput);

        if (newInput.length() < 2 || newInput.length() > 15)    { dInput.setSelectedTextColor(Color.RED); }
        else if (Dictionary.isValid(newInput))                  { dInput.setSelectedTextColor(Color.GREEN); }
        else                                                    { dInput.setSelectedTextColor(Color.RED); }
    }

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

    public void conditionalClearInput() {
        if (input.getText().equals(inputStart)) input.setText("");
    }











    private int getCurrentGameIndex(int selected)
    {
        for (int i = 0; i < boardChecks.length; i++) if (i != selected && boardChecks[i].isSelected()) return i;
        return selected;
    }

    public String getDictType()
    {
        for (int i = 0; i < dictChecks.length; i++) if (dictChecks[i].isSelected()) return dictChecks[i].getText();
        return null;
    }












    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == submit)
        {
            select1.hide(); select2.hide(); select3.hide();
            WordFinder.killWithoutClear();
            this.submitAnswer();
            this.conditionalClearInput();
            this.saveCurrentGame();
            mainParent.save();
            WordFinder.update();
        }
        else if (source == reset)
        {
            this.resetGame();
            this.saveCurrentGame();
            mainParent.save();
        }
        else
        {
            WordFinder.kill();

            for (int i = 0; i < boardChecks.length; i++) if (source == boardChecks[i]) this.selectBoardCheck(i);
            for (int i = 0; i < gameTypeChecks.length; i++) if (source == gameTypeChecks[i]) this.selectGameTypeCheck(i);
            for (int i = 0; i < dictChecks.length; i++) if (source == dictChecks[i]) this.selectDictCheck(i);

            this.saveCurrentGame();
            mainParent.save();
        }
    }

    public void selectBoardCheck(int select)
    {
        input.setText(inputStart);
        if (select >= boardChecks.length) select = 0;
        if (select != this.getCurrentGameIndex(select))
        {
            this.saveCurrentGame(this.getCurrentGameIndex(select));
            this.setGameState(mainParent.games[select]);
        }
        for (int j = 0; j < boardChecks.length; j++) boardChecks[j].setSelected(select == j);
    }

    public void selectGameTypeCheck(int select)
    {
        input.setText(inputStart);
        if (select >= gameTypeChecks.length) select = 0;
        for (int j = 0; j < gameTypeChecks.length; j++) gameTypeChecks[j].setSelected(select == j);
        if (select == 1)
        {
            this.selectDictCheck(2);
            for (int j = 0; j < dictChecks.length; j++) dictChecks[j].setEnabled(false);
        }
        else for (int j = 0; j < dictChecks.length; j++) dictChecks[j].setEnabled(true);

        this.setBoardBaseColor();
        for(int i = 0; i<15; i++)
            for(int k = 0; k<15; k++)
                if (!tile[i][k].getColor().equals(brickColor)) tile[i][k].setColor(baseColor[i][k]);
    }

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
    public void keyPressed(KeyEvent e) {
        int x = 0, y = 0;

        if (e.getKeyCode() == KeyEvent.VK_UP)		{ lastD = false; if (select1.getY() > tile[0][0].getY())   {y = -(RECT_DIM + GAP); }}
        else if (e.getKeyCode() == KeyEvent.VK_DOWN)	{ lastD = false; if (select1.getY() < tile[14][14].getY()) {y = (RECT_DIM + GAP); }}
        else if (e.getKeyCode() == KeyEvent.VK_LEFT)	{ lastD = true;  if (select1.getX() > tile[0][0].getX())   {x = -(RECT_DIM + GAP); }}
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)	{ lastD = true;  if (select1.getX() < tile[14][14].getX()) {x = (RECT_DIM + GAP); }}

        select1.move(x, y);
        select2.move(x, y);
        select3.move(x, y);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        Character typedChar = new Character(e.getKeyChar());

        if (e.getSource() == dInput)
        {
            e.setKeyChar(KeyEvent.CHAR_UNDEFINED);
            dInputAutoUpdate(typedChar);
            dInput.selectAll();
        }
        else if (e.getSource() == input)
        {
            this.conditionalClearInput();
            e.setKeyChar(KeyEvent.CHAR_UNDEFINED);
            inputAutoUpdate(typedChar);
        }

        else
        {
            for (int i = 14; i >= 0; i--)
            {
                for (int k = 14; k >= 0; k--)
                {
                    if (select1.getY() == tile[i][k].getY() && select1.getX() == tile[i][k].getX())
                    {
                        if (Character.isLetter(typedChar))
                        {
                            tile[i][k].setColor(brickColor);
                            let[i][k].setText(typedChar);
                            if (Character.isUpperCase(typedChar)) {  let[i][k].setColor(textColorWild); }
                            else                                  {  let[i][k].setColor(textColor); }
                        }
                        else
                        {
                            tile[i][k].setColor(baseColor[i][k]);
                            let[i][k].setText(" ");
                        }
                        center(i, k);
                    }
                }
            }

            int x = 0, y = 0;

            if (typedChar == '\b')
            {
                if (!lastD && select1.getY() > tile[0][0].getY()) { y = -(RECT_DIM + GAP); }
                if ( lastD && select1.getX() > tile[0][0].getX()) { x = -(RECT_DIM + GAP); }
            }
            else if (!lastD && select3.getY()+1 < tile[14][14].getY()) { y = (RECT_DIM + GAP); }
            else if ( lastD && select3.getX()+1 < tile[14][14].getX()) { x = (RECT_DIM + GAP); }

            select1.move(x, y);
            select2.move(x, y);
            select3.move(x, y);
        }
    }












    @Override
    public void mousePressed(MouseEvent me) {
        if (me.getSource() == dInput || me.getSource() == input)
        {
            if (me.getSource() == input) conditionalClearInput();
            if (me.getSource() == dInput) dInput.selectAll();
            if (!select1.isHidden())
            {
                WordFinder.kill();
                this.saveCurrentGame();
                mainParent.save();
                WordFinder.update();
            }
            select1.hide(); select2.hide(); select3.hide();
        }
        else
        {
            Location point = new Location(me.getX(), me.getY());
            WordFinder.kill();

            for(int i = 0; i < 15; i++)
            {
                for(int k = 0; k < 15; k++)
                {
                    if (tile[i][k].contains(point))
                    {
                        select1.moveTo(tile[i][k].getX() + 0, tile[i][k].getY() + 0);
                        select2.moveTo(tile[i][k].getX() + 1, tile[i][k].getY() + 1);
                        select3.moveTo(tile[i][k].getX() + 2, tile[i][k].getY() + 2);
                        select1.show();
                        select2.show();
                        select3.show();
                        canvas.requestFocus();
                        return;
                    }
                }
            }
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