package autoscrabbler;

/**
 * @author Samuel Seifert
 * autoscrabbler@gmail.com
 */

import SamuelSeifert.CookieController;
import java.awt.Container;
import javax.swing.JApplet;

public class mainAppletCookie extends JApplet implements main
{
    // HTML Cookie name
    private String cookieName = "SavedGames";

    // HTML SAVE COOKIE METHODS
    public String getCookieName() { return cookieName; }
    public void dispose() { System.exit(0); }


    // Make sure I'm on my website, then start initializing crap!
    @Override
    public void init()
    {
        mainParent.myStart(this);
    }

    // Read and return saved cookie
    @Override
    public String getSavedGames()
    {
        CookieController.setApplet(this);
        return CookieController.readCookie(cookieName);
    }

    // So mainparent knows where to draw.
    @Override
    public Container getContainer()
    {
        return this;
    }
    
    // SAVES GAMES, savePermCookie required because cookies won't stick in some browsers (namely safari)
    @Override
    public void save(String savedString)
    {
        CookieController.writeCookie(cookieName, savedString);
        CookieController.callJavaScriptMethod("savePermCookie");
    }
}