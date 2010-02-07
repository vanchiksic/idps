package iDPS.model;

import iDPS.Attributes;
import iDPS.Race;
import iDPS.Talents;
import iDPS.gear.Setup;
import iDPS.gear.Weapon;
import iDPS.gear.Setup.Profession;
import iDPS.gear.Weapon.weaponType;

public class Modifiers {
	
	protected static final float cAGI = 25000/3;
	private final float cARP = 1399.572719F;
	private final float cHIT = 3278.998947F;
	private final float cHST = 3278.998947F;
	private final float cPHIT = 2623.199272F;
	private final float cCRIT = 4590.598679F;
	private final float cEXP = 3278.998947F;
	
	/** Total Attack Power */
	private float totalATP;
	
	// Global Mods
	private float gHit, gExp, gCri, gHst, gHstGear;
	// Spell Mods (m for magic)
	private float mCri, mHit;
	// Hit Tables
	private HitTable htMH, htOH, htMHS, htOHS, htSS, htMut, htFin;
	// Armor
	private float modArmorMH, modArmorOH;
	private int arpExceeded;
	
	private Attributes attr;
	private Setup setup;
	private Talents talents;
	
	public Modifiers(Attributes attr, Setup setup) {
		totalATP = 0;
		this.attr = attr;
		this.setup = setup;
		this.talents = setup.getTalents();
		calcMods();
	}
	
	public void calcMods() {
		float atp, agi, str, hit, cri, exp, hst, arp;
		atp = attr.getAtp();
		agi = attr.getAgi();
		str = attr.getStr();
		hit = attr.getHit();
		cri = attr.getCri();
		exp = attr.getExp();
		hst = attr.getHst();
		arp = attr.getArp();
		
		// Apply Buffs
		agi += 229; str += 229;
		agi *= 1.1F; str *= 1.1F;
		atp += 687 + 260;
		if (setup.hasProfession(Profession.Alchemy))
			atp += 80;
		
		// calc total atp
		totalATP = (atp + agi + str);
				
		float baseAgi = setup.getRace().getAttr().getAgi()-166;
		gHit = hit/cHIT + 0.05F;
		gCri = (agi-baseAgi)/cAGI + cri/cCRIT + 0.13F;
		gExp = exp/cEXP + 0.0025F * talents.getExpertise();
		
		gHst  = 1.4F * 1.2F * talents.getLightref();
	
		gHst -= 1;
		gHstGear = hst/cHST;
		
		mHit = hit/cPHIT + 0.05F + 0.03F;
		mCri = cri/cCRIT + 0.05F + 0.05F + 0.03F - 0.03F;
		
		Weapon.weaponType wt1 = setup.getWeapon1().getType();
		Weapon.weaponType wt2 = setup.getWeapon2().getType();
		
		// Arp
		float tmpArp = arp;
		if (wt1 == weaponType.Mace)
			tmpArp += cARP*0.03F*talents.getMaceSpec();
		if (tmpArp>cARP)
			setArpExceeded(2);
		modArmorMH = calcArmorMod(tmpArp);
		tmpArp = arp;
		if (wt2 == weaponType.Mace)
			tmpArp += cARP*0.03F*talents.getMaceSpec();
		modArmorOH = calcArmorMod(tmpArp);
		
		// Hit Tables MainHand
		float tmpCri = gCri, tmpExp = gExp;
		switch (wt1) {
			case Axe:
				if (setup.getRace().getType() == Race.Type.Orc)
					tmpExp += 0.0025F * 5;
				break;
			case Dagger:
				tmpCri += talents.getCQC()/100F;
				break;
			case Fist:
				if (setup.getRace().getType() == Race.Type.Orc)
					tmpExp += 0.0025F * 5;
				tmpCri += talents.getCQC()/100F;
				break;
			case Mace:
				if (setup.getRace().getType() == Race.Type.Dwarf)
					tmpExp += 0.0025F * 5;
				if (setup.getRace().getType() == Race.Type.Human)
					tmpExp += 0.0025F * 3;
				break;
			case Sword:
				if (setup.getRace().getType() == Race.Type.Human)
					tmpExp += 0.0025F * 3;
				break;
		}
		htMH = new HitTable(HitTable.Type.White, talents, gHit, tmpCri, tmpExp);
		htMHS = new HitTable(HitTable.Type.Special, talents, gHit, tmpCri, tmpExp);
		htFin = new HitTable(HitTable.Type.Finish, talents, gHit, tmpCri, tmpExp);
		if (setup.getTier9()>=4)
			tmpCri += 0.05F;
		htSS = new HitTable(HitTable.Type.Special, talents, gHit, tmpCri, tmpExp);
		htMut = new HitTable(HitTable.Type.Special, talents, gHit, tmpCri+0.15F, tmpExp);
		
		// Hit Tables Offhand
		tmpCri = gCri; tmpExp = gExp;
		switch (wt2) {
			case Axe:
				if (setup.getRace().getType() == Race.Type.Orc)
					tmpExp += 0.0025F * 5;
				break;
			case Dagger:
				tmpCri += talents.getCQC()/100F;
				break;
			case Fist:
				if (setup.getRace().getType() == Race.Type.Orc)
					tmpExp += 0.0025F * 5;
				tmpCri += talents.getCQC()/100F;
				break;
		}
		htOH = new HitTable(HitTable.Type.White, talents, gHit, tmpCri, tmpExp);
		htOHS = new HitTable(HitTable.Type.Special, talents, gHit, tmpCri, tmpExp);
	}
	
	private float calcArmorMod(float arp) {
		float armorC = (467.5F * 80F - 22167.5F);
		int armorDefault = 10643;
		float armor = (armorDefault * 0.8F * 0.95F);
		float armorRemovable = Math.min(((armorC+armorDefault)/3F),armor);
		armor = armor - (armorRemovable * Math.min(arp/1400F, 1F));
		float dr = armor / (armor + (467.5F * 80F - 22167.5F));
		return (1F-dr);
	}
	
	public void registerArpProc(float arp, float uptime) {
		Weapon.weaponType wt1 = setup.getWeapon1().getType();
		Weapon.weaponType wt2 = setup.getWeapon2().getType();
		float tmpArp = attr.getArp()+arp;
		if (wt1 == weaponType.Mace)
			tmpArp += cARP*0.03F*talents.getMaceSpec();
		if (tmpArp>cARP)
			setArpExceeded(1);
		modArmorMH = calcArmorMod(tmpArp)*uptime + modArmorMH*(1-uptime);
		tmpArp = attr.getArp()+arp;
		if (wt2 == weaponType.Mace)
			tmpArp += cARP*0.03F*talents.getMaceSpec();
		modArmorOH = calcArmorMod(tmpArp)*uptime + modArmorOH*(1-uptime);
	}
	
	public void registerHasteProc(float hst, float uptime) {
		gHstGear += hst/cHST*uptime;
	}
	
	public void registerStaticHasteProc(float hst, float uptime) {
		gHst = ((gHst+1)*(hst*uptime+1))-1;
	}
	
	public void registerCritProc(float cri, float uptime) {
		registerPhysCritProc(cri, uptime);
		mCri += cri/cCRIT*uptime;
	}
	
	public void registerPhysCritProc(float cri, float uptime) {
		htMH.registerCritProc(cri/cCRIT, uptime);
		htOH.registerCritProc(cri/cCRIT, uptime);
		htMHS.registerCritProc(cri/cCRIT, uptime);
		htOHS.registerCritProc(cri/cCRIT, uptime);
		htSS.registerCritProc(cri/cCRIT, uptime);
		htMut.registerCritProc(cri/cCRIT, uptime);
		htFin.registerCritProc(cri/cCRIT, uptime);
	}
	
	public void registerProc(Proc proc) {
		
		if (proc.isIncreaseCri())
			registerCritProc(proc.getAttributes().getCri(), proc.getUptime());
		
		if (proc.isIncreaseAgi()) {
			registerPhysCritProc(proc.getAttributes().getAgi()/cAGI*cCRIT, proc.getUptime());
			totalATP += proc.getAttributes().getAgi()*1.1F * proc.getUptime();
		}
		
		if (proc.isIncreaseAtp())
			totalATP += proc.getAttributes().getAtp() * proc.getUptime();
		
		if (proc.isIncreaseHst())
			registerHasteProc(proc.getAttributes().getHst(), proc.getUptime());
		
		if (proc.isIncreaseArp())
			registerArpProc(proc.getAttributes().getArp(), proc.getUptime());
	}

	public float getHastePercent() {
		return ((gHst+1)*(gHstGear+1) - 1)*100F;
	}

	public float getSpellCritPercent() {
		return mCri*100F;
	}

	public float getSpellHitPercent() {
		return mHit*100F;
	}
	
	public float getSpellMissPercent() {
		return Math.max((0.13F-mHit),0)*100F;
	}
	
	public float getPhysCritMult() {
		int cESD = (setup.hasChaoticESD()) ? 1 : 0;
		return (2+0.06F*cESD) * talents.getPotw();
	}
	
	public float getComboMoveCritMult() {
		return ((getPhysCritMult()-1) * talents.getLethality())+1;
	}
	
	public float getPoisonCritMult() {
		int cESD = (setup.hasChaoticESD()) ? 1 : 0;
		return (1.5F+0.045F*cESD) * talents.getPotw();
	}

	public HitTable getHtMH() {
		return htMH;
	}

	public HitTable getHtOH() {
		return htOH;
	}

	public HitTable getHtMHS() {
		return htMHS;
	}
	
	public HitTable getHtOHS() {
		return htOHS;
	}

	public HitTable getHtSS() {
		return htSS;
	}

	public HitTable getHtMut() {
		return htMut;
	}
	
	public HitTable getHtFin() {
		return htFin;
	}

	public float getModArmorMH() {
		return modArmorMH;
	}

	public float getModArmorOH() {
		return modArmorOH;
	}

	public int getArpExceeded() {
		return arpExceeded;
	}

	public void setArpExceeded(int arpExceeded) {
		if (this.arpExceeded<arpExceeded)
			this.arpExceeded = arpExceeded;
	}
	
	public float getTotalATP() {
		return totalATP * 1.1F * (1+0.02F*talents.getSavageCombat());
	}

}
