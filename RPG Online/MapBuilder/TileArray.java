import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;;


public class TileArray
{
	public static TileArray tileArray;
	
	private BufferedImage array;
	
	public TileArray(String fname)
	{
		tileArray=this;
		try
		{
			array=ImageIO.read(getClass().getResourceAsStream("Tiles/"+fname+".png"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public int size()
	{
		return((array.getWidth()/16)*(array.getHeight()/16));
	}
	
	public int getWidth()
	{
		return(array.getWidth());
	}
	
	public int getHeight()
	{
		return(array.getHeight());
	}
	
	public Image getTile(int i)
	{
		if(i>=0)
			return(array.getSubimage((i%(array.getWidth()/16)*16),(i/(array.getWidth()/16))*16,16,16));
		else
			return(null);
	}
	
	public Image getTile(int i, int a)
	{
		return(array.getSubimage(i*16,a*16,16,16));
	}
}
