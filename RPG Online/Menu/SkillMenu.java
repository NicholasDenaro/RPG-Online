import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;


public class SkillMenu extends Menu
{
	private PlayerEntity player;
	private SkillList skills;
	private int show;
	private int cursor;
	
	public SkillMenu(String name,int i,int a, PlayerEntity pl)
	{
		super(name,i,a);
		show=0;
		cursor=0;
		player=pl;
		skills=player.skills;
	}
	
	public void drawStuff(Graphics2D g)
	{
		g.setColor(Color.black);
		g.setFont(new Font("Courier New",Font.BOLD,20));
		g.drawString("Skills",14,14+16);
		Font f=new Font("Courier new",Font.PLAIN,12);
		g.setFont(f);
		int fheight=g.getFontMetrics().getHeight()/2+3;
		//item info
		if(cursor>=skills.size())
			cursor=skills.size()-1;
		if(skills.size()!=0)
		{
			Skill skill=skills.getSkillAtIndex(cursor);
			g.drawString(""+skill.getName(),14,42+fheight);
			g.drawString("Type: "+skill.getType(),14,42+fheight*2);
			PlayerEffect effect=skill.getEffect();
			if(effect!=null)
			{
				g.drawString("Effect: "+effect.toStringEffect(),14,42+fheight*3);
				if(effect.getId()==PlayerEffect.tumble)
				{
					g.drawString("Value: "+effect.value(),14,42+fheight*4);
				}
				else
				{
					g.drawString("Duration: "+effect.getHours()+":"+(effect.getMinutes()<10?"0":"")+effect.getMinutes()+":"+(effect.getSeconds()<10?"0":"")+effect.getSeconds(),14,42+fheight*4);
				}
			}
			else
			{
				g.drawString("Effect: None",14,42+fheight*3);
			}
			
			
			//item scroll
			for(int i=show;i<Math.min(skills.size(),show+9);i++)
			{
				g.drawString(skills.getSkillAtIndex(i).getName(),14,126+(i+1-show)*fheight);
			}
			g.drawRoundRect(12,126+(cursor-show)*fheight,120,fheight,4,4);
			//g.setFont(oldfont);
			//scroll bar
		}
	}
	
	public void draw(Graphics2D g)
	{
		super.draw(g);
	}
	
	public void keyPressed(KeyEvent ke)
	{
		if(ke.getKeyCode()==KeyEvent.VK_K||ke.getKeyCode()==KeyEvent.VK_Z||ke.getKeyCode()==KeyEvent.VK_ESCAPE)
		{
			GamePanel.panel.setMenu(null);
		}
		if(ke.getKeyCode()==KeyEvent.VK_UP)
		{
			if(cursor>0)
				cursor--;
			if(cursor<show)
				show--;
		}
		if(ke.getKeyCode()==KeyEvent.VK_DOWN)
		{
			if(cursor<skills.size())
				cursor++;
			if(cursor-show>=9)
				show++;
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_A)
		{
			if(player.skill_A==null||skills.getSkillAtIndex(cursor).getId()!=player.skill_A.getId())
				player.skill_A=skills.getSkillAtIndex(cursor);
			else
				player.skill_A=null;
		}
		if(ke.getKeyCode()==KeyEvent.VK_S)
		{
			if(player.skill_S==null||skills.getSkillAtIndex(cursor).getId()!=player.skill_A.getId())
				player.skill_S=skills.getSkillAtIndex(cursor);
			else
				player.skill_S=null;
		}
		if(ke.getKeyCode()==KeyEvent.VK_D)
		{
			if(player.skill_D==null||skills.getSkillAtIndex(cursor).getId()!=player.skill_A.getId())
				player.skill_D=skills.getSkillAtIndex(cursor);
			else
				player.skill_D=null;
		}
		if(ke.getKeyCode()==KeyEvent.VK_F)
		{
			if(player.skill_F==null||skills.getSkillAtIndex(cursor).getId()!=player.skill_A.getId())
				player.skill_F=skills.getSkillAtIndex(cursor);
			else
				player.skill_F=null;
		}
	}

	public void keyReleased(KeyEvent ke)
	{
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent ke)
	{
		// TODO Auto-generated method stub
		
	}
}
