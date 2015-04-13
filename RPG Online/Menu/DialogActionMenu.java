import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class DialogActionMenu extends Menu
{
	public static DialogActionMenu actionMenu;
	private DialogMenu parent;
	protected String action;
	protected ArrayList<String> actions;
	private int cursor;
	private boolean fixPos;
	private boolean finished;
	private DialogBuilder builder;

	public DialogActionMenu(String name,int i,int a, DialogMenu par, String act)
	{
		super(name,i,a);
		builder=new DialogBuilder(this,act);
		new Thread(builder).start();
		actionMenu=this;
		finished=false;
		parent=par;
		//action=parseTag(act);
		//actions=new ArrayList<String>();
		/*
		//if(action.equals("Action"))
		{
			if(action.contains(","))
			{
				actions.add(action.substring(action.indexOf("=")+1,action.indexOf(',')));
				action=action.substring(action.indexOf(',')+1);
				while(action.indexOf(',')!=-1)
				{
					actions.add(action.substring(0,action.indexOf(',')));
					action=action.substring(action.indexOf(',')+1);
				}
				actions.add(action.substring(0,action.indexOf(']')));
			}
			else
			{
				actions.add(action.substring(action.indexOf("=")+1,action.indexOf(']')));
			}
		}
		if(act.indexOf('=')!=-1)
			action=act.substring(1,act.indexOf('='));
		else
			action=act.substring(1,act.indexOf(']'));
		System.out.println("act: "+act);
		System.out.println("Action: "+action);
		//processAction();
		cursor=0;
		img=new BufferedImage(GamePanel.panel.getView().getWidth(),32,BufferedImage.TYPE_INT_ARGB);
		shouldScale=true;
		fixPos=true;
		finished=true;*/
	}
	
	public void unfreezeBuilder(boolean query)
	{
		builder.query=query;
		builder.freeze=false;
	}
	
	public void processAction()
	{
		if(action.equalsIgnoreCase("skill"))
		{
			for(int i=0;i<actions.size();i++)
			{
				Message mes=new Message(Message.ADDSKILL);
				mes.addInt(new Integer(actions.get(i)));
				GameClient.client.addMessage(mes);
			}
			//parent.menu=null;
		}
		else if(action.equalsIgnoreCase("action"))
		{
			
		}
		else if(action.equalsIgnoreCase("heal"))
		{
			Message mes=new Message(Message.EFFECT);
			mes.addByte(PlayerStat.health);
			mes.addInt(999999);
			mes.addInt(2);
			mes.addInt(0);
			mes.addInt(0);
			GameClient.client.addMessage(mes);
		}
		else if(action.equalsIgnoreCase("goto"))
		{
			actionMenu=null;
			parent.menu=null;
			parent.loadTag(actions.get(0));
		}
		
		cursor=0;
		img=new BufferedImage(GamePanel.panel.getView().getWidth(),32,BufferedImage.TYPE_INT_ARGB);
		shouldScale=true;
		fixPos=true;
		finished=true;
	}
	
	
	public void drawStuff(Graphics2D g)
	{
		if(!finished)return;
		
		Font f=new Font("Courier new",Font.PLAIN,12);
		g.setFont(f);
		int fwidth=g.getFontMetrics().stringWidth(" ");
		int fheight=g.getFontMetrics().getHeight()/2+3;
		if(fixPos)
		{
			fixPos=false;
			y-=(fheight*(actions.size()-1))/2-3;
			img=new BufferedImage(GamePanel.panel.getView().getWidth(),8+fheight*actions.size(),BufferedImage.TYPE_INT_ARGB);
		}
		if(action.equals("Actions"))
		{
			g.setColor(Color.black);
			g.fillRect(0,0,10*fwidth+4,fheight*actions.size()+4);
			
			g.setColor(Color.white);
			for(int i=0;i<actions.size();i++)
			{
				g.drawString(actions.get(i),2,2+fheight*(i+1));
			}
			g.drawRect(1,fheight*cursor+1,fwidth*10+1,fheight+1);
		}
	}
	
	public void draw(Graphics2D g)
	{
		if(!finished)return;
		
		super.draw(g);
	}
	
	public void keyPressed(KeyEvent ke)
	{
		if(!finished)return;
		
		if(menu!=null)
		{
			menu.keyPressed(ke);
			return;
		}
		if(ke.getKeyCode()==KeyEvent.VK_DOWN)
		{
			cursor++;
			cursor%=actions.size();
		}
		if(ke.getKeyCode()==KeyEvent.VK_UP)
		{
			cursor--;
			cursor+=actions.size();
			cursor%=actions.size();
		}
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			if(action.equals("Actions"))
			{
				actionMenu=null;
				parent.menu=null;
				parent.loadTag(actions.get(cursor));
			}
			else
			{
				parent.line++;
				parent.cursor=0;
				if(parent.line<parent.text.size()-1)
				{
					parent.show=parent.text.get(parent.line);
				}
				else
				{
					actionMenu=null;
					GamePanel.panel.setMenu(null);
				}
			}
		}
	}

	public void keyReleased(KeyEvent ke)
	{
		if(!finished)return;
		
		if(menu!=null)
		{
			menu.keyReleased(ke);
			return;
		}
	}

	public void keyTyped(KeyEvent ke)
	{
		if(!finished)return;
		
		if(menu!=null)
		{
			menu.keyTyped(ke);
			return;
		}
	}
}

class DialogBuilder implements Runnable
{
	public DialogActionMenu menu;
	public String action;
	public ArrayList<String> actions;
	volatile public boolean freeze;
	public boolean query;
	
	public DialogBuilder(DialogActionMenu m, String act)
	{
		menu=m;
		action=act;
		//action=parseTag(act);
		actions=new ArrayList<String>();
		/*//if(!action.contains("<"))
		{
			if(action.contains(","))
			{
				actions.add(action.substring(action.indexOf("=")+1,action.indexOf(',')));
				action=action.substring(action.indexOf(',')+1);
				while(action.indexOf(',')!=-1)
				{
					actions.add(action.substring(0,action.indexOf(',')));
					action=action.substring(action.indexOf(',')+1);
				}
				actions.add(action.substring(0,action.indexOf(']')));
			}
			else
			{
				actions.add(action.substring(action.indexOf("=")+1,action.indexOf(']')));
			}
			if(act.indexOf('=')!=-1)
				action=act.substring(1,act.indexOf('='));
			else
				action=act.substring(1,act.indexOf(']'));
		}*/
	}
	
	public String parseTag(String tag)
	{
		while(tag.contains("?"))
		{
			int start=tag.indexOf('?');
			int left=tag.indexOf('<');
			int right=tag.indexOf('>');
			String function=tag.substring(left+1,right);
			
			System.out.println("Pre eval tag: "+tag);
			int indexOfColon=indexOfClosingBracket(tag,tag.indexOf('{'))+1;
			int indexOfClosingBracket=indexOfClosingBracket(tag,indexOfColon+1);
			System.out.println("closing bracket: "+indexOfClosingBracket);
			if(evaluateFunction(function))
			{
				String sleft=tag.substring(0,start);
				String smid=tag.substring(right+2,indexOfColon-1);
				String sright=tag.substring(indexOfClosingBracket+1);
				System.out.println("left|"+sleft+"\nmid|"+smid+"\nright|"+sright);
				//tag=tag.substring(0,start)+tag.substring(right+2,tag.indexOf(':')-1)+tag.substring(indexOfClosingBracket+1);
				tag=sleft+smid+sright;
			}
			else
			{
				String sleft=tag.substring(0,start);
				String smid=tag.substring(indexOfColon+2,indexOfClosingBracket);
				String sright=tag.substring(indexOfClosingBracket+1);
				System.out.println("left|"+sleft+"\nmid|"+smid+"\nright|"+sright);
				//tag=tag.substring(0,start)+tag.substring(tag.indexOf(':')+2,indexOfClosingBracket)+tag.substring(indexOfClosingBracket+1);
				tag=sleft+smid+sright;
			}
			System.out.println("post eval tag: "+tag);
		}
		System.out.println("finish tag: "+tag);
		return(tag);
	}
	
	public int indexOfClosingBracket(String tag, int open)
	{
		int index=open+1;
		int count=1;
		while(index<tag.length()&&count>0)
		{
			if(tag.charAt(index)=='{')
				count++;
			if(tag.charAt(index)=='}')
				count--;
			index++;
		}
		return(index-1);
	}
	
	public boolean evaluateFunction(String function)
	{
		String[] args=new String[function.length()-function.replace(",","").length()+1];
		int index=function.indexOf('(');
		int next=function.indexOf(',')==-1?function.indexOf(')'):function.indexOf(',');
		for(int i=0;i<args.length;i++)
		{
			args[i]=function.substring(index+1,next);
			index=next;
			next=function.indexOf(',')==-1?function.indexOf(')'):function.indexOf(',');
		}
		//System.out.println("args[0]"+args[0]);
		function=function.substring(0,function.indexOf('('));
		Message mes=new Message(Message.QUERY);
		switch(function)
		{
			case "isQuestDone":
				mes.addByte(Query.QUESTDONE);
				mes.addInt(new Integer(args[0]));
			break;
			case "hasQuest":
				mes.addByte(Query.HASQUEST);
				mes.addInt(new Integer(args[0]));
			break;
			case "startQuest":
				mes.addByte(Query.STARTQUEST);
				mes.addInt(new Integer(args[0]));
			break;
			case "canCompleteQuest":
				mes.addByte(Query.COMPLETEQUEST);
				mes.addInt(new Integer(args[0]));
			break;
		}
		GameClient.client.addMessage(mes);
		
		freeze=true;
		
		//System.out.println("Going to wait.");
		while(freeze){if(!freeze)break;};
		//System.out.println("Made it here.");
		
		return(query);
	}
	
	public void parseActions()
	{
		String act=action;
		actions=new ArrayList<String>();
		if(action.contains(","))
		{
			actions.add(action.substring(action.indexOf("=")+1,action.indexOf(',')));
			action=action.substring(action.indexOf(',')+1);
			while(action.indexOf(',')!=-1)
			{
				actions.add(action.substring(0,action.indexOf(',')));
				action=action.substring(action.indexOf(',')+1);
			}
			actions.add(action.substring(0,action.indexOf(']')));
		}
		else
		{
			actions.add(action.substring(action.indexOf("=")+1,action.indexOf(']')));
		}
		if(act.indexOf('=')!=-1)
			action=act.substring(1,act.indexOf('='));
		else
			action=act.substring(1,act.indexOf(']'));
	}
	
	@Override
	public void run()
	{
		action=parseTag(action);
		parseActions();
		System.out.println("action: "+action);
		menu.action=action;
		menu.actions=actions;
		menu.processAction();
		System.out.println("got here!");
	}
	
}
