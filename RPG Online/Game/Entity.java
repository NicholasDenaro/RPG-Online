import java.awt.Graphics2D;
import java.awt.Rectangle;


public class Entity
{
	protected double x,y;
	protected double xlast, ylast;
	protected double xspeed, yspeed;
	protected Sprite sprite;
	protected int imageIndex;
	protected int depth;
	protected Zone zone;
	
	protected Rectangle.Double mask;
	
	public Entity(Zone z)
	{
		zone=z;
		mask=new Rectangle.Double(x,y,16,16);
		depth=3;
	}
	
	public int distance(Entity other)
	{
		return((int)(Math.abs(x/16-other.x/16)+Math.abs(y/16-other.y/16)));
	}
	
	public byte direction(Entity other)
	{
		double dir=Math.toDegrees(Math.atan2(y-other.y,other.x-x))+360;
		dir%=360;
		//System.out.println("dir: "+dir);
		
		if(dir<=45||dir>=315)
			return(EAST);
		else if(dir<=135)
			return(NORTH);
		else if(dir<=225)
			return(WEST);
		else
			return(SOUTH);
	}
	
	public void setDepth(int d)
	{
		zone.changeDepth(this,depth,d);
		depth=d;
	}
	
	public void draw(Graphics2D g)
	{
		
	}
	
	public static final byte NORTH=0;
	public static final byte EAST=1;
	public static final byte SOUTH=2;
	public static final byte WEST=3;
}
