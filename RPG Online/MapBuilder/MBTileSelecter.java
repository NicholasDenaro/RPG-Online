import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;


public class MBTileSelecter extends JPanel implements MouseListener
{
	public int index;
	
	public MBTileSelecter()
	{
		index=-1;
		setPreferredSize(new Dimension(MBFrame.frame.tileArray.getWidth(),MBFrame.frame.tileArray.getHeight()));
		addMouseListener(this);
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		for(int a=0;a<MBFrame.frame.tileArray.getHeight()/16;a++)
		{
			for(int i=0;i<MBFrame.frame.tileArray.getWidth()/16;i++)
			{
				g.drawImage(MBFrame.frame.tileArray.getTile(i,a),i*16,a*16,null);
			}
		}
		g.setColor(Color.cyan);
		
		int w=MBFrame.frame.tileArray.getWidth()/16;
		
		if(index!=-1)
			g.drawRect((index%w)*16,(index/w)*16,16,16);
	}
	
	@Override
	public void mouseClicked(MouseEvent me)
	{
		index=me.getX()/16+(me.getY()/16)*(MBFrame.frame.tileArray.getWidth()/16);
		//System.out.println("index: "+index);
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
	
}
