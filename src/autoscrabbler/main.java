package autoscrabbler;

/**
 * @author Samuel Seifert
 * autoscrabbler@gmail.com
 */

import java.awt.Container;
import java.awt.Dimension;

/*
 * Because I wanted to make the program work with as an applet and an application,
 * this interface was required.
 */

public interface main
{
    public String getSavedGames();
    public Container getContainer();
    public void setSize(Dimension d);
    public void save(String savedString);
}