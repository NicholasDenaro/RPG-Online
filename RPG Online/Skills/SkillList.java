import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;


public class SkillList
{
	private  HashMap<Integer, Skill> skills;
	
	public SkillList()
	{
		skills=new HashMap<Integer, Skill>();
	}
	
	public Skill addSkill(Skill skill)
	{
		if(!skills.containsKey(skill.getId()))
		{
			skills.put(skill.getId(),skill);
			return(skill);
		}
		return(null);
	}
	
	public Skill getSkill(int skill)
	{
		if(skills.containsKey(skill))
		{
			return(skills.get(skill));
		}
		
		return(null);
	}
	
	public Skill getSkillAtIndex(int index)
	{
		SortedSet<Integer> keys=new TreeSet<Integer>(skills.keySet());
		Iterator<Integer> it=keys.iterator();
		int i=0;
		while(i<index&&it.hasNext())
		{
			it.next();
		}
		return(skills.get(it.next()));
	}
	
	public void tick()
	{
		Iterator<Skill> it=skills.values().iterator();
		
		while(it.hasNext())
		{
			Skill skill=it.next();
			skill.tick();
		}
	}
	
	public int size()
	{
		return(skills.size());
	}
	
	public Iterator<Skill> iterator()
	{
		return(skills.values().iterator());
	}
}
