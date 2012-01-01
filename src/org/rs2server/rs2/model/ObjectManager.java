package org.rs2server.rs2.model;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.rs2server.cache.Cache;
import org.rs2server.cache.InvalidCacheException;
import org.rs2server.cache.index.impl.MapIndex;
import org.rs2server.cache.index.impl.StandardIndex;
import org.rs2server.cache.map.LandscapeListener;
import org.rs2server.cache.map.LandscapeParser;
import org.rs2server.cache.obj.ObjectDefinitionListener;
import org.rs2server.cache.obj.ObjectDefinitionParser;
import org.rs2server.util.XMLController;


/**
 * Manages all of the in-game objects.
 * @author Graham Edgecombe
 *
 */
public class ObjectManager implements LandscapeListener, ObjectDefinitionListener {
	
	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(ObjectManager.class.getName());
	
	/**
	 * The number of definitions loaded.
	 */
	private int definitionCount = 0;
	
	/**
	 * The count of objects loaded.
	 */
	private int objectCount = 0;
	
	/**
	 * Loads the objects in the map.
	 * @throws IOException if an I/O error occurs.
	 * @throws InvalidCacheException if the cache is invalid.
	 */
	public void load() throws IOException, InvalidCacheException {
		Cache cache = new Cache(new File("./data/cache/"));
		try {
			logger.info("Loading definitions...");
			StandardIndex[] defIndices = cache.getIndexTable().getObjectDefinitionIndices();
			new ObjectDefinitionParser(cache, defIndices, this).parse();
			logger.info("Loaded " + definitionCount + " object definitions.");
			logger.info("Loading map...");
			MapIndex[] mapIndices = cache.getIndexTable().getMapIndices();
			for(MapIndex index : mapIndices) {
				new LandscapeParser(cache, index.getIdentifier(), this).parse();
			}
			int customObjectCount = 0;
			List<GameObject> customObjects = XMLController.readXML(new File("data/customObjects.xml"));
			for(GameObject obj : customObjects) {
				World.getWorld().register(new GameObject(obj.getLocation(), obj.getId(), obj.getType(), obj.getDirection(), obj.isLoadedInLandscape()));
				customObjectCount++;
			}
			logger.info("Loaded " + objectCount + " objects and " + customObjectCount+" custom objects.");
		} finally {
			cache.close();
		}
	}

	@Override
	public void objectParsed(GameObject obj) {
		objectCount++;
		World.getWorld().register(obj);
	}

	@Override
	public void objectDefinitionParsed(GameObjectDefinition def) {
		definitionCount++;
		GameObjectDefinition.addDefinition(def);
	}

}
