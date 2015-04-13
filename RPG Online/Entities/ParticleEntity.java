
public class ParticleEntity extends TickingEntity
{
	private int time;
	
	public ParticleEntity(Zone z, double i, double a, int t)
	{
		super(z);
		x=i;
		y=a;
		time=t;
	}
	
	
	public void tick()
	{
		time--;
		if(time<0)
		{
			zone.removeEntity(this);
		}
	}
	
}
