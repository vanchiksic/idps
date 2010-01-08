package iDPS.model;


import iDPS.gear.Weapon.weaponType;


public class CalculationsMutilate extends Calculations {

	private float mutPerSec, envPerSec, rupPerSec;
	float dpsMut, dpsEnv;
	
	protected CalculationsMutilate() {
		super();
		this.type = ModelType.Mutilate;
	}
	
	protected void calcDPS() {
		dpsWH = calcWhiteDPS();
		dpsMut = calcMutilateDamage() * mutPerSec;
		dpsEnv = calcEnvenomDamage() * envPerSec;
		dpsDP = calcDeadlyPoisonDPS();
		dpsIP = calcInstantPoisonDPS();
		dpsRU = calcRuptureDPS();
		total = dpsWH + dpsMut + dpsEnv + dpsDP + dpsIP + dpsRU;
		
		/*System.out.format("%10s %10.4f dps%n", "White:", dpsWH);
		System.out.format("%10s %10.4f dps%n", "Mutilate:", dpsMut);
		System.out.format("%10s %10.4f dps%n", "Envenom:", dpsEnv);
		System.out.format("%10s %10.4f dps%n", "DeadlyP:", dpsDP);
		System.out.format("%10s %10.4f dps%n", "InstantP:", dpsIP);
		System.out.format("%10s %10.4f dps%n", "Rupture:", dpsRU);
		System.out.format("%10s %10.4f dps%n", "Total:", total);
		System.out.println();*/
	}
	
	@SuppressWarnings("unused")
	protected void calcCycle() {
		float cMut2cp, cMut3cp, avgCpMut;
		cMut2cp  = (1-mod.getHtMut().crit)*(1-mod.getHtMut().crit);
		cMut3cp  = 1-cMut2cp;
		avgCpMut = 2+cMut3cp;
		
		if (gear.getWeapon1().getType() != weaponType.Dagger
				|| gear.getWeapon2().getType() != weaponType.Dagger) {
			mutPerSec = 0;
			envPerSec = 0;
			rupPerSec = 0;
			avgCpFin = 0;
			envenomUptime = 0;
			return;
		}
		
		float pT10 = 0;
		if (gear.getTier10()>=4)
			pT10 = 0.13F;
		
		float c0MutFin4, c1MutFin4, c1MutFin5, c2MutFin4, c2MutFin5, mutPerFin;
		c0MutFin4 = 0.6F*pT10;
		c1MutFin4 = 0.6F*(1-pT10)*cMut3cp;
		c1MutFin5 = 0.4F*pT10;
		c2MutFin4 = 0.4F*(1-pT10)*cMut2cp*cMut2cp;
		c2MutFin5 = 0.4F*(1-pT10)-c2MutFin4+0.6F*(1-pT10)*cMut2cp;
		float cFin4 = c0MutFin4+c1MutFin4+c2MutFin4;
		float cFin5 = c1MutFin5+c2MutFin5;
		float cMut0 = c0MutFin4;
		float cMut1 = c1MutFin4+c1MutFin5;
		float cMut2 = c2MutFin4+c2MutFin5;
		avgCpFin = 4*cFin4+5*cFin5;
		mutPerFin = 0*cMut0 + 1*cMut1 + 2*cMut2;
		
		float eRegenFA, eLossHFB, eLossTOT;
		eRegenFA = calcWhiteCritsPerSec()*2;
		eLossHFB = 0.5F;
		eLossTOT = 0.5F;
		if (gear.getTier10()>=2)
			eLossTOT = -0.5F;
		float eRegen = super.eRegen + eRegenFA - eLossHFB - eLossTOT;
		
		float eCostMut, eCostEnv;
		eCostMut = 55 *(0.8F+0.2F/(mod.getHtMut().getContacts()));
		eCostMut -= 2*mod.getHtMut().crit*2;
		eCostEnv = 35/(mod.getHtMHS().getContacts());
		eCostEnv -= mod.getHtMHS().crit*2;
		
		// Average Envenom Length
		// We allow to pool 20 energy
		float pool = 20;
		float lE4M0 = Math.min((eCostEnv-4*25*talents.getRStrikes()+pool)/eRegen, 5);
		float lE4M1 = Math.min((eCostEnv-4*25*talents.getRStrikes()+eCostMut+pool)/eRegen, 5);
		float lE4M2 = Math.min((eCostEnv-4*25*talents.getRStrikes()+2*eCostMut+pool)/eRegen, 5);
		float lE5M1 = Math.min((eCostEnv-4*25*talents.getRStrikes()+eCostMut+pool)/eRegen, 6);
		float lE5M2 = Math.min((eCostEnv-4*25*talents.getRStrikes()+2*eCostMut+pool)/eRegen, 6);
		
		float envLen = lE4M0*cFin4*cMut0 + lE4M1*cFin4*cMut1 + lE4M2*cFin4*cMut2
								 + lE5M1*cFin5*cMut1 + lE5M2*cFin5*cMut2;
		
		eCostEnv -= avgCpFin*talents.getRStrikes()*25;
		
		if (false) {
			float avgRupLength = avgCpFin*2+10;
			//System.out.println("Rupture Len: "+avgRupLength);
			rupPerCycle = (mutPerFin*eCostMut+eCostEnv)/(avgRupLength*eRegen+10);
		} else
			rupPerCycle = 0;
		//System.out.println("Ruptures per C: "+rupPerCycle);
		
		float eCostCycle, eCostFin, lengthCycle;
		eCostFin = eCostEnv - 10*rupPerCycle;
		eCostCycle = mutPerFin*eCostMut + eCostFin;
		lengthCycle = eCostCycle / eRegen;
		//System.out.println("Cycle length: "+lengthCycle);
		
		envenomUptime = envLen / lengthCycle;
		//System.out.println("Envenom Uptime: "+envenomUptime);
		
		mutPerSec = mutPerFin/lengthCycle;
		envPerSec = (1-rupPerCycle)/lengthCycle;
		rupPerSec = rupPerCycle/lengthCycle;
		
		mhSPS = (mutPerSec+envPerSec+rupPerSec);
		ohSPS = mutPerSec;
	}
	
	private float calcMutilateDamage() {
		float dmg, dmg1, dmg2;
		if (gear.getWeapon1().getType() != weaponType.Dagger || gear.getWeapon2().getType() != weaponType.Dagger)
			return 0;
		dmg1 = gear.getWeapon1().getInstantDmg(totalATP) + 181;
		dmg2 = (gear.getWeapon2().getInstantDmg(totalATP)*0.5F + 181)*1.5F;
		// total * poisoned * critMod * Opp+FW 
		dmg = (dmg1+dmg2) * 1.2F * ((mod.getComboMoveCritMult()-1)*mod.getHtMut().crit + 1);
		dmg *= (1+talents.getOpportunity()+talents.getFindWeakness());
		// Global Mods
		dmg *= talents.getMurder() * talents.getHfb() * 1.03F * 1.04F;
		
		dmg *= mod.getModArmorMH();
		return dmg;
	}

}
