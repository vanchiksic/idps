package iDPS.model;

import iDPS.gear.Gear;

public class CalculationsCombat extends Calculations {
	
	private float dpsSS, dpsEvi, dpsKS;
	private float ssPerSec, eviPerSec, rupPerSec;
	private float sndPerCycle;
	
	protected CalculationsCombat() {
		super();
		this.type = ModelType.Combat;
	}

	protected void calcDPS() {
		dpsWH = calcWhiteDPS();
		dpsKS = calcKillingSpreeDPS();
		dpsSS = calcSinisterDamage() * ssPerSec;
		dpsEvi = calcEviscerateDamage() * eviPerSec;
		dpsDP = calcDeadlyPoisonDPS();
		dpsIP = calcInstantPoisonDPS();
		dpsRU = calcRuptureDPS();
		total = dpsWH + dpsSS + dpsKS + dpsEvi + dpsDP + dpsIP + dpsRU;
		
		/*System.out.format("%10s %10.4f dps%n", "White:", dpsWH);
		System.out.format("%10s %10.4f dps%n", "KS:", dpsKS);
		System.out.format("%10s %10.4f dps%n", "SS:", dpsSS);
		System.out.format("%10s %10.4f dps%n", "Evisc:", dpsEvi);
		System.out.format("%10s %10.4f dps%n", "DeadlyP:", dpsDP);
		System.out.format("%10s %10.4f dps%n", "InstantP:", dpsIP);
		//System.out.format("%10s %10.4f dps%n", "Rupture:", dpsRU);
		System.out.format("%10s %10.4f dps%n", "Total:", total);
		System.out.println();*/
	}
	
	@SuppressWarnings("unused")
	protected void calcCycle() {
		float ss1cp, ss2cp;
		ss2cp = mod.getHtSS().crit*0.5F;
		ss1cp = 1-ss2cp;
		float avgCpSS = 1+ss2cp;
		
		float pT10 = 0;
		if (gear.getTier10()>=4)
			pT10 = 0.13F;
		
		float c0ss0cp, c0ss1cp, c0ss2cp, c0ss3cp, c0ss4cp, c0ss5cp;
		float c1ss1cp, c1ss2cp, c1ss3cp, c1ss4cp, c1ss5cp;
		float c2ss2cp, c2ss3cp, c2ss4cp, c2ss5cp;
		float c3ss3cp, c3ss4cp, c3ss5cp;
		float c4ss4cp, c4ss5cp;
		c0ss0cp = 0.4F*(1-pT10);
		c0ss1cp = 0.6F*(1-pT10);
		c0ss2cp = 0;
		c0ss3cp = 0.4F*pT10;
		c0ss4cp = 0.6F*pT10;
		c0ss5cp = 0;
		c1ss1cp = c0ss0cp*ss1cp;
		c1ss2cp = c0ss0cp*ss2cp + c0ss1cp*ss1cp;
		c1ss3cp = c0ss1cp*ss2cp + c0ss2cp*ss1cp;
		c1ss4cp = c0ss2cp*ss2cp + c0ss3cp*ss1cp;
		c1ss5cp = c0ss3cp*ss2cp;
		c2ss2cp = c1ss1cp*ss1cp;
		c2ss3cp = c1ss1cp*ss2cp + c1ss2cp*ss1cp;
		c2ss4cp = c1ss2cp*ss2cp + c1ss3cp*ss1cp;
		c2ss5cp = c1ss3cp*ss2cp;
		c3ss3cp = c2ss2cp*ss1cp;
		c3ss4cp = c2ss2cp*ss2cp + c2ss3cp*ss1cp;
		c3ss5cp = c2ss3cp*ss2cp;
		c4ss4cp = c3ss3cp*ss1cp;
		c4ss5cp = c3ss3cp*ss2cp;

		avgCpFin = 5*(c0ss5cp+c1ss5cp+c2ss5cp+c3ss5cp+c4ss5cp)
			+ 4*(c0ss4cp+c1ss4cp+c2ss4cp+c3ss4cp+c4ss4cp);
		float ssPerFin = 4*(c4ss4cp+c4ss5cp) + 3*(c3ss4cp+c3ss5cp) + 2*(c2ss4cp+c2ss5cp) + (c1ss4cp+c1ss5cp);
		
		float eLossTOT = 0.5F;
		if (gear.getTier10()>=2)
			eLossTOT = -0.5F;
		float eRegen = super.eRegen - eLossTOT;
		eRegen += combatPotencyPPS(gear, mod)*15;
		
		float eCostSS, eCostEvi;
		eCostSS = 40 *(0.8F+0.2F/(mod.getHtSS().getContacts()));
		eCostEvi = 35/(mod.getHtMHS().getContacts());
		eCostEvi -= avgCpFin*0.2F*25;
		
		float avgSndLength = (avgCpFin*3+6)*1.5F;
		//System.out.println("SND Length: "+avgSndLength);
		sndPerCycle = (ssPerFin*eCostSS+eCostEvi)/(avgSndLength*eRegen+10);
		//System.out.println("SND per C: "+sndPerCycle);
		
		float eCostCycle, eCostFin, lengthCycle;
		eCostFin = eCostEvi - 10*sndPerCycle;
		eCostCycle = ssPerFin*eCostSS + eCostFin;
		lengthCycle = eCostCycle / eRegen;
		//System.out.println("Cycle length: "+lengthCycle);
		
		ssPerSec = ssPerFin/lengthCycle;
		eviPerSec = (1-sndPerCycle)/lengthCycle;
		rupPerSec = rupPerCycle/lengthCycle;
		
		mhSPS = (ssPerSec+eviPerSec+rupPerSec);
		ohSPS = 0;
	}
	
	private float calcSinisterDamage() {
		float dmg = gear.getWeapon1().getInstantDmg(totalATP) + 180;
		dmg *= 1+0.10F+0.10F+0.15F;
		dmg *= ((mod.getComboMoveCritMult()-1)*mod.getHtSS().crit+1);
		// Global Mods
		dmg *= 1.03F * 1.04F;

		dmg *= mod.getModArmorMH();
		dmg *= 1+bbIncrease;
		return dmg;
	}
	
	private float combatPotencyPPS(Gear gear, Modifiers mod) {
		float pps;
		pps = gear.getWeapon1().getEffectiveAPS(mod.getHastePercent()/100);
		pps *= (mod.getHtOH().getContacts());
		pps *= 0.2F;
		return pps;
	}
	
	private float calcKillingSpreeDPS() {
		float dps = 0;
		if (talents.getKs()) {
			float dmg, dmgMh, dmgOh;
			dmgMh = gear.getWeapon1().getAverageDmg(totalATP)*mod.getModArmorMH();
			dmgMh = dmgMh*mod.getHtMHS().hit + dmgMh*mod.getHtMHS().crit*mod.getPhysCritMult();
			dmgOh = gear.getWeapon2().getAverageDmg(totalATP)*mod.getModArmorOH()*0.75F;
			dmgOh = dmgOh*mod.getHtMHS().hit + dmgOh*mod.getHtMHS().crit*mod.getPhysCritMult();
			dmg = (dmgMh + dmgOh) * 5 * 1.2F;
			// Global Mods
			dmg *= 1.03F * 1.04F;
			dps = dmg / 75F;
			mhSPS += 5/75F * (mod.getHtMHS().getContacts());
			ohSPS += 5/75F * (mod.getHtOHS().getContacts());
			dps *= 1+bbIncrease;
		}
		return dps;
	}

}
