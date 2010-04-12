package iDPS.gear;


import iDPS.Setup;
import iDPS.gear.Gem.GemColor;
import iDPS.gear.Armor.SocketType;
import iDPS.model.Calculations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class GemComparison {
	
	private Gear gear;
	private Setup setup;
	private int slot, index;
	private float defaultDPS;
	private Gem orgGem;
	private ArrayList<Gem> comparedGems;
	
	public GemComparison(Setup setup, Gear gear, int slot, int index) {
		this(setup, gear, slot, index, true);
	}
	
	public GemComparison(Setup setup, Gear gear, int slot, int index, boolean anyColor) {
		this.setup = setup;
		this.gear = gear.clone();
		this.slot = slot;
		this.index = index;
		orgGem = this.gear.getGem(slot, index);
		this.gear.setGem(slot, index, null);
		comparedGems = new ArrayList<Gem>();
		runComparison(anyColor);
	}
	
	private void runComparison(boolean anyColor) {
		//System.out.println("  running gem comparison...");
		Calculations m = Calculations.createInstance();
		m.calculate(setup, gear);
		defaultDPS = m.getTotalDPS();
		
		Collection<Gem> gems;
		SocketType socket = gear.getItem(slot).getSocket(index);
		if (anyColor && socket != SocketType.Meta)
			gems = Gem.getAll();
		else
			gems = Gem.findSocket(socket);
		
		// Make sure orgGem is in the list
		if (orgGem != null && !gems.contains(orgGem))
			gems.add(orgGem);
		
		for (Gem gem: gems)  {
			if (gem == null)
				continue;
			if (gem.getColor() == GemColor.Meta && socket != SocketType.Meta)
				continue;
			gear.setGem(slot, index, null);
			if (!gear.canAdd(gem))
				continue;
			gear.setGem(slot, index, gem);
			m.calculate(setup, gear);
			gem.setComparedDPS(m.getTotalDPS()-defaultDPS);
			comparedGems.add(gem);
		}
		Collections.sort(comparedGems);
	}
	
	public ArrayList<Gem> getComparedGems() {
		return comparedGems;
	}
	
	public Gem getBestGem() {
		if (comparedGems.size()>0)
			return comparedGems.get(0);
		return null;
	}
	
	public float getDefaultDPS() {
		return defaultDPS;
	}
	
	public float getMaxDPS() {
		if (comparedGems.size()>0)
			return comparedGems.get(0).getComparedDPS();
		return 0;
	}

}
