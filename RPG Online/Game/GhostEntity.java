import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;


public class GhostEntity extends LivingEntity
{
	public static final int MOVEBUFF=8;
	
	protected LinkedList<GhostAction> actions;
	protected short walk;
	protected byte move;
	protected int lastmes;
	//protected short xtrue, ytrue;
	
	public GhostEntity(Zone z,long id, int i, int a)
	{
		super(z,id);
		actions=new LinkedList<GhostAction>();
		walk=0;
		move=0;
		x=i;
		y=a;
		xtrue=(short)(i/16);
		ytrue=(short)(a/16);
	}
	
	public void addMovement(GhostAction act)
	{
		actions.addLast(act);
		if(act.walk)
		{
			if(act.xspeed<0)
				xtrue--;
			else if(act.xspeed>0)
				xtrue++;
			
			if(act.yspeed<0)
				ytrue--;
			else if(act.yspeed>0)
				ytrue++;
			setDepth(ytrue);
		}
	}
	
	public void setCoords()
	{
		x=xtrue*16;
		y=ytrue*16;
	}
	
	public void clearMovements()
	{
		actions.clear();
	}
	
	public short getX()
	{
		return(xtrue);
	}
	
	public short getY()
	{
		return(ytrue);
	}
	
	public void tick()
	{
		
		if(actions.size()!=0)
		{
			if(move<MOVEBUFF)
				move++;
		}
		else
			move=0;
		
		if(move==MOVEBUFF)
		{
			if(actions.peekFirst().walk)
			{
				xspeed=actions.peekFirst().xspeed;
				yspeed=actions.peekFirst().yspeed;
				walk++;
				if(walk==33)
				{
					xspeed=0;
					yspeed=0;
					walk=0;
					actions.pollFirst();
				}
			}
			else
			{
				imageIndex=actions.peekFirst().imageIndex;
				actions.pollFirst();
			}
		}
		
		x+=xspeed;
		y+=yspeed;
	}
	
	public void draw(Graphics2D g)
	{
		g.fillRect(xtrue,ytrue,16,16);
	}
}
