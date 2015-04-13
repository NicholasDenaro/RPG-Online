
public class GhostAction
{
	protected boolean walk;
	protected double xspeed, yspeed;
	protected int imageIndex;
	
	public GhostAction(double xs, double ys)
	{
		walk=true;
		xspeed=xs;
		yspeed=ys;
	}
	
	public GhostAction(int index)
	{
		walk=false;
		imageIndex=index;
	}
}
