package org.rs2server.util;

import java.util.Random;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;

public class Misc {

	public static boolean intToBoolean(int i) {
		return i > 0 ? true : false;
	}
	public static int booleanToInt(boolean b) {
		return b ? 1 : 0;
	}

	/**
	 * Returns a random integer with min as the inclusive
	 * lower bound and max as the exclusive upper bound.
	 *
	 * @param min The inclusive lower bound.
	 * @param max The exclusive upper bound.
	 * @return Random integer min <= n < max.
	 */
	public static int random(int min, int max) {
		Random random = new Random();
		int n = Math.abs(max - min);
		return Math.min(min, max) + (n == 0 ? 0 : random.nextInt(n));
	}

	/**
	 * @return Returns the distance between two positions.
	 */
	public static int getDistance(int coordX1, int coordY1, int coordX2,
			int coordY2) {
		int deltaX = coordX2 - coordX1;
		int deltaY = coordY2 - coordY1;
		return ((int) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)));
	}
	public static int getDistance(Location loc1, Location loc2) {
		int deltaX = loc2.getX() - loc1.getX();
		int deltaY = loc2.getY() - loc1.getY();
		return ((int) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)));
	}

	public static boolean isClose(Mob mob1, Mob mob2) {
		return getDistance(mob1.getLocation().getX(), mob1.getLocation().getY(), mob2.getLocation().getX(), mob2.getLocation().getY()) <= 1;
	}

	public static double round1000(double x) {
		return Math.round(1000 * x) / (double) 1000;
	}

	public static double round100(double x) {
		return Math.round(100 * x) / (double) 100;
	}

	public static double round10(double x) {
		return Math.round(10 * x) / (double) 10;
	}

	public static int round(double x) {
		return (int) Math.round(x);
	}

	public static int random(int range) {
		return (int) (Math.random() * (range + 1));
	}

	public static int randomSlayer(int range) {
		return (int) (Math.random() * (range + 1)) == 0 ? (int) (Math.random() * (range + 1)) + 1 : (int) (Math.random() * (range + 1) + 1);
	}


}
