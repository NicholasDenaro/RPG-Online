import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;


public class QuestMenu extends Menu
{
	private QuestList quests;
	private int show;
	private int cursor;
	
	public QuestMenu(String name,int i,int a, QuestList que)
	{
		super(name,i,a);
		quests=que;
		cursor=0;
		show=0;
	}

	public void drawStuff(Graphics2D g)
	{
		g.setColor(Color.black);
		g.setFont(new Font("Courier New",Font.BOLD,20));
		g.drawString("Quests",14,14+16);
		Font f=new Font("Courier new",Font.PLAIN,12);
		g.setFont(f);
		int fheight=g.getFontMetrics().getHeight()/2+3;
		//item info
		if(cursor>=quests.size())
			cursor=quests.size()-1;
		if(quests.size()!=0)
		{
			Quest quest=quests.get(cursor);
			g.drawString(""+quest.getName(),14,42+fheight);
			g.drawString("Objectives:",14,42+fheight*2);
			for(int i=0;i<quest.getObjectives().size();i++)
			{
				QuestObjective objective=quest.getObjectives().get(i);
				g.drawString(""+objective,14,42+fheight*(3+i));
			}
			
			//quest scroll
			for(int i=show;i<Math.min(quests.size(),show+9);i++)
			{
				g.drawString(quests.get(i).getName(),14,126+(i+1-show)*fheight);
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
		if(menu!=null)
		{
			menu.keyPressed(ke);
			return;
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
			if(cursor<quests.size()-1)
				cursor++;
			if(cursor-show>=9)
				show++;
		}
		if(ke.getKeyCode()==KeyEvent.VK_L||ke.getKeyCode()==KeyEvent.VK_Z||ke.getKeyCode()==KeyEvent.VK_ESCAPE)
		{
			GamePanel.panel.setMenu(null);
		}
		/*if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			if(quests.size()!=0)
			{
				Item item=inventory.get(cursor).getItem();
				if(item instanceof Equip)
					menu=new ConfirmationMenu(this,"ConfirmationMenu",x-32,y+61,"Equip?","Yes","No");
				if(item instanceof UseItem)
					menu=new ConfirmationMenu(this,"ConfirmationMenu",x-32,y+61,"Use?","Yes","No");
			}
		}*/
	}

	public void keyReleased(KeyEvent ke)
	{
		if(menu!=null)
		{
			menu.keyReleased(ke);
			return;
		}
	}

	public void keyTyped(KeyEvent ke)
	{
		if(menu!=null)
		{
			menu.keyTyped(ke);
			return;
		}
	}
}
