package iDPS.gear;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import iDPS.gear.Armor.SlotType;
import iDPS.gear.Weapon.weaponType;
import iDPS.model.Calculations;
import iDPS.model.Calculations.ModelType;

public class ItemComparison {
	
	private Setup gear;
	private int slotId;
	private SlotType slotType;
	private float defaultDPS;
	private ArrayList<Armor> comparedItems;
	
	public ItemComparison(Setup gear, int slotId, SlotType slotType) {
		this.gear = gear.clone();
		this.slotId = slotId;
		this.gear.setItem(slotId, null);
		this.slotType = slotType;
		this.comparedItems = new ArrayList<Armor>();
		runComparison();
	}
	
	private void runComparison() {
		Calculations m = Calculations.createInstance();
		m.calculate(gear);
		defaultDPS = m.getTotalDPS();
		
		ArrayList<Armor> items;
		if ((slotId == 16 || slotId == 17) && 
				gear.getTalents().getModel() == ModelType.Mutilate)
			items = Armor.findWeapon(weaponType.Dagger);
		else
			items = Armor.findSlot(slotType);
		
		Iterator<Armor> iter = items.iterator();
		while (iter.hasNext())  {
			Armor item = iter.next();
			gear.setItem(slotId, item);
			gear.gemBest(slotId);
			m.calculate(gear);
			item.setComparedDPS(m.getTotalDPS()-defaultDPS);
			comparedItems.add(item);
		}
		Collections.sort(comparedItems);
	}
	
	public ArrayList<Armor> getComparedItems() {
		return comparedItems;
	}
	
	public float getDefaultDPS() {
		return defaultDPS;
	}
	
	public float getMaxDPS() {
		if (comparedItems.size()>0)
			return comparedItems.get(0).getComparedDPS();
		return 0;
	}

}
