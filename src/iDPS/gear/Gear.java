package iDPS.gear;

import iDPS.Attributes;
import iDPS.Setup;
import iDPS.gear.Armor.SocketType;
import iDPS.model.Calculations;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;

import org.jdom.Element;


public class Gear implements Cloneable {

	private final Setup setup;
	private Attributes attr;
	private HashMap<Integer,Integer> containsMap;
	private Enchant[] enchants;
	private Gem[][] gems;
	private Armor[] items;

	private boolean[] socketBonus;

	private EnumMap<Armor.Tier,Integer> tiers;
	private HashMap<String, int[]> uniqueMap;

	public Gear(Setup setup) {
		this.setup = setup;
		items = new Armor[19];
		gems = new Gem[19][3];
		enchants = new Enchant[19];
		socketBonus = new boolean[19];
		attr = new Attributes();
		containsMap = new HashMap<Integer,Integer>();
		uniqueMap = new HashMap<String,int[]>();
		tiers = new EnumMap<Armor.Tier,Integer>(Armor.Tier.class);
		for (Armor.Tier t: Armor.Tier.values())
			tiers.put(t, 0);
	}

	@SuppressWarnings("unchecked")
	public Gear(Setup setup, Element element) {
		this(setup);
		Iterator<Element> iter = element.getChildren().iterator();
		while (iter.hasNext()) {
			Element eGear = iter.next();
			String s = eGear.getName();
			if (s.equals("items")) {
				Iterator<Element> iter2 = eGear.getChildren().iterator();
				while (iter2.hasNext()) {
					Element eItem = iter2.next();
					int iid = Integer.parseInt(eItem.getAttributeValue("id"));
					int slot = Integer.parseInt(eItem.getAttributeValue("slot"));
					Armor item = Armor.find(iid);
					if (item!=null)
						setItem(slot, item);
					Iterator<Element> iter3 = eItem.getChildren().iterator();
					while (iter3.hasNext()) {
						Element eGem = iter3.next();
						int index = Integer.parseInt(eGem.getAttributeValue("index"));
						int gemId = Integer.parseInt(eGem.getAttributeValue("id"));
						setGem(slot,index,Gem.find(gemId));
					}
				}
			} else if (s.equals("enchants")) {
				Iterator<Element> iter2 = eGear.getChildren().iterator();
				while (iter2.hasNext()) {
					Element eEnchant = iter2.next();
					int id = Integer.parseInt(eEnchant.getAttributeValue("id"));
					int slot = Integer.parseInt(eEnchant.getAttributeValue("slot"));
					Enchant enchant = Enchant.find(id);
					if (enchant!=null)
						setEnchant(slot, enchant);
				}
			}
		}
	}
	
	public Gear clone() {
		return clone(setup);
	}

	@SuppressWarnings("unchecked")
	public Gear clone(Setup setup) {
		Gear clone = new Gear(setup);
		clone.items = items.clone();
		// depp copy of the gems
		for (int i=0; i<clone.gems.length; i++)
			clone.gems[i] = gems[i].clone();
		clone.enchants = enchants.clone();
		clone.socketBonus = socketBonus.clone();
		clone.attr = attr.clone();
		clone.containsMap = (HashMap<Integer, Integer>) containsMap.clone();
		// deep copy of the unique map
		for (String key: uniqueMap.keySet())
			clone.uniqueMap.put(key, uniqueMap.get(key).clone());
		clone.tiers = tiers.clone();
		return clone;
	}
	
	@SuppressWarnings("unchecked")
	public Element toXML() {
		Element eGear = new Element("gear");

		// Items
		Element eItems = new Element("items");
		for (int i=0; i<=18; i++) {
			if (getItem(i)==null)
				continue;
			Element eItem = new Element("item");
			eItem.setAttribute("id", getItem(i).getId()+"");
			eItem.setAttribute("slot", i+"");
			for (int j=0; j<=2; j++) {
				Gem gem = getGem(i, j);
				if (gem != null) {
					Element eGem = new Element("gem");
					eGem.setAttribute("index",j+"");
					eGem.setAttribute("id", gem.getId()+"");
					eItem.getChildren().add(eGem);
				}
			}
			eItems.getChildren().add(eItem);
		}
		eGear.getChildren().add(eItems);

		// Enchants
		Element eEnchants = new Element("enchants");
		for (int i=0; i<=18; i++) {
			if (getEnchant(i)==null)
				continue;
			Element eEnchant = new Element("enchant");
			eEnchant.setAttribute("id", getEnchant(i).getId()+"");
			eEnchant.setAttribute("slot", i+"");
			eEnchants.getChildren().add(eEnchant);
		}
		eGear.getChildren().add(eEnchants);
		
		return eGear;
	}

	public boolean canAdd(Item i) {
		if (i == null)
			return true;
		if (i.getUniqueLimit()>0) {
			int[] vect;
			if (uniqueMap.containsKey(i.getUniqueName())) {
				vect = uniqueMap.get(i.getUniqueName());
				if (vect[0] >= vect[1])
					return false;
			}
		}
		return true;
	}

	public int contains(int id) {
		if (containsMap.containsKey(id))
			return containsMap.get(id);
		return 0;
	}

	public boolean containsAny(int... ids) {
		for (int id: ids) {
			if (containsMap.containsKey(id))
				return true;
		}
		return false;
	}

	private void containsDec(Item item) {
		if (item.getUniqueLimit()>0) {
			int[] vect;
			if (uniqueMap.containsKey(item.getUniqueName())) {
				vect = uniqueMap.get(item.getUniqueName());
				if (vect[0] > 0)
					vect[0]--;
			}
		}
		if (containsMap.containsKey(item.getId())) {
			int c = containsMap.get(item.getId())-1;
			if (c>0)
				containsMap.put(item.getId(), c--);
			else
				containsMap.remove(item.getId());
		}
	}

	private boolean containsInc(Item item) {
		if (item.getUniqueLimit()>0) {
			int[] vect;
			if (uniqueMap.containsKey(item.getUniqueName())) {
				vect = uniqueMap.get(item.getUniqueName());
				if (vect[0] >= vect[1])
					return false;
				vect[0]++;
			} else {
				vect = new int[2];
				vect[0] = 1;
				vect[1] = item.getUniqueLimit();
				uniqueMap.put(item.getUniqueName(), vect);
			}
		}
		int c = (containsMap.containsKey(item.getId())) ? containsMap.get(item.getId()) : 0;
		c++;
		containsMap.put(item.getId(), c);
		return true;
	}

	public void gemBest(int slot) {
		Calculations m = Calculations.createInstance();
		Armor item = getItem(slot);
		//Setup setup = this.setup.clone();
		//Gear gear = clone();
		if (item == null || !item.hasSockets())
			return;
		Gem[] gemsAny = new Gem[3], gemsMatch = new Gem[3], gemsTemp, gemsFinal;
		GemComparison gc;
		float dpsAny = 0, dpsMatch = 0;
		// Iteration 1: Any Color
		for (int index=0; index<=2; index++) {
			if (item.getSocket(index) == null)
				continue;
			gc = new GemComparison(setup, this, slot, index, true);
			setGem(slot, index, gc.getBestGem());
			gemsAny[index] = gc.getBestGem();
		}
		m.calculate(setup, this);
		dpsAny = m.getTotalDPS();
		if (isSocketBonusActive(slot))
			dpsMatch = dpsAny;
		else {
			// Iteration 2: Matching Color
			int[][] runs;
			if (this.getItem(slot).getMaxSocketIndex() == 1)
				runs = new int[][] {{0}};
			if (this.getItem(slot).getMaxSocketIndex() == 2)
				runs = new int[][] {{0,1},{1,0}};
			else
				runs = new int[][] {{0,1,2},{0,2,1},{1,0,2},{1,2,0},{2,1,0},{2,0,1}};
			for (int[] run: runs) {
				gemsTemp = new Gem[3];
				//Gear gear = this.clone();
				for (int index: run) {
					if (item.getSocket(index) == null)
						continue;
					gc = new GemComparison(setup, this, slot, index, false);
					setGem(slot, index, gc.getBestGem());
					gemsTemp[index] = gc.getBestGem();
				}
				m.calculate(setup, this);
				if (m.getTotalDPS() > dpsMatch) {
					dpsMatch = m.getTotalDPS();
					gemsMatch = gemsTemp;
				}
			}
		}
		// Pick Best Setup
		if (dpsAny > dpsMatch)
			gemsFinal = gemsAny;
		else
			gemsFinal =  gemsMatch;
		// Apply Gems
		for (int index=0; index<=2; index++) {
			if (gemsFinal[index] == null)
				continue;
			setGem(slot, index, gemsFinal[index]);
		}
	}

	public Attributes getAttributes() {
		return attr;
	}

	public Enchant getEnchant(int slot) {
		return enchants[slot];
	}

	public Gem getGem(int slot, int index) {
		return gems[slot][index];
	}

	public Gem[] getGems(int slot) {
		return gems[slot];
	}

	public Armor getItem(int slot) {
		return items[slot];
	}

	public int getTier10() {
		return tiers.get(Armor.Tier.Tier10);
	}

	public int getTier9() {
		return tiers.get(Armor.Tier.Tier9);
	}

	public Weapon getWeapon1() {
		Weapon weapon = (Weapon) getItem(16);
		if (weapon == null)
			weapon = new Weapon(Weapon.WeaponType.Dagger);
		return weapon;
	}

	public Weapon getWeapon2() {
		Weapon weapon = (Weapon) getItem(17);
		if (weapon == null)
			weapon = new Weapon(Weapon.WeaponType.Dagger);
		return weapon;
	}

	public boolean hasChaoticESD() {
		return (contains(41398)>0);
	}

	public boolean isEnchanted(int slot) {
		return (enchants[slot]!=null);
	}

	public boolean isSocketBonusActive(int slot) {
		Armor item = getItem(slot);
		if (item == null)
			return false;
		if (!item.hasSockets())
			return false;
		Gem[] gems = getGems(slot);
		SocketType socket;
		for (int index=0; index<=item.getMaxSocketIndex(); index++) {
			socket = item.getSocket(index);
			if (gems[index] == null || !gems[index].isMatch(socket))
				return false;
		}
		return true;
	}

	public void print() {
		System.out.println(this);
		for (int i=0; i<=18; i++) {
			if (items[i] != null)
				printItem(i);
			else
				System.out.format("Slot %02d: Empty%n", i);
		}
		System.out.println();
	}

	public void printItem(int slot) {
		Armor item = getItem(slot);
		if (item == null)
			return;
		System.out.println("Slot "+slot+": "+item);
		for (int index=0; index<=item.getMaxSocketIndex(); index++) {
			SocketType socket = item.getSocket(index);
			if (socket == null)
				continue;
			Gem gem = getGem(slot,index);
			if (gem == null)
				System.out.format("%15s: %s%n", socket.toString(), "Empty");
			else
				System.out.format("%15s: %s%n", socket.toString(), gem.toString());
		}
	}

	public void setEnchant(int slot, Enchant enchant) {
		Enchant oldenchant = enchants[slot];
		if (oldenchant != null)
			attr.sub(oldenchant.getAttributes());
		if (enchant != null)
			attr.add(enchant.getAttributes());
		enchants[slot] = enchant;
	}

	public void setGem(int slot, int index, Gem gem) {
		Armor item = getItem(slot);
		if (item ==  null)
			return;
		int max = item.getMaxSocketIndex();
		if (hasExtraSocket(slot))
			max++;
		if (index > max)
			return;
		// Remove attributes from socket bonus
		if (socketBonus[slot])
			attr.sub(item.getSocketBonus());
		Gem oldgem = getGem(slot, index);
		if (oldgem != null) {
			attr.sub(oldgem.getAttr());
			containsDec(oldgem);
			gems[slot][index] = null;
		}
		if (gem != null) {
			// Check if we can equip it
			if (containsInc(gem)) {
				attr.add(gem.getAttr());
				gems[slot][index] = gem;
			}
		}
		// Check if socket bonus is active
		socketBonus[slot] = isSocketBonusActive(slot);
		// if yes add bonus attributes again
		if (socketBonus[slot])
			attr.add(item.getSocketBonus());
	}

	public void setItem(int slot, Armor item) {
		Armor olditem = items[slot];
		Gem[] oldgems = getGems(slot).clone();
		if (olditem != null) {
			attr.sub(olditem.getAttr());
			for (int index=0; index<=2; index++)
				setGem(slot, index, null);
			if (olditem.getTier() != null)
				tierDec(olditem.getTier());
			containsDec(olditem);
			items[slot] = null;
		}
		if (item != null) {
			items[slot] = item;
			attr.add(item.getAttr());
			for (int index=0; index<=item.getMaxSocketIndex(); index++)
				setGem(slot, index, oldgems[index]);
			if (item.getTier() != null)
				tierInc(item.getTier());
			containsInc(item);
		}
	}

	private void tierDec(Armor.Tier t) {
		int c = tiers.get(t);
		if (c > 0) {
			c--;
			tiers.put(t, c);
		}
	}

	private void tierInc(Armor.Tier t) {
		int c = tiers.get(t);
		c++;
		tiers.put(t, c);
	}

	private boolean hasExtraSocket(int slot) {
		if (slot == 9)
			return true;
		if ((slot == 7 || slot == 8) && setup.hasProfession(Setup.Profession.Blacksmithing))
			return true;
		return false;
	}

}
