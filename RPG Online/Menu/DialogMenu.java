import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class DialogMenu extends Menu
{
	private String filename;
	protected ArrayList<String> text;
	private boolean format;
	protected String show;
	protected int line;
	protected double cursor;
	private boolean down;
	private Font font;
	
	public DialogMenu(String name,int i,int a,String fname)
	{
		super(name,i,a);
		filename=fname;
		//line=0;
		text=new ArrayList<String>();
		//cursor=0;
		down=false;
		//format=true;
		loadTag("Root");
		//show=text.get(0);
		font=new Font("Courier New",Font.BOLD,10);
		img=new BufferedImage(GamePanel.panel.getView().getWidth(),32,BufferedImage.TYPE_INT_ARGB);
		shouldScale=true;
		//System.out.println("done");
	}
	
	public void loadTag(String tag)
	{
		System.out.println("loading tag: "+tag);
		line=0;
		cursor=0;
		text=new ArrayList<String>();
		BufferedReader fin=new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("NPC_Dialog/"+filename+".txt")));
		String ln;
		String ftag=null;
		try
		{
			while((ln=fin.readLine())!=null)
			{
				if(ln.contains("[tag="))
				{
					
					ftag=ln.substring(ln.indexOf('=')+1,ln.indexOf(']'));
					//System.out.println("ftag: "+ftag);
				}
				else if(ftag.equals(tag))
				{
					if(!ln.isEmpty()&&ln.charAt(0)!='{'&&ln.charAt(0)!='}')
					{
						text.add(ln.trim());
						System.out.println("ln: "+ln);
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		format=true;
		show=text.get(0);
	}
	
	public void drawStuff(Graphics2D g)
	{
		g.setColor(Color.black);
		g.fillRect(0,0,GamePanel.panel.getView().getWidth(),32);
		g.setColor(Color.white);
		g.setFont(font);
		int width=g.getFontMetrics().stringWidth(" ");
		int height=g.getFontMetrics().getHeight()+1;
		int chrs=GamePanel.panel.getView().getWidth()/width-1;
		if(cursor>=chrs*2)
		{
			cursor=chrs;
			show=show.substring(chrs);
		}
		if(cursor>=chrs)
		{
			g.drawString(show.substring(0,chrs),2,height-1);
			g.drawString(show.substring(chrs,(int)cursor),2,height*2-1);
		}
		else
		{
			g.drawString(show.substring(0,(int)cursor),2,height-1);
		}
	}
	
	public void draw(Graphics2D g)
	{
		if(format)
		{
			format=false;
			g.setFont(font);
			int width=g.getFontMetrics().stringWidth(" ");
			int chrs=GamePanel.panel.getView().getWidth()/width-1;
			ArrayList<String> newtext=new ArrayList<String>();
			for(int i=0;i<text.size();i++)
			{
				String s=text.get(i);
				s=s.trim();
				String news="";
				while(s.length()>0)
				{
					if(!s.substring(0,Math.min(chrs,s.length())).contains("["))
					{
						int len=Math.min(chrs,s.length());
						String ln=s.substring(0,len);
						
						s=s.replace(ln,"");
						if(s.length()>0)
						{
							if(ln.charAt(len-1)!=' '&&s.charAt(0)!=' ')
							{
								int sp=ln.lastIndexOf(' ');
								if(sp!=-1)
								{
									s=ln.substring(sp)+s;
									ln=ln.substring(0,sp);
									while(ln.length()<chrs)
										ln+=" ";
								}
								//System.out.println("ln: "+ln);
							}
						}
						if(ln.charAt(0)==' ')
							ln=ln.substring(1)+" ";
						news+=ln;
					}
					else
					{
						if(s.charAt(0)==' ')
							s=s.substring(1)+" ";
						news+=s;
						s="";
					}
				}
				newtext.add(news);
				//System.exit(0);
			}
			text=newtext;
			show=text.get(0);
		}
		if(line<text.size())
		{
			if(cursor<show.length())
			{
				if(show.charAt((int)(cursor))!='[')
				{
					while(show.charAt((int)cursor)==' '&&cursor<show.length()-1&&show.charAt((int)(cursor+1))==' ')
						cursor++;
					cursor+=0.25;
				}
				else
				{
					if(menu==null)
					{
						menu=new DialogActionMenu("DialogActionMenu",x,y-img.getHeight(),this,show.substring((int)cursor));
						//((DialogActionMenu)menu).processAction();
					}
				}				
			}
			super.draw(g);
		}
	}
	
	public void keyPressed(KeyEvent ke)
	{
		if(menu!=null)
		{
			menu.keyPressed(ke);
			return;
		}
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			if(down==false)
			{
				if(cursor>=show.length())
				{
					System.out.println("cursor:show.length"+cursor+":"+show.length());
					if(line<text.size()-1)
					{
						System.out.println("line:text.size()-1"+line+":"+(text.size()-1));
						line++;
						cursor=0;
						show=text.get(line);
					}
					else
					{
						//System.out.println("should exit...");
						GamePanel.panel.setMenu(null);
					}
				}
				down=true;
			}
		}
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			if(cursor<show.length())
				{
					if(show.charAt((int)(cursor))!='[')
					{
						cursor++;
					}
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
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			down=false;
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
