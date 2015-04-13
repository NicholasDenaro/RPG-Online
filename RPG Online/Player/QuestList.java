import java.util.ArrayList;


public class QuestList
{
	private ServerClientHandler handler;
	private ArrayList<Quest> quests;
	
	public QuestList(ServerClientHandler hand)
	{
		handler=hand;
		quests=new ArrayList<Quest>();
	}

	public void addQuest(Quest q)
	{
		quests.add(q);
	}
	
	public Quest get(int index)
	{
		return(quests.get(index));
	}
	
	public Quest get(Quest q)
	{
		for(int i=quests.size()-1;i>=0;i--)
		{
			if(quests.get(i).getId()==q.getId())
				return(quests.get(i));
		}
		return(null);
	}
	
	public void remove(Quest q)
	{
		for(int i=quests.size()-1;i>=0;i--)
		{
			if(quests.get(i).getId()==q.getId())
				quests.remove(i);
		}
	}
	
	public int size()
	{
		return(quests.size());
	}
	
	public boolean contains(int questId)
	{
		for(Quest q:quests)
		{
			if(q.getId()==questId)
				return(true);
		}
		return(false);
	}
	
	public void checkCondition(QuestCondition cond)
	{
		for(Quest q:quests)
		{
			if(q.checkCondition(cond)&&handler!=null)
			{
				Message mes=new Message(Message.QUEST);
				mes.addInt(q.getId());
				mes.addBoolean(true);
				mes.addString(q.convertToString());
				handler.addMessage(mes);
			}
		}
	}
}
