
public class Equipment
{
	private Helmet helmet;
	private Mask mask;
	private Mantle mantle;
	private Chestpiece chestpiece;
	private Leggings leggings;
	private Boots boots;
	private Accessory accessory;
	private Weapon weapon;
	
	public Equipment()
	{
		this(-1,-1,-1,-1,-1,-1,-1,-1,"",false);
	}
	
	public Equipment(int helmid, int maskid, int mantid, int chestid, int legid, int bootid, int accid, int wepid, String wepparams, boolean wepappraised)
	{
		helmet=(Helmet)Item.get(helmid);
		mask=(Mask)Item.get(maskid);
		mantle=(Mantle)Item.get(mantid);
		chestpiece=(Chestpiece)Item.get(chestid);
		leggings=(Leggings)Item.get(legid);
		boots=(Boots)Item.get(bootid);
		accessory=(Accessory)Item.get(accid);
		weapon=(Weapon)Item.get(wepid);
		if(weapon!=null)
		{
			weapon.setAppraised(wepappraised);
			weapon.setParameters(wepparams);
		}
	}
	
	public Equip equip(Equip item)
	{
		Equip old=null;
		if(item instanceof Helmet)
		{
			old=helmet;
			helmet=(Helmet)item;
		}
		else if(item instanceof Mask)
		{
			old=mask;
			mask=(Mask)item;
		}
		else if(item instanceof Mantle)
		{
			old=mantle;
			mantle=(Mantle)item;
		}
		else if(item instanceof Chestpiece)
		{
			old=chestpiece;
			chestpiece=(Chestpiece)item;
		}
		else if(item instanceof Leggings)
		{
			old=leggings;
			leggings=(Leggings)item;
		}
		else if(item instanceof Boots)
		{
			old=boots;
			boots=(Boots)item;
		}
		else if(item instanceof Accessory)
		{
			old=accessory;
			accessory=(Accessory)item;
		}
		else if(item instanceof Weapon)
		{
			old=weapon;
			Weapon wep=(Weapon)item;
			weapon=(Weapon)Item.get(item.getId());
			if(weapon!=null)
			{
				weapon.setAppraised(wep.isAppraised());
				weapon.setParameters(wep.getParametersAsString());
			}
		}
		
		return(old);
	}
	
	public Equip getEquip(int i)
	{
		switch(i)
		{
			case 0:
				return(helmet);
			case 1:
				return(mask);
			case 2:
				return(mantle);
			case 3:
				return(chestpiece);
			case 4:
				return(leggings);
			case 5:
				return(boots);
			case 6:
				return(accessory);
			case 7:
				return(weapon);
		}
		System.out.println("index: "+i);
		return(null);
	}
	
	public void removeEquip(int i)
	{
		switch(i)
		{
			case 0:
				helmet=null;
				return;
			case 1:
				mask=null;
				return;
			case 2:
				mantle=null;
				return;
			case 3:
				chestpiece=null;
				return;
			case 4:
				leggings=null;
				return;
			case 5:
				boots=null;
				return;
			case 6:
				accessory=null;
				return;
			case 7:
				weapon=null;
				return;
		}
	}
	
	public int getAgility()
	{
		int agi=0;
		if(helmet!=null)
			agi+=helmet.getAgility();
		if(mask!=null)
			agi+=mask.getAgility();
		if(mantle!=null)
			agi+=mantle.getAgility();
		if(chestpiece!=null)
			agi+=chestpiece.getAgility();
		if(leggings!=null)
			agi+=leggings.getAgility();
		if(boots!=null)
			agi+=boots.getAgility();
		if(accessory!=null)
			agi+=accessory.getAgility();
		if(weapon!=null)
			agi+=weapon.getAgility();
		return(agi);
	}
	public int getDexterity()
	{
		int dex=0;
		if(helmet!=null)
			dex+=helmet.getDexterity();
		if(mask!=null)
			dex+=mask.getDexterity();
		if(mantle!=null)
			dex+=mantle.getDexterity();
		if(chestpiece!=null)
			dex+=chestpiece.getDexterity();
		if(leggings!=null)
			dex+=leggings.getDexterity();
		if(boots!=null)
			dex+=boots.getDexterity();
		if(accessory!=null)
			dex+=accessory.getDexterity();
		if(weapon!=null)
			dex+=weapon.getDexterity();
		return(dex);
	}
	public int getVitality()
	{
		int vit=0;
		if(helmet!=null)
			vit+=helmet.getVitality();
		if(mask!=null)
			vit+=mask.getVitality();
		if(mantle!=null)
			vit+=mantle.getVitality();
		if(chestpiece!=null)
			vit+=chestpiece.getVitality();
		if(leggings!=null)
			vit+=leggings.getVitality();
		if(boots!=null)
			vit+=boots.getVitality();
		if(accessory!=null)
			vit+=accessory.getVitality();
		if(weapon!=null)
			vit+=weapon.getVitality();
		return(vit);
	}
	public int getStrength()
	{
		int str=0;
		if(helmet!=null)
			str+=helmet.getStrength();
		if(mask!=null)
			str+=mask.getStrength();
		if(mantle!=null)
			str+=mantle.getStrength();
		if(chestpiece!=null)
			str+=chestpiece.getStrength();
		if(leggings!=null)
			str+=leggings.getStrength();
		if(boots!=null)
			str+=boots.getStrength();
		if(accessory!=null)
			str+=accessory.getStrength();
		if(weapon!=null)
			str+=weapon.getStrength();
		return(str);
	}
	
	public int getDefense()
	{
		int def=0;
		if(helmet!=null)
			def+=helmet.getDefense();
		if(mask!=null)
			def+=mask.getDefense();
		if(mantle!=null)
			def+=mantle.getDefense();
		if(chestpiece!=null)
			def+=chestpiece.getDefense();
		if(leggings!=null)
			def+=leggings.getDefense();
		if(boots!=null)
			def+=boots.getDefense();
		if(accessory!=null)
			def+=accessory.getDefense();
		if(weapon!=null)
			def+=weapon.getDefense();
		return(def);
	}
	
	public Helmet getHelmet()
	{
		return(helmet);
	}
	
	public Mask getMask()
	{
		return(mask);
	}
	
	public Mantle getMantle()
	{
		return(mantle);
	}
	
	public Chestpiece getChestpiece()
	{
		return(chestpiece);
	}
	
	public Leggings getLeggings()
	{
		return(leggings);
	}
	
	public Boots getBoots()
	{
		return(boots);
	}
	
	public Accessory getAccessory()
	{
		return(accessory);
	}
	
	public Weapon getWeapon()
	{
		return(weapon);
	}
}
