package iDPS.gear;


import iDPS.gear.Armor.SlotType;
import iDPS.model.Calculations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class EnchantComparison {
	
	private Gear gear;
	private int slot;
	private float defaultDPS;
	private ArrayList<Enchant> comparedEnchants;
	
	public EnchantComparison(Gear gear, int slot) {
		this.gear = gear.clone();
		this.slot = slot;
		this.gear.setEnchant(slot, null);
		this.comparedEnchants = new ArrayList<Enchant>();
		runComparison();
	}
	
	private void runComparison() {
		Calculations m = Calculations.createInstance();
		m.calculate(gear);
		defaultDPS = m.getTotalDPS();
		
		Collection<Enchant> enchants;
		SlotType slotType = SlotType.Trinket;
		switch (slot) {
		case 0:
			slotType = SlotType.Head;
			break;
		case 2:
			slotType = SlotType.Shoulder;
			break;
		case 3:
			slotType = SlotType.Back;
			break;
		case 4:
			slotType = SlotType.Chest;
			break;
		case 7:
			slotType = SlotType.Wrist;
			break;
		case 8:
			slotType = SlotType.Hands;
			break;
		case 10:
			slotType = SlotType.Legs;
			break;
		case 11:
			slotType = SlotType.Feet;
			break;
		case 12:
		case 13:
			slotType = SlotType.Finger;
			break;
		case 16:
		case 17:
			slotType = SlotType.OneHand;
			break;
		}
		enchants = Enchant.findSlot(slotType);

		for (Enchant e: enchants)  {
			gear.setEnchant(slot, e);
			m.calculate(gear);
			e.setComparedDPS(m.getTotalDPS()-defaultDPS);
			comparedEnchants.add(e);
		}
		Collections.sort(comparedEnchants);
	}
	
	public ArrayList<Enchant> getComparedEnchants() {
		return comparedEnchants;
	}
	
	public float getDefaultDPS() {
		return defaultDPS;
	}
	
	public float getMaxDPS() {
		if (comparedEnchants.size()>0)
			return comparedEnchants.get(0).getComparedDPS();
		return 0;
	}

}
