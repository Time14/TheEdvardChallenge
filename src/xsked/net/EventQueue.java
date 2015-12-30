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
			
			if(packet instanceof PacketSummonSpell) {
				PacketSummonSpell p = (PacketSummonSpell) packet;
				level.summonSpell(p.X, p.Y, p.TYPE, new Vector2f(p.DX, p.DY));
			} else if(packet instanceof PacketSummonGhost) {
				PacketSummonGhost p = (PacketSummonGhost) packet;
				level.summonGhost(p.X, p.Y, p.TYPE);
			} else if(packet instanceof PacketPlayerDeath) {
				level.getPlayer().kill();
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