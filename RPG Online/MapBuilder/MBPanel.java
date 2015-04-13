import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class MBPanel extends JPanel implements MouseListener, MouseMotionListener
{
	public int width, height;
	public String name;
	public HashMap<Integer,int[][]> layers;
	public boolean[][] moveArea;
	
	public ZoneWarpPoint[][] warps;
	
	public ArrayList<String> commands;
	
	public MBPanel(int w, int h)
	{
		width=w;
		height=h;
		setOpaque(false);
		name="Default";
		addMouseListener(this);
		addMouseMotionListener(this);
		setPreferredSize(new Dimension(width*16,height*16));
		layers=new HashMap<Integer,int[][]>();
		layers.put(1,new int[width][height]);
		moveArea=new boolean[width][height];
		warps=new ZoneWarpPoint[width][height];
		commands=new ArrayList<String>();
	}
	
	public void changeSize(int w, int h)
	{
		int oldwidth=width;
		int oldheight=height;
		width=w;
		height=h;
		setPreferredSize(new Dimension(width*16,height*16));
		repaint();
		HashMap<Integer,int[][]> newlayers=new HashMap<Integer,int[][]>();
		Iterator<Integer> it=layers.keySet().iterator();
		while(it.hasNext())
		{
			int layer=it.next();
			int[][] newtiles=new int[width][height];
			
			for(int a=0;a<Math.min(height,oldheight);a++)
			{
				for(int i=0;i<Math.min(width,oldwidth);i++)
				{
					newtiles[i][a]=layers.get(layer)[i][a];
				}
			}
			newlayers.put(layer,newtiles);
		}
		
		boolean[][] newwalk=new boolean[width][height];
		ZoneWarpPoint[][] newwarps=new ZoneWarpPoint[width][height];
		for(int a=0;a<Math.min(height,oldheight);a++)
		{
			for(int i=0;i<Math.min(width,oldwidth);i++)
			{
				newwalk[i][a]=moveArea[i][a];
				newwarps[i][a]=warps[i][a];
			}
		}
		moveArea=newwalk;
		warps=newwarps;
		layers=newlayers;
	}
	
	public void createNew()
	{
		width=8;
		height=8;
		//tiles=new int[width][height];
		layers=new HashMap<Integer,int[][]>();
		layers.put(1,new int[width][height]);
		warps=new ZoneWarpPoint[width][height];
		commands=new ArrayList<String>();
		MBFrame.frame.options.list.clear();
		MBFrame.frame.options.list.addElement(1);
		repaint();
		
	}
	
	public void createNewLayer(int layer)
	{
		if(!layers.containsKey(layer))
		{
			layers.put(layer,new int[width][height]);
			MBFrame.frame.options.list.addElement(layer);
		}
	}
	
	public void changeLayer(int oldlayer, int layer)
	{
		if(!layers.containsKey(layer)&&layer!=oldlayer)
		{
			layers.put(layer,layers.get(oldlayer));
			MBFrame.frame.options.list.addElement(layer);
			removeLayer(oldlayer);
		}
	}
	
	public void removeLayer(int layer)
	{
		layers.remove(layer);
		MBFrame.frame.options.list.removeElement(layer);
	}
	
	public void clearLayers()
	{
		layers.clear();
		MBFrame.frame.options.list.clear();
	}
	
	public void setCommands(String text)
	{
		commands.clear();
		while(text.length()>0)
		{
			if(text.contains("\n"))
			{
				commands.add(text.substring(0,text.indexOf('\n')));
				text=text.substring(text.indexOf('\n')+1);
			}
			else
			{
				commands.add(text);
				text="";
			}
			
		}
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		SortedSet<Integer> keys=new TreeSet<Integer>(layers.keySet());
		
		Iterator<Integer> it=keys.iterator();
		while(it.hasNext())
		{
			int layer=it.next();
			for(int a=0;a<height;a++)
			{
				for(int i=0;i<width;i++)
				{
					if(layers.get(layer)[i][a]!=0)
						g.drawImage(MBFrame.frame.tileArray.getTile(layers.get(layer)[i][a]-1),i*16,a*16,null);
				}
			}
		}
		if(MBFrame.frame.options.movement.isSelected())
		{
			for(int a=0;a<moveArea[0].length;a++)
			{
				for(int i=0;i<moveArea.length;i++)
				{
					if(moveArea[i][a])//can't walk
						g.setColor(new Color(255,0,0,100));
					else
						g.setColor(new Color(0,255,0,100));
					g.fillRect(i*16,a*16,16,16);
						
				}
			}
		}
		if(MBFrame.frame.options.warps.isSelected())
		{
			for(int a=0;a<warps[0].length;a++)
			{
				for(int i=0;i<warps.length;i++)
				{
					ZoneWarpPoint warp=warps[i][a];
					if(warp!=null)
					{
						g.setColor(Color.cyan.brighter());
						g.fillOval(warp.x*16,warp.y*16,16,16);
						g.setColor(Color.black);
						g.drawString(""+warp.toZone,warp.x*16+2,warp.y*16+12);
					}
				}
			}
		}
		
		g.setColor(Color.black);
			
		for(int i=0;i<width;i++)
				g.drawLine(i*16,0,i*16,height*16);
			
		for(int a=0;a<height;a++)
			g.drawLine(0,a*16,width*16,a*16);
	}
	
	@Override
	public void mouseDragged(MouseEvent me)
	{
		int x=me.getX()/16;
		int y=me.getY()/16;
		if(x>=0&&x<width&&y>=0&&y<height)
		{
			if(MBFrame.frame.options.movement.isSelected())
			{
				boolean oldbool=moveArea[x][y];
				if(SwingUtilities.isLeftMouseButton(me))
					moveArea[x][y]=false;
				else if(SwingUtilities.isRightMouseButton(me))
					moveArea[x][y]=true;
				if(oldbool==moveArea[x][y])
					repaint();
			}
			else
			{
				if(MBFrame.frame.tileSelecter.index!=-1)
				{
					int layer=(int)MBFrame.frame.options.layerlist.getSelectedValue();
					int oldindex=layers.get(layer)[x][y];
					if(SwingUtilities.isLeftMouseButton(me))
						layers.get(layer)[x][y]=MBFrame.frame.tileSelecter.index+1;
					else if(SwingUtilities.isRightMouseButton(me))
						layers.get(layer)[x][y]=0;
					if(oldindex!=MBFrame.frame.tileSelecter.index+1)
						repaint();
				}
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent me)
	{
		
	}

	@Override
	public void mouseClicked(MouseEvent me)
	{
		int x=me.getX()/16;
		int y=me.getY()/16;
		if(x>=0&&x<width&&y>=0&&y<height)
		{
			if(MBFrame.frame.options.warps.isSelected())
			{
				if(me.getButton()==MouseEvent.BUTTON1)
				{
					int toZone=0,toX=0,toY=0;
					if(MBFrame.frame.panel.warps[x][y]!=null)
					{
						toZone=MBFrame.frame.panel.warps[x][y].toZone;
						toX=MBFrame.frame.panel.warps[x][y].tox;
						toY=MBFrame.frame.panel.warps[x][y].toy;
					}
					JOptionPane pane=new JOptionPane();
					JTextField ftoZone=new JTextField(""+toZone);
					JTextField ftoX=new JTextField(""+toX);
					JTextField ftoY=new JTextField(""+toY);
					pane.showMessageDialog(null,ftoZone,"zone",JOptionPane.QUESTION_MESSAGE);
					pane.showMessageDialog(null,ftoX,"to x",JOptionPane.QUESTION_MESSAGE);
					pane.showMessageDialog(null,ftoY,"to y",JOptionPane.QUESTION_MESSAGE);
					toZone=new Integer(ftoZone.getText());
					toX=new Integer(ftoX.getText());
					toY=new Integer(ftoY.getText());
					MBFrame.frame.panel.warps[x][y]=new ZoneWarpPoint(x,y,toZone,toX,toY);
					
				}
				else if(me.getButton()==MouseEvent.BUTTON3)
				{
					if(MBFrame.frame.panel.warps[x][y]!=null)
					{
						MBFrame.frame.panel.warps[x][y]=null;
					}
				}
				repaint();
			}
			else if(MBFrame.frame.options.movement.isSelected())
			{
				boolean oldbool=moveArea[x][y];
				if(SwingUtilities.isLeftMouseButton(me))
					moveArea[x][y]=false;
				else if(SwingUtilities.isRightMouseButton(me))
					moveArea[x][y]=true;
				if(oldbool!=moveArea[x][y])
					repaint();
			}
			else
			{
				if(MBFrame.frame.tileSelecter.index!=-1)
				{
					int layer=(int)MBFrame.frame.options.layerlist.getSelectedValue();
					int oldindex=layers.get(layer)[x][y];
					if(SwingUtilities.isLeftMouseButton(me))
						layers.get(layer)[x][y]=MBFrame.frame.tileSelecter.index+1;
					else if(SwingUtilities.isRightMouseButton(me))
						layers.get(layer)[x][y]=0;
					if(oldindex!=MBFrame.frame.tileSelecter.index+1)
						repaint();
				}
			}
		}
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
