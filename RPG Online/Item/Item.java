import java.util.HashMap;


public class Item
{
	private static HashMap<Integer,Item> items=new HashMap<Integer,Item>();
	private int id;
	private String name;
	
	public Item(int i, String n)
	{
		id=i;
		name=n;
		items.put(id,this);
	}
	
	public Item(Item it)
	{
		id=it.id;
		name=it.name;
	}
	
	public static Item get(int getid)
	{
		if(getid!=-1)
			return(items.get(getid));
		else
			return(null);
	}
	
	public int getId()
	{
		return(id);
	}
	
	public String getName()
	{
		return(name);
	}
	
	public static Sword wooden_sword=new Sword(0,"Wooden Sword",1,false,0,1,0,0,0,3,0,2);
	public static Sword training_sword=new Sword(1,"Training Sword",3,false,1,0,0,1,0,1,2,5);
	public static UseItem apple=new UseItem(2,"Apple",new ItemEffect[]{new ItemEffect(new PlayerStat(PlayerStat.health,5)),new ItemEffect(new PlayerStat(PlayerStat.hunger,-20))});
	public static UseItem strpotion1=new UseItem(3,"Str Potion",new ItemEffect[]{new ItemEffect(new PlayerStat(PlayerStat.strength,5),0,10,0),new ItemEffect(new PlayerStat(PlayerStat.hunger,-5))});
	public static Weapon blacksmith_hammer=new Weapon(4,"Blacksmith Hammer",1,false,0,0,0,0,0,0,0,0);
	public static MiscItem wood=new MiscItem(5,"Wood");
}
