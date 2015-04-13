import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;


public class MonsterGhostEntity extends GhostEntity
{
	private double skilltimer=0;

	public MonsterGhostEntity(Zone z,long id,int i,int a)
	{
		super(z,id,i,a);
		x=i;
		y=a;
		xtrue=(short)(i/16);
		ytrue=(short)(a/16);
	}
	
	public void tick()
	{
		super.tick();
		if(yspeed<0)
			imageIndex=4;
		if(yspeed>0)
			imageIndex=0;
		if(xspeed<0)
			imageIndex=8;
		if(xspeed>0)
			imageIndex=12;
	}
	
	public void endTick()
	{
		skilltimer++;
		if(skilltimer>=60.0/24)
		{
			skilltimer-=60.0/24;
			conditions.checkEffects();
		}
	}
	
	public void draw(Graphics2D g)
	{
		if(sprite!=null)
			sprite.draw(g,x,y-12,imageIndex+(walk/8)%4);
		if(stats!=null)
		{
			int yoffset=0;
			switch(sprite.name)
			{
				case "Slime":
					yoffset=4;
				break;
			}
			g.setColor(Color.black);
			g.fillRect((int)x,(int)y-yoffset,16,3);
			g.setColor(Color.red);
			g.fillRect((int)x+1,(int)y-yoffset+1,(int)Math.max(1,stats.getStat(PlayerStat.health).value()*1.0/stats.getStat(PlayerStat.maxhealth).value()*14),1);
			
		}
		if(conditions.contains(PlayerEffect.stun))
		{
			PlayerEffect effect=conditions.getEffect(PlayerEffect.stun);
			int yoffset=sprite.getHeight()/2-3;
			g.setColor(Color.orange);
			g.fillRect((int)x,(int)y-yoffset,(int)(effect.percentLeft()*16),3);
		}
		if(conditions.contains(PlayerEffect.paralysis))
		{
			PlayerEffect effect=conditions.getEffect(PlayerEffect.paralysis);
			int yoffset=sprite.getHeight()/2-3;
			g.setColor(Color.yellow);
			g.fillRect((int)x,(int)y-yoffset,(int)(effect.percentLeft()*16),3);
		}
		if(conditions.contains(PlayerEffect.delay))
		{
			PlayerEffect effect=conditions.getEffect(PlayerEffect.delay);
			int yoffset=sprite.getHeight()/2-3;
			g.setColor(Color.blue);
			g.fillRect((int)x,(int)y-yoffset,(int)(effect.percentLeft()*16),3);
		}
	}
}
