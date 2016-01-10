package xsked.net;

import java.util.PriorityQueue;

import sk.net.SKPacket;
import time.api.debug.Debug;
import time.api.gamestate.GameStateManager;
import time.api.math.Vector2f;
import xsked.level.Level;
import xsked.level.Player;

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
				if(level.getPlayer().isDead())
					return;
				PacketSummonSpell p = (PacketSummonSpell) packet;
				level.summonSpell(p.X, p.Y, p.TYPE, new Vector2f(p.DX, p.DY));
			} else if(packet instanceof PacketSummonGhost) {
				if(level.getPlayer().isDead())
					return;
				PacketSummonGhost p = (PacketSummonGhost) packet;
				level.summonGhost(p.X, p.Y, p.TYPE);
			} else if(packet instanceof PacketPlayerDeath) {
				if(level.getPlayer().MODE == Player.MODE_WIZARD)
					level.getPlayer().kill();
			} else if(packet instanceof PacketInitLevel) {
				PacketInitLevel p = (PacketInitLevel) packet;
				level.nextFloor(p.PLAYER_MODE, p.FLOOR, p.SEED);
				
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