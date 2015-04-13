import java.awt.Color;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;


public class GameClient extends Thread
{
	public static GameClient client;
	protected GameLoginFrame loginFrame;
	private GameFrame frame;
	private World world;
	private boolean running;
	private Socket socket;
	private CustomInputStream in;
	private CustomOutputStream out;
	private PlayerEntity player;
	private String name;
	private long uuid;
	
	private int zonemes;
	
	public GameClient(GameFrame fr, World w)
	{
		client=this;
		frame=fr;
		world=w;
		player=null;
		name=null;
		running=false;
		socket=new Socket();
		zonemes=-1;
		try
		{
			socket.connect(new InetSocketAddress("66.71.28.93",ServerEngine.PORT),5000);
			in=new CustomInputStream(socket.getInputStream());
			out=new CustomOutputStream(socket.getOutputStream());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public PlayerEntity getPlayer()
	{
		return(player);
	}
	
	public String getPayerName()
	{
		return(name);
	}
	
	public void addMessage(Message mes)
	{
		out.addMessage(mes);
	}
	
	public void run()
	{
		running=true;
		
		while(running)
		{
			try
			{
				in.read();
				byte message=in.readByte();
				//System.out.println("message: "+message);
				while(message!=Message.END)
				{
					if(message==Message.LOGIN)
					{
						boolean success=in.readBoolean();
						if(success)
						{
							System.out.println("successful login");
							name=in.readString();
							long id=in.readLong();
							long pid=in.readLong();
							short z=in.readShort();
							String zName=in.readString();
							short x=in.readShort();
							short y=in.readShort();
							uuid=id;
							player=new PlayerEntity(new Zone(world,z,zName,true),pid,x*16,y*16);
							GamePanel.panel.hud=new Hud(player,name);
							//frame.getPanel().addKeyListener(player);
							player.zone.addEntity(player);
							//frame.getWorld().currentZone=player.zone;
							frame.startEngine();
							frame.setVisible(true);
							loginFrame.dispose();
						}
						else
						{
							boolean reg=in.readBoolean();
							if(reg)
							{
								JOptionPane.showMessageDialog(loginFrame,"A user with this name exists.");
							}
							else
							{
								JOptionPane.showMessageDialog(loginFrame,"Invalid username/password.");
							}
						}
					}
					else if(message==Message.LOGOUT)
					{
						long id=in.readLong();
						LivingEntity living=player.zone.getLiving(id);
						//if(zonemes==player.zone.getId())
						{
							//System.out.println(id+":"+living.uuid);
							if(living!=null&&living instanceof GhostEntity)
								player.zone.removeEntity(living);
						}
						
					}
					else if(message==Message.ZONE)
					{
						zonemes=in.readShort();
					}
					else if(message==Message.MOVE)
					{
						long id=in.readLong();
						boolean move=in.readBoolean();
						if(move)//move
						{
							short ispeed=in.readByte();
							short aspeed=in.readByte();
							//if(zonemes==player.zone.getId())
							{
								LivingEntity ent=player.zone.getLiving(id);
								if(ent instanceof GhostEntity)
								{
									GhostEntity ghost=((GhostEntity)ent);
									ghost.addMovement(new GhostAction(ispeed/2.0,aspeed/2.0));
								}
								else if(id==-1)
								{
									short i=player.xtrue;
									short a=player.ytrue;
									
									if(ispeed>0)
										i++;
									else if(ispeed<0)
										i--;
									
									if(aspeed>0)
										a++;
									else if(aspeed<0)
										a--;
									
									player.x=i*16;
									player.y=a*16;
									player.xtrue=i;
									player.ytrue=a;
									player.walk=0;
									player.xspeed=0;
									player.yspeed=0;
									player.switchZone=false;
								}
							}
						}
						else//turn
						{
							byte index=in.readByte();
							//if(zonemes==player.zone.getId())
							{
								LivingEntity ent=player.zone.getLiving(id);
								if(ent instanceof GhostEntity)
								{
									//System.out.println("index: "+index);
									((GhostEntity)ent).addMovement(new GhostAction(index));
								}
							}
						}
					}
					else if(message==Message.SPAWN)
					{
						boolean pl=in.readBoolean();
						
						if(pl)
						{
							long id=in.readLong();
							long ghostid=in.readLong();
							short i=in.readShort();
							short a=in.readShort();
							//if(zonemes==player.zone.getId())
							{
								if(id!=uuid)
								{
									PlayerGhostEntity ghost=new PlayerGhostEntity(player.zone,ghostid,i*16,a*16);
									ghost.zone.addEntity(ghost);
									ghost.sprite=new Sprite("Player",16,24);
								}
							}
						}
						else
						{
							long ghostid=in.readLong();
							short i=in.readShort();
							short a=in.readShort();
							String n=in.readString();
							if(!n.equalsIgnoreCase("NPC"))
							{
								MonsterGhostEntity ghost=new MonsterGhostEntity(player.zone,ghostid,i*16,a*16);
								ghost.zone.addEntity(ghost);
								ghost.sprite=new Sprite(n,16,24);
								ghost.stats=MonsterEntity.monsterList.get(n).stats.clone();
							}
							else
							{
								NPCGhostEntity ghost=new NPCGhostEntity(player.zone,ghostid,i*16,a*16,in.readString());
								ghost.zone.addEntity(ghost);
								ghost.sprite=new Sprite(n,16,24);
							}
						}
					}
					else if(message==Message.LEAVE)
					{
						long id=in.readLong();
						//if(zonemes==player.zone.getId())
						{
							LivingEntity living=player.zone.getLiving(id);
							if(living!=null)
							{
								player.zone.removeEntity(living);
							}
						}
					}
					else if(message==Message.WARP)
					{
						player.setWalk(10);
						short zid=in.readShort();
						String zname=in.readString();
						short i=in.readShort();
						short a=in.readShort();
						Zone z=new Zone(world,zid,zname,true);
						player.zone=z;
						player.zone.addEntity(player);
						System.out.println("player.zone: "+player.zone);
						player.x=i*16;
						player.y=a*16;
						player.xtrue=i;
						player.ytrue=a;
						player.xlast=player.x;
						player.ylast=player.y;
						player.xspeed=0;
						player.yspeed=0;
						player.switchZone=false;
						player.targetId=-1;
					}
					else if(message==Message.ADDITEM)
					{
						boolean add=in.readBoolean();
						if(add)
						{
							int itemid=in.readInt();
							int itemcount=in.readInt();
							if(Item.get(itemid) instanceof Weapon)
							{
								String params=in.readString();
								boolean appr=in.readBoolean();
								Weapon wep=new Weapon((Weapon)Item.get(itemid),params,appr);
								player.inventory.add(new ItemStack(wep));
							}
							else//not weapons
								player.inventory.add(new ItemStack(itemid,itemcount));
						}
						else
						{
							int index=in.readInt();
							int count=in.readInt();
							player.inventory.remove(index,count);
						}
					}
					else if(message==Message.USEITEM)
					{
						byte cursor=in.readByte();
						if(player.inventory.get(cursor).getItem() instanceof Equip)
						{
							player.equipment.equip((Equip)player.inventory.get(cursor).getItem());
						}
						player.inventory.remove(cursor,1);
					}
					else if(message==Message.STAT)
					{
						boolean me=in.readBoolean();
						if(me)
						{
							byte stat=in.readByte();
							int val=in.readInt();
							if(stat!=PlayerStat.ap)
							{
								int oldval=player.stats.getStat(stat).value();
								player.stats.getStat(stat).set(val);
								if(stat==PlayerStat.health)
								{
									if(oldval-val>=0)
										player.zone.addEntity(new DamageParticle(player.zone,player.x+8,player.y+8,60,""+(oldval-val),Color.red));
									else
										player.zone.addEntity(new DamageParticle(player.zone,player.x+8,player.y+8,60,""+(val-oldval),Color.green));
								}
							}
							else//is ap
							{
								boolean max=in.readBoolean();
								if(max)
									player.stats.getStat(stat).set(val);
								else
									player.stats.currentAp=val;
							}
						}
						else//not me
						{
							long id=in.readLong();
							byte stat=in.readByte();
							int val=in.readInt();
							LivingEntity target=player.zone.getLiving(id);
							//System.out.println("uuid:id "+player.uuid+":"+id);
							if(target!=null)
							{
								//System.out.println("should be healing...");
								int oldval=target.stats.getStat(stat).value();
								target.stats.getStat(stat).set(val);
								if(stat==PlayerStat.health)
								{
									if(oldval-val>0)
										target.zone.addEntity(new DamageParticle(target.zone,target.x+8,target.y+8,60,""+(oldval-val),Color.yellow));
								}
							}
						}
					}
					else if(message==Message.STATS)
					{
						int level=in.readInt();
						int statpoints=in.readInt();
						int exp=in.readInt();
						int maxhp=in.readInt();
						int hp=in.readInt();
						int dur=in.readInt();
						int sta=in.readInt();
						int hun=in.readInt();
						int agi=in.readInt();
						int dex=in.readInt();
						int vit=in.readInt();
						int str=in.readInt();
						int def=in.readInt();
						System.out.println("max health: "+maxhp);
						player.stats=new PlayerStatus(level,statpoints,exp,maxhp,hp,dur,sta,hun,agi,dex,vit,str,def);
					}
					else if(message==Message.EQUIPALL)
					{
						int helmid=in.readInt();
						int maskid=in.readInt();
						int mantid=in.readInt();
						int chestid=in.readInt();
						int legid=in.readInt();
						int bootid=in.readInt();
						int accid=in.readInt();
						int wepid=in.readInt();
						String wepparams=in.readString();
						boolean wepappr=in.readBoolean();
						player.equipment=new Equipment(helmid,maskid,mantid,chestid,legid,bootid,accid,wepid,wepparams,wepappr);
					}
					else if(message==Message.EQUIP)
					{
						int index=in.readInt();
						player.equipment.removeEquip(index);
						System.out.println("remove equipment: "+index);
					}
					else if(message==Message.ACTION)
					{
						byte action=in.readByte();
						System.out.println("Action: "+action);
						if(action==Action.TALK)
						{
							String name=in.readString();
							GamePanel.panel.setMenu(new DialogMenu("DialogMenu",0,160-32-16,name));
						}
					}
					else if(message==Message.EFFECT)
					{
						long id=in.readLong();
						boolean add=in.readBoolean();
						if(add)
						{
							byte efid=in.readByte();
							int val=in.readInt();
							int sec=in.readInt();
							int min=in.readInt();
							int hr=in.readInt();
							PlayerEffect effect=new PlayerEffect(efid,val,sec,min,hr);
							LivingEntity living=player.zone.getLiving(id);
							if(living!=null)
								living.addEffect(effect);
							//player.addEffect(effect);
						}
						else
						{
							byte efindex=in.readByte();
							//player.removeEffect(efindex);
							LivingEntity living=player.zone.getLiving(id);
							if(living!=null)
								living.removeEffect(efindex);
							System.out.println("living: "+living);
							//System.out.println("Removing effect: "+efindex);
						}
					}
					else if(message==Message.ADDSKILL)
					{
						int skillid=in.readInt();
						int lvl=in.readInt();
						int cooldown=in.readInt();
						Skill skill=Skill.list.get(skillid).clone(lvl,cooldown);
						player.skills.addSkill(skill);
						if(skill.getId()!=0)
						{
							System.out.println("skill: "+skill.getId());
							if(player.skill_A==null)
								player.skill_A=skill;
							else if(player.skill_S==null)
								player.skill_S=skill;
							else if(player.skill_D==null)
								player.skill_D=skill;
							else if(player.skill_F==null)
								player.skill_F=skill;
						}
					}
					else if(message==Message.SKILL)
					{
						int skillid=in.readInt();
						if(skillid!=0)
							player.skills.getSkill(skillid).use();
						else
							player.battleStance=in.readBoolean();
						//System.out.println("Used skill: "+Skill.list.get(skillid));
					}
					else if(message==Message.UUID)
					{
						player.uuid=in.readLong();
					}
					else if(message==Message.QUERY)
					{
						boolean val=in.readBoolean();
						System.out.println("val of query: "+val);
						DialogActionMenu.actionMenu.unfreezeBuilder(val);
					}
					else if(message==Message.QUEST)
					{
						int questid=in.readInt();
						boolean isObjective=in.readBoolean();
						if(isObjective)
						{
							String status=in.readString();
							player.quests.get(Quest.questList.get(questid)).update(status);
							//System.out.println("status: "+status);
						}
						else
						{
							boolean add=in.readBoolean();
							if(add)
							{
								String status=in.readString();
								player.quests.addQuest(Quest.questList.get(questid).clone(status));
							}
							else
							{
								player.quests.remove(Quest.questList.get(questid));
								System.out.println("quest removed");
							}
						}
					}
					else if(message==Message.TIME)
					{
						byte sec=in.readByte();
						byte min=in.readByte();
						byte hour=in.readByte();
						byte day=in.readByte();
						byte month=in.readByte();
						int year=in.readInt();
						if(GamePanel.panel.hud!=null)
							GamePanel.panel.hud.time=new GameTime(null,sec,min,hour,day,month,year);
						//System.out.println(min+" "+hour+" "+" "+day+" "+month+" "+year);
					}
					message=in.readByte();
					//System.out.println("message: "+message);
				}//end while
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				running=false;
			}
		}
	}
	
	public void flush() throws IOException
	{
		if(!out.isEmpty())
		{
			out.flush();
		}
	}
}
