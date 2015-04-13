import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;


public class LightLayer
{
	private BufferedImage darkness;
	private BufferedImage daylight;
	//private BufferedImage lights;
	private int width, height;
	
	public LightLayer(int w, int h)
	{
		width=w;
		height=h;
		darkness=new BufferedImage(width/*+32*/,height/*+32*/,BufferedImage.TYPE_INT_ARGB);
		//lights=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB); 
		try
		{
			daylight=ImageIO.read(getClass().getResourceAsStream("Daylight.png"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void setLighting(Color color, float val)
	{
		darkness=new BufferedImage(width/*+32*/,height/*+32*/,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=darkness.createGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,val));
		g.setColor(color);
		g.fillRect(0,0,width/*+32*/,height/*+32*/);
		//lights=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB); 
	}
	
	public void setLighting(GameTime time)
	{
		darkness=new BufferedImage(width/*+32*/,height/*+32*/,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=darkness.createGraphics();
		//g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
		Color color=new Color(daylight.getRGB(time.getHour(),0),true);
		Color color2=new Color(daylight.getRGB((time.getHour()+1)%24,0),true);
		g.setColor(mergeColor(color,color2,time.getMinute()/60.0));
		//g.setColor(color);
		g.fillRect(0,0,width+32,height+32);
	}
	
	public Color mergeColor(Color c1, Color c2, double val)
	{
		int red=(int)(c1.getRed()*(1-val)+c2.getRed()*val);
		int green=(int)(c1.getGreen()*(1-val)+c2.getGreen()*val);
		int blue=(int)(c1.getBlue()*(1-val)+c2.getBlue()*val);
		int alpha=(int)(c1.getAlpha()*(1-val)+c2.getAlpha()*val);
		return(new Color(red,green,blue,alpha));
	}
	
	/*public void addLight(double x, double y, Color color, double val, int distance, int iterations)
	{
		Graphics2D g=lights.createGraphics();

		g.setColor(color);
		int dist=distance/(iterations/2);
		for(int i=1;i<=iterations;i++)
		{
			Ellipse2D.Double circ=new Ellipse2D.Double();
			circ.x=x-(dist/2);
			circ.y=y-(dist/2);
			circ.width=dist;
			circ.height=dist;
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.XOR,(float)val/i));
			g.fill(circ);
			dist+=(distance-dist)/2;
		}
	}*/
	
	public void draw(Graphics2D g)
	{
		Graphics2D darkg=darkness.createGraphics();
		
		Composite oldcomp=g.getComposite();
		//g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,1.0f));
		//g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER,1.0f));
		g.drawImage(darkness,0/*-16*/,0/*-16*/,null);
		
		g.setComposite(oldcomp);
	}

}
