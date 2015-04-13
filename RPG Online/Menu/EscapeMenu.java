import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.IOException;


public class EscapeMenu extends Menu
{
	private int cursor;
	
	public EscapeMenu(String name,int i,int a)
	{
		super(name,i,a);
		if(img!=null)
		{
			x-=img.getWidth()/4;
			y-=img.getHeight()/8;
		}
		cursor=0;
	}
	
	public void drawStuff(Graphics2D g)
	{
		g.setColor(Color.black);
		g.setFont(new Font("Courier New",Font.BOLD,20));
		g.drawString("Menu",14,14+16);
		Font f=new Font("Courier new",Font.PLAIN,12);
		g.setFont(f);
		int fheight=g.getFontMetrics().getHeight()/2+3;
		//item info
		if(cursor>=2)
			cursor=0;
		g.drawRect(12,48+(cursor-1)*fheight,136,fheight);
		g.drawString("Settings",14,48);
		g.drawString("Log off",14,48+fheight);
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
		}
		if(ke.getKeyCode()==KeyEvent.VK_DOWN)
		{
			if(cursor<2)
				cursor++;
		}
		if(ke.getKeyCode()==KeyEvent.VK_Z||ke.getKeyCode()==KeyEvent.VK_ESCAPE)
		{
			GamePanel.panel.setMenu(null);
		}
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			if(cursor==0)
			{
				//this is for settings
			}
			else if(cursor==1)
			{
				//this is for logout
				Message mes=new Message(Message.LOGOUT);
				GameClient.client.addMessage(mes);
				try
				{
					GameClient.client.flush();
				}
				catch(Exception ex)
				{
					//Couldn't send message, oh well. Close anyways =P
					ex.printStackTrace();
				}
				System.exit(0);
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
