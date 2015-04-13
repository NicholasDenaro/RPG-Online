import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;


public class ConfirmationMenu extends Menu
{
	private Menu parent;
	private String dialog;
	private String action1, action2;
	private boolean cursor;
	
	public ConfirmationMenu(Menu par, String name,int i,int a,String diag, String ac1, String ac2)
	{
		super(name,i,a);
		parent=par;
		dialog=diag;
		action1=ac1;
		action2=ac2;
		cursor=true;
	}
	
	public void drawStuff(Graphics2D g)
	{
		g.setColor(Color.cyan.brighter().brighter().brighter());
		g.fillRoundRect(0,0,32,32,8,8);
		g.setColor(Color.black);		
		g.drawRoundRect(0,0,32,32,8,8);
		
		Font oldfont=g.getFont();
		g.setFont(new Font("Courier new",Font.PLAIN,6));
		
		g.drawString(dialog,2,10);
		
		g.drawString(action1,2,26);
		g.drawString(action2,20,26);
		
		g.drawRoundRect((cursor?0:1)*16,16,16,16,4,4);
		
		g.setFont(oldfont);
	}
	
	public void draw(Graphics2D g)
	{
		super.draw(g);
	}
	
	public void keyPressed(KeyEvent ke)
	{
		if(ke.getKeyCode()==KeyEvent.VK_LEFT)
		{
			cursor=true;
		}
		if(ke.getKeyCode()==KeyEvent.VK_RIGHT)
		{
			cursor=false;
		}
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			if(parent instanceof ItemMenu)
			{
				if(cursor)
					((ItemMenu)parent).useItem();
				parent.menu=null;
			}
			
			if(parent instanceof EquipmentMenu)
			{
				if(cursor)
					((EquipmentMenu)parent).useItem();
				parent.menu=null;
			}
		}
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			if(parent instanceof ItemMenu)
			{
				parent.menu=null;
			}
			if(parent instanceof EquipmentMenu)
			{
				parent.menu=null;
			}
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
