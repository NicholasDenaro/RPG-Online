import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;


public class MBMenuBar implements ActionListener
{
	public MBMenuBar()
	{
		JMenuBar bar=new JMenuBar();
		
		JMenu file=new JMenu("File");
		JMenuItem item=new JMenuItem("New");
		item.addActionListener(this);
		file.add(item);
		item=new JMenuItem("Load");
		item.addActionListener(this);
		file.add(item);
		item=new JMenuItem("Save");
		item.addActionListener(this);
		file.add(item);
		bar.add(file);
		
		file=new JMenu("Settings");
		item=new JMenuItem("Name");
		item.addActionListener(this);
		file.add(item);
		item=new JMenuItem("Size");
		item.addActionListener(this);
		file.add(item);
		item=new JMenuItem("New Layer");
		item.addActionListener(this);
		file.add(item);
		item=new JMenuItem("Commands");
		item.addActionListener(this);
		file.add(item);
		item=new JMenuItem("Change Layer");
		item.addActionListener(this);
		file.add(item);
		item=new JMenuItem("Remove Layer");
		item.addActionListener(this);
		file.add(item);
		bar.add(file);
		
		MBFrame.frame.setJMenuBar(bar);
	}

	@Override
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() instanceof JMenuItem)
		{
			JMenuItem item=(JMenuItem)ae.getSource();
			if(item.getText().equals("New"))
			{
				MBPanel panel=MBFrame.frame.panel;
				panel.createNew();
			}
			else if(item.getText().equals("Load"))
			{
				try
				{
					MBPanel panel=MBFrame.frame.panel;
					
					//String loc=new File(getClass().getResource("Default/").toURI()).toString();
					String loc=new File("Resources/Default/").toString();
					
					JFileChooser fChooser=new JFileChooser(loc);
					
					fChooser.setVisible(true);
					
					fChooser.showOpenDialog(MBFrame.frame);
					
					File f=fChooser.getSelectedFile();
					
					if(f==null)
						return;
						
					panel.commands.clear();
					
					panel.createNew();
					
					panel.clearLayers();
					
					MBFrame.frame.options.nameField.setText(f.getName().substring(0,f.getName().indexOf('.')));
					panel.name=f.getName().substring(0,f.getName().indexOf('.'));
					
					BufferedInputStream fin=new BufferedInputStream(new FileInputStream(f));
					
					byte[] bytes=new byte[8];
					fin.read(bytes);
					ByteBuffer buffer=ByteBuffer.wrap(bytes);
					
					int width=buffer.getInt();
					int height=buffer.getInt();
					
					//panel.width=width;
					//panel.height=height;
					
					panel.changeSize(width,height);
					
					//move area
					bytes=new byte[width*height];
					fin.read(bytes);
					buffer=ByteBuffer.wrap(bytes);
					
					for(int a=0;a<height;a++)
					{
						for(int i=0;i<width;i++)
						{
							panel.moveArea[i][a]=buffer.get()==1;
						}
					}
					
					//layers
					bytes=new byte[4];
					fin.read(bytes);
					buffer=ByteBuffer.wrap(bytes);
					int layers=buffer.getInt();
					
					bytes=new byte[layers*(4+width*height*4)];
					fin.read(bytes);
					buffer=ByteBuffer.wrap(bytes);
					
					for(int l=0;l<layers;l++)
					{
						int layer=buffer.getInt();
						panel.createNewLayer(layer);
						for(int a=0;a<height;a++)
						{
							for(int i=0;i<width;i++)
							{
								panel.layers.get(layer)[i][a]=buffer.getInt();
							}
						}
					}
					
					//warps
					bytes=new byte[4];
					fin.read(bytes);
					buffer=ByteBuffer.wrap(bytes);
					int warps=buffer.getInt();
					
					bytes=new byte[warps*10];
					fin.read(bytes);
					buffer=ByteBuffer.wrap(bytes);
					
					for(int i=0;i<warps;i++)
					{
						short x=buffer.getShort();
						short y=buffer.getShort();
						panel.warps[x][y]=new ZoneWarpPoint(x,y,buffer.getShort(),buffer.getShort(),buffer.getShort());
					}
					
					//commands
					CustomInputStream in=new CustomInputStream(fin);
					in.read();
					byte c=in.readByte();
					for(int i=0;i<c;i++)
					{
						String comm=in.readString();
						panel.commands.add(comm);
					}
					
					panel.repaint();
					fin.close();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			else if(item.getText().equals("Save"))
			{
				MBPanel panel=MBFrame.frame.panel;
				ByteArrayOutputStream out=new ByteArrayOutputStream();
				
				try
				{
					//size
					ByteBuffer buffer=ByteBuffer.allocate(8);
					buffer.putInt(panel.width);
					buffer.putInt(panel.height);
					out.write(buffer.array());
					
					//movement
					buffer=ByteBuffer.allocate(panel.width*panel.height);
					for(int a=0;a<panel.height;a++)
					{
						for(int i=0;i<panel.width;i++)
						{
							buffer.put((byte)(panel.moveArea[i][a]?1:0));
						}
					}
					out.write(buffer.array());
					
					//layers
					int layers=panel.layers.size();
					buffer=ByteBuffer.allocate(4+panel.width*panel.height*4*layers+layers*4);
					Iterator<Integer> it=panel.layers.keySet().iterator();
					buffer.putInt(layers);
					while(it.hasNext())
					{
						int layer=it.next();
						buffer.putInt(layer);
						for(int a=0;a<panel.height;a++)
						{
							for(int i=0;i<panel.width;i++)
							{
								buffer.putInt(panel.layers.get(layer)[i][a]);
							}
						}
					}
					out.write(buffer.array());
					
					//warps
					int warpCount=0;
					for(int a=0;a<panel.warps[0].length;a++)
					{
						for(int i=0;i<panel.warps.length;i++)
						{
							if(panel.warps[i][a]!=null)
								warpCount++;
						}
					}
					buffer=ByteBuffer.allocate(4+warpCount*10);
					buffer.putInt(warpCount);
					for(int a=0;a<panel.height;a++)
					{
						for(int i=0;i<panel.width;i++)
						{
							if(panel.warps[i][a]!=null)
							{
								buffer.putShort(panel.warps[i][a].x);
								buffer.putShort(panel.warps[i][a].y);
								buffer.putShort(panel.warps[i][a].toZone);
								buffer.putShort(panel.warps[i][a].tox);
								buffer.putShort(panel.warps[i][a].toy);
							}
						}
					}
					out.write(buffer.array());
					
					//commands
					Message mes=new Message((byte)panel.commands.size());
					for(int i=0;i<panel.commands.size();i++)
						mes.addString(panel.commands.get(i));
					
					buffer=ByteBuffer.allocate(4);
					buffer.putInt(mes.size());
					out.write(buffer.array());
					out.write(mes.getBytes());
					
					//actual saving begins
					//String loc=getClass().getResource("Default/").toURI()+MBFrame.frame.options.nameField.getText()+".mp";
					String loc="Resources/Default/"+MBFrame.frame.options.nameField.getText()+".mp";
					System.out.println("loc: "+loc);
					//URI uri=new URI(loc);
					File f=new File(loc);
					//System.out.println("location: "+f.getAbsolutePath());
					f.createNewFile();
					FileOutputStream fout=new FileOutputStream(f);
					out.writeTo(fout);
					fout.close();
					System.out.println("file: "+f);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			else if(item.getText().equals("Name"))
			{
				JOptionPane pane=new JOptionPane();
				JTextField nameField=new JTextField("Default");
				pane.showMessageDialog(null,nameField,"Name",JOptionPane.QUESTION_MESSAGE);
				String name=nameField.getText();
				MBFrame.frame.panel.name=name;
			}
			else if(item.getText().equals("Size"))
			{
				JOptionPane pane=new JOptionPane();
				JTextField wField=new JTextField("8");
				JTextField hField=new JTextField("8");
				wField.requestFocus();
				pane.showMessageDialog(null,wField,"Width",JOptionPane.QUESTION_MESSAGE);
				hField.requestFocus();
				pane.showMessageDialog(null,hField,"Height",JOptionPane.QUESTION_MESSAGE);
				//pane.setVisible(true);
				Integer width=new Integer(wField.getText());
				Integer height=new Integer(hField.getText());
				MBFrame.frame.panel.changeSize(width,height);
			}
			else if(item.getText().equals("New Layer"))
			{
				JTextField fLayer=new JTextField();
				JOptionPane pane=new JOptionPane();
				pane.showMessageDialog(null,fLayer,"Layer",JOptionPane.QUESTION_MESSAGE);
				Integer layer=new Integer(fLayer.getText());
				MBFrame.frame.panel.createNewLayer(layer);
			}
			else if(item.getText().equals("Commands"))
			{
				JOptionPane pane=new JOptionPane();
				JTextArea fComm=new JTextArea();
				String commands="";
				for(int i=0;i<MBFrame.frame.panel.commands.size();i++)
					commands+=MBFrame.frame.panel.commands.get(i)+"\n";
				fComm.setText(commands);
				pane.showMessageDialog(null,new JScrollPane(fComm),"Commands",JOptionPane.QUESTION_MESSAGE);
				MBFrame.frame.panel.setCommands(fComm.getText());
			}
			else if(item.getText().equals("Change Layer"))
			{
				JTextField fLayer=new JTextField(""+(Integer)MBFrame.frame.options.layerlist.getSelectedValue());
				JOptionPane pane=new JOptionPane();
				pane.showMessageDialog(null,fLayer,"Layer",JOptionPane.QUESTION_MESSAGE);
				Integer layer=new Integer(fLayer.getText());
				MBFrame.frame.panel.changeLayer((Integer)MBFrame.frame.options.layerlist.getSelectedValue(),layer);
			}
			else if(item.getText().equals("Remove Layer"))
			{
				MBFrame.frame.panel.removeLayer((Integer)MBFrame.frame.options.layerlist.getSelectedValue());
			}
		}
	}
	
	
}
