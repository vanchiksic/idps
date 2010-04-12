package iDPS.model;

import iDPS.Attributes;

public interface Proc {
	
	public float getUptime();
	
	public Attributes getAttributes();
	
	public boolean isIncreaseAtp();

	public boolean isIncreaseAgi();

	public boolean isIncreaseArp();

	public boolean isIncreaseCri();

	public boolean isIncreaseHst();
	
}
