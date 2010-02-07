package iDPS.gear;

import java.util.ArrayList;
import java.util.Collections;

import iDPS.gear.Armor.SlotType;
import iDPS.gear.Weapon.weaponType;
import iDPS.model.Calculations;
import iDPS.model.Calculations.ModelType;

public class ItemComparison {
	
	private Setup gear;
	private Armor orgItem;
	private int slotId;
	private SlotType slotType;
	private float defaultDPS;
	private ArrayList<Armor> comparedItems;
	
	public ItemComparison(Setup gear, int slotId, SlotType slotType) {
		this.gear = gear.clone();
		this.slotId = slotId;
		orgItem = gear.getItem(slotId);
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
		
		// Make sure orgItem is in the list
		if (!items.contains(orgItem))
			items.add(orgItem);
		
		for (Armor item: items)  {
			gear.setItem(slotId, null);
			if (!gear.canAdd(item))
				continue;
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
