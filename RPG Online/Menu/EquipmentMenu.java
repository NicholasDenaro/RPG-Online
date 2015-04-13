import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;


public class EquipmentMenu extends Menu
{
	private Equipment equipment;
	private int cursor;
	
	public EquipmentMenu(String name,int i,int a, Equipment equip)
	{
		super(name,i,a);
		equipment=equip;
		cursor=0;
	}
	
	public void useItem()
	{
		Message mes=new Message(Message.EQUIP);
		mes.addByte(cursor);
		GameClient.client.addMessage(mes);
	}

	public void drawStuff(Graphics2D g)
	{
		g.setColor(Color.black);
		g.setFont(new Font("Courier New",Font.BOLD,20));
		g.drawString("Equips",14,14+16);
		Font f=new Font("Courier new",Font.PLAIN,12);
		g.setFont(f);
		int fheight=g.getFontMetrics().getHeight()/2+3;
		//item info
		if(cursor>=8)
			cursor=7;
		Item item=equipment.getEquip(cursor);
		if(item!=null)
			g.drawString(""+item.getName(),14,42+fheight);
		if(item instanceof Equip)
			drawEquipInfo(g,fheight,(Equip)item);
		
		//item scroll
		for(int i=0;i<8;i++)
		{
			if(equipment.getEquip(i)!=null)
				g.drawString(equipment.getEquip(i).getName(),14,126+(i+1)*fheight);
			else
				g.drawString("None",14,126+(i+1)*fheight);
		}
		g.drawRoundRect(12,126+(cursor)*fheight,120,fheight,4,4);
		//g.setFont(oldfont);
		//scroll bar
	}
	
	public void draw(Graphics2D g)
	{
		super.draw(g);
	}
	
	public void drawEquipInfo(Graphics2D g, int fheight, Equip equip)
	{
		g.drawString("lvl. "+equip.getLevel(),14,52+fheight);
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
		}
		if(ke.getKeyCode()==KeyEvent.VK_DOWN)
		{
			if(cursor<8)
				cursor++;
		}
		if(ke.getKeyCode()==KeyEvent.VK_I||ke.getKeyCode()==KeyEvent.VK_Z||ke.getKeyCode()==KeyEvent.VK_ESCAPE)
		{
			GamePanel.panel.setMenu(null);
		}
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			if(equipment.getEquip(cursor)!=null)
			{
				Item item=equipment.getEquip(cursor);
				if(item instanceof Equip)
					menu=new ConfirmationMenu(this,"ConfirmationMenu",x-32,y+61,"Unequip?","Yes","No");
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
