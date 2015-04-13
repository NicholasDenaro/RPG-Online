
public class TickingEntity extends Entity
{
	public TickingEntity(Zone z)
	{
		super(z);
	}
	
	public void beginTick()
	{
		xlast=x;
		ylast=y;
	}
	
	public void tick()
	{
		
	}
	
	public void endTick()
	{
		
	}
}
