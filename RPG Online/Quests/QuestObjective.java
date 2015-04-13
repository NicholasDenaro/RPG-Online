
public class QuestObjective
{
	private byte type;
	private String target;
	private int count;
	private int completed;
	
	public QuestObjective(byte ty,String tar, int c)
	{
		type=ty;
		target=tar;
		count=c;
		completed=0;
	}
	
	public QuestObjective(QuestObjective other, int compl)
	{
		type=other.type;
		target=other.target;
		count=other.count;
		completed=compl;
	}
	
	public boolean checkCondition(QuestCondition cond)
	{
		if(cond.type==type)
		{
			if(cond.target.equalsIgnoreCase(target))
			{
				completed+=1;
				return(true);
			}
		}
		return(false);
	}
	
	public boolean isComplete()
	{
		return(completed>=count);
	}
	
	public String convertToString()
	{
		return(""+completed);
	}
	
	public String toString()
	{
		String mark=(isComplete()?"!":"");
		switch(type)
		{
			case TALK:
				return(mark+"Talk to: "+target);
			case KILL:
				return(mark+"Kill: "+((count>1)?(completed+"/"):"")+count+" "+target+((count>1)?"s":""));
			case GATHER:
				return(mark+"Gather: "+((count>1)?(completed+"/"):"")+count+" "+target+((count>1)?"s":""));
			case SKILL:
				return(mark+"Get skill: "+target);
			case USEITEM:
				return(mark+"Use item: "+target);
			case CRAFT:
				return(mark+"Craft: "+target);
			default:
				return("oops...");
		}
	}
	
	public static final byte TALK=0;
	public static final byte KILL=1;
	public static final byte GATHER=2;
	public static final byte SKILL=3;
	public static final byte USEITEM=4;
	public static final byte CRAFT=5;
}
