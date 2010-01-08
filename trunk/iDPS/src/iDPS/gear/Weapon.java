package iDPS.gear;


import org.jdom.Element;

public class Weapon extends Item {
	
	public enum weaponType { Axe, Dagger, Sword, Fist, Mace, Thrown, Gun, Bow, Crossbow }
	
	private weaponType type;
	private float speed;
	private float dps;
	
	public Weapon(Element element) {
		super(element);
		if (element.getChild("type") != null)
			type = weaponType.valueOf(element.getChildText("type"));
		speed = 1.7F;
		dps = 0;
		Element e;
		e = element.getChild("speed");
		if (e != null)
			speed = Float.parseFloat(e.getText());
		e = element.getChild("dps");
		if (e != null)
			dps = Float.parseFloat(e.getText());
	}
	
	public Weapon(weaponType type) {
		this();
		this.type = type;
		speed = 1.7F;
		dps = 0;
	}
	
	public Weapon(int id) {
		super(id);
	}
	
	public Weapon() {
		super();
	}

	public weaponType getType() {
		return type;
	}

	public float getSpeed() {
		return speed;
	}
	
	public float getEffectiveSpeed(float haste) {
		return speed / (1+haste);
	}
	
	public float getEffectiveAPS(float haste) {
		return 1/speed*(1+haste);
	}
	
	public float getZerkUptime(float attacksPerSec) {
		return getPPMUptime(1F, 15F, attacksPerSec);
	}
	
	public float getPPMUptime(float ppm, float procLength, float attacksPerSec) {
		float procChance = Math.min((speed/60)*ppm,1);
		return (float) (1-Math.pow((1-procChance), (procLength*attacksPerSec)));
	}

	public float getDps() {
		return dps;
	}

	public float getAverageDmg() {
		return getAverageDmg(0);
	}
	
	public float getAverageDmg(float attackPower) {
		return speed * (dps+attackPower/14F);
	}
	
	public float getInstantDmg(float attackPower) {
		float mod;
		switch (type) {
			case Dagger:
				mod = 1.7F;
				break;
			default:
				mod = 2.4F;
				break;
		}
		float dmg = speed * dps + (attackPower/14F)*mod;
		return dmg;
	}

	public void setType(weaponType type) {
		this.type = type;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void setDps(float dps) {
		this.dps = dps;
	}

}
