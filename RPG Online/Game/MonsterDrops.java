import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class MonsterDrops
{
	public ArrayList<Drop> drops;
	
	public MonsterDrops()
	{
		drops=new ArrayList<Drop>();
	}
	
	public MonsterDrops addItem(int id, String wepParams, boolean appr, int count, double rate)
	{
		drops.add(new Drop(id,wepParams,appr,count,rate));
		return(this);
	}
	
	public ArrayList<ItemStack> dropItems()
	{
		ArrayList<ItemStack> items=new ArrayList<ItemStack>();
		
		double rand=0;
		
		for(int i=0;i<drops.size();i++)
		{
			Drop drop=drops.get(i);
			rand=Math.random();
			if(drop.rate>=rand)
			{
				if(Item.get(drop.id) instanceof Weapon)
				{
					Weapon wep=new Weapon((Weapon)Item.get(drop.id),drop.params,drop.appraised);
					items.add(new ItemStack(wep));
				}
				else
					items.add(new ItemStack(drop.id,drop.count));
			}
		}
		
		return(items);
	}
}

class Drop
{
	protected int id, count;
	protected double rate;
	protected String params;
	protected boolean appraised;
	
	public Drop(int i, String wepParams, boolean appr, int c, double r)
	{
		id=i;
		params=wepParams;
		appraised=appr;
		count=c;
		rate=r;
	}
}
