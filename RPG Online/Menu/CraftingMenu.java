import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;


public class CraftingMenu extends Menu
{
	private Inventory inventory;
	private int show;
	private int cursor;
	private Inventory selected;
	
	public CraftingMenu(String name,int i,int a, Inventory inv)
	{
		super(name,i,a);
		inventory=inv.clone();
		cursor=0;
		show=0;
		selected=new Inventory();
	}
	
	public void craftItem()
	{
		Message mes=new Message(Message.CRAFTITEM);
		mes.addInt(selected.size());
		for(int i=0;i<selected.size();i++)
		{
			Item item=selected.get(i).getItem();
			mes.addInt(item.getId());
			mes.addInt(selected.get(i).count());
			if(item instanceof Weapon)
			{
				Weapon wep=(Weapon)item;
				mes.addString(wep.getParametersAsString());
				mes.addBoolean(wep.isAppraised());
			}
		}
		GameClient.client.addMessage(mes);
	}

	public void drawStuff(Graphics2D g)
	{
		g.setColor(Color.black);
		g.setFont(new Font("Courier New",Font.BOLD,20));
		g.drawString("Items",14,14+16);
		Font f=new Font("Courier new",Font.PLAIN,12);
		g.setFont(f);
		int fheight=g.getFontMetrics().getHeight()/2+3;
		//item info
		if(cursor>=inventory.size())
			cursor=inventory.size()-1;
		if(inventory.size()!=0)
		{
			//Item item=inventory.get(cursor).getItem();
			//g.drawString(""+inventory.get(cursor),14,42+fheight);
			for(int i=0;i<selected.size();i++)
			{
				g.drawString(selected.get(i).getItem().getName()+"x"+selected.get(i).count(),14,42+fheight*(i+1));
			}
			
			
			//item scroll
			for(int i=show;i<Math.min(inventory.size(),show+9);i++)
			{
				g.drawString(inventory.get(i).getItem().getName(),14,126+(i+1-show)*fheight);
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
			if(cursor<inventory.size()-1)
				cursor++;
			if(cursor-show>=9)
				show++;
		}
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			if(selected.size()>0)
			{
				while(selected.size()>0)
				{
					inventory.add(selected.remove(0,1));
				}
			}
			else
				GamePanel.panel.setMenu(null);
		}
		if(ke.getKeyCode()==KeyEvent.VK_ESCAPE)
		{
			GamePanel.panel.setMenu(null);
		}
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			if(inventory.size()!=0)
			{
				//if(!selected.contains(cursor))
				{
					selected.add(inventory.remove(cursor,1));
				}
			}
		}
		if(ke.getKeyCode()==KeyEvent.VK_ENTER)
		{
			craftItem();
			GamePanel.panel.setMenu(null);
		}
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
