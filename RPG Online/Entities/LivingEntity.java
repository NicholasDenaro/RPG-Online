import java.awt.Point;


public class LivingEntity extends TickingEntity
{
	protected long uuid;
	
	protected long targetId;
	
	protected short xtrue, ytrue;
	
	protected PlayerStatus stats;
	protected PlayerConditions conditions;
	
	protected byte direction;
	
	public LivingEntity(Zone z, long id)
	{
		super(z);
		uuid=id;
		targetId=-1;
		direction=SOUTH;
		conditions=new PlayerConditions(this);
	}
	
	public int distance(LivingEntity other)
	{
		return(Math.abs(xtrue-other.xtrue)+Math.abs(ytrue-other.ytrue));
	}
	
	public byte direction(LivingEntity other)
	{
		double dir=Math.toDegrees(Math.atan2(ytrue-other.ytrue,other.xtrue-xtrue))+360;
		dir%=360;
		
		if(dir<=45||dir>=315)
			return(EAST);
		else if(dir<=135)
			return(NORTH);
		else if(dir<=225)
			return(WEST);
		else
			return(SOUTH);
	}
	
	public Point getFront()
	{
		if(direction==NORTH)
			return(new Point(xtrue,ytrue-1));
		else if(direction==SOUTH)
			return(new Point(xtrue,ytrue+1));
		else if(direction==EAST)
			return(new Point(xtrue+1,ytrue));
		else
			return(new Point(xtrue-1,ytrue));
	}
	
	public boolean isMoving()
	{
		return(xspeed!=0||yspeed!=0);
	}
	
	public void addEffect(PlayerEffect effect)
	{
		conditions.addEffect(effect);
	}
	
	public void removeEffect(int index)
	{
		conditions.remove(index);
	}
}
