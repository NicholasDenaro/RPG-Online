import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;


public class MessageCluster
{
	private LinkedList<Message> messages;
	
	public MessageCluster()
	{
		messages=new LinkedList<Message>();
	}
	
	public LinkedList<Message> getMessages()
	{
		return(messages);
	}
	
	synchronized public void addMessage(Message mes)
	{
		messages.addLast(mes);
	}
	
	synchronized public void addCluster(MessageCluster cluster)
	{
		Iterator<Message> it=cluster.messages.iterator();
		while(it.hasNext())
		{
			messages.add(it.next());
		}
	}
}
