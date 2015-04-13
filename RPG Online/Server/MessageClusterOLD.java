import java.nio.ByteBuffer;


public class MessageClusterOLD
{
	protected ByteBuffer buffer;
	//private boolean canSend;
	
	public MessageClusterOLD()
	{
		buffer=ByteBuffer.allocate(0);
		//canSend=true;
	}
	
	public boolean isEmpty()
	{
		return(buffer.capacity()==0);
	}
	
	public int size()
	{
		return(buffer.capacity());
	}
	
	public byte[] getBytes()
	{
		int size=buffer.position();
		byte[] bytes=new byte[size];
		buffer.position(0);
		buffer.get(bytes,0,bytes.length);
		buffer.position(size);
		return(bytes);
	}
	
	synchronized public void addMessage(Message mes)
	{
		//while(!canSend){}
		//canSend=false;
		int size=buffer.position();
		byte[] old=buffer.array();
		buffer=ByteBuffer.allocate(size+mes.size());
		buffer.put(old);
		buffer.put(mes.getBytes());
		//canSend=true;
	}
}
