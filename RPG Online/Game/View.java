
public class View
{
	private int x,y;
	private int width, height;
	private double scale;
	
	public View(int w, int h, double s)
	{
		width=w;
		height=h;
		scale=s;
	}
	
	public int getX()
	{
		return(x);
	}
	
	public int getY()
	{
		return(y);
	}
	
	public int getWidth()
	{
		return(width);
	}
	
	public int getHeight()
	{
		return(height);
	}
	
	public double getScale()
	{
		return(scale);
	}
}
