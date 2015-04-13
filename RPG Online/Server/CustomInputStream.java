import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;


public class CustomInputStream
{
	private InputStream in;
	private ByteBuffer buffer;
	
	public CustomInputStream(InputStream i)
	{
		in=i;
		buffer=null;
	}
	
	public void read() throws IOException
	{
		byte[] b=new byte[4];
		in.read(b);
		buffer=ByteBuffer.wrap(b);
		int size=buffer.getInt();
		b=new byte[size];
		in.read(b);
		buffer=ByteBuffer.wrap(b);
	}
	
	public byte readByte()
	{
		return(buffer.get());
	}
	
	public boolean readBoolean()
	{
		byte b=buffer.get();
		return(b==1);
	}
	
	public char readChar()
	{
		return(buffer.getChar());
	}
	
	public short readShort()
	{
		return(buffer.getShort());
	}
	
	public int readInt()
	{
		return(buffer.getInt());
	}
	
	public long readLong()
	{
		return(buffer.getLong());
	}
	
	public double readDouble()
	{
		return(buffer.getDouble());
	}
	
	public String readString()
	{
		String s="";
		int len=buffer.getInt();
		for(int i=0;i<len;i+=1)
			s+=buffer.getChar();
		return(s);
	}
}
