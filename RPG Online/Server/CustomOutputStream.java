import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;


public class CustomOutputStream
{
	private OutputStream out;
	protected ByteBuffer buffer;
	
	private boolean canwrite, cansend;
	
	public CustomOutputStream(OutputStream o)
	{
		out=o;
		buffer=ByteBuffer.allocate(0);
		
		canwrite=true;
		cansend=true;
	}
	
	public boolean isEmpty()
	{
		return(buffer.capacity()==0);
	}
	
	public int size()
	{
		return(buffer.capacity());
	}
	
	synchronized public void addMessage(Message mes)
	{
		while(!canwrite){}
		cansend=false;
		int size=buffer.position();
		byte[] old=buffer.array();
		buffer=ByteBuffer.allocate(size+mes.size());
		buffer.put(old);
		buffer.put(mes.getBytes());
		cansend=true;
	}
	
	synchronized public void flush()// throws IOException
	{
		while(!cansend){}
		//addMessage(new Message(Message.END));
		canwrite=false;
		ByteBuffer tempbuf=ByteBuffer.allocate(4+buffer.array().length+5);
		tempbuf.putInt(buffer.array().length+5);
		tempbuf.put(buffer.array());
		tempbuf.put(Message.END);
		tempbuf.putInt(-1);
		
		//System.out.println("sent size: "+tempbuf.capacity());
		try
		{
			out.write(tempbuf.array());
			out.flush();
		}
		catch(Exception ex)
		{
			//System.out.println("error with out.write()");
		}
		buffer=ByteBuffer.allocate(0);
		canwrite=true;
	}
}
