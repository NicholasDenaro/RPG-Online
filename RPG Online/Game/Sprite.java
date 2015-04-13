import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;


public class Sprite
{
	protected String name;
	private BufferedImage image;
	private int width, height;
	
	public Sprite(String n, int w, int h)
	{
		name=n;
		try
		{
			image=ImageIO.read(getClass().getResourceAsStream(n+".png"));
		}
		catch(Exception ex)
		{
			System.out.println("Could not load sprite image");
			//ex.printStackTrace();
		}
		width=w;
		height=h;
	}
	
	public int getWidth()
	{
		return(width);
	}
	
	public int getHeight()
	{
		return(height);
	}
	
	public void draw(Graphics2D g, double x, double y, int sub)
	{
		if(image!=null)
		//	g.drawImage(image.getSubimage((sub%(image.getWidth()/width))*width,(sub/(image.getWidth()/width))*height,width,height),(int)x,(int)y,null);
		{
			AffineTransform at=new AffineTransform();
			at.translate(x,y);
			g.drawImage(image.getSubimage((sub%(image.getWidth()/width))*width,(sub/(image.getWidth()/width))*height,width,height),at,null);
		}
	}
}
