package iDPS.model;

import iDPS.Launcher;
import iDPS.Glyphs.Glyph;

public class CalculationsSubBStab extends Calculations {
	
	private float dpsAmb, dpsBS, dpsEvi;
	private float bsPerSec, eviPerSec, rupPerSec, ambPerSec;

	@Override
	protected void calcDPS() {
		dpsWH = calcWhiteDPS();
		dpsBS = calcDamageBackstab()*bsPerSec;
		dpsEvi = calcEviscerateDamage(5) * eviPerSec;
		dpsDP = calcDeadlyPoisonDPS();
		dpsIP = calcInstantPoisonDPS();
		dpsRU = calcDamageRupture(5) * rupPerSec;
		dpsAmb = calcDamageAmbush() * ambPerSec;
		total = dpsWH + dpsBS + dpsEvi + dpsDP + dpsIP + dpsRU + dpsAmb;
				
		/*System.out.format("%10s %10.4f dps%n", "White:", dpsWH);
		System.out.format("%10s %10.4f dps%n", "Backstab:", dpsBS);
		System.out.format("%10s %10.4f dps%n", "Evisc:", dpsEvi);
		System.out.format("%10s %10.4f dps%n", "DeadlyP:", dpsDP);
		System.out.format("%10s %10.4f dps%n", "InstantP:", dpsIP);
		System.out.format("%10s %10.4f dps%n", "Rupture:", dpsRU);
		System.out.format("%10s %10.4f dps%n", "Ambush:", dpsAmb);
		System.out.format("%10s %10.4f dps%n", "Total:", total);
		System.out.println();*/
	}
	
	@Override
	protected void calcCycle() {
		float pT10 = 0;
		if (gear.getTier10()>=4)
			pT10 = 0.13F;
		float pRuth = 0.2F*talents.getTalentPoints("Ruth");
		
		float eCostBS = (60-4*talents.getTalentPoints("SftS"))*(0.8F+0.2F/(mod.getHtBS().getContacts()));
		float eCostSnD = 25 - 5*talents.getTalentPoints("RStrikes");
		float eCostRup = 25/(mod.getHtMHS().getContacts()) - 5*talents.getTalentPoints("RStrikes");
		float eCostEA = eCostRup;
		float eCostEvi = 35/(mod.getHtMHS().getContacts()) - 5*talents.getTalentPoints("RStrikes");
		
		float hatPerSec = 0;
		if (talents.getTalentPoints("HaT")>0)
			hatPerSec = 1/(2.2F-0.3F*talents.getTalentPoints("HaT"));
		//System.out.println("HaT: "+hatPerSec);
		
		float eRegen = calcERegen();
		
		float sdEvisc = 0;
		
		// Shadowdance
		if (talents.getTalentPoints("SDance")>0) {
			int sdLength = 6;
			if (glyphs.has(Glyph.SD))
				sdLength += 2;
			int ambushes = 3;
			if (glyphs.has(Glyph.SD) && gear.getTier10()>=2)
				ambushes++;
			ambPerSec = ambushes/60F;
			float cpAmbush = ambushes * (2+talents.getTalentPoints("Init")/3F);
			float sdCp = cpAmbush + sdLength*hatPerSec;
			sdEvisc = sdCp/5F;
			float sdEnergy = ambushes*40F + eCostEvi*sdEvisc;
			//System.out.println("Energy used during SD: "+sdEnergy);
			eRegen -= sdEnergy/60F;
		}
		
		float cpDuringBS = 1+eCostBS/eRegen*hatPerSec;
		float bsPerEA = (5-pRuth-3*pT10-eCostEA/eRegen*hatPerSec)/cpDuringBS;
		float bsPerSnD = (5-pRuth-3*pT10-eCostSnD/eRegen*hatPerSec)/cpDuringBS;
		float bsPerRup = (5-pRuth-3*pT10-eCostRup/eRegen*hatPerSec)/cpDuringBS;
		float bsPerEvi = (5-pRuth-3*pT10-eCostEvi/eRegen*hatPerSec)/cpDuringBS;
		
		// Build Time EA
		float btEA = (eCostEA+bsPerEA*eCostBS)/eRegen;
		// Build Time SnD
		float btSnD = (eCostEA+bsPerSnD*eCostBS)/eRegen;
		// Build Time Rupture
		float btRup = (eCostEA+bsPerRup*eCostBS)/eRegen;
		// Build Time Evisc
		float btEvi = (eCostEA+bsPerEvi*eCostBS)/eRegen;
		
		float lEA = 0;
		if (Launcher.getApp().getUseExpose()) {
			lEA = 30;
			if (glyphs.has(Glyph.EA))
				lEA += 10;
		}
		float lSnD = 21*(1+talents.getTalentPoints("ISnD")/4F);
		float lRup = 16.5F;
		if (glyphs.has(Glyph.Rup))
			lRup += 4;
		float lCycle = Math.max(lSnD,lEA);
		
		float pcSnD = lCycle/lSnD;
		
		float timeLeft = lCycle - pcSnD*btSnD - 2; // add some slack
		//System.out.println("TL after SnD: "+timeLeft);
		
		float pcEA = 0;
		if (Launcher.getApp().getUseExpose()) {
			pcEA = lCycle/lEA;
			timeLeft -= pcEA*btEA + 2; // add some slack
		}
		//System.out.println("TL after EA: "+timeLeft);
		
		float pcRup = 0;
		if (Launcher.getApp().getUseRupture()) {
			pcRup = lCycle/lRup;
			if (timeLeft < (pcRup*btRup))
				pcRup = timeLeft/btRup;
			timeLeft -= pcRup*btRup + 2; // add some slack
		}
		//System.out.println("TL after Rup: "+timeLeft);
		
		float pcEvi = timeLeft/btEvi;
		
		float sndPerSec = pcSnD/lCycle;
		float eaPerSec = pcEA/lCycle;
		rupPerSec = pcRup/lCycle;
		eviPerSec = pcEvi/lCycle;
		
		bsPerSec = sndPerSec*bsPerSnD + eaPerSec*bsPerEA
					+ rupPerSec*bsPerRup + eviPerSec*bsPerEvi;
		
		eviPerSec += sdEvisc/60F;
		
		mhSPS = bsPerSec+eviPerSec+eaPerSec+rupPerSec+ambPerSec;
		mhSCPS = bsPerSec*mod.getHtSS().getCrit()
					+ eviPerSec*mod.getHtFin().getCrit()
					+ ambPerSec*mod.getHtAmb().getCrit();
		
		ohSPS = 0;
		ohSCPS = 0;
		
	}

}
