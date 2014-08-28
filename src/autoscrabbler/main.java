package autoscrabbler;

/**
 * @author Samuel Seifert
 * autoscrabbler@gmail.com
 */

import java.awt.Container;
import java.awt.Dimension;

public interface main
{
    public String getSavedGames();
    public Container getContainer();
    public void setSize(Dimension d);
    public void save(String savedString);
}