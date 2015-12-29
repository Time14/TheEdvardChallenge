package xsked.net;

import java.util.PriorityQueue;

import sk.net.SKPacket;
import time.api.debug.Debug;
import time.api.gamestate.GameStateManager;
import time.api.math.Vector2f;
import xsked.level.Level;

public class EventQueue {
	
	private static final PriorityQueue<SKPacket> queue = new PriorityQueue<>();
	
	private static Level level;
	
	public static final synchronized void process() {
		while(!queue.isEmpty()) {
			SKPacket packet = queue.poll();
			
			if(packet instanceof PacketState) {
				GameStateManager.enterState(((PacketState) packet).STATE);
			}
			
			if(packet instanceof PacketPosition) {
				switch(((PacketPosition) packet).TYPE) {
				case "player":
					level.getPlayer().setPosition(((PacketPosition) packet).X, ((PacketPosition) packet).Y);
					level.getPlayer().getBody().setVel(new Vector2f(((PacketPosition) packet).VX, ((PacketPosition) packet).VY));
					break;
				}
			}
		}
	}
	
	public static final void queue(SKPacket packet) {
		queue.add(packet);
	}
	
	public static final void setLevel(Level level) {
		EventQueue.level = level;
	}
	
	public static final Level getLevel() {
		return level;
	}
}