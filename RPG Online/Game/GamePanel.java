import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JPanel;


public class GamePanel extends JPanel implements KeyListener
{
	public static GamePanel panel;
	private GameFrame frame;
	private View view;
	
	private int ticks;
	private int frames;
	
	protected Hud hud;
	
	private Menu menu;
	
	public GamePanel(GameFrame fr, View v)
	{
		panel=this;
		frame=fr;
		view=v;
		setPreferredSize(new Dimension((int)(view.getWidth()*view.getScale()),(int)(view.getHeight()*view.getScale())));
		addKeyListener(this);
		setFocusTraversalKeysEnabled(false);
		setFocusable(true);
		requestFocus();
		setBackground(Color.black);
		//setOpaque(false);//YOU MAY NEED THIS
		ticks=0;
		frames=0;
		
		menu=null;
	}
	
	public void setEngineInfo(int t, int f)
	{
		ticks=t;
		frames=f;
	}
	
	public void setMenu(Menu m)
	{
		menu=m;
	}
	
	public boolean hasMenu()
	{
		return(menu!=null);
	}
	
	public View getView()
	{
		return(view);
	}
	
	public void paintComponent(Graphics g1)
	{
		super.paintComponent(g1);
		Graphics2D g=(Graphics2D)g1;
		g.scale(view.getScale(),view.getScale());
		PlayerEntity player=frame.getPlayer();
		if(player!=null)
		{
			double xoff=-player.x-player.sprite.getWidth()/2+view.getWidth()/2;
			double yoff=-player.y-player.sprite.getHeight()/2+view.getHeight()/2;
			g.translate(xoff,yoff);
			frame.getPlayer().zone.draw(g);
			g.translate(-xoff,-yoff);
		}
		g.setColor(Color.white);
		g.drawString(ticks+", "+frames,0,32);
		
		if(menu!=null)
			menu.draw(g);
		
		g.scale(1/view.getScale(),1/view.getScale());
		
		if(hud!=null)
			hud.draw(g);
	}

	@Override
	public void keyPressed(KeyEvent ke)
	{
		if(menu!=null)
			menu.keyPressed(ke);
		else if(frame.getPlayer()!=null)
			frame.getPlayer().keyPressed(ke);
	}

	@Override
	public void keyReleased(KeyEvent ke)
	{
		if(menu!=null)
			menu.keyReleased(ke);
		else if(frame.getPlayer()!=null)
			frame.getPlayer().keyReleased(ke);
	}

	@Override
	public void keyTyped(KeyEvent ke)
	{
		// TODO Auto-generated method stub
		
	}
}
