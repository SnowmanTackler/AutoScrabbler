package autoscrabbler;

/**
 * @author Samuel Seifert
 * autoscrabbler@gmail.com
 */

import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class mainApplication extends JFrame implements main
{
    // Entry point
    public static void main(String[] args)
    {
        mainApplication ma = new mainApplication();
    }

    // Creates a frame, and if security check, starts program.
    public mainApplication()
    {                 
        super();
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
              
        String myComputer = System.getProperty("user.name");

        /*
        System.out.println("A:" + ((int)'A'));
        System.out.println("B:" + ((int)'B'));
        System.out.println("Y:" + ((int)'Y'));
        System.out.println("Z:" + ((int)'Z'));
        System.out.println("a:" + ((int)'a'));
        System.out.println("b:" + ((int)'b'));
        System.out.println("y:" + ((int)'y'));
        System.out.println("z:" + ((int)'z'));
        */
        
        // MUHAHA ONLY I CAN RUN THIS CLASS...UNLESS your reading this...
        if (myComputer.equals("Sam"))
        {
            this.setVisible(true);
            mainParent.myStart(this);
        }
        else
        {
            this.setSize(new Dimension(400, 200));
            JOptionPane.showMessageDialog(this, "Don't take my things.");
            System.exit(0);
        }
    }

    
    
    // Who doesn't like this method!
    @Override
    public Container getContainer()
    {
        return this;
    }

    // Sets size of GLASS PANE to dimension. Adjusts with control bar height
    @Override
    public void setSize(Dimension d)
    {
        super.setSize(d);
        super.validate();
        super.setSize(d.width*2-this.getGlassPane().getWidth(),
                     d.height*2-this.getGlassPane().getHeight());
        super.setResizable(false);
    }

    // Could add a file based save system.
    @Override
    public String getSavedGames() {return "";}
    
    @Override
    public void save(String savedString)
    {
        System.out.println("Saved: " + String.valueOf(System.currentTimeMillis()));
    }
}