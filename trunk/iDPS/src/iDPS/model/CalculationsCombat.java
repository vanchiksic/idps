package iDPS.model;

import iDPS.Launcher;
import iDPS.Glyphs.Glyph;

public class CalculationsCombat extends Calculations {
	
	private float dpsSS, dpsEvi, dpsKS;
	private float ssPerSec, eviPerSec, rupPerSec;
	
	protected CalculationsCombat() {
		super();
		this.type = ModelType.Combat;
	}

	protected void calcDPS() {
		dpsWH = calcWhiteDPS();
		dpsKS = calcKillingSpreeDPS();
		dpsSS = calcSinisterDamage() * ssPerSec;
		dpsEvi = calcEviscerateDamage(avgCP4Plus) * eviPerSec;
		dpsDP = calcDeadlyPoisonDPS();
		dpsIP = calcInstantPoisonDPS();
		dpsRU = calcDamageRupture(avgCP4Plus) * rupPerSec;
		total = dpsWH + dpsSS + dpsKS + dpsEvi + dpsDP + dpsIP + dpsRU;
		
		/*System.out.format("%10s %10.4f dps%n", "White:", dpsWH);
		System.out.format("%10s %10.4f dps%n", "KS:", dpsKS);
		System.out.format("%10s %10.4f dps%n", "SS:", dpsSS);
		System.out.format("%10s %10.4f dps%n", "Evisc:", dpsEvi);
		System.out.format("%10s %10.4f dps%n", "DeadlyP:", dpsDP);
		System.out.format("%10s %10.4f dps%n", "InstantP:", dpsIP);
		System.out.format("%10s %10.4f dps%n", "Rupture:", dpsRU);
		System.out.format("%10s %10.4f dps%n", "Total:", total);
		System.out.println();*/
	}
	
	protected void calcCycle() {
		
		float pT10 = 0;
		if (gear.getTier10()>=4)
			pT10 = 0.13F;
		float pRuth = 0.2F*talents.getTalentPoints("Ruth");
		
		float cpPerSS = 1;
		if (glyphs.has(Glyph.SS))
			cpPerSS += mod.getHtSS().crit*0.5F;
		avgCP4Plus = 5;
			
		float eCostSS = 45;
		if (talents.getTalentPoints("ISS")>0)
			eCostSS -= 3;
		if (talents.getTalentPoints("ISS")>1)
			eCostSS -= 2;
		eCostSS += (eCostSS-45*0.8F)*(1-mod.getHtMHS().getContacts());
		float eCostEvi = 35/(mod.getHtEvi().getContacts())
			- avgCP4Plus*talents.getTalentPoints("RStrikes");
		float eCostRup = 25/(mod.getHtFin().getContacts())
			- avgCP4Plus*talents.getTalentPoints("RStrikes");
		float eCostEA = eCostRup - 5 * talents.getTalentPoints("IEA");
		
		float eRegen = calcERegen();
		// Build Time EA
		float btEA = ((5/cpPerSS-pRuth-3*pT10)*eCostSS+eCostEA)/eRegen;
		// Build Time SnD
		float btSnD = ((5/cpPerSS-pRuth-3*pT10)*eCostSS+25)/eRegen;
		// Build Time Rupture
		float btRup = ((5/cpPerSS-pRuth-3*pT10)*eCostSS+eCostRup)/eRegen;
		// Build Time Evisc
		float btEvi = ((5/cpPerSS-pRuth-3*pT10)*eCostSS+eCostEvi)/eRegen;
		
		float lEA = 30;
		if (glyphs.has(Glyph.EA))
			lEA += 10;
		if (!Launcher.getApp().getUseExpose())
			lEA = 0;
		float lSnD = 21*(1+talents.getTalentPoints("ISnD")/4F);
		float lRup = 16.5F;
		if (glyphs.has(Glyph.Rup))
			lRup += 4;
		float lCycle = Math.max(lSnD,lEA);
		float pcSnD = lCycle/lSnD;
		
		float timeLeft = lCycle - pcSnD*btSnD - 1; // add some slack
		
		float pcEA = 0;
		if (Launcher.getApp().getUseExpose()) {
			pcEA = lCycle/lEA;
			timeLeft -= pcEA*btEA + 1; // add some slack
		}
		
		float pcRup = 0;
		if (Launcher.getApp().getUseRupture()) {
			pcRup = lCycle/lRup;
			if (timeLeft < (pcRup*btRup))
				pcRup = timeLeft/btRup;
			timeLeft -= pcRup*btRup + 1; // add some slack
		}
		
		float pcEvi = timeLeft/btEvi;
				
		float sndPerSec = pcSnD/lCycle;
		float eaPerSec = pcEA/lCycle;
		rupPerSec = pcRup/lCycle;
		eviPerSec = pcEvi/lCycle;
		
		ssPerSec = (sndPerSec+eaPerSec+rupPerSec+eviPerSec)*(5-pRuth-3*pT10)/cpPerSS;
				
		mhSPS = ssPerSec+eviPerSec+eaPerSec+rupPerSec;
		mhSCPS = ssPerSec*mod.getHtSS().getCrit() + eviPerSec*mod.getHtFin().getCrit();
		
		ohSPS = 0;
		ohSCPS = 0;
	}
	
	protected float calcERegen() {
		float eRegen = super.calcERegen();
		
		eRegen += 10/12F*talents.getTalentPoints("Vitality");
		if (talents.getTalentPoints("AR")>0) {
			if (glyphs.has(Glyph.AR))
				eRegen += 200F*getMaxUses(180)/fightDuration;
			else
				eRegen += 150F*getMaxUses(180)/fightDuration;
		}
		if (talents.getTalentPoints("BF")>0 && !glyphs.has(Glyph.BF))
			eRegen -= getMaxUses(120)*25/fightDuration;
		calcWhiteDPS();
		if (talents.getTalentPoints("CPotency")>0)
			eRegen += combatPotencyRegen();
		
		return eRegen;
	}
	
	private float combatPotencyRegen() {
		float pps;
		pps = ohWPS;
		// OH Hits from Tiny Abom
		if (gear.containsAny(50351,50706)) {
			float moteFactor;
			if (gear.containsAny(50706))
				moteFactor = 1/7F;
			else
				moteFactor = 1/8F;
			pps += (ohWPS+ohSPS)*0.5F*moteFactor*mod.getHtOHS().getContacts();
		}
		pps *= 0.2F;
		
		float regen = pps * 3*talents.getTalentPoints("CPotency");
		//System.out.println("cpt regen: "+regen);
		return regen;
	}
	
	private float calcSinisterDamage() {
		float dmg = gear.getWeapon1().getInstantDmg(totalATP) + 180;
		dmg *= 1 + 0.05F*talents.getTalentPoints("BTwist")
			+ 0.10F*talents.getTalentPoints("SAttacks")
			+ 0.03F*talents.getTalentPoints("Aggr");
		dmg *= ((mod.getComboMoveCritMult()-1)*mod.getHtSS().crit+1);
		// Global Mods
		dmg *= getGlobalDmgMod(true, false);
		dmg *= mod.getModArmorMH();

		dmg *= 1+bbIncrease;
		return dmg;
	}
	
	private float calcKillingSpreeDPS() {
		float dps = 0;
		if (talents.getTalentPoints("KS")>0) {
			float dmg, dmgMh, dmgOh;
			dmgMh = gear.getWeapon1().getAverageDmg(totalATP)*mod.getModArmorMH();
			dmgMh = dmgMh*mod.getHtMHS().hit + dmgMh*mod.getHtMHS().crit*mod.getPhysCritMult();
			dmgOh = gear.getWeapon2().getAverageDmg(totalATP)*mod.getModArmorOH()
				* 0.5F * (1+0.1F*talents.getTalentPoints("DWield"));
			dmgOh = dmgOh*mod.getHtMHS().hit + dmgOh*mod.getHtMHS().crit*mod.getPhysCritMult();
			dmg = (dmgMh + dmgOh) * 5 * 1.2F;
			// Global Mods
			dmg *= getGlobalDmgMod(true, false);

			if (glyphs.has(Glyph.KS))
				dps = dmg / 75F;
			else
				dps = dmg / 120F;
			dps *= 1+bbIncrease;
			
			mhSPS += 5/75F * mod.getHtMHS().getContacts();
			mhSCPS += 5/75F * mod.getHtMHS().getCrit();
			ohSPS += 5/75F * mod.getHtOHS().getContacts();
			ohSCPS += 5/75F * mod.getHtOHS().getCrit();
		}
		return dps;
	}

}
