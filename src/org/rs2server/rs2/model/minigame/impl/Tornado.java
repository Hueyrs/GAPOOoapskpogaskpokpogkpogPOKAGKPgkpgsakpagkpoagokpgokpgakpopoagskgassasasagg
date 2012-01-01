package org.rs2server.rs2.model.minigame.impl;

import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;

/**
 * 
 * @author Jinrake
 * Tornado Target System
 */

public class Tornado implements Runnable {
	
	public static CopyOnWriteArrayList<Target> list = new CopyOnWriteArrayList<Target>();
	public static CopyOnWriteArrayList<Long> players = new CopyOnWriteArrayList<Long>();
	public static LinkedList<Event> eventQueue = new LinkedList<Event>();
	public boolean isListLocked = false;
	
	public Tornado() {
		new Thread(this).run();
	}
	
	public void process(Event e) {
		if (isListLocked) {
			eventQueue.add(e);
		} else {
			World.getWorld().submit(e);
		}
	}
	
	@Override
	public void run() {
		try {
			isListLocked = true;
			if (players.size() > 1) {
				for (Long p : players) {
					if (p == null) {
						players.remove(p);
					} else {
						Player t = World.getWorld().getPlayerNames().get(p);
						if (t == null) {
							players.remove(p);
						} else {
							int combat = t.getSkills().getCombatLevel();
							for (Long x : players) {
								if (x == null) {
									players.remove(x);
								} else {
									Player k = World.getWorld().getPlayerNames().get(x);
									if (k == null) {
										players.remove(x);
									}
									if (k.getSkills().getCombatLevel() - combat <= 5 && k.getSkills().getCombatLevel() - combat >= -5) {
										Target targ = new Target(t, k);
										list.add(targ);
										players.remove(x);
										players.remove(p);
									}
								}
							}
						}
					}
				}				
			}
			for (Target t : list) {
				if (t == null) {
					list.remove(t);
				} else {
					boolean[] online = {isPlayerOnline(t.p), isPlayerOnline(t.j)};
					if (!online[0] || !online[1]) {
						String msg = "You will be assigned a new target.";
						t.destroy();
						if (online[0]) {
							players.add(t.p);
							t.q.getActionSender().sendMessage(msg);
						}
						if (online[1]) {
							players.add(t.j);
							t.k.getActionSender().sendMessage(msg);
						}
					}
				}
			}
			isListLocked = false;
			/*
			 * List isn't locked, process events that we needed to do while it was
			 */
			for (Event e : eventQueue) {
				World.getWorld().submit(e);				
			}
			eventQueue.clear();
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public boolean isPlayerOnline(Long s) {
		return World.getWorld().getPlayerNames().containsKey(s);
	}
	
}

class Target {
	
	public Long p, j;
	public Player q, k;
	
	public Target(Player p, Player j) {
		this.p = p.getNameAsLong();
		this.j = j.getNameAsLong();
		p.getActionSender().sendHintArrow(j, 0, 2);
		j.getActionSender().sendHintArrow(p, 0, 2);
		String msg = "You have been assigned a target!";
		p.getActionSender().sendMessage(msg);
		j.getActionSender().sendMessage(msg);
		this.q = p;
		this.k = j;
	}
	
	public void destroy() {
		Tornado.list.remove(this);
	}
	
}
