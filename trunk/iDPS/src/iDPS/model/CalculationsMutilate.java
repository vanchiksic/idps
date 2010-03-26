package iDPS.model;


import iDPS.Glyphs.Glyph;
import iDPS.gear.Weapon.WeaponType;


public class CalculationsMutilate extends Calculations {

	private float mutPerSec, envPerSec, rupPerSec;
	float dpsMut, dpsEnv;

	protected CalculationsMutilate() {
		super();
		this.type = ModelType.Mutilate;
	}

	protected void calcDPS() {
		dpsWH = calcWhiteDPS();
		dpsMut = calcDamageMutilate() * mutPerSec;
		dpsEnv = calcDamageEnvenom() * envPerSec;
		dpsDP = calcDeadlyPoisonDPS();
		dpsIP = calcInstantPoisonDPS();
		dpsRU = 0;
		if (setup.isUseRupture())
			dpsRU = calcDamageRupture(avgCP4Plus) / (avgCP4Plus*2+10)
			* setup.getRuptureUptime();
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

	protected void calcCycle() {
		float cMut2cp, cMut3cp;
		float pCritCp = 0.2F*talents.getTalentPoints("SFate");
		cMut2cp  = (float) Math.pow((1-mod.getHtMut().crit*pCritCp), 2);
		cMut3cp  = 1-cMut2cp;

		if (gear.getWeapon1().getType() != WeaponType.Dagger
				|| gear.getWeapon2().getType() != WeaponType.Dagger) {
			mutPerSec = 0;
			envPerSec = 0;
			rupPerSec = 0;
			avgCP4Plus = 0;
			envenomUptime = 0;
			return;
		}

		float pT10 = 0;
		if (gear.getTier10()>=4)
			pT10 = 0.13F;
		float pRuth = 0.2F * talents.getTalentPoints("Ruth");

		float c0MutFin4, c1MutFin4, c1MutFin5, c2MutFin4, c2MutFin5, mutPerFin;
		c0MutFin4 = pRuth*pT10;
		c1MutFin4 = pRuth*(1-pT10)*cMut3cp;
		c1MutFin5 = (1-pRuth)*pT10;
		c2MutFin4 = (1-pRuth)*(1-pT10)*cMut2cp*cMut2cp;
		c2MutFin5 = (1-pRuth)*(1-pT10)-c2MutFin4+pRuth*(1-pT10)*cMut2cp;
		float cFin4 = c0MutFin4+c1MutFin4+c2MutFin4;
		float cFin5 = c1MutFin5+c2MutFin5;
		float cMut0 = c0MutFin4;
		float cMut1 = c1MutFin4+c1MutFin5;
		float cMut2 = c2MutFin4+c2MutFin5;
		avgCP4Plus = 4*cFin4+5*cFin5;
		mutPerFin = 0*cMut0 + 1*cMut1 + 2*cMut2;

		float eRegen = calcERegen();

		float eCostEnv1;
		float eCostMut = 60;
		if (glyphs.has(Glyph.Mut))
			eCostMut -= 5;
		eCostMut = eCostMut*(0.8F+0.2F/(mod.getHtMut().getContacts()));

		eCostMut -= 2*mod.getHtMut().crit*2;
		eCostEnv1 = 35/(mod.getHtMHS().getContacts());
		eCostEnv1 -= mod.getHtMHS().crit*2;
		float eCostEnv = eCostEnv1
		- avgCP4Plus*talents.getTalentPoints("RStrikes");
		float eCostRup = 25/(mod.getHtMHS().getContacts())
		- avgCP4Plus*talents.getTalentPoints("RStrikes");
		float eCostEA = eCostRup - 5 * talents.getTalentPoints("IEA");
		float envBuildTime = (mutPerFin*eCostMut+eCostEnv)/eRegen;
		float eaLength = 0;
		if (setup.isUseExpose()) {
			eaLength = avgCP4Plus*6;
			if (glyphs.has(Glyph.EA))
				eaLength += 10;
		}
		float rupLength = 0;
		if (setup.isUseRupture() && setup.getRuptureUptime()>0) {
			rupLength = avgCP4Plus*2+6;
			if (glyphs.has(Glyph.Rup))
				rupLength += 4;
			rupLength /= setup.getRuptureUptime();
		}
		float cycleLength = Math.max(eaLength, Math.max(rupLength, envBuildTime));
		float eaPerCycle = 0;
		if (setup.isUseExpose())
			eaPerCycle = cycleLength/eaLength;
		float rupPerCycle = 0;
		if (setup.isUseRupture())
			rupPerCycle = cycleLength/rupLength;
		float eCostEACycle = mutPerFin*eCostMut + eCostEA;
		float eCostRupCycle = mutPerFin*eCostMut + eCostRup;
		eRegen -= (eaPerCycle*eCostEACycle)/cycleLength;
		eRegen -= (rupPerCycle*eCostRupCycle)/cycleLength;
		envBuildTime = (mutPerFin*eCostMut+eCostEnv)/eRegen;
		float envPerCycle = cycleLength/envBuildTime;
		float finPerCycle = envPerCycle + rupPerCycle + eaPerCycle;
		float envRatio = envPerCycle/finPerCycle;

		// Average Envenom Length
		// We let Envenom only last as long as it takes to regen the spent energy
		// However there is a chance the following finisher will not be Envenom
		float lE4M0 = Math.min((eCostEnv1-4*talents.getTalentPoints("RStrikes"))/eRegen, 5);
		lE4M0 = lE4M0 * envRatio + 5 * (1-envRatio);
		float lE4M1 = Math.min((eCostEnv1-4*talents.getTalentPoints("RStrikes")+eCostMut)/eRegen, 5);
		lE4M1 = lE4M1 * envRatio + 5 * (1-envRatio);
		float lE4M2 = Math.min((eCostEnv1-4*talents.getTalentPoints("RStrikes")+2*eCostMut)/eRegen, 5);
		lE4M2 = lE4M2 * envRatio + 5 * (1-envRatio);
		float lE5M1 = Math.min((eCostEnv1-5*talents.getTalentPoints("RStrikes")+eCostMut)/eRegen, 6);
		lE5M1 = lE5M1 * envRatio + 6 * (1-envRatio);
		float lE5M2 = Math.min((eCostEnv1-5*talents.getTalentPoints("RStrikes")+2*eCostMut)/eRegen, 6);
		lE5M2 = lE5M2 * envRatio + 6 * (1-envRatio);

		float envLen = lE4M0*cFin4*cMut0 + lE4M1*cFin4*cMut1 + lE4M2*cFin4*cMut2
		+ lE5M1*cFin5*cMut1 + lE5M2*cFin5*cMut2;

		mutPerSec = mutPerFin*finPerCycle/cycleLength;
		envPerSec = envPerCycle/cycleLength;

		envenomUptime = envLen * envPerSec;

		mhSPS = (mutPerSec+envPerSec+rupPerSec);
		ohSPS = mutPerSec;
	}

	protected float calcERegen() {
		float eRegen = super.calcERegen();

		if (talents.getTalentPoints("OKill")>0) {
			int overkills = 1+getMaxUses(120);
			float okRegen = 60F*overkills/fightDuration;
			okRegen = Math.min(okRegen, 3);
			eRegen += okRegen;
		}
		calcWhiteDPS();
		if (talents.getTalentPoints("FAttacks")>0)
			eRegen += (mhWCPS+ohWCPS)*2F/3F*talents.getTalentPoints("FAttacks");

		if (talents.getTalentPoints("HfB")>0)
			eRegen -= 0.25F;

		return eRegen;
	}

	private float calcDamageEnvenom() {
		float dmg = (215*avgCP4Plus + 0.09F*avgCP4Plus * totalATP)
		* (1+2/30F*talents.getTalentPoints("VPoisons")
				+ 0.02F*talents.getTalentPoints("FWeakness"));
		dmg += dmg*(mod.getPhysCritMult()-1)*mod.getHtFin().crit;
		// Global Mods
		dmg *= getGlobalDmgMod(false, false);

		return dmg;
	}

	private float calcDamageMutilate() {
		float dmg, dmg1, dmg2;
		if (gear.getWeapon1().getType() != WeaponType.Dagger || gear.getWeapon2().getType() != WeaponType.Dagger)
			return 0;
		dmg1 = gear.getWeapon1().getInstantDmg(totalATP) + 181;
		dmg2 = (gear.getWeapon2().getInstantDmg(totalATP)*0.5F + 181)
		* (1+0.1F*talents.getTalentPoints("DWield"));
		// total * poisoned * critMod * Opp+FW 
		dmg = (dmg1+dmg2) * 1.2F * ((mod.getComboMoveCritMult()-1)*mod.getHtMut().crit + 1);
		dmg *= (1+0.1F*talents.getTalentPoints("Opp")
				+0.02F*talents.getTalentPoints("FWeakness"));
		// Global Mods
		dmg *= getGlobalDmgMod(true, false);

		dmg *= mod.getModArmorMH();
		return dmg;
	}

}
