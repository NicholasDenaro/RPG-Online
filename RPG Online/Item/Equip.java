import java.util.ArrayList;


public class Equip extends Item
{
	private int level;
	
	private int agility, dexterity, vitality, strength, defense;
	
	public Equip(Equip it)
	{
		super(it);
		level=it.level;
		agility=it.agility;
		dexterity=it.dexterity;
		vitality=it.vitality;
		strength=it.strength;
		defense=it.defense;
	}
	
	public Equip(Equip it,int agi, int dex, int vit, int str, int def)
	{
		super(it);
		level=it.level;
		agility=agi;
		dexterity=dex;
		vitality=vit;
		strength=str;
		defense=def;
	}
	
	public Equip(int i,String n, int lvl,int agi, int dex, int vit, int str, int def)
	{
		super(i,n);
		level=lvl;
		agility=agi;
		dexterity=dex;
		vitality=vit;
		strength=str;
		defense=def;
	}
	
	public int getLevel()
	{
		return(level);
	}
	
	public int getAgility()
	{
		return(agility);
	}
	
	public int getDexterity()
	{
		return(dexterity);
	}
	
	public int getVitality()
	{
		return(vitality);
	}
	
	public int getStrength()
	{
		return(strength);
	}
	
	public int getDefense()
	{
		return(defense);
	}
}
