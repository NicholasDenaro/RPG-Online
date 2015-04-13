import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;


public class FixDatabase
{
	public static void main(String args[])
	{
		ServerDatabase database=new ServerDatabase(null,"jdbc:mysql://localhost:3306/RPGOnlineDB","root","asdfasdf");
		database.logoutAll();
		System.out.println("Fixed logins");
	}
}
