import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;


public class DamageParticle extends ParticleEntity
{
	private String damage;
	private Color color;

	public DamageParticle(Zone z, double i,double a,int t, String dmg, Color col)
	{
		super(z,i,a,t);
		damage=dmg;
		depth=99999;
		color=col;
	}
	
	
	public void tick()
	{
		super.tick();
		y-=0.5;
	}
	
	
	public void draw(Graphics2D g)
	{
		g.setColor(color);
		g.setFont(new Font("Courier New",Font.BOLD,8));
		g.drawString(damage,(int)x-g.getFontMetrics().stringWidth(damage)/2,(int)y);
	}
}
