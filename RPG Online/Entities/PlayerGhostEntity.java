import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;


public class PlayerGhostEntity extends GhostEntity
{
	protected ServerClientHandler handler;
	
	protected Equipment equipment;
	
	public PlayerGhostEntity(Zone z,long id, int i, int a)
	{
		super(z,id,i,a);
		//sprite=new Sprite("Player",16,24);
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
	
	public void draw(Graphics2D g)
	{
		if(sprite!=null)
		{
			Composite co=g.getComposite();
			AlphaComposite ac=AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.85f);
			g.setComposite(ac);
			sprite.draw(g,x,y-12,imageIndex+(walk/8)%4);
			g.setComposite(co);
		}

		//zone.lights.addLight(x+8,y+4,Color.green,0.8,96,5);
	}
}
