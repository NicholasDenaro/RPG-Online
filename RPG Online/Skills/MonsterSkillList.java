import java.util.HashMap;
import java.util.Iterator;


public class MonsterSkillList
{
private  HashMap<Integer, MonsterSkill> skills;
	
	public MonsterSkillList()
	{
		skills=new HashMap<Integer, MonsterSkill>();
	}
	
	public MonsterSkillList addSkill(MonsterSkill skill)
	{
		if(!skills.containsKey(skill.getId()))
		{
			skills.put(skill.getId(),skill);
		}
		return(this);
	}
	
	public MonsterSkill getSkill(int skill)
	{
		if(skills.containsKey(skill))
		{
			return(skills.get(skill));
		}
		
		return(null);
	}
	
	public void tick()
	{
		Iterator<MonsterSkill> it=skills.values().iterator();
		
		while(it.hasNext())
		{
			MonsterSkill skill=it.next();
			skill.tick();
		}
	}
	
	public Iterator<MonsterSkill> iterator()
	{
		return(skills.values().iterator());
	}
	
	public MonsterSkillList clone()
	{
		MonsterSkillList skills=new MonsterSkillList();
		
		Iterator<MonsterSkill> it=iterator();
		
		while(it.hasNext())
		{
			MonsterSkill skill=it.next();
			skills.addSkill(new MonsterSkill(skill,skill.getLevel(),0));
		}
		
		return(skills);
	}
}
