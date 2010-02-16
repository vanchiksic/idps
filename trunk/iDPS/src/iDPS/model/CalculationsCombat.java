package iDPS.model;

import iDPS.BuffController.Buff;
import iDPS.BuffController.Debuff;

public class CalculationsCombat extends Calculations {
	
	private float dpsSS, dpsEvi, dpsKS;
	private float ssPerSec, eviPerSec;
	
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
		if (setup.getTier10()>=4)
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
		
		float eCostSS = 45*(0.8F+0.2F/(mod.getHtSS().getContacts()))-5;
		float eCostFin = 35/(mod.getHtMHS().getContacts());
		eCostFin -= avgCpFin*talents.getRStrikes();
		
		float sndLength = (avgCpFin*3+6)*1.5F;
		float eaLength = avgCpFin*6;
		float eRegen = calcERegen();
		
		float cycleLength = Math.max(sndLength,eaLength);
		float sndPerCycle = cycleLength/sndLength;
		float eaPerCycle = 0;
		if (bc.hasDebuff(Debuff.armorMajorMaintain))
			eaPerCycle = cycleLength/eaLength;
		float eCostSndCycle = ssPerFin*eCostSS + eCostFin-10;
		float eCostEaCycle = ssPerFin*eCostSS + eCostFin-10;
		float eCostEviCycle = ssPerFin*eCostSS + eCostFin;
		eRegen -= (sndPerCycle*eCostSndCycle+eaPerCycle*eCostEaCycle)/cycleLength;
		float eviPerCycle = eRegen*cycleLength/eCostEviCycle;
		ssPerSec = ((sndPerCycle+eaPerCycle+eviPerCycle)*ssPerFin)/cycleLength;
		eviPerSec = eviPerCycle/cycleLength;
		float eaPerSec = eaPerCycle/cycleLength;
				
		mhSPS = ssPerSec+eviPerSec+eaPerSec;
		mhSCPS = ssPerSec*mod.getHtSS().getCrit() + eviPerSec*mod.getHtFin().getCrit();
		
		ohSPS = 0;
		ohSCPS = 0;
	}
	
	protected float calcERegen() {
		float eRegen = super.calcERegen();
		
		eRegen += talents.getVitality();
		if (talents.getAr())
			eRegen += 150F*getMaxUses(180)/fightDuration;
		calcWhiteDPS();
		if (talents.getCombatPotency()>0)
			eRegen += combatPotencyRegen();
		
		return eRegen;
	}
	
	private float combatPotencyRegen() {
		float pps;
		pps = ohWPS;
		// OH Hits from Tiny Abom
		if (setup.containsAny(50351,50706)) {
			float moteFactor;
			if (setup.containsAny(50706))
				moteFactor = 1/7F;
			else
				moteFactor = 1/8F;
			pps += (ohWPS+ohSPS)*0.5F*moteFactor*mod.getHtOHS().getContacts();
		}
		pps *= 0.2F;
		
		float regen = pps*((float) talents.getCombatPotency());
		//System.out.println("cpt regen: "+regen);
		return regen;
	}
	
	private float calcSinisterDamage() {
		float dmg = setup.getWeapon1().getInstantDmg(totalATP) + 180;
		dmg *= 1+0.10F+0.10F+0.15F;
		dmg *= ((mod.getComboMoveCritMult()-1)*mod.getHtSS().crit+1);
		// Global Mods
		dmg *= mod.getModArmorMH();
		if (bc.hasDebuff(Debuff.physicalDamage))
			dmg *= 1.04F;
		if (bc.hasBuff(Buff.damage))
			dmg *= 1.03F;

		dmg *= 1+bbIncrease;
		return dmg;
	}
	
	private float calcKillingSpreeDPS() {
		float dps = 0;
		if (talents.getKs()) {
			float dmg, dmgMh, dmgOh;
			dmgMh = setup.getWeapon1().getAverageDmg(totalATP)*mod.getModArmorMH();
			dmgMh = dmgMh*mod.getHtMHS().hit + dmgMh*mod.getHtMHS().crit*mod.getPhysCritMult();
			dmgOh = setup.getWeapon2().getAverageDmg(totalATP)*mod.getModArmorOH()*0.75F;
			dmgOh = dmgOh*mod.getHtMHS().hit + dmgOh*mod.getHtMHS().crit*mod.getPhysCritMult();
			dmg = (dmgMh + dmgOh) * 5 * 1.2F;
			// Global Mods
			if (bc.hasDebuff(Debuff.physicalDamage))
				dmg *= 1.04F;
			if (bc.hasBuff(Buff.damage))
				dmg *= 1.03F;
			dps = dmg / 75F;
			dps *= 1+bbIncrease;
			
			mhSPS += 5/75F * mod.getHtMHS().getContacts();
			mhSCPS += 5/75F * mod.getHtMHS().getCrit();
			ohSPS += 5/75F * mod.getHtOHS().getContacts();
			ohSCPS += 5/75F * mod.getHtOHS().getCrit();
		}
		return dps;
	}

}
