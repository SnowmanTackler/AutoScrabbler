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
    public static void main(String[] args)
    {
        new mainApplication();
    }

    public mainApplication()
    {
        super();
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);

        String myComputer = System.getProperty("user.name");

        if (myComputer.equals("SamSeifert"))
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

    @Override
    public String getSavedGames() {return "";}

    @Override
    public Container getContainer()
    {
        return this;
    }

    @Override
    public void setSize(Dimension d)
    {
        super.setSize(d);
        super.validate();
        super.setSize(d.width*2-this.getGlassPane().getWidth(),
                     d.height*2-this.getGlassPane().getHeight());
        super.setResizable(false);
    }

    @Override
    public void save(String savedString)
    {
        System.out.println("Saved: " + String.valueOf(System.currentTimeMillis()));
    }
}