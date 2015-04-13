import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Menu// implements KeyListener
{
	protected BufferedImage img;
	//private GamePanel panel;
	protected int x,y;
	protected Menu menu;
	protected boolean shouldScale;
	
	public Menu(/*GamePanel p, */String name, int i, int a)
	{
		//panel=p;
		x=i;
		y=a;
		menu=null;
		shouldScale=false;
		try
		{
			img=ImageIO.read(getClass().getResourceAsStream(name+".png"));
			//System.out.println("loaded image!");
		}
		catch(Exception ex)
		{
			System.out.println("Could not load menu image.");
			//ex.printStackTrace();
		}
	}
	
	public void addMenu(Menu m)
	{
		menu=m;
	}
	
	public void drawStuff(Graphics2D g)
	{
		
	}
	
	public void draw(Graphics2D g)
	{
		View v=GamePanel.panel.getView();
		
		if(img!=null)
		{
			BufferedImage stuff=new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_ARGB);
			Graphics2D gstuff=stuff.createGraphics();
			if(img!=null)
				gstuff.drawImage(img,0,0,null);
			
			//g.setColor(Color.black);
			
			drawStuff(gstuff);
			if(!shouldScale)
			{
				g.scale(1/v.getScale(),1/v.getScale());
				g.drawImage(stuff,(int)(x*v.getScale()),(int)(y*v.getScale()),null);
				g.scale(v.getScale(),v.getScale());
			}
			else
				g.drawImage(stuff,(int)(x),(int)(y),null);
		}
		if(menu!=null)
			menu.draw(g);
	}

	//@Override
	public void keyPressed(KeyEvent ke)
	{
		// TODO Auto-generated method stub
		
	}

	//@Override
	public void keyReleased(KeyEvent ke)
	{
		// TODO Auto-generated method stub
		
	}

	//@Override
	public void keyTyped(KeyEvent ke)
	{
		// TODO Auto-generated method stub
		
	}
}
