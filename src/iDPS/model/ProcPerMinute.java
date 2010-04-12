package iDPS.model;

import iDPS.Attributes;
import iDPS.gear.Weapon;

public class ProcPerMinute implements Proc {
	
	private Attributes attr;
	private boolean increaseAtp;
	private boolean increaseAgi;
	private boolean increaseArp;
	private boolean increaseCri;
	private boolean increaseHst;
	private float uptime;
	
	public ProcPerMinute(Attributes attr, float duration, float cooldown,
			float ppm, Weapon mh, float hitsMH, Weapon oh, float hitsOH) {
		this.attr = attr;
		
		increaseAtp = (attr.getAtp()>0);
		increaseAgi = (attr.getAgi()>0);
		increaseArp = (attr.getArp()>0);
		increaseCri = (attr.getCri()>0);
		increaseHst = (attr.getHst()>0);
		
		if (cooldown > 0) {
			float pps = mh.getProcChancePPM(ppm)*hitsMH + oh.getProcChancePPM(ppm)*hitsOH;
			uptime = duration/(cooldown+1/pps);
		} else {
			float uptimeMH = (float) (1-Math.pow((1-mh.getProcChancePPM(ppm)), (duration*hitsMH)));
			float uptimeOH = (float) (1-Math.pow((1-oh.getProcChancePPM(ppm)), (duration*hitsOH)));
			uptime = 1-((1-uptimeMH)*(1-uptimeOH));
		}
	}
	
	public Attributes getAttr() {
		return attr;
	}

	public boolean isIncreaseAtp() {
		return increaseAtp;
	}

	public boolean isIncreaseAgi() {
		return increaseAgi;
	}

	public boolean isIncreaseArp() {
		return increaseArp;
	}

	public boolean isIncreaseCri() {
		return increaseCri;
	}

	public boolean isIncreaseHst() {
		return increaseHst;
	}

	public Attributes getAttributes() {
		return attr;
	}

	public float getUptime() {
		return uptime;
	}

}
