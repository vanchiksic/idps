package iDPS;

import iDPS.gui.MainFrame;
import iDPS.model.Calculations;

import java.util.ArrayList;
import java.util.HashMap;


public class Talents {
	
	public enum Spec { None, MutilateRS, MutilateLR, CombatCQC, CombatHnS, CombatMace };
	
	private static HashMap<Integer,Talents> map = null;
	
	private int id;
	private String name;
	
	private Spec spec;
	private Calculations.ModelType model;
	
	private int lethality, vilepoisons, improvedpoisons, hfb, murder, fa, fweakness;
	private int cqc, hns, mace, lightref, ar, compot, bf, vitality, expertise, sattacks, potw, savage, ks;
	private int rstrikes, opportunity;
	
	public Talents() {
		spec = Spec.None;
		model = Calculations.ModelType.Combat;
	}
	
	public Talents(Spec s) {
		this();
		spec = s;
		name = s.name();
		model = Calculations.ModelType.Combat;
		switch (s) {
			case MutilateRS:
				id = 1;
				model = Calculations.ModelType.Mutilate;
				lethality = 5;
				vilepoisons = 3;
				improvedpoisons = 5;
				murder = 2;
				fweakness = 3;
				fa = 3;
				hfb = 1;
				cqc = 3;
				rstrikes = 5;
				opportunity = 2;
				break;
			case MutilateLR:
				id = 2;
				model = Calculations.ModelType.Mutilate;
				lethality = 5;
				vilepoisons = 3;
				improvedpoisons = 5;
				murder = 2;
				fweakness = 3;
				fa = 3;
				hfb = 1;
				cqc = 5;
				lightref = 3;
				rstrikes = 2;
				break;
			case CombatCQC:
				id = 3;
				lethality = 5;
				vilepoisons = 2;
				improvedpoisons = 3;
				cqc = 5;
				lightref = 3;
				bf = 1;
				ar = 1;
				compot = 5;
				vitality = 3;
				expertise = 2;
				sattacks = 1;
				potw = 5;
				ks = 1;
				savage = 2;
				rstrikes = 0;
				break;
			case CombatHnS:
				id = 4;
				lethality = 5;
				vilepoisons = 2;
				improvedpoisons = 3;
				hns = 5;
				lightref = 3;
				bf = 1;
				ar = 1;
				compot = 5;
				vitality = 3;
				expertise = 2;
				sattacks = 1;
				potw = 5;
				savage = 2;
				ks = 1;
				break;
			case CombatMace:
				id = 5;
				lethality = 5;
				vilepoisons = 2;
				improvedpoisons = 3;
				cqc = 1;
				mace = 5;
				lightref = 3;
				bf = 1;
				ar = 1;
				compot = 5;
				vitality = 3;
				expertise = 2;
				sattacks = 1;
				potw = 5;
				savage = 2;
				ks = 1;
				break;
		}
	}

	public float getPotw() {
		return 1+.04F*potw;
	}

	public float getLightref() {
		switch (lightref) {
			default:
				return 1;
			case 1:
				return 1.04F;
			case 2:
				return 1.07F;
			case 3:
				return 1.1F;
		}
	}

	public int getExpertise() {
		return expertise*5;
	}

	public boolean getSupriseattacks() {
		return sattacks>0;
	}

	public float getLethality() {
		return 1+.06F*lethality;
	}

	public float getHfb() {
		return 1+.08F*hfb;
	}

	public float getMurder() {
		return 1+.02F*murder;
	}
	
	public float getRStrikes() {
		return .04F*rstrikes;
	}
	
	public float getOpportunity() {
		return .1F*opportunity;
	}
	
	public float getFindWeakness() {
		return .02F*fweakness;
	}
	
	public int getCQC() {
		return cqc;
	}
	
	public int getHnS() {
		return hns;
	}
	
	public int getMaceSpec() {
		return mace;
	}

	public Spec getSpec() {
		return spec;
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
	
	public static ArrayList<Talents> getAll() {
		if (map != null)
			return new ArrayList<Talents>(map.values());
		return new ArrayList<Talents>();
	}
	
	public static Talents find(int id) {
		if (map.containsKey(id))
			return map.get(id);
		return null;
	}
	
	public static void load() {
		map = new HashMap<Integer,Talents>();
		Talents t;
		for (Spec s: Spec.values()) {
			if (s == Spec.None)
				continue;
			t = new Talents(s);
			map.put(t.id, t);
		}
		
		MainFrame.getInstance().getMyMenuBar().createTalentsMenu();
	}

	public Calculations.ModelType getModel() {
		return model;
	}

	public float getVilePoisons() {
		switch (vilepoisons) {
			default:
				return 0;
			case 1:
				return .07F;
			case 2:
				return .14F;
			case 3:
				return .2F;
		}
	}
	
	public float getVitality() {
		switch (vitality) {
			default:
				return 0;
			case 1:
				return .8F;
			case 2:
				return 1.6F;
			case 3:
				return 2.5F;
		}
	}
	
	public float getFocusedAttacks() {
		switch (fa) {
			default:
				return 0;
			case 1:
				return .33F;
			case 2:
				return .66F;
			case 3:
				return 1F;
		}
	}

	public int getImprovedPoisons() {
		return improvedpoisons;
	}

	public boolean getKs() {
		return ks>0;
	}

	public boolean getAr() {
		return ar>0;
	}

	public boolean getBf() {
		return bf>0;
	}

	public int getCombatPotency() {
		return compot*3;
	}
	
	public int getSavageCombat() {
		return savage;
	}

	public int getId() {
		return id;
	}
	
}
