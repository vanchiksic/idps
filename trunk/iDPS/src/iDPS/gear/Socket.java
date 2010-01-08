package iDPS.gear;

public class Socket {
	
	public enum SocketType { Red, Blue, Yellow, Meta, Prismatic };
	
	private SocketType type;
	private Item item;
	private int index;
	
	public Socket(Item item, int index, SocketType type) {
		this.item = item;
		this.type = type;
		this.index = index;
	}
	
	public SocketType getType() {
		return type;
	}
	
	public Item getItem() {
		return item;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String toString() {
		return type + "Socket";
	}

}
