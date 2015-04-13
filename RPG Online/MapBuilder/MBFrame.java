import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;


public class MBFrame extends JFrame
{
	public static MBFrame frame;
	public MBPanel panel;
	public MBTileSelecter tileSelecter;
	public MBOptionPane options;
	public MBMenuBar menuBar;
	public TileArray tileArray;
	
	public MBFrame()
	{
		super("Frame");
		frame=this;
		tileArray=new TileArray("Tiles");
		panel=new MBPanel(8,8);
		tileSelecter=new MBTileSelecter();
		options=new MBOptionPane();
		menuBar=new MBMenuBar();
		
		getContentPane().add(panel);
		
		makeFrame();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		setResizable(false);
	}
	
	public void makeFrame()
	{
		Container c=getContentPane();
		c.setMinimumSize(new Dimension(640,480));
		GridBagLayout gbl=new GridBagLayout();
		c.setLayout(gbl);
		GridBagConstraints gbc=new GridBagConstraints();
		
		gbc.fill=gbc.BOTH;
		gbc.ipadx=0;
		gbc.ipady=0;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.weighty=1;
		
		gbc.gridx=0;
		gbc.gridy=0;
		JScrollPane scroll=new JScrollPane(tileSelecter);
		scroll.setPreferredSize(new Dimension(128,-1));
		c.add(scroll,gbc);
		//scroll.revalidate();
		
		gbc.gridx=0;
		gbc.gridy=1;
		c.add(options,gbc);
		
		gbc.gridx=1;
		gbc.gridy=0;
		gbc.weightx=1;
		gbc.gridheight=2;
		scroll=new JScrollPane(panel);
		scroll.setPreferredSize(new Dimension(512,480));
		c.add(scroll,gbc);
	}
	
	public static void main(String args[])
	{
		MBFrame fr=new MBFrame();
		frame.panel.repaint();
	}
}
