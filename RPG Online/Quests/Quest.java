import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class Quest
{
	public static HashMap<Integer,Quest> questList=new HashMap<Integer,Quest>();
	
	private int id;
	private String name;
	private List<QuestObjective> objectives;
	
	public Quest(int i, String n, QuestObjective[] obj)
	{
		questList.put(i,this);
		id=i;
		name=n;
		objectives=Arrays.asList(obj);
	}
	
	public Quest(int i, String n, List<QuestObjective> obj)
	{
		id=i;
		name=n;
		objectives=obj;
	}
	
	public int getId()
	{
		return(id);
	}
	
	public String getName()
	{
		return(name);
	}
	
	public List<QuestObjective> getObjectives()
	{
		return(objectives);
	}
	
	public boolean canComplete()
	{
		for(int i=0;i<objectives.size();i++)
		{
			if(!objectives.get(i).isComplete())
				return(false);
		}
		return(true); 
	}
	
	public String convertToString()
	{
		String str="";
		str+=objectives.get(0).convertToString();
		for(int i=1;i<objectives.size();i++)
		{
			str+=","+objectives.get(i).convertToString();
		}
		return(str);
	}
	
	public boolean checkCondition(QuestCondition cond)
	{
		boolean changed=false;
		for(int i=0;i<objectives.size();i++)
		{
			changed=objectives.get(i).checkCondition(cond)?(true):(changed);
		}
		return(changed);
	}
	
	public Quest clone()
	{
		return(new Quest(id,name,objectives));
	}
	
	public Quest clone(String status)
	{
		if(status.isEmpty())
			return(clone());
		ArrayList<QuestObjective> objs=new ArrayList<QuestObjective>();
		
		for(int i=0;i<objectives.size();i++)
		{
			int val=0;
			if(status.indexOf(',')!=-1)
			{
				val=new Integer(status.substring(0,status.indexOf(',')));
				status=status.substring(status.indexOf(',')+1);
			}
			else
			{
				val=new Integer(status);
			}
			objs.add(new QuestObjective(objectives.get(i),val));
		}
		
		return(new Quest(id,name,objs));
	}
	
	public void update(String status)
	{
		ArrayList<QuestObjective> objs=new ArrayList<QuestObjective>();
		
		for(int i=0;i<objectives.size();i++)
		{
			int val=0;
			if(status.indexOf(',')!=-1)
			{
				val=new Integer(status.substring(0,status.indexOf(',')));
				status=status.substring(status.indexOf(',')+1);
			}
			else
			{
				val=new Integer(status);
			}
			objs.add(new QuestObjective(objectives.get(i),val));
		}
		objectives=objs;
	}
	
	public static Quest questSlimes=new Quest(0,"Learn The Ropes",new QuestObjective[]{new QuestObjective(QuestObjective.SKILL,"Punch",1),new QuestObjective(QuestObjective.KILL,"Slime",3)});
}
