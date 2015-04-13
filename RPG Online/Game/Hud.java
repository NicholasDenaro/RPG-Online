import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;


public class Hud
{
	private PlayerEntity player;
	private BufferedImage nameHud;
	private BufferedImage healthBar;
	private BufferedImage actionBar;
	private String name;
	private int bufferWidth;
	protected GameTime time;
	
	public Hud(PlayerEntity p, String n)
	{
		player=p;
		name=n;
		try
		{
			BufferedImage temp=ImageIO.read(getClass().getResourceAsStream("HealthBar.png"));
			Graphics2D g=temp.createGraphics();
			g.setFont(new Font("Courier New",Font.BOLD,12));
			int width=g.getFontMetrics().stringWidth("*");
			bufferWidth=n.length()*width;
			
			nameHud=new BufferedImage(temp.getWidth()+bufferWidth,temp.getHeight(),BufferedImage.TYPE_INT_ARGB);
			g=nameHud.createGraphics();
			g.drawImage(temp.getSubimage(0,0,1,20),0,0,null);
			for(int i=0;i<n.length();i++)
			{
				g.drawImage(temp.getSubimage(1,0,width,20),1+i*width,0,null);
			}
			g.drawImage(temp.getSubimage(10,0,182,20),1+bufferWidth,0,null);
			
			healthBar=new BufferedImage(temp.getWidth()+bufferWidth,temp.getHeight(),BufferedImage.TYPE_INT_ARGB);
			g=healthBar.createGraphics();
			g.drawImage(temp.getSubimage(10,20,182,20),1+n.length()*width,0,null);
			
			actionBar=ImageIO.read(getClass().getResourceAsStream("ActionBar.png"));
			
			
			time=new GameTime(null, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, 0);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void draw(Graphics2D g)
	{
		View view=GamePanel.panel.getView();
		
		g.drawImage(nameHud,0,0,null);
		g.setFont(new Font("Courier New",Font.BOLD,12));
		g.setColor(Color.black);
		g.drawString(name,2,12);
		
		PlayerStat maxHealth=player.stats.getStat(PlayerStat.maxhealth);
		PlayerStat health=player.stats.getStat(PlayerStat.health);
		if(maxHealth!=null&&health!=null)
		{
			g.setFont(new Font("Courier New",Font.BOLD,8));
			int off=g.getFontMetrics().stringWidth(""+maxHealth.value());
			String drawhp="";
			String hp=""+health.value();
			String mhp=""+maxHealth.value();
			for(int i=0;i<mhp.length()-hp.length();i++)
				drawhp+=" ";
			drawhp+=hp+"/"+mhp;
			g.drawString(drawhp,bufferWidth+80+32-off,10+8);
		}
		
		g.drawString("Lvl."+player.stats.getStat(PlayerStat.level).value(),bufferWidth+148,10+8);
		
		if(maxHealth!=null&&health!=null)
		{
			if(health.value()>0)
			{
				BufferedImage tempHealth=new BufferedImage(healthBar.getWidth(),healthBar.getHeight(),BufferedImage.TYPE_INT_ARGB);
				Graphics2D tempg=tempHealth.createGraphics();
				tempg.drawImage(healthBar,0,0,null);
				AlphaComposite ac=AlphaComposite.getInstance(AlphaComposite.SRC_IN,0.95f);
				tempg.setComposite(ac);
				if(health.value()*1.0/maxHealth.value()>=0.43)
					tempg.setColor(Color.green);
				else if(health.value()*1.0/maxHealth.value()>=0.10)
					tempg.setColor(Color.yellow);
				else
					tempg.setColor(Color.red);
				tempg.fillRect(0,0,healthBar.getWidth(),20);
				
				
				g.drawImage(tempHealth.getSubimage(1+bufferWidth,0,Math.max(4,180*health.value()/maxHealth.value()),20),1+bufferWidth,0,null);
			}
		}
		int hunger=(int)(player.stats.getStat(PlayerStat.hunger).value()*100.0/PlayerStatus.FOODMAX);
		
		g.setColor(Color.white);
		g.fillRect(0,16,80,32);
		
		g.setFont(new Font("Courier New",Font.PLAIN,12));
		g.setColor(Color.black);
		g.drawString("Hunger: "+hunger,2,26);
		if(player.stats.getStat(PlayerStat.ap)!=null)
			g.drawString("AP: "+player.stats.currentAp+"/"+player.stats.getStat(PlayerStat.ap).value(),2,26+12);
		
		g.drawImage(actionBar,(int)(view.getWidth()*view.getScale())/2-actionBar.getWidth()/2,(int)(view.getHeight()*view.getScale())-actionBar.getHeight(),null);
		if(player.skill_A!=null)
		{
			Skill skill=player.skill_A;
			g.drawImage(Skill.getImage(skill.getId()),(int)(view.getWidth()*view.getScale())/2-actionBar.getWidth()/2,(int)(view.getHeight()*view.getScale())-actionBar.getHeight(),null);
			if(!skill.correctEquip(player.equipment))
			{
				g.setColor(new Color(0,0,0,150));
				g.fillRoundRect((int)(view.getWidth()*view.getScale())/2-actionBar.getWidth()/2,(int)(view.getHeight()*view.getScale())-actionBar.getHeight(),31,31,8,8);
			}
			else if(player.stats.currentAp<skill.cost)
			{
				g.setColor(new Color(255,0,0,100));
				g.fillRoundRect((int)(view.getWidth()*view.getScale())/2-actionBar.getWidth()/2,(int)(view.getHeight()*view.getScale())-actionBar.getHeight(),31,31,8,8);
			}
			if(skill.cooldown>0)
			{
				g.setColor(new Color(0,0,0,100));
				int yoff=(int)((1-skill.cooldown*1.0/skill.getMaxCooldown())*31);
				g.fillRoundRect((int)(view.getWidth()*view.getScale())/2-actionBar.getWidth()/2,(int)(view.getHeight()*view.getScale())-actionBar.getHeight()+yoff,31,31-yoff,8,8);
			}
			
		}
		if(player.skill_S!=null)
		{
			Skill skill=player.skill_S;
			g.drawImage(Skill.getImage(skill.getId()),31+(int)(view.getWidth()*view.getScale())/2-actionBar.getWidth()/2,(int)(view.getHeight()*view.getScale())-actionBar.getHeight(),null);
			if(!skill.correctEquip(player.equipment))
			{
				g.setColor(new Color(0,0,0,150));
				g.fillRoundRect(31+(int)(view.getWidth()*view.getScale())/2-actionBar.getWidth()/2,(int)(view.getHeight()*view.getScale())-actionBar.getHeight(),31,31,8,8);
			}
			else if(player.stats.currentAp<skill.cost)
			{
				g.setColor(new Color(255,0,0,100));
				g.fillRoundRect(31+(int)(view.getWidth()*view.getScale())/2-actionBar.getWidth()/2,(int)(view.getHeight()*view.getScale())-actionBar.getHeight(),31,31,8,8);
			}
			if(skill.cooldown>0)
			{
				g.setColor(new Color(0,0,0,100));
				int yoff=(int)((1-skill.cooldown*1.0/skill.getMaxCooldown())*31);
				g.fillRoundRect(31+(int)(view.getWidth()*view.getScale())/2-actionBar.getWidth()/2,(int)(view.getHeight()*view.getScale())-actionBar.getHeight()+yoff,31,31-yoff,8,8);
			}
			
		}
		if(player.skill_D!=null)
		{
			Skill skill=player.skill_D;
			g.drawImage(Skill.getImage(skill.getId()),62+(int)(view.getWidth()*view.getScale())/2-actionBar.getWidth()/2,(int)(view.getHeight()*view.getScale())-actionBar.getHeight(),null);
			if(!skill.correctEquip(player.equipment))
			{
				g.setColor(new Color(0,0,0,150));
				g.fillRoundRect(62+(int)(view.getWidth()*view.getScale())/2-actionBar.getWidth()/2,(int)(view.getHeight()*view.getScale())-actionBar.getHeight(),31,31,8,8);
			}
			else if(player.stats.currentAp<skill.cost)
			{
				g.setColor(new Color(255,0,0,100));
				g.fillRoundRect(62+(int)(view.getWidth()*view.getScale())/2-actionBar.getWidth()/2,(int)(view.getHeight()*view.getScale())-actionBar.getHeight(),31,31,8,8);
			}
			if(skill.cooldown>0)
			{
				g.setColor(new Color(0,0,0,100));
				int yoff=(int)((1-skill.cooldown*1.0/skill.getMaxCooldown())*31);
				g.fillRoundRect(62+(int)(view.getWidth()*view.getScale())/2-actionBar.getWidth()/2,(int)(view.getHeight()*view.getScale())-actionBar.getHeight()+yoff,31,31-yoff,8,8);
			}
			
		}
		if(player.skill_F!=null)
		{
			Skill skill=player.skill_F;
			g.drawImage(Skill.getImage(skill.getId()),93+(int)(view.getWidth()*view.getScale())/2-actionBar.getWidth()/2,(int)(view.getHeight()*view.getScale())-actionBar.getHeight(),null);
			if(!skill.correctEquip(player.equipment))
			{
				g.setColor(new Color(0,0,0,150));
				g.fillRoundRect(93+(int)(view.getWidth()*view.getScale())/2-actionBar.getWidth()/2,(int)(view.getHeight()*view.getScale())-actionBar.getHeight(),31,31,8,8);
			}
			else if(player.stats.currentAp<skill.cost)
			{
				g.setColor(new Color(255,0,0,100));
				g.fillRoundRect(93+(int)(view.getWidth()*view.getScale())/2-actionBar.getWidth()/2,(int)(view.getHeight()*view.getScale())-actionBar.getHeight(),31,31,8,8);
			}
			if(skill.cooldown>0)
			{
				g.setColor(new Color(0,0,0,100));
				int yoff=(int)((1-skill.cooldown*1.0/skill.getMaxCooldown())*31);
				g.fillRoundRect(93+(int)(view.getWidth()*view.getScale())/2-actionBar.getWidth()/2,(int)(view.getHeight()*view.getScale())-actionBar.getHeight()+yoff,31,31-yoff,8,8);
			}
			
		}
		
		if(time!=null)
		{
			byte minute=time.getMinute();
			byte hour=time.getHour();
			byte second=time.getSecond();
			g.setFont(new Font("Courier New",Font.PLAIN,12));
			g.setColor(Color.white);
			g.fillRect(0,(int)(view.getHeight()*view.getScale())-16,60,16);
			g.setColor(Color.black);
			g.drawString((hour<10?" ":"")+hour+":"+(minute<10?"0":"")+minute+":"+(second<10?"0":"")+second,2,(int)(view.getHeight()*view.getScale()));
		}
	}
}
