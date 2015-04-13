import java.awt.Point;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


public class ServerClientHandler extends Thread
{
	
	//health = -0.0064*level^3 + 2.6551*level^2 - 2.7112*level + 70.55
	public static int HEALTIME=60*10;//x in game minutes
	
	private ServerEngine server;
	private Socket socket;
	private boolean running;
	private long uuid;
	private CustomOutputStream out;
	private CustomInputStream in;
	private PlayerGhostEntity ghost;
	private PlayerStatus stats;
	private SkillList skills;
	private Inventory inventory;
	private Equipment equipment;
	private PlayerConditions conditions;
	private QuestList quests;
	private short zone;
	private String username;
	private byte lastHunger;
	
	private MessageCluster messageCluster;
	
	private byte lastmin;
	
	private int heal=0;
	
	//private boolean battleStance;
	private int battleStanceCount=0;
	
	public ServerClientHandler(ServerEngine ser, Socket soc, long id)
	{
		server=ser;
		socket=soc;
		running=false;
		uuid=id;
		username=null;
		ghost=null;
		heal=0;
		
		messageCluster=new MessageCluster();
		
		try
		{
			in=new CustomInputStream(socket.getInputStream());
			out=new CustomOutputStream(socket.getOutputStream());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	synchronized public void addMessageCluster(MessageCluster cluster)
	{
		messageCluster.addCluster(cluster);
	}
	
	synchronized public void addMessage(Message mes)
	{
		if(mes.getId()==Message.TIME)
		{
			if(ServerEngine.server.time.getMinute()!=lastmin)
			{
				messageCluster.addMessage(mes);
				lastmin=ServerEngine.server.time.getMinute();
			}
		}
		else
		{
			messageCluster.addMessage(mes);
		}
		if(mes.getId()==Message.TIME)
		{
			if(stats!=null)
			{
				//if(battleStance)
				//{
					if(stats.currentAp<stats.getStat(PlayerStat.ap).value())
					{
						battleStanceCount++;
						if(battleStanceCount>=30)//30 in game seconds
						{
							battleStanceCount=0;
							stats.currentAp+=stats.getAPs(equipment);
							Message statmes=new Message(Message.STAT);
							statmes.addBoolean(true);
							statmes.addByte(PlayerStat.ap);
							statmes.addInt(stats.currentAp);
							statmes.addBoolean(false);
							messageCluster.addMessage(statmes);
						}
					}
				/*}
				else if(stats.currentAp>0)
				{
					battleStanceCount--;
					if(battleStanceCount<=0)
					{
						stats.currentAp=0;
						Message statmes=new Message(Message.STAT);
						statmes.addBoolean(true);
						statmes.addByte(PlayerStat.ap);
						statmes.addInt(stats.currentAp);
						statmes.addBoolean(false);
						messageCluster.addMessage(statmes);
					}
				}*/
			}
			
			if(conditions!=null)
			{
				
				MessageCluster cluster=conditions.checkEffectsServer();
				messageCluster.addCluster(cluster);
			}
			
			if(stats!=null)
			{
				ItemEffect effect=new ItemEffect(new PlayerStat(PlayerStat.hunger,1));
				stats.increase(effect.getStat());
				if(stats.getStat(PlayerStat.hunger).value()*100.0/PlayerStatus.FOODMAX>lastHunger)
				{
					lastHunger=(byte)(stats.getStat(PlayerStat.hunger).value()*100.0/PlayerStatus.FOODMAX);
					Message mess=new Message(Message.STAT);
					mess.addBoolean(true);
					mess.addByte(effect.getStat().getId());
					mess.addInt(stats.getStat(effect.getStat().getId()).value());
					messageCluster.addMessage(mess);
				}
				
				int curhp=stats.getStat(PlayerStat.health).value();
				int maxhp=stats.getStat(PlayerStat.maxhealth).value();
				int hunger=stats.getStat(PlayerStat.hunger).value();
				
				if((curhp<maxhp)&&(curhp*1.0/maxhp>=0.43)&&(hunger*100.0/PlayerStatus.FOODMAX<50))
					heal++;
				else
					heal=0;
				
				if(heal>=HEALTIME)
				{
					int vit=stats.getStat(PlayerStat.vitality).value();
					int hp=(int)(-0.00005*vit*vit*vit*vit + 0.0035*vit*vit*vit - 0.1046*vit*vit + 3.1047*vit);
					stats.increase(new PlayerStat(PlayerStat.health,hp));
					Message mess=new Message(Message.STAT);
					mess.addBoolean(true);
					mess.addByte(PlayerStat.health);
					mess.addInt(stats.getStat(PlayerStat.health).value());
					messageCluster.addMessage(mess);
					heal=0;
				}
			}
			
			if(skills!=null)
			{
				skills.tick();
			}
		}
	}
	
	public long getUUID()
	{
		return(uuid);
	}
	
	public long getPlayerUUID()
	{
		return(ghost.uuid);
	}
	
	public short getZone()
	{
		return(zone);
	}
	
	public short getPlayerX()
	{
		return(ghost.getX());
	}
	
	public short getPlayerY()
	{
		return(ghost.getY());
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
				while(message!=Message.END)
				{
					//System.out.println("message: "+message);
					if(message==Message.REGISTER)
					{
						String user=in.readString();
						String pass=in.readString();
						uuid=server.database.register(user,pass);
						Message mes=new Message(Message.LOGIN);
						mes.addBoolean(uuid!=-1);
						if(uuid!=-1)
						{
							login(mes);
						}
						else
						{
							System.out.println("failed register");
							mes.addBoolean(true);
							out.addMessage(mes);
						}
						out.flush();
					}
					else if(message==Message.LOGIN)
					{
						String user=in.readString();
						String pass=in.readString();
						//System.out.println("user: "+user+" \n  pass: "+pass);
						uuid=server.database.login(user,pass);
						Message mes=new Message(Message.LOGIN);
						mes.addBoolean(uuid!=-1);
						if(uuid!=-1)
						{
							login(mes);
						}
						else
						{
							mes.addBoolean(false);
							out.addMessage(mes);
						}
						out.flush();
					}
					else if(message==Message.LOGOUT)
					{
						System.out.println(username+" logged out");
						logout();
						running=false;
						break;
					}
					else if(message==Message.MOVE)
					{
						if(in.readBoolean())
						{
							double xspeed=in.readByte()/2.0;
							double yspeed=in.readByte()/2.0;
							int xx=0, yy=0;
							if(xspeed>0)
								xx=1;
							else if(xspeed<0)
								xx=-1;
							
							if(yspeed>0)
								yy=1;
							else if(yspeed<0)
								yy=-1;
							
							if(ghost.zone.canWalk(ghost.xtrue+xx,ghost.ytrue+yy))
							{
								ghost.addMovement(new GhostAction(xspeed,yspeed));
								//ghost.setCoords();
								
								/*if(battleStance)
								{
									battleStance=false;
									Message messtat=new Message(Message.SKILL);
									messtat.addInt(0);
									messtat.addBoolean(false);
									messageCluster.addMessage(messtat);
									battleStanceCount=60*5;//5 in game minutes;
								}*/
								
								Message mes=new Message(Message.MOVE);
								mes.addLong(ghost.uuid);
								mes.addBoolean(true);
								mes.addByte((byte)(xspeed*2));
								mes.addByte((byte)(yspeed*2));
								server.addZoneMessage(zone,mes,this);
								
								ZoneWarpPoint warp;
								if((warp=ghost.zone.isWarpPoint(ghost.xtrue,ghost.ytrue))!=null)
								{
									mes=new Message(Message.WARP);
									mes.addShort(warp.toZone);
									mes.addString(server.database.getZoneName(warp.toZone));
									mes.addShort(warp.tox);
									mes.addShort(warp.toy);
									addMessage(mes);
									ghost.zone.removeEntity(ghost);
									server.changeClientZone(zone,warp.toZone,uuid);
									
									mes=new Message(Message.LEAVE);
									mes.addLong(ghost.uuid);
									server.addZoneMessage(zone,mes,this);
									
									ghost.zone.removeUUID(ghost.uuid);
									zone=warp.toZone;
									ghost.zone=server.getZone(zone);
									ghost.zone.addEntity(ghost);
									ghost.uuid=ghost.zone.getUUID();
									ghost.clearMovements();
									ghost.x=warp.tox*16;
									ghost.y=warp.toy*16;
									ghost.xtrue=warp.tox;
									ghost.ytrue=warp.toy;
									
									mes=new Message(Message.UUID);
									mes.addLong(ghost.uuid);
									addMessage(mes);
									
									MessageCluster cluster=server.getZoneSnap(zone);
									messageCluster.addCluster(cluster);
									//out.flush();
									
									mes=new Message(Message.SPAWN);
									mes.addBoolean(true);
									mes.addLong(uuid);
									mes.addLong(ghost.uuid);
									mes.addShort(ghost.xtrue);
									mes.addShort(ghost.ytrue);
									server.addZoneMessage(zone,mes,this);
								}
							}
							else//can't walk
							{
								Message mes=new Message(Message.MOVE);
								mes.addLong(-1);
								mes.addBoolean(true);
								mes.addByte((byte)(-xspeed*2));
								mes.addByte((byte)(-yspeed*2));
								messageCluster.addMessage(mes);
							}
						}
						else
						{
							byte index=in.readByte();
							
							Message mes=new Message(Message.MOVE);
							mes.addLong(ghost.uuid);
							mes.addBoolean(false);
							mes.addByte(index);
							server.addZoneMessage(zone,mes,this);
						}
					}//end move
					else if(message==Message.USEITEM)
					{
						byte cursor=in.readByte();
						
						ItemStack is=inventory.get(cursor);
						if(is.getItem() instanceof UseItem)
						{
							UseItem item=(UseItem)is.getItem();
							ItemEffect[] effects=item.getEffects();
							boolean containsHunger=false;
							for(ItemEffect eff:effects)
							{
								if(eff.getStat().getId()==PlayerStat.hunger)
								{
									containsHunger=true;
									break;
								}
							}
							if(!containsHunger||stats.getStat(PlayerStat.hunger).value()*100.0/PlayerStatus.FOODMAX>0)
							{
								for(ItemEffect effect:effects)
								{
									if(!effect.isInstant())
									{
										PlayerEffect pef=new PlayerEffect(effect.getStat().getId(),effect.getStat().value(),effect.getSeconds(),effect.getMinutes(),effect.getHours());
										conditions.addEffect(pef);
										
										Message mes=new Message(Message.EFFECT);
										mes.addLong(ghost.uuid);
										mes.addBoolean(true);
										mes.addByte(pef.getId());
										mes.addInt(pef.value());
										mes.addInt(pef.getSeconds());
										mes.addInt(pef.getMinutes());
										mes.addInt(pef.getHours());
										
										addMessage(mes);
									}
									else
									{
										//instant effect!
										if(effect.getStat().getId()!=PlayerStat.hunger)
											stats.increase(effect.getStat());
										else
											stats.increase(new PlayerStat(effect.getStat().getId(),(int)(effect.getStat().value()*1.0/100*PlayerStatus.FOODMAX)));
										Message mes=new Message(Message.STAT);
										mes.addBoolean(true);
										mes.addByte(effect.getStat().getId());
										mes.addInt(stats.getStat(effect.getStat().getId()).value());
										messageCluster.addMessage(mes);
									}
								}
								inventory.remove(cursor,1);
							}
						}//use item
						else if(is.getItem() instanceof Equip)
						{
							Equip item=(Equip)is.getItem();
							Equip old=equipment.equip(item);
							inventory.remove(cursor,1);
							if(old!=null)
							{
								inventory.add(new ItemStack(old));
								Message itemmes=new Message(Message.ADDITEM);
								itemmes.addBoolean(true);
								itemmes.addInt(old.getId());
								itemmes.addInt(1);
								if(old instanceof Weapon)
								{
									Weapon wep=(Weapon)old;
									itemmes.addString(wep.getParametersAsString());
									itemmes.addBoolean(wep.isAppraised());
								}
								messageCluster.addMessage(itemmes);
							}
						}
						
						
						Message mes=new Message(Message.USEITEM);
						mes.addByte(cursor);
						messageCluster.addMessage(mes);
					}//use item
					else if(message==Message.EQUIP)
					{
						int index=in.readByte();
						Item item=equipment.getEquip(index);
						inventory.add(new ItemStack(item));
						Message equipmes=new Message(Message.EQUIP);
						equipmes.addInt(index);
						messageCluster.addMessage(equipmes);
						Message itemmes=new Message(Message.ADDITEM);
						itemmes.addBoolean(true);
						itemmes.addInt(item.getId());
						itemmes.addInt(1);
						if(item instanceof Weapon)
						{
							Weapon wep=(Weapon)item;
							itemmes.addString(wep.getParametersAsString());
							itemmes.addBoolean(wep.isAppraised());
						}
						messageCluster.addMessage(itemmes);
						equipment.removeEquip(index);
					}
					else if(message==Message.EFFECT)
					{
						PlayerEffect effect=new PlayerEffect(in.readByte(),in.readInt(),in.readInt(),in.readInt(),in.readInt());
						conditions.addEffect(effect);
						Message mes=new Message(Message.EFFECT);
						mes.addLong(ghost.uuid);
						mes.addBoolean(true);
						mes.addByte(effect.getId());
						mes.addInt(effect.value());
						mes.addInt(effect.getSeconds());
						mes.addInt(effect.getMinutes());
						mes.addInt(effect.getHours());
						addMessage(mes);
						//System.out.println("added effect");
					}
					else if(message==Message.ACTION)
					{
						byte direction=in.readByte();
						ghost.direction=direction;
						ActionEntity ent=ghost.zone.getEntityAt(ghost.getFront());
						if(ent!=null)
						{
							Message mes=new Message(Message.ACTION);
							mes.addByte(ent.getAction());
							if(ent.getAction()==Action.TALK)
							{
								NPCEntity npc=(NPCEntity)ent;
								npc.turnToFace(direction);
								mes.addString(npc.name);
								Message turnmes=new Message(Message.MOVE);
								turnmes.addLong(npc.uuid);
								turnmes.addBoolean(false);
								turnmes.addByte(npc.imageIndex);
								messageCluster.addMessage(turnmes);
							}
							messageCluster.addMessage(mes);
						}
					}
					else if(message==Message.ADDSKILL)
					{
						int skillid=in.readInt();
						Skill skill=skills.addSkill(Skill.list.get(skillid).clone(0,1));
						quests.checkCondition(new QuestCondition(QuestObjective.SKILL,Skill.list.get(skillid).getName()));
						if(skill!=null)
						{
							Message mes=new Message(Message.ADDSKILL);
							mes.addInt(skill.getId());
							mes.addInt(skill.level);
							mes.addInt(skill.cooldown);
							messageCluster.addMessage(mes);
						}
					}
					else if(message==Message.SKILL)
					{
						int skillid=in.readInt();
						long targetId=in.readLong();
						Skill skill=skills.getSkill(skillid);
						LivingEntity target=ghost.zone.getLiving(targetId);
						
						if(skillid!=0)//doesn't equal battle stance
						{
							if(target!=null&&ghost.distance(target)==1&&!target.isMoving())
							{
								if(skill.canUse(equipment,stats))
								{
									//activate battlestance
									/*if(battleStance==false)
									{
										battleStance=true;
										battleStanceCount=0;
										Message mes=new Message(Message.SKILL);
										mes.addInt(0);//battle stance skill
										mes.addBoolean(true);
										messageCluster.addMessage(mes);
									}*/
									
									Message mes=new Message(Message.SKILL);
									mes.addInt(skillid);
									messageCluster.addMessage(mes);
									int dmg=skill.use(ghost,target);
									stats.currentAp-=skill.cost;
									//use skill stuffs
									mes=new Message(Message.STAT);
									mes.addBoolean(true);
									mes.addByte(PlayerStat.ap);
									mes.addInt(stats.currentAp);
									mes.addBoolean(false);
									messageCluster.addMessage(mes);
									
									
									mes=new Message(Message.STAT);
									mes.addBoolean(false);
									mes.addLong(target.uuid);
									mes.addByte(PlayerStat.health);
									mes.addInt(target.stats.getStat(PlayerStat.health).value());
									ServerEngine.server.addZoneMessage(target.zone.getId(),mes,null);
									
									if(target.stats.getStat(PlayerStat.health).value()>0)
									{
										if(target instanceof MonsterEntity)
										{
											
											((MonsterEntity)target).wait=Math.min(((MonsterEntity)target).wait+5,30);
											((MonsterEntity)target).addAttacker(ghost.uuid);
										}
									}
									else
									{
										if(target instanceof MonsterEntity)
										{
											String monstername=((MonsterEntity)target).name;
											quests.checkCondition(new QuestCondition(QuestObjective.KILL,monstername));
											
											int exp=target.stats.getStat(PlayerStat.exp).value();
											int oldlevel=stats.getStat(PlayerStat.level).value();
											stats.increase(new PlayerStat(PlayerStat.exp,exp));
											if(stats.getStat(PlayerStat.level).value()>oldlevel)//check level up
											{
												Message statmes=new Message(Message.STAT);
												statmes.addBoolean(true);
												statmes.addByte(PlayerStat.level);
												statmes.addInt(stats.getStat(PlayerStat.level).value());
												messageCluster.addMessage(statmes);
												
												statmes=new Message(Message.STAT);
												statmes.addBoolean(true);
												statmes.addByte(PlayerStat.maxhealth);
												statmes.addInt(stats.getStat(PlayerStat.maxhealth).value());
												messageCluster.addMessage(statmes);
												
												statmes=new Message(Message.STAT);
												statmes.addBoolean(true);
												statmes.addByte(PlayerStat.health);
												statmes.addInt(stats.getStat(PlayerStat.health).value());
												messageCluster.addMessage(statmes);
												
												statmes=new Message(Message.STAT);
												statmes.addBoolean(true);
												statmes.addByte(PlayerStat.statpoints);
												statmes.addInt(stats.getStat(PlayerStat.statpoints).value());
												messageCluster.addMessage(statmes);
											}
											ArrayList<ItemStack> drops=MonsterEntity.monsterList.get(((MonsterEntity)target).name).drops.dropItems();
											for(int i=0;i<drops.size();i++)
											{
												ItemStack item=drops.get(i);
												inventory.add(item);
												Message itemmes=new Message(Message.ADDITEM);
												itemmes.addBoolean(true);
												itemmes.addInt(item.getItem().getId());
												itemmes.addInt(item.count());
												if(item.getItem() instanceof Weapon)
												{
													Weapon wep=(Weapon)item.getItem();
													itemmes.addString(wep.getParametersAsString());
													itemmes.addBoolean(wep.isAppraised());
												}
												messageCluster.addMessage(itemmes);
											}
										}
										mes=new Message(Message.LEAVE);
										mes.addLong(target.uuid);
										ServerEngine.server.addZoneMessage(target.zone.getId(),mes,null);
										target.zone.removeEntity(target);
									}
								}//can use
							}//can attack
						}
						else//it is battle stance
						{
							/*if(battleStance==false)
							{
								battleStance=true;
								battleStanceCount=0;
								Message mes=new Message(Message.SKILL);
								mes.addInt(skillid);
								mes.addBoolean(true);
								messageCluster.addMessage(mes);
							}*/
						}
					}
					else if(message==Message.QUERY)
					{
						byte query=in.readByte();
						processQuery(query);
					}
					else if(message==Message.CRAFTITEM)
					{
						//List<Integer> indexlist=new ArrayList<Integer>();
						Inventory itemlist=new Inventory();
						int size=in.readInt();
						for(int i=0;i<size;i++)
						{
							int itemId=in.readInt();
							int count=in.readInt();
							Item item=Item.get(itemId);
							if(item instanceof Weapon)
							{
								Weapon wep=(Weapon)item;
								wep.setParameters(in.readString());
								wep.setAppraised(in.readBoolean());
								itemlist.add(new ItemStack(wep,count));
							}
							else
								itemlist.add(new ItemStack(item,count));
							System.out.println(itemlist.get(0).getItem().getId()+": "+itemlist.get(0).count());
						}
						boolean hasItems=true;
						for(int i=0;i<size;i++)
						{
							if(!inventory.contains(itemlist.get(i)))
							{
								hasItems=false;
								break;
							}
						}
						CraftingRecipe recipe=CraftingRecipe.recipes.get(itemlist);
						System.out.println("recipe: "+recipe);
						if(hasItems&&recipe!=null)
						{
							//if((recipe.getTool()!=null&&equipment.getWeapon()!=null&&recipe.getTool().getId()==equipment.getWeapon().getId())||(recipe.getTool()==equipment.getWeapon()))
							if(recipe.getTool().equals(equipment.getWeapon()))
							{
								for(int i=0;i<itemlist.size();i++)
								{
									int index=inventory.remove(itemlist.get(i));
									Message mes=new Message(Message.ADDITEM);
									mes.addBoolean(false);
									mes.addInt(index);
									mes.addInt(itemlist.get(i).count());
									addMessage(mes);
								}
								ItemStack item=recipe.getProduct();
								inventory.add(item);
								Message mes=new Message(Message.ADDITEM);
								mes.addBoolean(true);
								mes.addInt(item.getItem().getId());
								mes.addInt(item.count());
								if(item.getItem() instanceof Weapon)
								{
									Weapon wep=(Weapon)item.getItem();
									mes.addString(wep.getParametersAsString());
									mes.addBoolean(wep.isAppraised());
								}
								addMessage(mes);
							}
						}
					}
					message=in.readByte();
				}//end while
			}
			catch(Exception ex)
			{
				if(!ex.toString().contains("Connection reset"))
					ex.printStackTrace();
				logout();
				running=false;
				System.out.println("ServerClienthandler caught this: run");
			}
		}
	}
	
	public void processQuery(int query)
	{
		switch(query)
		{
			case Query.QUESTDONE:
				//System.out.println("Processing QUESTDONE");
				int questId=in.readInt();
				Message mes=new Message(Message.QUERY);
				mes.addBoolean(server.database.isQuestFinished(username,questId));
				addMessage(mes);
			break;
			case Query.HASQUEST:
				//System.out.println("Processing QUESTDONE");
				questId=in.readInt();
				mes=new Message(Message.QUERY);
				mes.addBoolean(quests.contains(questId));
				addMessage(mes);
			break;
			case Query.STARTQUEST:
				//System.out.println("Processing QUESTDONE");
				questId=in.readInt();
				mes=new Message(Message.QUERY);
				if(!quests.contains(questId))
				{
					mes.addBoolean(true);
					Message questmes=new Message(Message.QUEST);
					questmes.addInt(questId);
					questmes.addBoolean(false);
					questmes.addBoolean(true);
					questmes.addString("");
					addMessage(questmes);
					quests.addQuest(Quest.questList.get(questId).clone());
				}
				else
					mes.addBoolean(false);
				addMessage(mes);
				
			break;
			case Query.COMPLETEQUEST:
				questId=in.readInt();
				Quest q;
				if((q=quests.get(questId))!=null)
				{
					mes=new Message(Message.QUERY);
					if(q.canComplete())
					{
						mes.addBoolean(true);
						//TODO give exp and rewards and stuff
						server.database.updateFinishedQuests(username,q);
						quests.remove(q);
						Message questmes=new Message(Message.QUEST);
						questmes.addInt(questId);
						questmes.addBoolean(false);
						questmes.addBoolean(false);
						addMessage(questmes);
						//collect rewards.
					}
					else
					{
						mes.addBoolean(false);
					}
					addMessage(mes);
				}
			break;
		}
	}
	
	public boolean login(Message mes)
	{
		try
		{
			username=server.database.getPlayerName(uuid);
			System.out.println(username+" logged in");
			server.addClient(this,uuid);
			mes.addString(username);
			mes.addLong(uuid);
			ResultSet result=server.database.getPositionData(username);
			zone=result.getShort(2);
			short x=result.getShort(3);
			short y=result.getShort(4);
			
			ghost=new PlayerGhostEntity(server.getZone(zone),uuid,result.getShort(3)*16,result.getShort(4)*16);
			ghost.uuid=ghost.zone.getUUID();
			ghost.zone.addEntity(ghost);
			ghost.handler=this;
			
			mes.addLong(ghost.uuid);
			mes.addShort(zone);
			mes.addString(server.database.getZoneName(zone));
			mes.addShort(x);
			mes.addShort(y);
			
			messageCluster.addMessage(mes);
			
			//System.out.println("completed user info");
			
			//stats
			result=server.database.getPlayerStats(username);
			stats=new PlayerStatus(result.getInt(2),result.getInt(3),result.getInt(4),result.getInt(5),result.getInt(6),result.getInt(7),result.getInt(8),result.getInt(9),result.getInt(10),result.getInt(11),result.getInt(12),result.getInt(13),result.getInt(14));
			ghost.stats=stats;
			
			Message statsmes=new Message(Message.STATS);
			statsmes.addInt(stats.getStat(PlayerStat.level).value());
			statsmes.addInt(stats.getStat(PlayerStat.statpoints).value());
			statsmes.addInt(stats.getStat(PlayerStat.exp).value());
			statsmes.addInt(stats.getStat(PlayerStat.maxhealth).value());
			statsmes.addInt(stats.getStat(PlayerStat.health).value());
			statsmes.addInt(stats.getStat(PlayerStat.durability).value());
			statsmes.addInt(stats.getStat(PlayerStat.stamina).value());
			statsmes.addInt(stats.getStat(PlayerStat.hunger).value());
			statsmes.addInt(stats.getStat(PlayerStat.agility).value());
			statsmes.addInt(stats.getStat(PlayerStat.dexterity).value());
			statsmes.addInt(stats.getStat(PlayerStat.vitality).value());
			statsmes.addInt(stats.getStat(PlayerStat.strength).value());
			statsmes.addInt(stats.getStat(PlayerStat.defense).value());

			messageCluster.addMessage(statsmes);
			
			//System.out.println("completed stats");
			
			//skills
			skills=new SkillList();
			result=server.database.getPlayerSkills(username);
			while(result.next())
			{
				Skill skill=Skill.list.get(result.getInt(1)).clone(result.getInt(2),result.getInt(3));
				skills.addSkill(skill);
				mes=new Message(Message.ADDSKILL);
				mes.addInt(skill.getId());
				mes.addInt(skill.getLevel());
				mes.addInt(skill.getCooldown());
				messageCluster.addMessage(mes);
			}
			
			//System.out.println("completed skills");
			
			//conditions
			conditions=new PlayerConditions(ghost);
			result=server.database.getConditionData(username);
			
			while(result.next())
			{
				int effid=result.getInt(1);
				int effval=result.getInt(2);
				int effsec=result.getInt(3);
				int effmin=result.getInt(4);
				int effhour=result.getInt(5);
				System.out.println("adding effect: "+effid+", "+effval+"," +effsec+", "+effmin+", "+effsec);
				
				PlayerEffect pef=new PlayerEffect((byte)effid,effval,effsec,effmin,effhour);
				conditions.addEffect(pef);
				mes=new Message(Message.EFFECT);
				mes.addLong(ghost.uuid);
				mes.addBoolean(true);
				mes.addByte(pef.getId());
				mes.addInt(pef.value());
				mes.addInt(pef.getSeconds());
				mes.addInt(pef.getMinutes());
				mes.addInt(pef.getHours());
				messageCluster.addMessage(mes);
			}
			
			ghost.conditions=conditions;
			
			//System.out.println("completed conditions");
			
			//items
			result=server.database.getItemData(username);
			inventory=new Inventory();
			
			while(result.next())
			{
				int itemid=result.getInt(1);
				int itemcount=result.getInt(2);
				mes=new Message(Message.ADDITEM);
				mes.addBoolean(true);
				mes.addInt(itemid);
				mes.addInt(itemcount);
				if(Item.get(itemid) instanceof Weapon)
				{
					String params=result.getString(3);
					boolean appraised=result.getBoolean(4);
					Weapon wep=new Weapon((Weapon)Item.get(itemid),params,appraised);
					mes.addString(params);
					mes.addBoolean(appraised);
					inventory.add(new ItemStack(wep));
				}
				else
					inventory.add(new ItemStack(itemid,itemcount));
				
				messageCluster.addMessage(mes);
			}
			//System.out.println("completed items");
			
			//equipment
			result=server.database.getEquipData(username);
			equipment=new Equipment(result.getInt(2),result.getInt(3),result.getInt(4),result.getInt(5),result.getInt(6),result.getInt(7),result.getInt(8),result.getInt(9), result.getString(10), result.getBoolean(11));
			ghost.equipment=equipment;
			
			mes=new Message(Message.EQUIPALL);
			mes.addInt(result.getInt(2));
			mes.addInt(result.getInt(3));
			mes.addInt(result.getInt(4));
			mes.addInt(result.getInt(5));
			mes.addInt(result.getInt(6));
			mes.addInt(result.getInt(7));
			mes.addInt(result.getInt(8));
			mes.addInt(result.getInt(9));
			mes.addString(result.getString(10));
			mes.addBoolean(result.getBoolean(11));
			messageCluster.addMessage(mes);
			
			//System.out.println("completed equips");
			
			quests=new QuestList(this);
			//Quests
			result=server.database.getCurrentQuests(username);
			while(result.next())
			{
				System.out.println("quest");
				int questid=result.getInt(1);
				System.out.println("quest id: "+questid);
				String status=result.getString(2);
				System.out.println("status: "+status);
				Quest quest=Quest.questList.get(questid).clone(status);
				quests.addQuest(quest);
				
				Message mesQuest=new Message(Message.QUEST);
				mesQuest.addInt(questid);
				mesQuest.addBoolean(false);
				mesQuest.addBoolean(true);
				mesQuest.addString(status);
				messageCluster.addMessage(mesQuest);
			}
			
			//spawn!
			Message spawnmes=new Message(Message.SPAWN);
			spawnmes.addBoolean(true);
			spawnmes.addLong(uuid);
			spawnmes.addLong(ghost.uuid);
			spawnmes.addShort(x);
			spawnmes.addShort(y);
			//System.out.println("before addZoneMessage: "+zone);
			server.addZoneMessage(zone,spawnmes,this);
			
			//System.out.println("completed spawn");
			
			MessageCluster cluster=server.getZoneSnap(zone);
			server.setClientZone(zone,uuid);
			messageCluster.addCluster(cluster);
			//System.out.println(username+" completed login.");
			return(true);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("ServerClienthandler caught this: login");
			return(false);
		}
	}

	public void logout()
	{
		if(username!=null)
		{
			String oldusername=username;
			username=null;
			System.out.println(oldusername+" logging out");
			server.database.logout(oldusername);
			server.database.updatePositionData(oldusername,zone,ghost.getX(),ghost.getY());
			server.database.updatePlayerStats(oldusername,stats);
			server.database.updatePlayerSkills(oldusername,skills);
			server.database.updateItemData(oldusername,inventory);
			server.database.updateEquipData(oldusername,equipment);
			server.database.updateConditionData(oldusername,conditions);
			server.database.updateCurrentQuests(oldusername,quests);
			username=null;
			server.removeClient(this);
			//System.out.println("removed client");
			ghost.zone.removeEntity(ghost);
			//System.out.println("removed ghost");
			Message mes=new Message(Message.LOGOUT);
			mes.addLong(ghost.uuid);
			//System.out.println("Before addZoneMessage");
			server.addZoneMessage(zone,mes,this);
			//System.out.println("end log out message");
			System.out.println(oldusername+" logged out");
		}
	}
	
	synchronized public void flush()// throws IOException
	{
		LinkedList<Message> messages=messageCluster.getMessages();
		int size=messages.size();
		int i=0, messageSize=0;
		while(i<size&&messageSize<1024)
		{
			Message mes=messages.poll();
			messageSize+=mes.size();
			out.addMessage(mes);
			i++;
		}
		if(!out.isEmpty())
		{
			//System.out.println("out.size(): "+out.size());
			out.flush();
		}
	}
}
