import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;


public class NPCGhostEntity extends GhostEntity
{
	private String name;
	
	public NPCGhostEntity(Zone z,long id,int i,int a, String n)
	{
		super(z,id,i,a);
		x=i;
		y=a;
		xtrue=(short)(i/16);
		ytrue=(short)(a/16);
		name=n;
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
			sprite.draw(g,x,y-12,imageIndex+(walk/8)%4);
			g.setFont(new Font("Courier New",Font.BOLD,8));
			g.setColor(Color.white);
			g.drawString("NPC",(int)x+8-g.getFontMetrics().stringWidth("NPC")/2,(int)y-12);
		}
	}
	
}
