import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;


public class PlayerEntity extends LivingEntity
{
	private byte[] keys;
	private boolean running;
	private boolean swimming;
	protected short walk;
	
	protected boolean switchZone;
	protected Inventory inventory;
	protected Equipment equipment;
	protected SkillList skills;
	protected QuestList quests;
	
	protected boolean battleStance;
	
	protected Skill skill_A;
	protected Skill skill_S;
	protected Skill skill_D;
	protected Skill skill_F;
	
	private double skilltimer=0;
	
	private ArrayList<Entity> targetList;
	private int targetListIndex;
	
	public PlayerEntity(Zone z, long u, double i, double a)
	{
		super(z,u);
		keys=new byte[KeyEvent.KEY_LAST];
		x=i;
		y=a;
		xtrue=(short)(x/16);
		ytrue=(short)(y/16);
		
		direction=SOUTH;
		running=false;
		swimming=false;
		walk=0;
		switchZone=false;
		inventory=new Inventory();
		equipment=new Equipment();
		stats=new PlayerStatus();
		skills=new SkillList();
		quests=new QuestList(null);
		imageIndex=0;
		sprite=new Sprite("Player",16,24);
		stats=new PlayerStatus();
		battleStance=false;
		
		targetList=new ArrayList<Entity>();
	}
	
	public void setWalk(int w)
	{
		walk=(short)w;
	}
	
	public void beginTick()
	{
		super.beginTick();
		for(int i=0;i<KeyEvent.KEY_LAST;i++)
			if(keys[i]>0&&keys[i]<Byte.MAX_VALUE)
				keys[i]++;
	}
	
	public void tick()
	{
		if(switchZone)
			return;
		
		if(keys[KeyEvent.VK_V]==2)
		{
			targetId=-1;
			MonsterGhostEntity nearest=null;
			ArrayList<Entity> monsters=zone.getEntities(MonsterGhostEntity.class);
			ArrayList<Entity> tempTargets=new ArrayList<Entity>();
			for(int i=0;i<monsters.size();i++)
			{
				MonsterGhostEntity monster=(MonsterGhostEntity)monsters.get(i);
				if(direction==direction(monster))
					tempTargets.add(monster);
					//if(nearest==null||distance(monster)<distance(nearest))
						//nearest=monster;
			}
			for(int i=0;i<tempTargets.size();i++)
			{
				targetList.add(tempTargets.get(i));
			}
			for(int i=targetList.size()-1;i>0;i--)
			{
				if(!tempTargets.contains(targetList.get(i)))
					targetList.remove(i);
			}
			targetListIndex++;
			if(targetList.size()>0)
				targetListIndex%=targetList.size();
			else
				targetListIndex=-1;
			if(targetListIndex!=-1)
				targetId=((LivingEntity)targetList.get(targetListIndex)).uuid;
		}
		
		if(keys[KeyEvent.VK_ESCAPE]==2)
		{
			if(targetId!=-1)
				targetId=-1;
			else
			{
				//double scale=GamePanel.panel.getView().getScale();
				double scale=1;
				EscapeMenu menu=new EscapeMenu("EscapeMenu", (int)(GamePanel.panel.getView().getWidth()*scale/2),(int)(GamePanel.panel.getView().getHeight()*scale/2));
				GamePanel.panel.setMenu(menu);
				clearKeys();
			}
		}
		
		if(walk==0)
		{
			setDepth(ytrue);
			xspeed=0;
			yspeed=0;
			
			if(zone.isWarpPoint((int)x/16,(int)y/16)!=null)
			{
				switchZone=true;
				walk=100;
				return;
			}
			
			if(keys[KeyEvent.VK_B]>=1)
			{
				ItemMenu menu=new ItemMenu("ItemMenu", 74, 6, inventory);
				GamePanel.panel.setMenu(menu);
				clearKeys();
			}
			
			if(keys[KeyEvent.VK_K]>=1)
			{
				SkillMenu menu=new SkillMenu("ItemMenu", 74, 6, this);
				GamePanel.panel.setMenu(menu);
				clearKeys();
			}
			
			if(keys[KeyEvent.VK_I]>=1)
			{
				EquipmentMenu menu=new EquipmentMenu("ItemMenu", 74, 6, equipment);
				GamePanel.panel.setMenu(menu);
				clearKeys();
			}
			
			if(keys[KeyEvent.VK_L]>=1)
			{
				QuestMenu menu=new QuestMenu("ItemMenu", 74, 6,quests);
				GamePanel.panel.setMenu(menu);
				clearKeys();
			}
			
			if(keys[KeyEvent.VK_O]>=1)
			{
				CraftingMenu menu=new CraftingMenu("ItemMenu", 74, 6,inventory);
				GamePanel.panel.setMenu(menu);
				clearKeys();
			}
			
			if(keys[KeyEvent.VK_A]>=1)
			{
				useSkill(skill_A);
			}
			
			if(keys[KeyEvent.VK_S]>=1)
			{
				useSkill(skill_S);
			}
			
			if(keys[KeyEvent.VK_D]>=1)
			{
				useSkill(skill_D);
			}
			
			if(keys[KeyEvent.VK_F]>=1)
			{
				useSkill(skill_F);
			}
			
			
			/*if(keys[KeyEvent.VK_C]>=1)
			{
				if(skills.getSkill(0).canUse(equipment,stats))
				{
					Message mes=new Message(Message.SKILL);
					mes.addInt(skills.getSkill(0).getId());
					mes.addLong(targetId);
					GameClient.client.addMessage(mes);
				}
			}*/
			
			byte lastIndex=(byte)imageIndex;
			
			if(keys[KeyEvent.VK_UP]>1)
			{
				imageIndex=4;
				direction=NORTH;
			}
			else if(keys[KeyEvent.VK_DOWN]>1)
			{
				imageIndex=0;
				direction=SOUTH;
			}
			else if(keys[KeyEvent.VK_LEFT]>1)
			{
				imageIndex=8;
				direction=WEST;
			}
			else if(keys[KeyEvent.VK_RIGHT]>1)
			{
				imageIndex=12;
				direction=EAST;
			}
			
			if(lastIndex!=imageIndex)
			{
				Message mes=new Message(Message.MOVE);
				mes.addBoolean(false);
				mes.addByte(imageIndex);
				GameClient.client.addMessage(mes);
			}
			
			if(keys[KeyEvent.VK_X]==2)
			{
				Message mes=new Message(Message.ACTION);
				mes.addByte(direction);
				GameClient.client.addMessage(mes);
			}
			
			
			if(keys[KeyEvent.VK_UP]>7&&zone.canWalk((int)x/16,(int)y/16-1))
			{
				walk=31;
				yspeed=-0.5;
				xspeed=0;
				imageIndex=4;
				direction=NORTH;
				ytrue--;
				setDepth(ytrue);
			}
			else if(keys[KeyEvent.VK_DOWN]>7&&zone.canWalk((int)x/16,(int)y/16+1))
			{
				walk=31;
				yspeed=0.5;
				xspeed=0;
				imageIndex=0;
				direction=SOUTH;
				ytrue++;
			}
			else if(keys[KeyEvent.VK_LEFT]>7&&zone.canWalk((int)x/16-1,(int)y/16))
			{
				walk=31;
				xspeed=-0.5;
				yspeed=0;
				imageIndex=8;
				direction=WEST;
				xtrue--;
			}
			else if(keys[KeyEvent.VK_RIGHT]>7&&zone.canWalk((int)x/16+1,(int)y/16))
			{
				walk=31;
				xspeed=0.5;
				yspeed=0;
				imageIndex=12;
				direction=EAST;
				xtrue++;
			}
			if(xspeed!=0||yspeed!=0)
			{
				Message mes=new Message(Message.MOVE);
				mes.addBoolean(true);
				mes.addByte((byte)Math.round(xspeed*2));
				mes.addByte((byte)Math.round(yspeed*2));
				GameClient.client.addMessage(mes);
			}
		}
		else
		{
			walk--;
		}
		
		x+=xspeed;
		y+=yspeed;
	}
	
	public void endTick()
	{
		skilltimer++;
		if(skilltimer>=60.0/24)
		{
			skilltimer-=60.0/24;
			skills.tick();
		}
	}
	
	public void useSkill(Skill s)
	{
		if(s!=null)
		{
			if(targetId!=-1)
			{
				LivingEntity living=zone.getLiving(targetId);
				if(living!=null)
				{
					if(direction==direction(living))
					{
						if(s.canUse(equipment,stats))
						{
							Message mes=new Message(Message.SKILL);
							mes.addInt(s.getId());
							mes.addLong(targetId);
							GameClient.client.addMessage(mes);
						}
					}
				}
				else
					targetId=-1;
			}
			else
			{
				LivingEntity living=(MonsterGhostEntity)zone.getEntityAt(getFront(),MonsterGhostEntity.class);
				if(living!=null)
				{
					targetId=living.uuid;
					//if(direction==direction(living))
					{
						if(s.canUse(equipment,stats))
						{
							Message mes=new Message(Message.SKILL);
							mes.addInt(s.getId());
							mes.addLong(targetId);
							GameClient.client.addMessage(mes);
						}
					}
				}
			}
		}
	}
	
	public void draw(Graphics2D g)
	{
		g.setColor(new Color(255,0,0,100));
		if(battleStance)
		{
			g.fillRect((int)x,(int)y,16,16);
		}
		sprite.draw(g,x,y-12,imageIndex+(walk/8)%4);
		//zone.lights.addLight(x+8,y+4,Color.red,1,96,5);
		
		if(targetId!=-1)
		{
			LivingEntity living=zone.getLiving(targetId);
			
			g.setColor(Color.yellow);
			if(living!=null)
				g.drawRect((int)living.x,(int)living.y,16,16);
			else
				targetId=-1;
		}
		
		
	}
	
	public void clearKeys()
	{
		keys=new byte[KeyEvent.KEY_LAST];
	}

	//@Override
	public void mouseClicked(MouseEvent me)
	{
	}

	//@Override
	public void mouseEntered(MouseEvent me)
	{
	}

	//@Override
	public void mouseExited(MouseEvent me)
	{
	}

	//@Override
	public void mousePressed(MouseEvent me)
	{
	}

	//@Override
	public void mouseReleased(MouseEvent me)
	{
	}

	//@Override
	public void keyPressed(KeyEvent ke)
	{
		if(keys[ke.getKeyCode()]==0)
			keys[ke.getKeyCode()]=1;
	}

	//@Override
	public void keyReleased(KeyEvent ke)
	{
		keys[ke.getKeyCode()]=0;
	}

	//@Override
	public void keyTyped(KeyEvent ke)
	{
		if(keys[ke.getKeyCode()]==0)
			keys[ke.getKeyCode()]=1;
	}
}
