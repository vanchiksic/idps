package iDPS.model;

import iDPS.Attributes;
import iDPS.Glyphs;
import iDPS.Launcher;
import iDPS.Race;
import iDPS.Setup;
import iDPS.Talents;
import iDPS.Glyphs.Glyph;
import iDPS.Setup.Profession;
import iDPS.controllers.BuffController;
import iDPS.controllers.BuffController.Buff;
import iDPS.controllers.BuffController.Consumable;
import iDPS.controllers.BuffController.Debuff;
import iDPS.gear.Gear;
import iDPS.gear.Weapon;
import iDPS.gear.Weapon.WeaponType;

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
	private HitTable htMH, htOH, htMHS, htOHS, htSS, htBS,
	htHemo, htMut, htFin, htEvi, htAmb;
	// Armor
	private float modArmorMH, modArmorOH;
	private int arpExceeded;

	private Attributes attr;
	private Setup setup;
	private Gear gear;
	private Glyphs glyphs;
	private Talents talents;
	private BuffController bc;

	public Modifiers(Attributes inject, Setup setup, Gear gear) {
		this.setup = setup;
		this.gear = gear;
		this.glyphs = setup.getGlyphs();
		this.talents = setup.getTalents();

		totalATP = 0;
		attr = new Attributes(inject);
		attr.add(gear.getAttributes());
		if (setup.getRace() != null)
			attr.add(setup.getRace().getAttr());
		if (setup.hasProfession(Profession.Skinning))
			attr.incCri(40F);

		bc = Launcher.getApp().getBuffController();
		calcAttributes();
		calcMods();
	}

	private void calcAttributes() {
		// Apply Buffs

		for (Buff b: Buff.values()) {
			if (!bc.hasBuff(b))
				continue;
			switch (b) {
			case agilityStrength:
				attr.incAgi(155);
				attr.incStr(155);
				break;
			case agilityStrengthImp:
				attr.incAgi(23);
				attr.incStr(23);
				break;
			case statsAdditive:
				attr.incAgi(37);
				attr.incStr(37);
				break;
			case statsAdditiveImp:
				attr.incAgi(15);
				attr.incStr(15);
				break;
			case attackPower:
				attr.incAtp(550);
				break;
			case attackPowerImp:
				attr.incAtp(138);
				break;
			}
		}

		for (Consumable c: Consumable.values()) {
			if (!bc.hasConsumable(c))
				continue;
			switch (c) {
			case flask:
				attr.incAtp(180);
				break;
			case foodAgi:
				attr.incAgi(40);
				break;
			case foodArp:
				attr.incArp(40);
				break;
			case foodAtp:
				attr.incAtp(80);
				break;
			case foodExp:
				attr.incExp(40);
				break;
			case foodHit:
				attr.incHit(40);
				break;
			case foodHst:
				attr.incHst(40);
				break;
			}
		}

		// Sinister Calling AGI
		if (talents.getTalentPoints("SCalling")>0) {
			float mult = 1+0.03F*talents.getTalentPoints("SCalling");
			attr.applyStatMult(Attributes.Type.AGI, mult);
		}
		// Flask of the North / Mixology
		if (setup.hasProfession(Profession.Alchemy))
			attr.incAtp(80);

		// Kings
		if (bc.hasBuff(Buff.statsMultiplicative)) {
			attr.applyStatMult(Attributes.Type.AGI, 1.1F);
			attr.applyStatMult(Attributes.Type.STR, 1.1F);
		}

		// Calc Total ATP
		attr.finalizeStats();
		if (bc.hasBuff(Buff.attackPowerMult))
			attr.applyAtpMult(1.1F);
		if (talents.getTalentPoints("SCombat")>0)
			attr.applyAtpMult(1+0.02F*talents.getTalentPoints("SCombat"));
		if (talents.getTalentPoints("Deadly")>0)
			attr.applyAtpMult(1+0.02F*talents.getTalentPoints("Deadly"));

	}

	public void calcMods() {
		totalATP = attr.getAtp();

		float agi, hit, cri, exp, hst, arp;
		agi = attr.getAgi();
		hit = attr.getHit();
		cri = attr.getCri();
		exp = attr.getExp();
		hst = attr.getHst();
		arp = attr.getArp();

		float baseAgi = setup.getRace().getAttr().getAgi()-166;
		gHit = hit/cHIT + 0.01F*talents.getTalentPoints("Precision");
		if (bc.hasBuff(Buff.partyHit))
			gHit += 0.01F;
		gCri = (agi-baseAgi)/cAGI + cri/cCRIT
		+ 0.01F*talents.getTalentPoints("Malice") - 0.048F;
		if (bc.hasBuff(Buff.physicalCrit))
			gCri += 0.05F;
		if (bc.hasDebuff(Debuff.crit))
			gCri += 0.03F;
		gExp = exp/cEXP + 0.0125F * talents.getTalentPoints("WExp");

		gHst  = 1.4F * (1+talents.getTalentPoints("LR")/30F);
		if (bc.hasBuff(Buff.meleHasteImp))
			gHst *= 1.2F;
		else if (bc.hasBuff(Buff.meleHaste))
			gHst *= 1.16F;
		gHst -= 1;
		gHstGear = hst/cHST;

		mHit = hit/cPHIT + 0.01F*talents.getTalentPoints("Precision");
		if (bc.hasDebuff(Debuff.spellHit))
			mHit += 0.03F;
		if (bc.hasBuff(Buff.partyHit))
			mHit += 0.01F;
		mCri = Math.max(0, (cri/cCRIT - 0.03F));
		if (bc.hasBuff(Buff.spellCrit))
			mCri += 0.05F;
		if (bc.hasDebuff(Debuff.spellCrit))
			mCri += 0.05F;
		if (bc.hasDebuff(Debuff.crit))
			mCri += 0.03F;

		Weapon.WeaponType wt1 = gear.getWeapon1().getType();
		Weapon.WeaponType wt2 = gear.getWeapon2().getType();

		// Arp
		arp += cARP*0.03F*talents.getTalentPoints("SBlades");
		float tmpArp = arp;
		if (wt1 == WeaponType.Mace)
			tmpArp += cARP*0.03F*talents.getTalentPoints("Mace");
		if (tmpArp>cARP)
			setArpExceeded(2);
		modArmorMH = calcArmorMod(tmpArp);
		tmpArp = arp;
		if (wt2 == WeaponType.Mace)
			tmpArp += cARP*0.03F*talents.getTalentPoints("Mace");
		modArmorOH = calcArmorMod(tmpArp);

		// Hit Tables MainHand
		float tmpCri = gCri, tmpExp = gExp;
		switch (wt1) {
		case Axe:
			if (setup.getRace().getType() == Race.Type.Orc)
				tmpExp += 0.0025F * 5;
			break;
		case Dagger:
			tmpCri += talents.getTalentPoints("CQC")/100F;
			break;
		case Fist:
			if (setup.getRace().getType() == Race.Type.Orc)
				tmpExp += 0.0025F * 5;
			tmpCri += talents.getTalentPoints("CQC")/100F;
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
		if (glyphs.has(Glyph.Evi))
			htEvi = new HitTable(HitTable.Type.Finish, talents, gHit, tmpCri+0.1F, tmpExp);
		else
			htEvi = new HitTable(HitTable.Type.Finish, talents, gHit, tmpCri, tmpExp);
		float criAmb = tmpCri + 0.25F * talents.getTalentPoints("IAmb");
		htAmb = new HitTable(HitTable.Type.Special, talents, gHit, criAmb, tmpExp);
		if (gear.getTier9()>=4)
			tmpCri += 0.05F;
		htSS = new HitTable(HitTable.Type.Special, talents, gHit, tmpCri, tmpExp);
		htHemo = new HitTable(HitTable.Type.Special, talents, gHit, tmpCri, tmpExp);
		tmpCri += 0.05F*talents.getTalentPoints("PWounds");
		htMut = new HitTable(HitTable.Type.Special, talents, gHit, tmpCri, tmpExp);
		tmpCri += 0.05F*talents.getTalentPoints("PWounds");
		htBS = new HitTable(HitTable.Type.Special, talents, gHit, tmpCri, tmpExp);

		// Hit Tables Offhand
		tmpCri = gCri; tmpExp = gExp;
		switch (wt2) {
		case Axe:
			if (setup.getRace().getType() == Race.Type.Orc)
				tmpExp += 0.0025F * 5;
			break;
		case Dagger:
			tmpCri += talents.getTalentPoints("CQC")/100F;
			break;
		case Fist:
			if (setup.getRace().getType() == Race.Type.Orc)
				tmpExp += 0.0025F * 5;
			tmpCri += talents.getTalentPoints("CQC")/100F;
			break;
		}
		htOH = new HitTable(HitTable.Type.White, talents, gHit, tmpCri, tmpExp);
		htOHS = new HitTable(HitTable.Type.Special, talents, gHit, tmpCri, tmpExp);
	}

	private float calcArmorMod(float arp) {
		float armorC = (467.5F * 80F - 22167.5F);
		int armorDefault = 10643;
		float armor = armorDefault;
		if (bc.hasDebuff(Debuff.armorMajor))
			armor *= 0.8F;
		if (bc.hasDebuff(Debuff.armorMinor))
			armor *= 0.95F;
		float armorRemovable = Math.min(((armorC+armor)/3F),armor);
		armor = armor - (armorRemovable * Math.min(arp/cARP, 1F));
		float dr = armor / (armor + (467.5F * 80F - 22167.5F));
		return (1F-dr);
	}

	public void registerArpProc(float arp, float uptime) {
		Weapon.WeaponType wt1 = gear.getWeapon1().getType();
		Weapon.WeaponType wt2 = gear.getWeapon2().getType();
		float tmpArp = attr.getArp()+arp;
		if (wt1 == WeaponType.Mace)
			tmpArp += cARP*0.03F*talents.getTalentPoints("Mace");
		if (tmpArp>cARP)
			setArpExceeded(1);
		modArmorMH = calcArmorMod(tmpArp)*uptime + modArmorMH*(1-uptime);
		tmpArp = attr.getArp()+arp;
		if (wt2 == WeaponType.Mace)
			tmpArp += cARP*0.03F*talents.getTalentPoints("Mace");
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
		htBS.registerCritProc(cri/cCRIT, uptime);
		htAmb.registerCritProc(cri/cCRIT, uptime);
		htFin.registerCritProc(cri/cCRIT, uptime);
	}

	public void registerProc(Proc proc) {

		Attributes attr = proc.getAttributes();

		// Sinister Calling AGI
		if (talents.getTalentPoints("SCalling")>0)
			attr.applyStatMult(Attributes.Type.AGI, (1+0.03F*talents.getTalentPoints("SCalling")));
		// Kings
		if (bc.hasBuff(Buff.statsMultiplicative)) {
			attr.applyStatMult(Attributes.Type.AGI, 1.1F);
			attr.applyStatMult(Attributes.Type.STR, 1.1F);
		}
		attr.finalizeStats();
		if (bc.hasBuff(Buff.attackPowerMult))
			attr.applyAtpMult(1.1F);
		if (talents.getTalentPoints("SCombat")>0)
			attr.applyAtpMult(1+0.02F*talents.getTalentPoints("SCombat"));
		if (talents.getTalentPoints("Deadly")>0)
			attr.applyAtpMult(1+0.02F*talents.getTalentPoints("Deadly"));

		if (proc.isIncreaseCri())
			registerCritProc(attr.getCri(), proc.getUptime());

		if (proc.isIncreaseAgi()) {
			registerPhysCritProc(attr.getAgi()/cAGI*cCRIT, proc.getUptime());
			totalATP += attr.getAtp() * proc.getUptime();
		}

		if (proc.isIncreaseAtp())
			totalATP += attr.getAtp() * proc.getUptime();

		if (proc.isIncreaseHst())
			registerHasteProc(attr.getHst(), proc.getUptime());

		if (proc.isIncreaseArp())
			registerArpProc(attr.getArp(), proc.getUptime());
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
		return Math.max((0.17F-mHit),0)*100F;
	}

	public float getPhysCritMult() {
		int cESD = (gear.hasChaoticESD()) ? 1 : 0;
		return (2+0.06F*cESD) * (1+0.04F*talents.getTalentPoints("PotW"));
	}

	public float getComboMoveCritMult() {
		return ((getPhysCritMult()-1) * (1+0.06F*talents.getTalentPoints("Lethality")))+1;
	}

	public float getPoisonCritMult() {
		int cESD = (gear.hasChaoticESD()) ? 1 : 0;
		return (1.5F+0.045F*cESD) * (1+0.04F*talents.getTalentPoints("PotW"));
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

	public HitTable getHtBS() {
		return htBS;
	}

	public HitTable getHtHemo() {
		return htHemo;
	}

	public HitTable getHtAmb() {
		return htAmb;
	}

	public HitTable getHtFin() {
		return htFin;
	}

	public HitTable getHtEvi() {
		return htEvi;
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
		return totalATP;
	}

	public Attributes getAttr() {
		return attr;
	}

}
