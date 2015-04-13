import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;


public class ItemMenu extends Menu
{
	private Inventory inventory;
	private int show;
	private int cursor;
	
	public ItemMenu(String name,int i,int a, Inventory inv)
	{
		super(name,i,a);
		inventory=inv;
		cursor=0;
		show=0;
	}
	
	public void useItem()
	{
		Message mes=new Message(Message.USEITEM);
		mes.addByte(cursor);
		GameClient.client.addMessage(mes);
		System.out.println("*Used and item*");
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
			Item item=inventory.get(cursor).getItem();
			g.drawString(""+inventory.get(cursor),14,42+fheight);
			if(item instanceof Equip)
				drawEquipInfo(g,fheight,(Equip)item);
			if(item instanceof UseItem)
				drawUseItemInfo(g,fheight,(UseItem)item);
			
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
	
	public void drawEquipInfo(Graphics2D g, int fheight, Equip equip)
	{
		g.drawString("lvl. "+equip.getLevel(),14,52+fheight);
	}
	
	public void drawUseItemInfo(Graphics2D g, int fheight, UseItem item)
	{
		ItemEffect[] effects=item.getEffects();
		for(int i=0;i<effects.length;i++)
			g.drawString(""+effects[i],14,52+fheight*(i+1));
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
		if(ke.getKeyCode()==KeyEvent.VK_B||ke.getKeyCode()==KeyEvent.VK_Z||ke.getKeyCode()==KeyEvent.VK_ESCAPE)
		{
			GamePanel.panel.setMenu(null);
		}
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			if(inventory.size()!=0)
			{
				Item item=inventory.get(cursor).getItem();
				if(item instanceof Equip)
					menu=new ConfirmationMenu(this,"ConfirmationMenu",x-32,y+61,"Equip?","Yes","No");
				if(item instanceof UseItem)
					menu=new ConfirmationMenu(this,"ConfirmationMenu",x-32,y+61,"Use?","Yes","No");
			}
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
