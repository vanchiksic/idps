package iDPS.gear;


import org.jdom.Element;

public class Weapon extends Armor {
	
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
	
	public float getPPMUptime(float ppm, float procLength, float attacksPerSec) {
		float procChance = Math.min((speed/60)*ppm,1);
		return getUptime(procChance, procLength, attacksPerSec);
	}
	
	public float getUptime(float pProc, float procLength, float attacksPerSec) {
		return (float) (1-Math.pow((1-pProc), (procLength*attacksPerSec)));
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
	
	public String getToolTip() {
		String s = "<html><body style=\"padding:4px;background-color:#070c20;color:white;font-family:Verdana,sans-serif;font-size:8px;\"><p style=\"font-weight:bold;font-size:8px;margin:0 0 6px 0;\">"+getName()+"</p>";
		s += "<table border=0 cellspacing=0 cellpadding=0 style=\"width:100%;margin-bottom:3px;\"><tr><td>"+type.name()+"</td><td style=\"text-align:center;\">"+String.format("(%.2f dps)", dps)+"</td><td></td><td style=\"text-align:right;\">"+String.format("Speed: %1.2f", speed)+"</td></tr></table>";
		s += super.getAttr().getToolTip();
		if (hasSockets()) {
			s += "<p style=\"margin:6px 0 0 0;\">Socket Bonus:</p>";
			s += super.getSocketBonus().getMinToolTip();
		}
		s += "</body></html>";
		return s;
	}

}
