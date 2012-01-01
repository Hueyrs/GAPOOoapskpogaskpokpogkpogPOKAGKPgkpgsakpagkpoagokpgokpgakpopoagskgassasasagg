package org.rs2server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.RS2Server;
import org.rs2server.rs2.model.World;

/**
 * A class to start both the file and game servers.
 * @author Graham Edgecombe
 *
 */
public class Server {
	
	/**
	 * The protocol version.
	 */
	public static final int VERSION = 474;
	
	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(Server.class.getName());
	
	/**
	 * The entry point of the application.
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
		logger.info("Starting Hyperion...");
		logger.info("Reading config file...");
		try {
			BufferedReader reader = new BufferedReader(new FileReader("./config.cfg"));
			Constants.PORT = Integer.parseInt(reader.readLine());
			Constants.SERVER_NAME = reader.readLine();
			Constants.OWNER = reader.readLine();
			Constants.EXP_MODIFIER = Integer.parseInt(reader.readLine());
			Constants.WEBSITE = reader.readLine();
			Constants.LATEST_UPDATE = reader.readLine();
			Constants.EXTRA_MESSAGE = reader.readLine();
			Constants.PLAYER_COMMANDS = Boolean.parseBoolean(reader.readLine());
			reader.close();
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Could not read config file.");
			System.exit(1);
		}
		World.getWorld(); // this starts off background loading
		try {
			new RS2Server().bind(Constants.PORT).start();
		} catch(Exception ex) {
			logger.log(Level.SEVERE, "Error starting Hyperion.", ex);
			System.exit(1);
		}
	}

}
