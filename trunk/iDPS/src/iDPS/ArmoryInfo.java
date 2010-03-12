package iDPS;

public class ArmoryInfo {
	private String region;
	private String realm;
	private String character;

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public String getCharacter() {
		return character;
	}

	public void setCharacter(String character) {
		this.character = character;
	}

	public ArmoryInfo(String region, String realm, String character) {
		super();
		this.region = region;
		this.realm = realm;
		this.character = character;
	}
}
