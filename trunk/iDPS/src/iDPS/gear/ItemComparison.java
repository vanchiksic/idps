package iDPS.gear;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import iDPS.Player;
import iDPS.gear.Item.SlotType;
import iDPS.gear.Weapon.weaponType;
import iDPS.model.Calculations;
import iDPS.model.Calculations.ModelType;

public class ItemComparison {
	
	private Gear gear;
	private int slotId;
	private SlotType slotType;
	private float defaultDPS;
	private ArrayList<Item> comparedItems;
	
	public ItemComparison(Gear gear, int slotId, SlotType slotType) {
		this.gear = gear.clone();
		this.slotId = slotId;
		this.gear.setItem(slotId, null);
		this.slotType = slotType;
		this.comparedItems = new ArrayList<Item>();
		runComparison();
	}
	
	private void runComparison() {
		Calculations m = Calculations.createInstance();
		m.calculate(gear);
		defaultDPS = m.getTotalDPS();
		
		ArrayList<Item> items;
		if ((slotId == 16 || slotId == 17) && 
				Player.getInstance().getTalents().getModel() == ModelType.Mutilate)
			items = Item.findWeapon(weaponType.Dagger);
		else
			items = Item.findSlot(slotType);
		
		Iterator<Item> iter = items.iterator();
		while (iter.hasNext())  {
			Item item = iter.next();
			//System.out.println("Testing "+item.getName());
			gear.setItem(slotId, item);
			gear.gemBest(slotId);
			m.calculate(gear);
			item.setComparedDPS(m.getTotalDPS()-defaultDPS);
			comparedItems.add(item);
		}
		Collections.sort(comparedItems);
	}
	
	public ArrayList<Item> getComparedItems() {
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
