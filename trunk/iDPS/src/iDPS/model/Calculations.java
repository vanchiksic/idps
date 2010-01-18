package iDPS.model;

import iDPS.Attributes;
import iDPS.Player;
import iDPS.Race;
import iDPS.Talents;
import iDPS.Player.Profession;
import iDPS.gear.Gear;
import iDPS.gear.Weapon;
import iDPS.gear.Weapon.weaponType;

public abstract class Calculations {
	
	public enum ModelType { Combat, Mutilate };
	protected Attributes attrTotal;
	
	protected float avgCpFin, rupPerCycle;
	float ppsIP1, ppsIP2;
	float dpsWH, dpsDP, dpsIP, dpsRU, total;
	protected float envenomUptime;
	float epAGI, epHIT, epCRI, epHST, epARP, epEXP;
	protected Gear gear;
	protected float mhSPS, ohSPS;
	protected Modifiers mod;
	protected Talents talents;
	
	protected float bbIncrease;
	
	protected float totalATP;
	
	protected ModelType type;
	
	public Calculations() {
		talents = Player.getInstance().getTalents();
	}
	
	private void reset() {
		mhSPS = 0;
		ohSPS = 0;
	}
	
	protected float calcAtp(Attributes attr) {
		float atp, agi, str;
		atp = attr.getAtp();
		agi = attr.getAgi();
		str = attr.getStr();
		
		agi += 229;
		str += 229;
		atp += 687 + 260;
		if (Player.getInstance().hasProfession(Profession.Alchemy))
			atp += 80;
		agi *= 1.1F;
		str *= 1.1F;
		atp += agi + str;
		
		return atp;
	}
	
	protected void calcBerserking() {
		float attacksPerSec, uptime;
		
		if (gear.getWeapon1() != null && gear.isEnchanted(16) && gear.getEnchant(16).getId()==3789) {
			attacksPerSec = gear.getWeapon1().getEffectiveAPS(mod.getHastePercent()/100)*(mod.getHtMH().getContacts());
			attacksPerSec += mhSPS;
			uptime = gear.getWeapon1().getPPMUptime(1, 15, attacksPerSec);
			totalATP += uptime * 400;
		}
		
		if (gear.getWeapon2() != null && gear.isEnchanted(17) && gear.getEnchant(17).getId()==3789) {
			attacksPerSec = gear.getWeapon2().getEffectiveAPS(mod.getHastePercent()/100)*(mod.getHtOH().getContacts());
			attacksPerSec += ohSPS;
			uptime = gear.getWeapon2().getPPMUptime(1, 15, attacksPerSec);
			totalATP += uptime * 400;
		}
	}
	
	protected abstract void calcCycle();
	
	protected float calcDeadlyPoisonDPS() {
		//System.out.println("dp ap: "+totalATP);
		float dps = (296+0.108F*totalATP)/12*5 * (1+talents.getVilePoisons());
		// Global Mods
		dps *= talents.getMurder() * talents.getHfb() * 1.03F * 1.13F * 0.971875F;
		if (talents.getKs())
			dps *= 1 + (0.2F * 2.5F/75F);
		return dps;
	}
	
	protected abstract void calcDPS();
	
	protected float calcEnvenomDamage() {
		float dmg = (215*avgCpFin + 0.09F*avgCpFin * totalATP)*(1+talents.getVilePoisons()+talents.getFindWeakness());
		dmg += dmg*(mod.getPhysCritMult()-1)*mod.getHtFin().crit;
		// Global Mods
		dmg *= talents.getMurder() * talents.getHfb() * 1.03F * 1.13F;
		return dmg;
	}
	
	public void calcEP() {
		float dpsATP;
		Calculations c;
		try {
			c = getClass().newInstance();
			Attributes attr = new Attributes();
			//System.out.println("EP ATP");
			attr.setAtp(1);
			c.calculate(attr);
			dpsATP = c.total - total;
			attr.clear();
			//System.out.println("EP AGI");
			attr.setAgi(1);
			c.calculate(attr);
			epAGI = (c.total - total) / dpsATP;
			attr.clear();
			//System.out.println("EP HIT");
			attr.setHit(1);
			c.calculate(attr);
			epHIT = (c.total - total) / dpsATP;
			attr.clear();
			//System.out.println("EP CRI");
			attr.setCri(1);
			c.calculate(attr);
			epCRI = (c.total - total) / dpsATP;
			attr.clear();
			//System.out.println("EP HST");
			attr.setHst(1);
			c.calculate(attr);
			epHST = (c.total - total) / dpsATP;
			attr.clear();
			//System.out.println("EP EXP");
			attr.setExp(1);
			c.calculate(attr);
			epEXP = (c.total - total) / dpsATP;
			attr.clear();
			//System.out.println("EP ARP");
			attr.setArp(1);
			c.calculate(attr);
			epARP = (c.total - total) / dpsATP;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected float calcEviscerateDamage() {
		float baseDmg = 0;
		if (avgCpFin > 4)
			baseDmg = 1607*(5-avgCpFin)+1977*(avgCpFin-4);
		float dmg = (baseDmg + 0.07F*avgCpFin * totalATP)*(1+0.2F+0.15F);
		dmg += dmg*(mod.getPhysCritMult()-1)*mod.getHtFin().crit;
		// Global Mods
		dmg *= 1.04F * 1.03F;
		
		dmg *= mod.getModArmorMH();
		dmg *= 1+bbIncrease;
		return dmg;
	}
	
	protected float calcInstantPoisonDPS() {
		Weapon wip, wdp;
		if (gear.getWeapon2().getSpeed() >= gear.getWeapon1().getSpeed()) {
			wdp = gear.getWeapon1();
			wip = gear.getWeapon2();
		} else {
			wip = gear.getWeapon1();
			wdp = gear.getWeapon2();	
		}
		float ipProcChance, dpProcChance, hitChance, procsPerSec, damage;
		ipProcChance  = wip.getSpeed()/1.4F*(0.2F+0.02F*talents.getImprovedPoisons());
		ipProcChance *= 1+(envenomUptime*0.75F);
		//System.out.println("avg IP proc chance: "+ipProcChance);
		dpProcChance  = 0.3F + 0.04F*talents.getImprovedPoisons();
		dpProcChance += envenomUptime*0.15F;
		hitChance = Math.min(1, 0.83F+mod.getSpellHitPercent()/100);
		// Primary Procs
		ppsIP1  = wip.getEffectiveAPS(mod.getHastePercent()/100)*(mod.getHtMH().getContacts())*ipProcChance*hitChance;
		ppsIP1 += mhSPS*ipProcChance*hitChance;
		// Secondary Procs
		ppsIP2 = wdp.getEffectiveAPS(mod.getHastePercent()/100)*(mod.getHtOH().getContacts())*dpProcChance*hitChance;
		ppsIP2 += ohSPS*dpProcChance*hitChance*hitChance;
		procsPerSec = ppsIP1+ppsIP2;
		//System.out.println("IP pps: "+procsPerSec);
		// Damage
		damage = (350+0.09F*totalATP) * (1+talents.getVilePoisons()) * 0.971875F;
		//System.out.println("IP Proc dmg: "+damage);
		damage += damage*(mod.getPoisonCritMult()-1)*(mod.getSpellCritPercent()/100F);
		// Global Mods
		damage *= talents.getMurder() * talents.getHfb() * 1.03F * 1.13F;
		if (talents.getKs())
			damage *= 1 + (0.2F * 2.5F/75F);
		return procsPerSec * damage;
	}
	
	protected void calcMongoose() {
		float attacksPerSec, uptimeMH = 0, uptimeOH = 0;
		if (gear.isEnchanted(16) && gear.getEnchant(16).getId()==2673) {
			attacksPerSec = gear.getWeapon1().getEffectiveAPS(mod.getHastePercent()/100)*(mod.getHtMH().getContacts());
			attacksPerSec += mhSPS;
			uptimeMH = gear.getWeapon1().getPPMUptime(1, 15, attacksPerSec);
		}
		if (gear.isEnchanted(17) && gear.getEnchant(17).getId()==2673) {
			attacksPerSec = gear.getWeapon2().getEffectiveAPS(mod.getHastePercent()/100)*(mod.getHtOH().getContacts());
			attacksPerSec += ohSPS;
			uptimeOH = gear.getWeapon2().getPPMUptime(1, 15, attacksPerSec);
		}
		if ((uptimeMH+uptimeOH)>0) {
		// mongoose ~73 cri & 132 ap
			float uptimeD = uptimeMH * uptimeOH;
			float uptimeS = (1-(1-uptimeMH)*(1-uptimeOH))-uptimeD;
			mod.registerPhysCritProc(146, uptimeD);
			mod.registerPhysCritProc(73, uptimeS);
			mod.registerStaticHasteProc(0.02F, uptimeMH);
			mod.registerStaticHasteProc(0.02F, uptimeOH);
			totalATP += 264*uptimeD;
			totalATP += 132*uptimeS;
		}
	}
	
	protected void calcProcs() {		
		// Note: Apply Kings to these Buffs, but not the 10% AP buff
		
		// Mongoose
		calcMongoose();
		
		// Berserking
		calcBerserking();
		
		// Hyperspeed Accelerators
		if (gear.isEnchanted(8) && gear.getEnchant(8).getId()==3604)
			mod.registerHasteProc(340, 12F/60F);
		
		// Swordguard Embroidery
		if (gear.isEnchanted(3) && gear.getEnchant(3).getId()==3730)
			totalATP += 400F*15F/62F;
		
		// Orc Racial
		if (Player.getInstance().getRace().getType() == Race.Type.Orc)
			totalATP += 40.25F;
		
		// Grim Toll
		if (gear.contains(40256)>0)
			mod.registerArpProc(612, 10F/48F);
		
		// Tears of Bitter Anguish
		if (gear.contains(43573)>0)
			mod.registerHasteProc(410, 10F/56F);
		
		// Darkmoon Card: Greatness
		if (gear.contains(44253)>0) {
			// 330 Agi = 181 cri + 330 atp
			totalATP += 330F*15F/48F;
			mod.registerPhysCritProc(181, 15F/48F);
		}
		
		// Pyrite Infuser
		if (gear.contains(45286)>0) {
			totalATP += 1234F*10F/51F;
		}
		
		// Blood of the Old God
		if (gear.contains(45522)>0) {
			totalATP += 1284F*10F/51F;
		}
		
		// Comet's Trail
		if (gear.contains(45609)>0) {
			mod.registerHasteProc(726, 10F/48F);
		}
		
		// Mjolnir Runestone
		if (gear.contains(45931)>0) {
			mod.registerArpProc(665, 10F/48F);
		}
		
		// Dark Matter
		if (gear.contains(46038)>0) {
			mod.registerCritProc(612, 10F/48F);
		}
		
		// Banner of Victory
		if (gear.contains(47214)>0) {
			totalATP += 1008F*10F/49F;
		}
		
		// Death's Choice
		if (gear.containsAny(47303,47115)) {
			// 495 Agi = 273 cri + 459 atp
			totalATP += 495F*15F/48F;
			mod.registerPhysCritProc(273, 15F/48F);
		}
		
		// Death's Choice Heroic
		if (gear.containsAny(47464,47131)) {
			// 561 Agi = 309 cri + 561 atp
			totalATP += 561F*15F/48F;
			mod.registerPhysCritProc(309, 15F/48F);
		}
		
		// Mark of Supremacy
		if (gear.contains(47734)>0) {
			totalATP += 1024F*20F/120F;
		}
		
		// Vengeance of the Forsaken
		if (gear.containsAny(47881,47725)) {
			// proc average ~914AP for 20 sec every 120 sec
			totalATP += 914F*20F/120F;
		}
		
		// Vengeance of the Forsaken
		if (gear.containsAny(48020,47948)) {
			// proc average ~1063AP for 20 sec every 120 sec
			totalATP += 1063F*20F/120F;
		}
		
		// Shard of the Crystal Heart
		if (gear.contains(48722)>0) {
			// 512 hst for 20 sec every 120 sec
			mod.registerHasteProc(512, 20F/120F);
		}
		
		// Black Bruise
		if (gear.containsAny(50035,50692)) {
			float bbUptime = calcDWPPMUptime(0.6F, 10);
			//System.out.println("BB Uptime: "+bbUptime);
			if(gear.containsAny(50692))
				bbIncrease = bbUptime*0.10F;
			else
				bbIncrease =bbUptime*0.09F;
		} else
			bbIncrease = 0;
		
		// Needle-Encrusted Scorpion
		if (gear.contains(50198)>0) {
			mod.registerArpProc(678, 10F/57F);
		}
		
		// Whispering Fanged Skull
		if (gear.contains(50342)>0) {
			// proc 1100AP for 15 sec every 45 sec
			totalATP += 1100*15F/48F;
		}
		
		// Whispering Fanged Skull Heroic
		if (gear.contains(50343)>0) {
			// proc 1100AP for 15 sec every 45 sec
			totalATP += 1250*15F/48F;
		}
		
		// Herkuml War Token
		if (gear.contains(50355)>0) {
			totalATP += 340;
		}
		
		// Deathbringer's Will
		if (gear.contains(50362)>0) {
			// random +600 proc for 30 sec every ~105 sec
			// 1200 ap
			// 600 arp
			// 660 Agi = 364 cri + 660 atp
			totalATP += 1860*30F/415F;
			mod.registerHasteProc(600, 30F/318F);
			mod.registerPhysCritProc(364, 30F/318F);
		}
		
		// Deathbringer's Will Heroic
		if (gear.contains(50363)>0) {
			// random +700 proc for 30 sec every ~105 sec
			// 1400 atp
			// 700 arp
			// 770 Agi = 424 cri + 770 atp
			totalATP += 2170*30F/415F;
			mod.registerHasteProc(700, 30F/318F);
			mod.registerPhysCritProc(424, 30F/318F);
		}
		
		// Ashen Band of ...
		if (gear.containsAny(50401,50402)) {
			totalATP += 410F*10F/75F;
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
	
	protected float calcRuptureDPS() {
		if (rupPerCycle==0)
			return 0;
		float tick4cp, tick5cp, avgTick;
		tick4cp = 199 + 0.03428571F*totalATP;
		tick5cp = 217 + 0.0375F*totalATP;
		avgTick = tick4cp*(5-avgCpFin)+tick5cp*(avgCpFin-4);
		avgTick *= 1.42F* 1.04F * 1.18F * 1.03F * 1.3F;
		return avgTick/2;
	}
	
	public void calculate() {
		calculate(null, Player.getInstance().getEquipped());
	}
	
	public void calculate(Attributes a) {
		calculate(a, Player.getInstance().getEquipped());
	}
	
	public void calculate(Gear g) {
		calculate(null, g);
	}
	
	public void calculate(Attributes a, Gear g) {
		reset();
		
		attrTotal = new Attributes(a);
		gear = g;
		
		attrTotal.add(Player.getInstance().getAttr());
		attrTotal.add(gear.getAttributes());
		mod = new Modifiers(attrTotal, talents, gear);
		
		calcCycle();
		
		totalATP = 0F;
		totalATP += calcAtp(attrTotal);
		calcProcs();

		calcCycle();
		
		totalATP *= 1.1F * (1+0.02F*talents.getSavageCombat());
		//System.out.println("AP: "+totalATP);
		
		calcDPS();
	}
	
	protected float calcERegen() {
		float eRegen = 10;
		
		// Racial
		if (Player.getInstance().getRace().getType() == Race.Type.BloodElf)
			eRegen += 15F/120F;
		
		// Talents
		eRegen += talents.getVitality();
		//if (talents.getAr())
		//	eRegen += 150F/180F;
		if (talents.getCombatPotency()>0)
			eRegen += combatPotencyRegen();
		if (talents.getFocusedAttacks()>0)
			eRegen += calcWhiteCritsPerSec()*2F*talents.getFocusedAttacks();
		
		// ToTT every 32 sec
		float eLossTOT = 15F/32F;
		if (gear.getTier10()>=2)
			eLossTOT *= -1F;
		eRegen -= eLossTOT;
		
		// Heartpierce
		if (gear.containsAny(49982,50641))
			eRegen += calcHeartpierceRegen();
		
		//System.out.println("total regen: "+eRegen);
		return eRegen;
	}
	
	private float combatPotencyRegen() {
		float pps;
		pps = gear.getWeapon2().getEffectiveAPS(mod.getHastePercent()/100);
		// OH Hits from Tiny Abom
		if (gear.containsAny(50351,50706)) {
			float moteFactor;
			if (gear.containsAny(50706))
				moteFactor = 1/7F;
			else
				moteFactor = 1/8F;
			pps += (gear.getWeapon2().getEffectiveAPS(mod.getHastePercent()/100F)*(mod.getHtOH().getContacts()) + ohSPS)*0.5F*moteFactor;
		}
		pps *= (mod.getHtOH().getContacts());
		pps *= 0.2F;
		
		float regen = pps*((float) talents.getCombatPotency());
		//System.out.println("cpt regen: "+regen);
		return regen;
	}
	
	protected float calcWhiteCritsPerSec() {
		float critsPerSec = 0;
		if (gear.getWeapon1() != null)
			critsPerSec += gear.getWeapon1().getEffectiveAPS(mod.getHastePercent()/100)*mod.getHtMH().crit;
		if (gear.getWeapon2() != null)
			critsPerSec += gear.getWeapon2().getEffectiveAPS(mod.getHastePercent()/100)*mod.getHtOH().crit;
		return critsPerSec;
	}
	
	protected float calcWhiteDPS() {
		HitTable htMH = mod.getHtMH();
		HitTable htOH = mod.getHtMH();
		float dpsMH = 0, dpsOH = 0;
		// Mainhand
		if (gear.getWeapon1() != null) {
			dpsMH += gear.getWeapon1().getDps() + ((float)totalATP/14F);
			dpsMH *= htMH.glance*0.75F + htMH.crit * mod.getPhysCritMult() + htMH.hit;
			dpsMH *= mod.getHastePercent()/100F + 1;
		}
		// Offhand
		if (gear.getWeapon2() != null) {
			dpsOH += gear.getWeapon2().getDps() + ((float)totalATP/14F) * 0.75F;
			dpsOH *= htOH.glance*0.75F + htOH.crit * mod.getPhysCritMult() + htOH.hit;
			dpsOH *= mod.getHastePercent()/100F + 1;
		}
		// Sword Spec
		if (talents.getHnS()>0) {
			Weapon w1 = gear.getWeapon1(), w2 = gear.getWeapon2();
			float ssDmg, ssPps = 0, ssDps;
			ssDmg  = gear.getWeapon1().getAverageDmg(totalATP);
			// Mainhand Procs
			if (w1.getType() == weaponType.Axe || w1.getType() == weaponType.Sword) {
				ssPps += w1.getEffectiveAPS(mod.getHastePercent()/100F)*(htMH.getContacts())*(talents.getHnS()/100F);
				ssPps += mhSPS * (talents.getHnS()/100F);
			}
			// Offhand Procs
			if (w2.getType() == weaponType.Axe || w2.getType() == weaponType.Sword) {
				ssPps += gear.getWeapon2().getEffectiveAPS(mod.getHastePercent()/100F)*(htOH.getContacts())*(talents.getHnS()/100F);
				ssPps += ohSPS * (talents.getHnS()/100F);
			}
			mhSPS += ssPps * htMH.getContacts();
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
			mhSPS += ppsMH;
			ohSPS += ppsOH;
			dmgMH = gear.getWeapon1().getAverageDmg(totalATP)/2F;
			dmgMH = mod.getHtMHS().getHit()*dmgMH + mod.getHtMHS().getCrit()*dmgMH*mod.getPhysCritMult();
			dmgOH = gear.getWeapon2().getAverageDmg(totalATP)/2F;
			dmgOH = mod.getHtOHS().getHit()*dmgOH + mod.getHtOHS().getCrit()*dmgOH*mod.getPhysCritMult();
			//System.out.println(">>Dmg: MH: "+dmgMH+ " OH: "+dmgOH);
			//System.out.println(">>DPS added: "+(ppsMH*dmgMH+ppsOH*dmgOH));
			dpsMH += ppsMH * dmgMH;
			dpsOH += ppsMH * dmgOH;
		}
		// Global Mods
		dpsMH *= 1.04F * talents.getHfb() * 1.03F * 1.04F;
		dpsOH *= 1.04F * talents.getHfb() * 1.03F * 1.04F;
		if (talents.getKs()) {
			dpsMH *= 1 + (0.2F * 2.5F/75F);
			dpsOH *= 1 + (0.2F * 2.5F/75F);
		}
		// Armor Reduction
		dpsMH *= mod.getModArmorMH();
		dpsOH *= mod.getModArmorOH();
		float dps = dpsMH+dpsOH;
		dps *= 1+bbIncrease;
		return dps;
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

	public static Calculations createInstance() {
		ModelType m = Player.getInstance().getTalents().getModel();
		switch (m) {
			default:
			case Combat:
				return new CalculationsCombat();
			case Mutilate:
				return new CalculationsMutilate();
		}
	}

}
