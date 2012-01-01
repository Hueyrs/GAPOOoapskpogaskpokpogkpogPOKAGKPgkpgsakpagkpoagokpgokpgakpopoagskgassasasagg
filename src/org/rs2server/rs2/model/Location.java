package org.rs2server.rs2.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single location in the game world.
 * @author Graham Edgecombe
 *
 */
public class Location {
	
	   /**
	    * Gets the closest spot from a list of locations.
	    * @param steps The list of steps.
	    * @param location The location we want to be close to.
	    * @return The closest location.
	    */
		public static Location getClosestSpot(Location target, Location[] steps) {
			Location closestStep = null;
			for (Location p : steps) {
				if (closestStep == null || (getDistance(closestStep, target) > getDistance(p, target))) {
					// if (RS2RegionLoader.positionIsWalkalble(e, p.getX(),
					// p.getY())) {
					// System.out.println("Setting walkable pos..");
					closestStep = p;
					// }
				}
			}
			return closestStep;
		}

		/**
		 * Gets a list of all the valid spots around another location, within a specific "size/range".
		 * @param size The size/range.
		 * @param location The location we want to get locations within range from.
		 */
		public static Location[] getValidSpots(int size, Location location) {
			Location[] list = new Location[size * 4];
			int index = 0;
			for(int i = 0; i < size; i++) {
				list[index++] = (new Location(location.getX() - 1, location.getY() + i, location.getZ()));
				list[index++] = (new Location(location.getX() + i, location.getY() - 1, location.getZ()));
				list[index++] = (new Location(location.getX() + i, location.getY() + size, location.getZ()));
				list[index++] = (new Location(location.getX() + size, location.getY() + i, location.getZ()));
			}
			return list;
		}

		public static double getDistance(Location p, Location p2) {
			 return Math.sqrt((p2.getX()-p.getX())*(p2.getX()-p.getX()) + (p2.getY()-p.getY())*(p2.getY()-p.getY()));
		}
	
	/**
	 * Checks if we're in a specific arena based on location objects.
	 * @param minLocation The min location to check.
	 * @param maxLocation The max location to check.
	 * @return True if we're in the area, false it not.
	 */
	public boolean isInArea(Location minLocation, Location maxLocation) {
		return isInArea(x, y, z, minLocation.getX(), minLocation.getY(), minLocation.getZ(), maxLocation.getX(), maxLocation.getY(), maxLocation.getZ());
	}

	/**
	 * Checks if we're in a specific arena based on simple coordinates.
	 * @param minX The minimum x coordinate.
	 * @param minY The minimum y coordinate.
	 * @param minHeight the minimum height.
	 * @param maxX The maximum x coordinate.
	 * @param maxY The maximum y coordinate.
	 * @param maxHeight The maximum height.
	 * @return True if we're in the area, false it not.
	 */
	public static boolean isInArea(int x, int y, int z, int minX, int minY, int minHeight, int maxX, int maxY, int maxHeight) {
		if(z != minHeight || z != maxHeight) {
			return false;
		}
		return (x >= minX && y >= minY) && (x <= maxX && y <= maxY);
	}
	
 	/**
	 * Calculate the distance between a player and a point.
	 * @param p A point.
	 * @return The square distance.
	 */
	public double getDistance(Location other) {
		int xdiff = this.getX() - other.getX();
		int ydiff = this.getY() - other.getY();
		return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
	}
	
	/**
	 * The x coordinate.
	 */
	private final int x;
	
	/**
	 * The y coordinate.
	 */
	private final int y;
	
	/**
	 * The z coordinate.
	 */
	private int z;
	
	/**
	 * Creates a location.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 * @return The location.
	 */
	public static Location create(int x, int y, int z) {
		return new Location(x, y, z);
	}
	
	/**
	 * Creates a height 0 location.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return The location.
	 */
	public static Location create(int x, int y) {
		return new Location(x, y, 0);
	}
	
	/**
	 * Creates a location.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 */
	private Location(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Gets the absolute x coordinate.
	 * @return The absolute x coordinate.
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Gets the absolute y coordinate.
	 * @return The absolute y coordinate.
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Gets the z coordinate, or height.
	 * @return The z coordinate.
	 */
	public int getZ() {
		return z;
	}
	
	/**
	 * Gets the local x coordinate relative to this region.
	 * @return The local x coordinate relative to this region.
	 */
	public int getLocalX() {
		return getLocalX(this);
	}
	
	/**
	 * Gets the local y coordinate relative to this region.
	 * @return The local y coordinate relative to this region.
	 */
	public int getLocalY() {
		return getLocalY(this);
	}
	
	/**
	 * Gets the local x coordinate relative to a specific region.
	 * @param l The region the coordinate will be relative to.
	 * @return The local x coordinate.
	 */
	public int getLocalX(Location l) {
		return x - 8 * l.getRegionX();
	}
	
	/**
	 * Gets the local y coordinate relative to a specific region.
	 * @param l The region the coordinate will be relative to.
	 * @return The local y coordinate.
	 */
	public int getLocalY(Location l) {
		return y - 8 * l.getRegionY();
	}
	
	/**
	 * Gets the region x coordinate.
	 * @return The region x coordinate.
	 */
	public int getRegionX() {
		return (x >> 3) - 6;
	}
	
	/**
	 * Gets the region y coordinate.
	 * @return The region y coordinate.
	 */
	public int getRegionY() {
		return (y >> 3) - 6;
	}
	
	/**
	 * Checks if this location is within range of another.
	 * @param other The other location.
	 * @return <code>true</code> if the location is in range,
	 * <code>false</code> if not.
	 */
	public boolean isWithinDistance(Location other) {
		if(z != other.z) {
			return false;
		}
		int deltaX = other.x - x, deltaY = other.y - y;
		return deltaX <= 14 && deltaX >= -15 && deltaY <= 14 && deltaY >= -15;
	}
	
	/**
	 * Checks if this location is within interaction range of another.
	 * @param other The other location.
	 * @return <code>true</code> if the location is in range,
	 * <code>false</code> if not.
	 */
	public boolean isWithinInteractionDistance(Location other) {
		if(z != other.z) {
			return false;
		}
		int deltaX = other.x - x, deltaY = other.y - y;
		return deltaX <= 2 && deltaX >= -3 && deltaY <= 2 && deltaY >= -3;
	}
	
	/**
	 * Checks if this location is next to another.
	 * @param other The other location.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isNextTo(Location other) {
		if(z != other.z) {
			return false;
		}
		/*int deltaX = Math.abs(other.x - x), deltaY = Math.abs(other.y - y);
		return deltaX <= 1 && deltaY <= 1;*/
		return (getX() == other.getX() && getY() != other.getY()
				|| getX() != other.getX() && getY() == other.getY()
				|| getX() == other.getX() && getY() == other.getY());
	}

	/**
	 * Checks if a coordinate is within range of another.
	 * @return <code>true</code> if the location is in range,
	 * <code>false</code> if not.
	 */
	public boolean isWithinDistance(Entity attacker, Entity victim, int distance) {
		if(attacker.getWidth() == 1 && attacker.getHeight() == 1 &&
		   victim.getWidth() == 1 && victim.getHeight() == 1 && distance == 1) {
			return distanceToPoint(victim.getLocation()) <= distance;			
		}
		List<Location> myTiles = entityTiles(attacker);
		List<Location> theirTiles = entityTiles(victim);
		for(Location myTile : myTiles) {
			for(Location theirTile : theirTiles) {
				if(myTile.isWithinDistance(theirTile, distance)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if a coordinate is within range of another.
	 * @return <code>true</code> if the location is in range,
	 * <code>false</code> if not.
	 */
	public int distanceToEntity(Entity attacker, Entity victim) {
		if(attacker.getWidth() == 1 && attacker.getHeight() == 1 &&
		   victim.getWidth() == 1 && victim.getHeight() == 1) {
			return distanceToPoint(victim.getLocation());			
		}
		int lowestDistance = 100;
		List<Location> myTiles = entityTiles(attacker);
		List<Location> theirTiles = entityTiles(victim);
		for(Location myTile : myTiles) {
			for(Location theirTile : theirTiles) {
				int dist = myTile.distanceToPoint(theirTile);
				if(dist <= lowestDistance) {
					lowestDistance = dist;
				}
			}
		}
		return lowestDistance;
	}
	
	/**
	 * The list of tiles this entity occupies.
	 * @param entity The entity.
	 * @return The list of tiles this entity occupies.
	 */
	public List<Location> entityTiles(Entity entity) {
		List<Location> myTiles = new ArrayList<Location>();
		myTiles.add(entity.getLocation());
		if(entity.getWidth() > 1) {
			for(int i = 1; i < entity.getWidth(); i++) {
				myTiles.add(Location.create(entity.getLocation().getX() + i,
						entity.getLocation().getY(), entity.getLocation().getZ()));
			}
		}
		if(entity.getHeight() > 1) {
			for(int i = 1; i < entity.getHeight(); i++) {
				myTiles.add(Location.create(entity.getLocation().getX(),
						entity.getLocation().getY() + i, entity.getLocation().getZ()));
			}
		}
		int myHighestVal = (entity.getWidth() > entity.getHeight() ? entity.getWidth() : entity.getHeight());
		if(myHighestVal > 1) {
			for(int i = 1; i < myHighestVal; i++) {
				myTiles.add(Location.create(entity.getLocation().getX() + i,
						entity.getLocation().getY() + i, entity.getLocation().getZ()));
			}			
		}
		return myTiles;
	}

	/**
	 * Checks if a coordinate is within range of another.
	 * @return <code>true</code> if the location is in range,
	 * <code>false</code> if not.
	 */
	public boolean isWithinDistance(int width, int height, Location otherLocation, int otherWidth, int otherHeight, int distance) {
		Location myClosestTile = this.closestTileOf(otherLocation, width, height);
		Location theirClosestTile = otherLocation.closestTileOf(this, otherWidth, otherHeight);
		
		return myClosestTile.distanceToPoint(theirClosestTile) <= distance;
	}
	
	/**
	 * Checks if a coordinate is within range of another.
	 * @return <code>true</code> if the location is in range,
	 * <code>false</code> if not.
	 */
	public boolean isWithinDistance(Location location, int distance) {
		int objectX = location.getX();
		int objectY = location.getY();
		for (int i = 0; i <= distance; i++) {
		  for (int j = 0; j <= distance; j++) {
			if ((objectX + i) == x && ((objectY + j) == y || (objectY - j) == y || objectY == y)) {
				return true;
			} else if ((objectX - i) == x && ((objectY + j) == y || (objectY - j) == y || objectY == y)) {
				return true;
			} else if (objectX == x && ((objectY + j) == y || (objectY - j) == y || objectY == y)) {
				return true;
			}
		  }
		}
		return false;
	}
	
	/**
	 * Gets the closest tile of this location from a specific point.
	 */
	public Location closestTileOf(Location from, int width, int height) {
		if(width < 2 && height < 2) {
			return this;
		}
		Location location = null;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				Location loc = Location.create(this.x + x, this.y + y, this.z);
				if(location == null || loc.distanceToPoint(from) < location.distanceToPoint(from)) {
					location = loc;
				}
			}
		}
		return location;
	}
	
	/**
	 * Gets the tile that is opposite to where the player is standing.
	 */
	public Location oppositeTileOfEntity(Entity entity) {
		if(entity.getWidth() < 2 && entity.getHeight() < 2) {
			return entity.getLocation();
		}
		int newX = x;
		if(x < entity.getLocation().getX()) {
			newX += 1 + entity.getWidth();
		} else if(x > entity.getLocation().getX()) {
			newX -= 1 + entity.getWidth();
		}
		int newY = y;
		if(y < entity.getLocation().getY()) {
			newY += 1 + entity.getHeight();
		} else if(y > entity.getLocation().getY()) {
			newY -= 1 + entity.getHeight();
		}
		return Location.create(newX, newY, z);
	}
	
	/**
	 * Gets the distance to a location.
	 * @param other The location.
	 * @return The distance from the other location.
	 */
   	public int distanceToPoint(Location other) {
   		int absX = x;
   		int absY = y;
   		int pointX = other.getX();
   		int pointY = other.getY();
		return (int) Math.sqrt(Math.pow(absX - pointX, 2) + Math.pow(absY - pointY, 2));
  	}
	
	
	@Override
	public int hashCode() {
		return z << 30 | x << 15 | y;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Location)) {
			return false;
		}
		Location loc = (Location) other;
		return loc.x == x && loc.y == y && loc.z == z;
	}
	
	@Override
	public String toString() {
		return "["+x+","+y+","+z+"]";
	}

	/**
	 * Creates a new location based on this location.
	 * @param diffX X difference.
	 * @param diffY Y difference.
	 * @param diffZ Z difference.
	 * @return The new location.
	 */
	public Location transform(int diffX, int diffY, int diffZ) {
		return Location.create(x + diffX, y + diffY, z + diffZ);
	}
	
	public boolean inPestControlBoat() {
		return (x >= 2660 && x <= 2663 && y >= 2638 && y <= 2643);
	}

	public boolean inPestControlGame() {
		return(x >= 2624 && x <= 2690 && y >= 2550 && y <= 2625);
	}
	
	/**
	 * Checks if a specific set of coordinates is in a multi area.
	 * @return <code>true</code> if the coordinate set is in a multi area, <code>false</code> if not.
	 */
	public boolean isInMulti() {
		if ((x >= 3029 && x <= 3374 && y >= 3759 && y <= 3903)
				|| (x >= 2250 && x <= 2280 && y >= 4670 && y <= 4720)
				|| (x >= 3198 && x <= 3380 && y >= 3904 && y <= 3970)
				|| (x >= 3191 && x <= 3326 && y >= 3510 && y <= 3759)
				|| (x >= 2987 && x <= 3006 && y >= 3912 && y <= 3937)
				|| (x >= 2245 && x <= 2295 && y >= 4675 && y <= 4720)
				|| (x >= 2450 && x <= 3522 && y >= 9450 && y <= 9550)
				|| (x >= 3006 && x <= 3071 && y >= 3602 && y <= 3710)
				|| (x >= 3134 && x <= 3192 && y >= 3519 && y <= 3646)) {
			return true;
		} else if(inPestControlGame()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Cba remaking it. ;D
	 */
	public static boolean isInMulti(int x, int y, int z) {
		return create(x, y, z).isInMulti();
	}

	public void setZ(int height) {
		// TODO Auto-generated method stub
		this.z = height;
	}

}
