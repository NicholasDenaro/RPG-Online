import java.io.IOException;


public class GameEngine extends Thread
{
	private GameFrame frame;
	private GamePanel panel;
	private boolean running;
	private int tps,fps;
	private int ticks, frames;
	private byte send;
	
	public GameEngine(GameFrame fr, GamePanel pa, int t, int f)
	{
		frame=fr;
		panel=pa;
		tps=t;
		fps=f;
		ticks=0;
		frames=0;
		send=0;
	}
	
	public void run()
	{
		running=true;
		
		long startSec;
		long timeTick=0, timeFrame=0;
		final long npt=1000000000/tps, npf=1000000000/fps;
		long time;
		long start;
		
		startSec=System.currentTimeMillis();
		
		while(running)
		{
			try
			{
				start=System.nanoTime();
				sleep(0,1);
				while(timeTick>=npt)
				{
					tick();
					ticks++;
					timeTick-=npt;
				}
				
				if(timeFrame>=npf)
				{
					draw();
					frames++;
					timeFrame%=npf;
				}
				
				time=System.nanoTime()-start;
				timeTick+=time;
				timeFrame+=time;
				
				
				if(System.currentTimeMillis()-startSec>=1000)
				{
					//System.out.println(ticks+", "+frames);
					if(panel!=null)
						panel.setEngineInfo(ticks,frames);
					else
						if(ticks<60)
							System.out.println("ticks: "+ticks);
					ticks=0;
					frames=0;
					startSec=System.currentTimeMillis();
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				running=false;
			}
		}
	}
	
	public void tick()
	{
		frame.getPlayer().zone.tick();
		if(send++>fps/5)
		{
			send=0;
			try
			{
				GameClient.client.flush();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	public void draw()
	{
		panel.repaint();
	}
	
	public int getTickrate()
	{
		return(tps);
	}
}
