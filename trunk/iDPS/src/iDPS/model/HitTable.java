package iDPS.model;

import iDPS.Talents;

public class HitTable {
	
	enum Type { White, Special }
	private int critExceeded;
	private int hitExceeded;
	float miss, dodge, glance, crit, crit0, hit, hit0, critCap;
	
	public HitTable(Type t, Talents talents, float hit, float cri, float exp) {
		switch (t) {
			case White:
				this.miss = Math.max((0.27F-hit),0);
				if (hit>0.27F)
					setHitExceeded(2);
				this.dodge = Math.max((0.065F-exp),0);
				this.glance = 0.24F;
				this.critCap = 1-this.miss-this.dodge-this.glance-0.048F;
				if ((cri-0.048F)>critCap)
					setCritExceeded(2);
				this.crit = Math.min(critCap,(cri-0.048F));
				this.crit0 = this.crit;
				this.hit = 1-this.miss-this.dodge-this.glance-this.crit;
				this.hit0 = this.hit;
				break;
			case Special:
				this.miss = Math.max((0.08F-hit),0);
				this.dodge = Math.max((0.065F-exp),0);
				if (talents.getSupriseattacks())
					this.dodge = 0;
				this.glance = 0;
				this.crit = Math.min((1-this.miss-this.dodge),(cri-0.048F));
				this.crit0 = this.crit;
				this.hit = 1-this.miss-this.dodge-this.crit;
				this.hit0 = this.hit;
				this.critCap = 1-this.miss-this.dodge;
				break;
		}
	}
	
	public void registerCritProc(float criR, float upT) {
		//System.out.println("Crit: "+crit0+" Cap: "+critCap);
		float critNew = crit0+criR/4591F;
		if (critNew > critCap)
			setCritExceeded(1);
		float critInc = Math.min(critNew, critCap)-crit0;
		//System.out.format("CritProc Inc: %f %n",critInc);
		crit += critInc*upT;
		hit -= critInc*upT;
	}
	
	public float getContacts() {
		return (1-miss-dodge);
	}

	public float getMiss() {
		return miss;
	}

	public float getDodge() {
		return dodge;
	}

	public float getGlance() {
		return glance;
	}

	public float getCrit() {
		return crit;
	}

	public float getCrit0() {
		return crit0;
	}

	public float getHit() {
		return hit;
	}

	public float getHit0() {
		return hit0;
	}

	public float getCritCap() {
		return critCap;
	}
	
	public int getCritExceeded() {
		return critExceeded;
	}

	private void setCritExceeded(int critExceeded) {
		if (this.critExceeded<critExceeded)
			this.critExceeded = critExceeded;
	}

	public int getHitExceeded() {
		return hitExceeded;
	}

	public void setHitExceeded(int hitExceeded) {
		if (this.hitExceeded<hitExceeded)
			this.hitExceeded = hitExceeded;
	}

}
