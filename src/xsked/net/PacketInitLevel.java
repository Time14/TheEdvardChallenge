package xsked.net;

import sk.net.SKPacket;

public class PacketInitLevel extends SKPacket {
	
	public final int PLAYER_MODE;
	public final int FLOOR;
	public final int SEED;
	
	public PacketInitLevel(int playerMode, int floor, int seed) {
		this.PLAYER_MODE = playerMode;
		this.FLOOR = floor;
		this.SEED = seed;
	}
	
	@Override
	public String getName() {
		return "Init Level Packet";
	}
}