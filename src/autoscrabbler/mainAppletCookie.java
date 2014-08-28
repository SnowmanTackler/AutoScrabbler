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
    private String cookieName = "SavedGames";

    // HTML SAVE COOKIE METHODS
    public String getCookieName() { return cookieName; }
    public void dispose() { System.exit(0); }


    @Override
    public void init()
    {
        String domainName = "";
        domainName = this.getClass().getResource("").toString();
        if (domainName.contains("autoscrabbler.com"))
            mainParent.myStart(this);
    }

    @Override
    public String getSavedGames()
    {
        CookieController.setApplet(this);
        return CookieController.readCookie(cookieName);
    }

    @Override
    public Container getContainer()
    {
        return this;
    }

    @Override
    public void save(String savedString)
    {
        CookieController.writeCookie(cookieName, savedString);
        CookieController.callJavaScriptMethod("savePermCookie");
    }
}