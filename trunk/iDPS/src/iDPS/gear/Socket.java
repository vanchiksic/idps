package iDPS.gear;

public class Socket {
	
	public enum SocketType { Red, Blue, Yellow, Meta, Prismatic };
	
	private SocketType type;
	private Armor item;
	private int index;
	
	public Socket(Armor item, int index, SocketType type) {
		this.item = item;
		this.type = type;
		this.index = index;
	}
	
	public SocketType getType() {
		return type;
	}
	
	public Armor getItem() {
		return item;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String toString() {
		return type + "Socket";
	}

}
