import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;


public class MBOptionPane extends Container implements ActionListener
{
	public JTextField nameField;
	public JCheckBox movement;
	public JCheckBox warps;
	public DefaultListModel<Integer> list;
	public JList layerlist;
	
	public MBOptionPane()
	{
		setPreferredSize(new Dimension(128,-1));
		make();
	}
	
	public void make()
	{
		GridBagConstraints gbc=new GridBagConstraints();
		
		setLayout(new GridBagLayout());
		
		gbc.insets=new Insets(2,5,2,5);
		
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.weightx=1;
		gbc.weighty=0;
		gbc.gridwidth=2;
		
		gbc.fill=gbc.HORIZONTAL;
		gbc.anchor=gbc.PAGE_START;
		
		JLabel label=new JLabel("Name:");
		add(label,gbc);
		
		gbc.gridx=0;
		gbc.gridy=1;
		gbc.weighty=0;
		
		nameField=new JTextField("Default");
		add(nameField,gbc);
		
		gbc.gridx=0;
		gbc.gridy=2;
		gbc.gridwidth=1;
		add(new JLabel("Movement"),gbc);
		
		gbc.gridx=1;
		gbc.gridy=2;
		gbc.gridwidth=1;
		movement=new JCheckBox();
		movement.addActionListener(this);
		add(movement,gbc);
		
		gbc.gridx=0;
		gbc.gridy=3;
		gbc.gridwidth=1;
		add(new JLabel("Warps"),gbc);
		
		gbc.weighty=0;
		gbc.gridx=1;
		gbc.gridy=3;
		gbc.gridwidth=1;
		warps=new JCheckBox();
		warps.addActionListener(this);
		add(warps,gbc);
		
		gbc.weighty=1;
		gbc.gridx=0;
		gbc.gridy=4;
		gbc.gridwidth=2;
		list=new DefaultListModel<Integer>();
		list.addElement(1);
		layerlist=new JList<Integer>(list);
		layerlist.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		layerlist.setSelectedIndex(0);
		JScrollPane scroll=new JScrollPane(layerlist);
		scroll.setPreferredSize(new Dimension(0,100));
		add(scroll,gbc);
	}

	@Override
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() instanceof JButton)
		{
			MBFrame.frame.panel.name=nameField.getText();
			//System.out.println("New name: "+MBFrame.frame.panel.name);
		}
		else if(ae.getSource() instanceof JCheckBox)
		{
			MBFrame.frame.panel.repaint();
		}
	}
}
