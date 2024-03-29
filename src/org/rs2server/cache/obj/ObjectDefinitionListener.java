package org.rs2server.cache.obj;

import org.rs2server.rs2.model.GameObjectDefinition;

/**
 * An object definition listener, which is notified when object definitions
 * have been parsed.
 * @author Graham Edgecombe
 *
 */
public interface ObjectDefinitionListener {
	
	/**
	 * Called when an object definition is parsed.
	 * @param def The definition that was parsed.
	 */
	public void objectDefinitionParsed(GameObjectDefinition def);

}
