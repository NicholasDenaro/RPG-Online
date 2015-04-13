import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class GameLoginFrame extends JFrame implements ActionListener
{
	private GameFrame frame;
	
	private JTextField user;
	private JPasswordField pass;
	
	public GameLoginFrame()
	{
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame=new GameFrame();
		GameClient.client.loginFrame=this;
		make();
		setSize(new Dimension(320,320));
		setVisible(true);
	}
	
	private void make()
	{
		Container c=getContentPane();
		GridBagConstraints gbc=new GridBagConstraints();
		c.setLayout(new GridBagLayout());
		
		gbc.insets=new Insets(30,32,0,32);
		//gbc.anchor=gbc.NORTH;
		gbc.anchor=gbc.FIRST_LINE_START;
		gbc.fill=gbc.HORIZONTAL;
		gbc.weightx=1;
		gbc.gridwidth=4;
		gbc.gridheight=1;
		
		
		gbc.weighty=0.25;
		gbc.gridx=0;
		gbc.gridy=0;
		JLabel lbl=new JLabel("RPG Online");
		lbl.setFont(new Font("Courier New",Font.BOLD,18));
		c.add(lbl,gbc);
		gbc.insets=new Insets(0,32,0,32);
		
		gbc.anchor=gbc.FIRST_LINE_START;
		
		gbc.weighty=0;
		gbc.gridx=0;
		gbc.gridy=1;
		c.add(new JLabel("Username"),gbc);
		gbc.insets=new Insets(0,32,0,32);
		
		gbc.gridx=0;
		gbc.gridy=2;
		gbc.weighty=0.1;
		user=new JTextField();
		//user.setPreferredSize(new Dimension(160,user.getPreferredSize().height));
		c.add(user,gbc);
		
		gbc.weighty=0;
		gbc.gridx=0;
		gbc.gridy=3;
		c.add(new JLabel("Password"),gbc);
		
		gbc.gridx=0;
		gbc.gridy=4;
		gbc.weighty=0.25;
		pass=new JPasswordField();
		//pass.setPreferredSize(new Dimension(160,pass.getPreferredSize().height));
		c.add(pass,gbc);
		
		gbc.gridwidth=2;
		gbc.gridx=0;
		gbc.gridy=5;
		gbc.weighty=0.1;
		JButton button=new JButton("Login");
		button.addActionListener(this);
		c.add(button,gbc);
		
		gbc.gridx=2;
		gbc.gridy=5;
		gbc.weighty=0.1;
		button=new JButton("Register");
		button.addActionListener(this);
		c.add(button,gbc);
	}
	
	public boolean isValidUsername(String user)
	{
		if(user.length()==0||user.length()>18)
			return(false);
		for(int i=0;i<user.length();i++)
		{
			char c=user.charAt(i);
			if((c<'a'||c>'z')&&(c<'A'||c>'Z'))
				return(false);
		}
		return(true);
	}
	
	public static void main(String args[])
	{
		new GameLoginFrame();
	}

	@Override
	public void actionPerformed(ActionEvent ae)
	{
		JButton button=(JButton)ae.getSource();
		
		if(button.getText().equals("Login"))
		{
			String u=user.getText().trim();
			String p=new String(pass.getPassword());
			if(!isValidUsername(u))
			{
				JOptionPane.showMessageDialog(null,"Invalid username(max length 18)");
			}
			else if(p.isEmpty()||p.length()>30)
			{
				JOptionPane.showMessageDialog(null,"Invalid password(max length 30)");
			}
			else
			{
				Message mes=new Message(Message.LOGIN);
				mes.addString(u);
				mes.addString(p);
				GameClient.client.addMessage(mes);
				try
				{
					GameClient.client.flush();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
		else if(button.getText().equals("Register"))
		{
			String u=user.getText().trim();
			String p=new String(pass.getPassword());
			if(!isValidUsername(u))
			{
				JOptionPane.showMessageDialog(null,"Invalid username(max length 18)");
			}
			else if(p.isEmpty()||p.length()>30)
			{
				JOptionPane.showMessageDialog(null,"Invalid password(max length 30)");
			}
			else
			{
				Message mes=new Message(Message.REGISTER);
				mes.addString(u);
				mes.addString(p);
				GameClient.client.addMessage(mes);
				try
				{
					GameClient.client.flush();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
}
