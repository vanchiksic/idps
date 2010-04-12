package iDPS.model;

import iDPS.Attributes;

public class ProcStatic implements Proc {
		
	private Attributes attr;
	private boolean increaseAtp;
	private boolean increaseAgi;
	private boolean increaseArp;
	private boolean increaseCri;
	private boolean increaseHst;
	private float uptime;
	
	public ProcStatic(Attributes attr, float duration, float cooldown,
			float procChance, float hitsPerSec) {
		this.attr = attr;
		
		increaseAtp = (attr.getAtp()>0);
		increaseAgi = (attr.getAgi()>0);
		increaseArp = (attr.getArp()>0);
		increaseCri = (attr.getCri()>0);
		increaseHst = (attr.getHst()>0);
				
		if (cooldown > 0)
			uptime = duration/(cooldown+1/procChance/hitsPerSec);
		else
			uptime = (float) (1 - Math.pow(1-procChance,hitsPerSec*duration));
		
		if (uptime>1)
			uptime = 1;
	}
	
	public void calcUptime(float fightDuration) {
		
	}
	
	public Attributes getAttributes() {
		return attr;
	}
	
	public float getUptime() {
		return uptime;
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

}
