import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.UUID;


public class ServerDatabase
{
	private ServerEngine server;
	private Connection database;
	private Connection databaseItems;
	private Connection databaseStats;
	private Connection databaseConditions;
	private Connection databaseMonsterDrops;
	private Connection databaseQuests;
	
	public ServerDatabase(ServerEngine ser, String loc, String user, String pass)
	{
		server=ser;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			
			database=DriverManager.getConnection(loc,user,pass);
			databaseItems=DriverManager.getConnection(loc+"_items",user,pass);
			databaseStats=DriverManager.getConnection(loc+"_stats",user,pass);
			databaseConditions=DriverManager.getConnection(loc+"_conditions",user,pass);
			databaseMonsterDrops=DriverManager.getConnection(loc+"_monster_drops",user,pass);
			databaseQuests=DriverManager.getConnection(loc+"_quests",user,pass);
		}
		catch(Exception ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	
	public long newUUID()
	{
		long id=UUID.randomUUID().getMostSignificantBits();
		while(id==-1||!isUniqueUUID(id))
		{
			id=UUID.randomUUID().getMostSignificantBits();
		}
		return(id);
	}
	
	public boolean isUniqueUUID(long id)
	{
		try
		{
			PreparedStatement state=database.prepareStatement("select * from users where uuid = ?",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			state.setLong(1,id);
			
			ResultSet results=state.executeQuery();
			return(!results.next());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return(true);
	}
	
	public long register(String user, String pass)
	{
		try
		{
			PreparedStatement state=database.prepareStatement("select * from users where Username like \'"+user+"\'",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet results=state.executeQuery();
			
			while(results.next())
			{
				if(results.getString(1).equalsIgnoreCase(user))
				{
					return(-1);
				}
			}
			
			long uuid=newUUID();
			
			String com="insert into users (Username, Password, uuid, logged_in) values ('"+user+"', '"+pass+"', '"+uuid+"', '0')";
			state=database.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			com="insert into positions (Username, Zone, x, y) values ('"+user+"', '1', '3', '3')";
			state=database.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			com="create table "+user+"_items (item_id bigint, count int, weapon_params VARCHAR(8), weapon_appraised boolean)";
			state=databaseItems.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			com="insert into equipment (Username, helmet, mask, mantle, chestpiece, leggings, boots, accessory, weapon, weapon_params, weapon_appraised) values ('"+user+"', '-1', '-1', '-1', '-1', '-1', '-1', '-1', '-1', '', '0')";
			state=databaseItems.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			com="insert into stats (Username, level, statpoints, exp, max_health, health, durability, stamina, hunger, agility, dexterity, vitality, strength, defense) values ('"+user+"', '1', '0', '0', '50', '50', '30', '1', '0', '1', '1', '1', '1', '0')";
			state=databaseStats.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			com="create table "+user+"_skills (skill_id int, level int, cooldown int)";
			state=databaseStats.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			com="alter table "+user+"_skills add primary key (skill_id)";
			state=databaseStats.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			com="create table "+user+"_info (effect_id int, value int, seconds int, minutes int, hours int)";
			state=databaseConditions.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			com="create table "+user+"_current_quests (quest_id int, value VARCHAR(32))";
			state=databaseQuests.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			com="create table "+user+"_finished_quests (quest_id int)";
			state=databaseQuests.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			return(uuid);
		
		}
		catch(Exception ex)
		{
			System.out.println("register error:");
			ex.printStackTrace();
		}
		return(-1);
	}
	
	public void deleteUser(String user)
	{
		try
		{
			PreparedStatement state=database.prepareStatement("select * from users where Username like \'"+user+"\'",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			String com="delete from users where Username = '"+user+"'";
			state=database.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			com="delete from positions where Username = '"+user+"'";
			state=database.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			com="drop table "+user+"_items";
			state=databaseItems.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			com="delete from equipment where Username = '"+user+"'";
			state=databaseItems.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			com="delete from stats where Username = '"+user+"'";
			state=databaseStats.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			com="drop table "+user+"_skills";
			state=databaseStats.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			com="drop table "+user+"_info";
			state=databaseConditions.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			com="drop table "+user+"_current_quests";
			state=databaseQuests.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			com="drop table "+user+"_finished_quests";
			state=databaseQuests.prepareStatement(com,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			state.execute();
			
			System.out.println(user+" has been deleted.");
		}
		catch(Exception ex)
		{
			System.out.println("delete user error");
			ex.printStackTrace();
		}
	}
	
	public long login(String user, String pass)
	{
		try
		{
			PreparedStatement state=database.prepareStatement("select * from users where Username like \'"+user+"\'",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet results=state.executeQuery();
			
			while(results.next())
			{
				//System.out.println("User: "+results.getString(1)+" | Pass: "+results.getString(2));
				if(results.getString(1).equalsIgnoreCase(user)&&results.getString(2).equals(pass))
				{
					if(!results.getBoolean(4))//!logged in
					{
						results.updateBoolean(4,true);
						results.updateRow();
						return(results.getLong(3));
					}
					else
						return(-1);
				}
			}
		}
		catch(Exception ex)
		{
			System.out.println("Login error:");
			ex.printStackTrace();
		}
		return(-1);
	}
	
	public void logout(String user)
	{
		try
		{
			PreparedStatement state=database.prepareStatement("select * from users where Username like \'"+user+"\'",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet results=state.executeQuery();
			
			while(results.next())
			{
				if(results.getString(1).equalsIgnoreCase(user))
				{
					results.updateBoolean(4,false);
					results.updateRow();
				}
			}
		}
		catch(Exception ex)
		{
			System.out.println("Logout error: ");
			ex.printStackTrace();
		}
	}
	
	public void logoutAll()
	{
		try
		{
			PreparedStatement state=database.prepareStatement("select * from users",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet results=state.executeQuery();
			
			while(results.next())
			{
				results.updateBoolean(4,false);
				results.updateRow();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		System.out.println("All were logged out");
	}
	
	public ResultSet getPlayerStats(String name)
	{
		try
		{
			PreparedStatement state=databaseStats.prepareStatement("select * from stats where Username like \'"+name+"\'",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet results=state.executeQuery();
			
			results.next();
			
			return(results);
		}
		catch(Exception ex)
		{
			System.out.println("Stats error: ");
			ex.printStackTrace();
		}
		return(null);
	}
	
	public void updatePlayerStats(String name, PlayerStatus stats)
	{
		try
		{
			PreparedStatement state=databaseStats.prepareStatement("select * from stats where Username like \'"+name+"\'",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet results=state.executeQuery();
			
			results.next();
			results.updateString(1,name);
			results.updateInt(2,stats.getStat(PlayerStat.level).value());
			results.updateInt(3,stats.getStat(PlayerStat.statpoints).value());
			results.updateInt(4,stats.getStat(PlayerStat.exp).value());
			results.updateInt(5,stats.getStat(PlayerStat.maxhealth).value());
			results.updateInt(6,stats.getStat(PlayerStat.health).value());
			results.updateInt(7,stats.getStat(PlayerStat.durability).value());
			results.updateInt(8,stats.getStat(PlayerStat.stamina).value());
			results.updateInt(9,stats.getStat(PlayerStat.hunger).value());
			results.updateInt(10,stats.getStat(PlayerStat.agility).value());
			results.updateInt(11,stats.getStat(PlayerStat.dexterity).value());
			results.updateInt(12,stats.getStat(PlayerStat.vitality).value());
			results.updateInt(13,stats.getStat(PlayerStat.strength).value());
			results.updateInt(14,stats.getStat(PlayerStat.defense).value());
			
			results.updateRow();
		}
		catch(Exception ex)
		{
			System.out.println("Stats update error: ");
			ex.printStackTrace();
		}
		
	}
	
	public ResultSet getPlayerSkills(String name)
	{
		try
		{
			PreparedStatement state=databaseStats.prepareStatement("select * from "+name+"_skills",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet results=state.executeQuery();
			
			return(results);
		}
		catch(Exception ex)
		{
			System.out.println("Skills error: ");
			ex.printStackTrace();
		}
		return(null);
	}
	
	public void updatePlayerSkills(String name, SkillList list)
	{
		try
		{
			PreparedStatement state;//=databaseStats.prepareStatement("select * from "+name+"_skills",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			state=databaseStats.prepareStatement("truncate "+name+"_skills");
			
			state.execute();
			
			Iterator<Skill> it=list.iterator();
			
			while(it.hasNext())
			{
				Skill skill=it.next();
				state=databaseStats.prepareStatement("insert into "+name+"_skills (skill_id, level, cooldown) values ('"+skill.getId()+"', '"+skill.getLevel()+"', '"+skill.getCooldown()+"')");
				state.execute();
			}
			
		}
		catch(Exception ex)
		{
			System.out.println("Skills error: ");
			ex.printStackTrace();
		}
	}
	
	public ResultSet getPositionData(String name)
	{
		try
		{
			PreparedStatement state=database.prepareStatement("select * from positions where Username like \'"+name+"\'",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet results=state.executeQuery();
			
			results.next();
			
			return(results);
		}
		catch(Exception ex)
		{
			System.out.println("Position error: ");
			ex.printStackTrace();
		}
		return(null);
	}
	
	public void updatePositionData(String name,short z, short i, short a)
	{
		try
		{
			PreparedStatement state=database.prepareStatement("select * from positions where Username like \'"+name+"\'",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet results=state.executeQuery();
			
			results.next();
			results.updateShort(2,z);
			results.updateShort(3,i);
			results.updateShort(4,a);
			
			results.updateRow();
		}
		catch(Exception ex)
		{
			System.out.println("Position error: ");
			ex.printStackTrace();
		}
	}
	
	public ResultSet getItemData(String name)
	{
		try
		{
			PreparedStatement state=databaseItems.prepareStatement("select * from "+name+"_items",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet results=state.executeQuery();
			
			return(results);
		}
		catch(Exception ex)
		{
			System.out.println("Item error: ");
			ex.printStackTrace();
		}
		return(null);
	}
	
	public void updateItemData(String name, Inventory inv)
	{
		try
		{
			PreparedStatement state;//=databaseItems.prepareStatement("select * from "+name+"_items",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			state=databaseItems.prepareStatement("truncate "+name+"_items");
			
			state.execute();
			
			for(int i=0;i<inv.size();i++)
			{
				ItemStack it=inv.get(i);
				if(it.getItem() instanceof Weapon)
				{
					Weapon wep=(Weapon)it.getItem();
					state=databaseItems.prepareStatement("insert into "+name+"_items (item_id, count, weapon_params, weapon_appraised) values ('"+it.getItem().getId()+"', '"+it.count()+"', '"+wep.getParametersAsString()+"', '"+(wep.isAppraised()?1:0)+"')");
					state.execute();
				}
				else
				{
					state=databaseItems.prepareStatement("insert into "+name+"_items (item_id, count, weapon_params, weapon_appraised) values ('"+it.getItem().getId()+"', '"+it.count()+"', '', '0')");
					state.execute();
				}
			}
			
			
		}
		catch(Exception ex)
		{
			System.out.println("Item update error: ");
			ex.printStackTrace();
		}
	}
	
	public ResultSet getEquipData(String name)
	{
		try
		{
			PreparedStatement state=databaseItems.prepareStatement("select * from equipment where Username like '"+name+"'",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet results=state.executeQuery();
			
			results.next();
			
			return(results);
		}
		catch(Exception ex)
		{
			System.out.println("Equip error: ");
			ex.printStackTrace();
		}
		return(null);
	}
	
	public void updateEquipData(String name, Equipment equips)
	{
		try
		{
			PreparedStatement state=databaseItems.prepareStatement("select * from equipment where Username like '"+name+"'",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet results=state.executeQuery();
			
			results.next();
			
			results.updateString(1,name);
			results.updateInt(2,equips.getHelmet()!=null?equips.getHelmet().getId():-1);
			results.updateInt(3,equips.getMask()!=null?equips.getMask().getId():-1);
			results.updateInt(4,equips.getMantle()!=null?equips.getMantle().getId():-1);
			results.updateInt(5,equips.getChestpiece()!=null?equips.getChestpiece().getId():-1);
			results.updateInt(6,equips.getLeggings()!=null?equips.getLeggings().getId():-1);
			results.updateInt(7,equips.getBoots()!=null?equips.getBoots().getId():-1);
			results.updateInt(8,equips.getAccessory()!=null?equips.getAccessory().getId():-1);
			Weapon wep=equips.getWeapon();
			if(wep!=null)
			{
				results.updateInt(9,wep.getId());
				results.updateString(10,wep.getParametersAsString());
				results.updateBoolean(11,wep.isAppraised());
			}
			else
			{
				results.updateInt(9,-1);
				results.updateString(10,"");
				results.updateBoolean(11,false);
			}
			
			results.updateRow();
		}
		catch(Exception ex)
		{
			System.out.println("Equip error: ");
			ex.printStackTrace();
		}
	}
	
	public ResultSet getConditionData(String name)
	{
		try
		{
			PreparedStatement state=databaseConditions.prepareStatement("select * from "+name+"_info",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet results=state.executeQuery();
			
			return(results);
		}
		catch(Exception ex)
		{
			System.out.println("Conditions error: ");
			ex.printStackTrace();
		}
		return(null);
	}
	
	public void updateConditionData(String name, PlayerConditions conditions)
	{
		try
		{
			PreparedStatement state;//=databaseItems.prepareStatement("select * from "+name+"_items",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			state=databaseConditions.prepareStatement("truncate "+name+"_info");
			
			state.execute();
			
			for(int i=0;i<conditions.size();i++)
			{
				PlayerEffect eff=conditions.get(i);
				state=databaseConditions.prepareStatement("insert into "+name+"_info (effect_id, value, seconds, minutes, hours) values ('"+eff.getId()+"', '"+eff.value()+"', '"+eff.getSeconds()+"', '"+eff.getMinutes()+"', '"+eff.getHours()+"')");
				state.execute();
			}
			
		}
		catch(Exception ex)
		{
			System.out.println("Conditions update error: ");
			ex.printStackTrace();
		}
	}
	
	public ResultSet getCurrentQuests(String name)
	{
		try
		{
			PreparedStatement state=databaseQuests.prepareStatement("select * from "+name+"_current_quests",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet results=state.executeQuery();
			
			return(results);
		}
		catch(Exception ex)
		{
			System.out.println("Quests error: ");
			ex.printStackTrace();
		}
		return(null);
	}
	
	public void updateCurrentQuests(String name, QuestList quests)
	{
		try
		{
			PreparedStatement state;//=databaseItems.prepareStatement("select * from "+name+"_items",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			state=databaseQuests.prepareStatement("truncate "+name+"_current_quests");
			
			state.execute();
			
			for(int i=0;i<quests.size();i++)
			{
				Quest quest=quests.get(i);
				state=databaseQuests.prepareStatement("insert into "+name+"_current_quests (quest_id, value) values ('"+quest.getId()+"', '"+quest.convertToString()+"')");
				state.execute();
			}
			
		}
		catch(Exception ex)
		{
			System.out.println("current quests update error: ");
			ex.printStackTrace();
		}
	}
	
	public boolean isQuestFinished(String name, int quest)
	{
		try
		{
			PreparedStatement state;//=databaseItems.prepareStatement("select * from "+name+"_items",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			//state=databaseConditions.prepareStatement("truncate "+name+"_info");
			
			//state.execute();
			
			state=databaseQuests.prepareStatement("select "+quest+" from "+name+"_finished_quests");
			ResultSet results=state.executeQuery();
			
			while(results.next())
			{
				if(results.getInt(1)==quest)
					return(true);
			}
			
		}
		catch(Exception ex)
		{
			System.out.println("get finished quest error: ");
			ex.printStackTrace();
		}
		return(false);
	}
	
	public void updateFinishedQuests(String name, Quest quest)
	{
		try
		{
			PreparedStatement state;//=databaseItems.prepareStatement("select * from "+name+"_items",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			//state=databaseConditions.prepareStatement("truncate "+name+"_info");
			
			//state.execute();
			
			state=databaseQuests.prepareStatement("insert into "+name+"_finished_quests (quest_id) values ('"+quest.getId()+"')");
			state.execute();
		}
		catch(Exception ex)
		{
			System.out.println("finished quests update error: ");
			ex.printStackTrace();
		}
	}
	
	public MonsterDrops getMonsterDrops(String name)
	{
		MonsterDrops drops=new MonsterDrops();
		try
		{
			PreparedStatement state=databaseMonsterDrops.prepareStatement("select * from "+name+"_drops",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet results=state.executeQuery();
			
			while(results.next())
			{
				drops.addItem(results.getInt(1),results.getString(2),results.getBoolean(3),results.getInt(4),results.getDouble(5));
			}
		}
		catch(Exception ex)
		{
			System.out.println("MonsterDrops error: ");
			ex.printStackTrace();
		}
		return(drops);
	}
	
	public ResultSet getZones()
	{
		try
		{
			PreparedStatement state=database.prepareStatement("select * from zones",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet results=state.executeQuery();
			
			results.next();
			
			return(results);
		}
		catch(Exception ex)
		{
			System.out.println("Get Zone error: ");
			ex.printStackTrace();
		}
		return(null);
	}
	
	public String getZoneName(int z)
	{
		try
		{
			PreparedStatement state=database.prepareStatement("select * from zones where ind = ?");
			
			state.setInt(1,z);
			
			//System.out.println("statement: "+state);
			
			ResultSet results=state.executeQuery();
			
			results.next();
			
			return(results.getString(2));
		}
		catch(Exception ex)
		{
			System.out.println("Get Zone Name error: ");
			ex.printStackTrace();
		}
		return("Void");
	}
	
	public GameTime loadTime()
	{
		try
		{
			PreparedStatement state=database.prepareStatement("select * from time_");
			
			ResultSet results=state.executeQuery();
			
			results.next();
			
			return(new GameTime(server,results.getByte(1),results.getByte(2),results.getByte(3),results.getByte(4),results.getByte(5),results.getInt(6)));
		}
		catch(Exception ex)
		{
			System.out.println("Time error");
			ex.printStackTrace();
		}
		return(null);
	}
	
	public void updateTime(GameTime time)
	{
		try
		{
			PreparedStatement state=database.prepareStatement("truncate time_");
			
			state.execute();
			
			state=database.prepareStatement("insert into time_ (seconds, minutes, hours, days, months, years) values ('"+time.getSecond()+"', '"+time.getMinute()+"', '"+time.getHour()+"', '"+time.getDay()+"', '"+time.getMonth()+"', '"+time.getYear()+"')");
			
			state.execute();
		}
		catch(Exception ex)
		{
			System.out.println("Time update error");
			ex.printStackTrace();
		}
	}
	
	public String getPlayerName(long uuid)
	{
		try
		{
			PreparedStatement state=database.prepareStatement("select * from users where uuid = ?");
			
			state.setLong(1,uuid);
			
			ResultSet results=state.executeQuery();
			
			results.next();
			
			return(results.getString(1));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return("VOID");
	}
}
