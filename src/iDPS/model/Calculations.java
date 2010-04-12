package iDPS.model;

import iDPS.Attributes;
import iDPS.Glyphs;
import iDPS.Launcher;
import iDPS.Race;
import iDPS.Setup;
import iDPS.Talents;
import iDPS.Glyphs.Glyph;
import iDPS.controllers.BuffController;
import iDPS.controllers.BuffController.Buff;
import iDPS.controllers.BuffController.Debuff;
import iDPS.controllers.BuffController.Other;
import iDPS.gear.Gear;
import iDPS.gear.Weapon;
import iDPS.gear.Weapon.WeaponType;

public abstract class Calculations {
	
	public enum ModelType { Combat, Mutilate, SubHemo, SubBStab };
	
	protected float avgCP4Plus;
	float ppsIP1, ppsIP2;
	float dpsWH, dpsDP, dpsIP, dpsRU, total;
	protected float envenomUptime, envPerSec;
	float epAGI, epHIT, epCRI, epHST, epARP, epEXP;
	protected Setup setup;
	protected Gear gear;
	protected float mhSPS, ohSPS, mhSCPS, ohSCPS;
	protected float mhWPS, ohWPS, mhWCPS, ohWCPS;
	protected Modifiers mod;
	protected Talents talents;
	protected Glyphs glyphs;
	protected BuffController bc;
	
	protected float bbIncrease;
	
	protected float totalATP;
	
	protected float fightDuration = 300;
	
	protected ModelType type;
	
	private void reset() {
		mhSPS = 0;
		mhSCPS = 0;
		ohSPS = 0;
		ohSCPS = 0;
		mhWPS = 0;
		mhWCPS = 0;
		ohWPS = 0;
		ohWCPS = 0;
	}
	
	protected abstract void calcCycle();
		
	protected float calcDeadlyPoisonDPS() {
		//System.out.println("dp ap: "+totalATP);
		float dps = (296+0.108F*totalATP)/12*5;
		dps *= (1+2/30F*talents.getTalentPoints("VPoisons"));
		dps *= 0.971875F;
		dps *= getGlobalDmgMod(false, true);
		return dps;
	}
	
	protected abstract void calcDPS();
	
	public void calcEP() {
		float dpsATP;
		Calculations c;
		try {
			//System.out.println("EP Calcs");
			c = getClass().newInstance();
			Attributes attr = new Attributes();
			//System.out.println("EP ATP");
			attr.setAtp(1);
			c.calculate(attr, setup);
			dpsATP = c.total - total;
			attr.clear();
			//System.out.println("EP AGI");
			attr.setAgi(1);
			c.calculate(attr, setup);
			epAGI = (c.total - total) / dpsATP;
			attr.clear();
			//System.out.println("EP HIT");
			attr.setHit(1);
			c.calculate(attr, setup);
			epHIT = (c.total - total) / dpsATP;
			attr.clear();
			//System.out.println("EP CRI");
			attr.setCri(1);
			c.calculate(attr, setup);
			epCRI = (c.total - total) / dpsATP;
			attr.clear();
			//System.out.println("EP HST");
			attr.setHst(1);
			c.calculate(attr, setup);
			epHST = (c.total - total) / dpsATP;
			attr.clear();
			//System.out.println("EP EXP");
			attr.setExp(1);
			c.calculate(attr, setup);
			epEXP = (c.total - total) / dpsATP;
			attr.clear();
			//System.out.println("EP ARP");
			attr.setArp(1);
			c.calculate(attr, setup);
			epARP = (c.total - total) / dpsATP;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	protected float calcEviscerateDamage(float cp) {
		float baseDmg = 0;
		if (cp >= 4)
			baseDmg = 1607*(5-cp)+1977*(cp-4);
		float dmg = (baseDmg + 0.07F*cp * totalATP)
			* (1+2/30F*talents.getTalentPoints("IEvisc")
					+ 0.03F*talents.getTalentPoints("Aggr"));
		dmg += dmg*(mod.getPhysCritMult()-1)*mod.getHtEvi().crit;
		// Global Mods
		dmg *= getGlobalDmgMod(true, false);

		dmg *= mod.getModArmorMH();
		dmg *= 1+bbIncrease;
		
		return dmg;
	}
	
	protected float calcInstantPoisonDPS() {
		// possible procs per sec
		float ip_ppps = 0, dp_ppps = 0;
		Weapon wip; // Weapon with IP
		if (gear.getWeapon2().getSpeed() >= gear.getWeapon1().getSpeed()) {
			wip = gear.getWeapon2();
			ip_ppps = ohWPS + ohSPS;
			dp_ppps = mhWPS + mhSPS - envPerSec;
		} else {
			wip = gear.getWeapon1();
			ip_ppps = mhWPS + mhSPS - envPerSec;
			dp_ppps = ohWPS + ohSPS;
		}
		// Chance to proc IP
		float ipProcChanceDef = wip.getSpeed()/1.4F*(0.2F+0.02F*talents.getTalentPoints("IPoisons"));
		float ipProcChanceEnv = Math.min(1F, ipProcChanceDef*1.75F);
		float ipProcChance = ipProcChanceDef*(1-envenomUptime) + ipProcChanceEnv*envenomUptime;
		// Chance to proc DP
		float dpProcChanceDef = 0.3F + 0.04F*talents.getTalentPoints("IPoisons");
		float dpProcChanceEnv = Math.min(1F, dpProcChanceDef+0.15F);
		float dpProcChance = dpProcChanceDef*(1-envenomUptime) + dpProcChanceEnv*envenomUptime;
		// Chance to Hit
		float hitChance = Math.min(1, 0.83F+mod.getSpellHitPercent()/100);
		// Primary Procs
		ppsIP1 = ip_ppps*ipProcChance*hitChance;
		// Secondary Procs - through DP applications
		ppsIP2 = dp_ppps*dpProcChance*hitChance;
		// Procs from envenom applications
		if (wip == gear.getWeapon1())
			ppsIP1 += envPerSec * ipProcChanceEnv * hitChance;
		else
			ppsIP2 += envPerSec * dpProcChanceEnv * hitChance;
		float procsPerSec = ppsIP1+ppsIP2;
		// Damage
		float damage = (350+0.09F*totalATP)
			* (1+2/30F*talents.getTalentPoints("VPoisons"))
			* 0.971875F;
		damage += damage*(mod.getPoisonCritMult()-1)*(mod.getSpellCritPercent()/100F);
		// Global Mods
		damage *= getGlobalDmgMod(false, true);

		return procsPerSec * damage;
	}
	
	protected void calcProcs() {
		float hitsPerSec = mhWPS+ohWPS+mhSPS+ohSPS;
		float critsPerSec = mhWCPS+ohWCPS+mhSCPS+ohSCPS;
		Attributes a;
		float uptime;
		
		// Bloodlust / Heroism
		if (bc.hasOther(Other.bloodlust)) {
			uptime = 40/fightDuration;
			mod.registerStaticHasteProc(0.3F, uptime);
		}
		
		// Orc Racial
		if (setup.getRace().getType() == Race.Type.Orc) {
			a = new Attributes(Attributes.Type.ATP, 322);
			mod.registerProc(new ProcStatic(a, 15, 120, 1, 1));
		}
		
		// Troll Racial
		if (setup.getRace().getType() == Race.Type.Troll) {
			uptime = getMaxUses(180)*10F/fightDuration;
			mod.registerStaticHasteProc(0.2F, uptime);
		}
		
		// Blade Flurry
		if (talents.getTalentPoints("BF")>0) {
			uptime = getMaxUses(120)*15F/fightDuration;
			mod.registerStaticHasteProc(0.2F, uptime);
		}
		
		// Mongoose
		if (gear.getWeapon1() != null && gear.isEnchanted(16) && gear.getEnchant(16).getId()==2673) {
			a = new Attributes(Attributes.Type.AGI, 120);
			Proc p = new ProcPerMinute(a, 15, 0, 1, 
					gear.getWeapon1(), (mhWPS+mhSPS),
					gear.getWeapon2(), 0);
			mod.registerProc(p);
			mod.registerStaticHasteProc(0.02F, p.getUptime());
		}
		if (gear.getWeapon2() != null && gear.isEnchanted(17) && gear.getEnchant(17).getId()==2673) {
			a = new Attributes(Attributes.Type.AGI, 120);
			Proc p = new ProcPerMinute(a, 15, 0, 1, 
					gear.getWeapon1(), 0,
					gear.getWeapon2(), (ohWPS+ohSPS));
			mod.registerProc(p);
			mod.registerStaticHasteProc(0.02F, p.getUptime());
		}
		
		// Berserking
		if (gear.getWeapon1() != null && gear.isEnchanted(16) && gear.getEnchant(16).getId()==3789) {
			a = new Attributes(Attributes.Type.ATP, 400);
			Proc p = new ProcPerMinute(a, 15, 0, 1, 
					gear.getWeapon1(), (mhWPS+mhSPS),
					gear.getWeapon2(), 0);
			mod.registerProc(p);
		}
		if (gear.getWeapon2() != null && gear.isEnchanted(17) && gear.getEnchant(17).getId()==3789) {
			a = new Attributes(Attributes.Type.ATP, 400);
			Proc p = new ProcPerMinute(a, 15, 0, 1, 
					gear.getWeapon1(), 0,
					gear.getWeapon2(), (ohWPS+ohSPS));
			mod.registerProc(p);
		}
		
		// Black Magic
		if ((gear.getWeapon1() != null && gear.isEnchanted(16) && gear.getEnchant(16).getId()==3790) ||
				(gear.getWeapon2() != null && gear.isEnchanted(17) && gear.getEnchant(17).getId()==3790)) {
			a = new Attributes(Attributes.Type.HST, 250);
			calcInstantPoisonDPS();
			float poisonProcs = this.ppsIP1 + 2*ppsIP2;
			Proc p = new ProcStatic(a, 10, 35, 0.35F, poisonProcs);
			mod.registerProc(p);
		}
		
		// Hyperspeed Accelerators
		if (gear.isEnchanted(8) && gear.getEnchant(8).getId()==3604) {
			a = new Attributes(Attributes.Type.HST, 340);
			mod.registerProc(new ProcStatic(a, 12, 60, 1, 1));
		}
		
		// Swordguard Embroidery
		if (gear.isEnchanted(3) && gear.getEnchant(3).getId()==3730) {
			a = new Attributes(Attributes.Type.ATP, 400);
			mod.registerProc(new ProcStatic(a, 15, 60, 0.25F, hitsPerSec));
		}
		
		// Grim Toll
		if (gear.contains(40256)>0) {
			a = new Attributes(Attributes.Type.ARP, 612);
			mod.registerProc(new ProcStatic(a, 10, 45, 0.15F, hitsPerSec));
		}
		
		// Mirror of Truth
		if (gear.contains(40684)>0) {
			a = new Attributes(Attributes.Type.ATP, 1000);
			mod.registerProc(new ProcStatic(a, 10, 45, 0.1F, critsPerSec));
		}
		
		// Tears of Bitter Anguish
		if (gear.contains(43573)>0) {
			a = new Attributes(Attributes.Type.HST, 410);
			mod.registerProc(new ProcStatic(a, 10, 45, 0.1F, critsPerSec));
		}
		
		// Darkmoon Card: Greatness
		if (gear.contains(44253)>0) {
			a = new Attributes(Attributes.Type.AGI, 300);
			mod.registerProc(new ProcStatic(a, 15, 45, 0.35F, hitsPerSec));
		}
		
		// Pyrite Infuser
		if (gear.contains(45286)>0) {
			a = new Attributes(Attributes.Type.ATP, 1234);
			mod.registerProc(new ProcStatic(a, 10, 45, 0.1F, critsPerSec));
		}
		
		// Blood of the Old God
		if (gear.contains(45522)>0) {
			a = new Attributes(Attributes.Type.ATP, 1284);
			Proc p = new ProcStatic(a, 10, 45, 0.1F, critsPerSec);
			mod.registerProc(p);
		}
		
		// Comet's Trail
		if (gear.contains(45609)>0) {
			a = new Attributes(Attributes.Type.HST, 726);
			mod.registerProc(new ProcStatic(a, 10, 45, 0.15F, hitsPerSec));
		}
		
		// Mjolnir Runestone
		if (gear.contains(45931)>0) {
			a = new Attributes(Attributes.Type.ARP, 665);
			mod.registerProc(new ProcStatic(a, 10, 45, 0.15F, hitsPerSec));
		}
		
		// Dark Matter
		if (gear.contains(46038)>0) {
			a = new Attributes(Attributes.Type.CRI, 612);
			mod.registerProc(new ProcStatic(a, 10, 45, 0.15F, hitsPerSec));
		}
		
		// Banner of Victory
		if (gear.contains(47214)>0) {
			a = new Attributes(Attributes.Type.ATP, 1008);
			mod.registerProc(new ProcStatic(a, 10, 45, 0.2F, hitsPerSec));
		}
		
		// Death's Choice
		if (gear.containsAny(47303,47115)) {
			a = new Attributes(Attributes.Type.AGI, 450);
			mod.registerProc(new ProcStatic(a, 15, 45, 0.35F, hitsPerSec));
		}
		
		// Death's Choice Heroic
		if (gear.containsAny(47464,47131)) {
			a = new Attributes(Attributes.Type.AGI, 510);
			Proc p = new ProcStatic(a, 15, 45, 0.35F, hitsPerSec);
			mod.registerProc(p);
		}
		
		// Mark of Supremacy
		if (gear.contains(47734)>0) {
			a = new Attributes(Attributes.Type.ATP, 1024);
			mod.registerProc(new ProcStatic(a, 20, 120, 1, 1));
		}
		
		// Vengeance of the Forsaken
		if (gear.containsAny(47881,47725)) {
			float timeToCap = 5/hitsPerSec;
			float avgAtp = (timeToCap*537.5F + (20-timeToCap)*1075F)/20F;
			a = new Attributes(Attributes.Type.ATP, avgAtp);
			mod.registerProc(new ProcStatic(a, 20, 120, 1, 1));
		}
		
		// Vengeance of the Forsaken
		if (gear.containsAny(48020,47948)) {
			float timeToCap = 5/hitsPerSec;
			float avgAtp = (timeToCap*625F + (20-timeToCap)*1250F)/20F;
			a = new Attributes(Attributes.Type.ATP, avgAtp);
			mod.registerProc(new ProcStatic(a, 20, 120, 1, 1));
		}
		
		// Shard of the Crystal Heart
		if (gear.contains(48722)>0) {
			a = new Attributes(Attributes.Type.HST, 512);
			mod.registerProc(new ProcStatic(a, 20, 120, 1, 1));
		}
		
		// Black Bruise
		if (gear.containsAny(50035,50692)) {
			float bbUptime = calcDWPPMUptime(0.3F, 10);
			if(gear.containsAny(50692))
				bbIncrease = bbUptime*0.10F;
			else
				bbIncrease =bbUptime*0.09F;
		} else
			bbIncrease = 0;
		
		// Needle-Encrusted Scorpion
		if (gear.contains(50198)>0) {
			a = new Attributes(Attributes.Type.ARP, 678);
			mod.registerProc(new ProcStatic(a, 10, 50, 0.1F, critsPerSec));
		}
		
		// Whispering Fanged Skull
		if (gear.contains(50342)>0) {
			a = new Attributes(Attributes.Type.ATP, 1100);
			mod.registerProc(new ProcStatic(a, 15, 45, 0.35F, hitsPerSec));
		}
		
		// Whispering Fanged Skull Heroic
		if (gear.contains(50343)>0) {
			a = new Attributes(Attributes.Type.ATP, 1250);
			mod.registerProc(new ProcStatic(a, 15, 45, 0.35F, hitsPerSec));
		}
		
		// Herkuml War Token
		if (gear.contains(50355)>0) {
			a = new Attributes(Attributes.Type.ATP, 340);
			Proc p = new ProcStatic(a, 10, 1, 1, 1); // uptime = 100%
			mod.registerProc(p);
		}
		
		// Deathbringer's Will
		if (gear.contains(50362)>0) {
			a = new Attributes(Attributes.Type.ATP, 1200);
			mod.registerProc(new ProcStatic(a, 30, 315, 0.5F, hitsPerSec));
			a = new Attributes(Attributes.Type.AGI, 600);
			mod.registerProc(new ProcStatic(a, 30, 315, 0.5F, hitsPerSec));
			a = new Attributes(Attributes.Type.HST, 600);
			mod.registerProc(new ProcStatic(a, 30, 315, 0.5F, hitsPerSec));
		}
		
		// Deathbringer's Will Heroic
		if (gear.contains(50363)>0) {
			a = new Attributes(Attributes.Type.ATP, 1400);
			mod.registerProc(new ProcStatic(a, 30, 315, 0.5F, hitsPerSec));
			a = new Attributes(Attributes.Type.AGI, 700);
			mod.registerProc(new ProcStatic(a, 30, 315, 0.5F, hitsPerSec));
			a = new Attributes(Attributes.Type.HST, 700);
			mod.registerProc(new ProcStatic(a, 30, 315, 0.5F, hitsPerSec));
		}
		
		// Ashen Band of ...
		if (gear.containsAny(50401,50402)) {
			a = new Attributes(Attributes.Type.ATP, 480);
			mod.registerProc(new ProcPerMinute(a, 10, 60, 1,
					gear.getWeapon1(), (mhWPS+mhSPS),
					gear.getWeapon2(), (ohWPS+ohSPS)));
		}
	}
	
	protected float calcDWPPMUptime(float ppm, float buffLen) {
		float apsMH, apsOH, utMH, utOH, ut;
		
		apsMH = gear.getWeapon1().getEffectiveAPS(mod.getHastePercent()/100)*(mod.getHtMH().getContacts());
		apsMH += mhSPS;
		apsOH = gear.getWeapon2().getEffectiveAPS(mod.getHastePercent()/100)*(mod.getHtOH().getContacts());
		apsOH += ohSPS;
		
		utMH = gear.getWeapon1().getPPMUptime(ppm, buffLen, apsMH);
		utOH = gear.getWeapon1().getPPMUptime(ppm, buffLen, apsOH);
		ut = 1-((1-utMH)*(1-utOH));
		
		return ut;
	}
	
	protected float calcDWUptime(float pProc, float buffLen) {
		float apsMH, apsOH, utMH, utOH, ut;
		
		apsMH = gear.getWeapon1().getEffectiveAPS(mod.getHastePercent()/100)*(mod.getHtMH().getContacts());
		apsMH += mhSPS;
		apsOH = gear.getWeapon2().getEffectiveAPS(mod.getHastePercent()/100)*(mod.getHtOH().getContacts());
		apsOH += ohSPS;
		
		utMH = gear.getWeapon1().getUptime(0.04F, buffLen, apsMH);
		utOH = gear.getWeapon1().getUptime(0.04F, buffLen, apsOH);
		ut = 1-((1-utMH)*(1-utOH));
		
		return ut;
	}
	
	protected float calcHeartpierceRegen() {
		float regen = 0;
		float apsMH, apsOH, pp2s;
		apsMH = gear.getWeapon1().getEffectiveAPS(mod.getHastePercent()/100)*(mod.getHtMH().getContacts());
		apsMH += mhSPS;
		apsOH = gear.getWeapon2().getEffectiveAPS(mod.getHastePercent()/100)*(mod.getHtOH().getContacts());
		apsOH += ohSPS;
		pp2s = gear.getWeapon1().getPPMUptime(1, 2, apsMH);
		pp2s += gear.getWeapon2().getPPMUptime(1, 2, apsOH);
		if (gear.containsAny(49982)) {
			float uptime = calcDWPPMUptime(1, 10);
			regen += uptime*2*(1-pp2s);
		}
		if (gear.containsAny(50641)) {
			float uptime = calcDWPPMUptime(1, 12);
			regen += uptime*2*(1-pp2s);
		}
		
		return regen;
	}
	
	protected float calcDamageAmbush() {
		if (gear.getWeapon1().getType() != WeaponType.Dagger)
			return 0;
		float dmg = gear.getWeapon1().getInstantDmg(totalATP) * 2.75F + 907.5F;

		dmg *= (mod.getPhysCritMult()-1)*mod.getHtAmb().crit + 1;
		dmg *= (1+0.1F*talents.getTalentPoints("Opp")
				+0.02F*talents.getTalentPoints("FWeakness"));
		// Global Mods
		dmg *= getGlobalDmgMod(true, false);

		dmg *= mod.getModArmorMH();
		return dmg;
	}
	
	protected float calcDamageBackstab() {
		if (gear.getWeapon1().getType() != WeaponType.Dagger)
			return 0;
		float dmg = gear.getWeapon1().getInstantDmg(totalATP)
			* (1.5F+0.02F*talents.getTalentPoints("SCalling")) + 465;

		dmg *= (mod.getComboMoveCritMult()-1)*mod.getHtBS().crit + 1;
		dmg *= (1+0.1F*talents.getTalentPoints("Opp")
				+0.02F*talents.getTalentPoints("FWeakness"));
		// Global Mods
		dmg *= getGlobalDmgMod(true, false);

		dmg *= mod.getModArmorMH();
		return dmg;
	}
	
	protected float calcDamageRupture(float cp) {
		float tick4cp, tick5cp, avgTick;
		tick4cp = 199 + 0.03428571F*totalATP;
		tick5cp = 217 + 0.0375F*totalATP;
		avgTick = 0;
		if (cp>=4)
			avgTick = tick4cp*(5-cp)+tick5cp*(cp-4);
		avgTick += avgTick*(mod.getPhysCritMult()-1)*mod.getHtFin().crit;
		avgTick *= 1 + 0.15F*talents.getTalentPoints("BSpatter")
			+ 0.1F*talents.getTalentPoints("SBlades")
			+ 0.02F*talents.getTalentPoints("FWeakness");
		avgTick *= getGlobalDmgMod(true, true);

		if (bc.hasDebuff(Debuff.bleed))
			avgTick *= 1.3F;
		
		float dmg = avgTick*(3+cp);
		if (glyphs.has(Glyph.Rup))
			dmg += 2*avgTick;
		
		if (talents.getTalentPoints("SStep")>0 && glyphs.has(Glyph.Rup))
			dmg *= 1.2F;
		
		return dmg;
	}
	
	public void calculate(Setup setup) {
		calculate(null, setup, setup.getGear());
	}
	
	public void calculate(Setup setup, Gear gear) {
		calculate(null, setup, gear);
	}
	
	public void calculate(Attributes inject, Setup setup) {
		calculate(inject, setup, setup.getGear());
	}
	
	public void calculate(Attributes inject, Setup setup, Gear gear) {
		if (setup == null) {
			System.err.println("Cant calc with no setup!");
			return;
		}
		reset();
		//System.out.println("Calculating...");
		
		this.setup = setup;
		this.gear = gear;
		
		talents = setup.getTalents();
		glyphs = setup.getGlyphs();
		
		bc = Launcher.getApp().getBuffController();
		
		mod = new Modifiers(inject, setup, gear);
		
		//System.out.println(">> Iteration 1");
		calcWhiteDPS();
		calcCycle();
		calcProcs();
		//System.out.println("  AP: "+mod.getTotalATP());
		//System.out.println(">> Iteration 2");
		calcWhiteDPS();
		calcCycle();
		mod.calcMods();
		calcProcs();
		//System.out.println("  AP: "+mod.getTotalATP());
		//System.out.println(">> Iteration 3");
		calcWhiteDPS();
		calcCycle();
		mod.calcMods();
		calcProcs();
		//System.out.println("  AP: "+mod.getTotalATP());
		
		totalATP = mod.getTotalATP();
		//System.out.println("AP: "+totalATP);
		
		calcDPS();
	}
	
	protected float calcERegen() {
		float eRegen = 10;
		
		// Racial
		if (setup.getRace().getType() == Race.Type.BloodElf)
			eRegen += 15F/120F;
		
		// ToTT every 31 sec
		if (setup.isUseTotT()) {
			float eLossTOT;
			eLossTOT = (15F-talents.getTalentPoints("FTricks")*5F)
						/ (31F-talents.getTalentPoints("FTricks")*5F);
			if (gear.getTier10()>=2)
				eLossTOT -= 30F/(31F-talents.getTalentPoints("FTricks")*5F);;
			eRegen -= eLossTOT;
		}
		
		// 2P Tier9
		if (gear.getTier9()>=2 && setup.isUseRupture())
			eRegen += 0.4F * setup.getRuptureUptime();
		
		// Heartpierce
		if (gear.containsAny(49982,50641))
			eRegen += calcHeartpierceRegen();
		
		//System.out.println("ER: "+eRegen);
		return eRegen;
	}
	
	protected float calcWhiteDPS() {
		HitTable htMH = mod.getHtMH();
		HitTable htOH = mod.getHtOH();
		float dpsMH = 0, dpsOH = 0;
		mhWPS = 0; mhWCPS = 0;
		ohWPS = 0; ohWCPS = 0;
		// Mainhand
		if (gear.getWeapon1() != null) {
			dpsMH += gear.getWeapon1().getDps() + ((float)totalATP/14F);
			dpsMH *= htMH.glance*0.75F + htMH.crit * mod.getPhysCritMult() + htMH.hit;
			dpsMH *= mod.getHastePercent()/100F + 1;
			mhWPS = gear.getWeapon1().getEffectiveAPS(mod.getHastePercent()/100F)*mod.getHtMH().getContacts();
			mhWCPS = gear.getWeapon1().getEffectiveAPS(mod.getHastePercent()/100F)*mod.getHtMH().getCrit();
		}
		// Offhand
		if (gear.getWeapon2() != null) {
			dpsOH += gear.getWeapon2().getDps() + ((float)totalATP/14F)
				* 0.5F * (1+0.1F*talents.getTalentPoints("DWield"));
			dpsOH *= htOH.glance*0.75F + htOH.crit * mod.getPhysCritMult() + htOH.hit;
			dpsOH *= mod.getHastePercent()/100F + 1;
			ohWPS = gear.getWeapon2().getEffectiveAPS(mod.getHastePercent()/100F)*mod.getHtOH().getContacts();
			ohWCPS = gear.getWeapon2().getEffectiveAPS(mod.getHastePercent()/100F)*mod.getHtOH().getCrit();
		}
		// Sword Spec
		if (talents.getTalentPoints("HnS")>0) {
			Weapon w1 = gear.getWeapon1(), w2 = gear.getWeapon2();
			float ssDmg, ssPps = 0, ssDps;
			ssDmg  = gear.getWeapon1().getAverageDmg(totalATP);
			// Mainhand Procs
			if (w1.getType() == WeaponType.Axe || w1.getType() == WeaponType.Sword) {
				ssPps += w1.getEffectiveAPS(mod.getHastePercent()/100F)*(htMH.getContacts())*(talents.getTalentPoints("HnS")/100F);
				ssPps += mhSPS * (talents.getTalentPoints("HnS")/100F);
			}
			// Offhand Procs
			if (w2.getType() == WeaponType.Axe || w2.getType() == WeaponType.Sword) {
				ssPps += gear.getWeapon2().getEffectiveAPS(mod.getHastePercent()/100F)*(htOH.getContacts())*(talents.getTalentPoints("HnS")/100F);
				ssPps += ohSPS * (talents.getTalentPoints("HnS")/100F);
			}
			mhWPS += ssPps * htMH.getContacts();
			mhWCPS += ssPps * htMH.getCrit();
			ssDps  = ssDmg * ssPps;
			ssDps *= htMH.glance*0.75F + htMH.crit * mod.getPhysCritMult() + htMH.hit;
			dpsMH += ssDps;
		}
		if (gear.containsAny(50351,50706)) {
			float ppsMH, ppsOH, moteFactor, dmgMH, dmgOH;
			if (gear.containsAny(50706))
				moteFactor = 1/7F;
			else
				moteFactor = 1/8F;
			//System.out.println(">>MF: "+moteFactor);
			ppsMH = (gear.getWeapon1().getEffectiveAPS(mod.getHastePercent()/100F)*(htMH.getContacts()) + mhSPS)*0.5F*moteFactor;
			ppsOH = (gear.getWeapon2().getEffectiveAPS(mod.getHastePercent()/100F)*(htOH.getContacts()) + ohSPS)*0.5F*moteFactor;
			//System.out.println(">>Procs: MH: "+ppsMH+ " OH: "+ppsOH);
			
			mhSPS += ppsMH * mod.getHtMHS().getContacts();
			ohSPS += ppsOH * mod.getHtOHS().getContacts();
			mhSCPS += ppsMH * mod.getHtMHS().getCrit();
			ohSCPS += ppsOH * mod.getHtOHS().getCrit();
			
			dmgMH = gear.getWeapon1().getAverageDmg(totalATP)/2F;
			dmgMH = mod.getHtMHS().getHit()*dmgMH + mod.getHtMHS().getCrit()*dmgMH*mod.getPhysCritMult();
			dmgOH = gear.getWeapon2().getAverageDmg(totalATP)/2F;
			dmgOH = mod.getHtOHS().getHit()*dmgOH + mod.getHtOHS().getCrit()*dmgOH*mod.getPhysCritMult();
			//System.out.println(">>Dmg: MH: "+dmgMH+ " OH: "+dmgOH);
			//System.out.println(">>DPS added: "+(ppsMH*dmgMH+ppsOH*dmgOH));
			dpsMH += ppsMH * dmgMH;
			dpsOH += ppsMH * dmgOH;
		}
		// Armor Reduction
		dpsMH *= mod.getModArmorMH();
		dpsOH *= mod.getModArmorOH();
		float dps = dpsMH+dpsOH;
		
		// Global Mods
		dps *= getGlobalDmgMod(true, true);

		dps *= 1+bbIncrease;
		return dps;
	}
	
	protected float getGlobalDmgMod(boolean physical, boolean includeKS) {
		float mod = 1;
		mod *= (1+0.02F*talents.getTalentPoints("Murder"));
		mod *= (1+0.01F*talents.getTalentPoints("SftS"));
		if (glyphs.has(Glyph.HfB))
			mod *= (1+0.08F*talents.getTalentPoints("HfB"));
		else
			mod *= (1+0.05F*talents.getTalentPoints("HfB"));
		if (bc.hasBuff(Buff.damage))
			mod *= 1.03F;
		if (bc.hasOther(Other.tott)) {
			float duration = 6;
			if (bc.hasOther(Other.tottglyphed))
				duration += 4;
			float cooldown = 30;
			if (bc.hasOther(Other.totttalented))
				cooldown -= 10;
			mod *= 1 + 0.15F * duration/cooldown;
		}
		if (physical) {
			if (bc.hasDebuff(Debuff.physicalDamage))
				mod *= 1.04F;
			if (bc.hasOther(Other.hysteria))
				mod *= 1 + 1/30F;
		} else {
			if (bc.hasDebuff(Debuff.spellDamage))
				mod *= 1.13F;
		}
		if (includeKS) {
			if (talents.getTalentPoints("KS")>0) {
				if (glyphs.has(Glyph.KS))
					mod *= 1 + (0.2F * 2.5F/75F);
				else
					mod *= 1 + (0.2F * 2.5F/120F);
			}
		}
		return mod;
	}
	
	public float getEpAGI() {
		return epAGI;
	}
	
	public float getEpARP() {
		return epARP;
	}
	
	public float getEpCRI() {
		return epCRI;
	}
	
	public float getEpEXP() {
		return epEXP;
	}

	public float getEpHIT() {
		return epHIT;
	}

	public float getEpHST() {
		return epHST;
	}

	public float getMhSPS() {
		return mhSPS;
	}

	public Modifiers getModifiers() {
		return mod;
	}

	public float getOhSPS() {
		return ohSPS;
	}

	public float getTotalDPS() {
		return total;
	}
	
	protected int getMaxUses(int cooldown) {
		return 1 + ((int) fightDuration/cooldown);
	}
	
	public static Calculations createInstance() {
		Setup s = Launcher.getApp().getSetup();
		ModelType m = s.getTalents().getModel();
		switch (m) {
			default:
			case Combat:
				return new CalculationsCombat();
			case Mutilate:
				return new CalculationsMutilate();
			case SubHemo:
				return new CalculationsSubHemo();
			case SubBStab:
				return new CalculationsSubBStab();
		}
	}

}
