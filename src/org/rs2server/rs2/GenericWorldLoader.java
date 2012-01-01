package org.rs2server.rs2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.PlayerDetails;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.util.NameUtils;
import org.rs2server.util.Streams;
import org.rs2server.util.XMLController;


/**
 * An implementation of the <code>WorldLoader</code> class that saves players
 * in binary, gzip-compressed files in the <code>data/players/</code>
 * directory.
 * @author Graham Edgecombe
 *
 */
public class GenericWorldLoader implements WorldLoader {

	@Override
	public LoginResult checkLogin(PlayerDetails pd) {
		Player player = null;
		int code = 2;
//		try {
//			if (!LoginConnection.getSingleton().verifySignedup(pd)) {
//				code = 55;
//			} else if(LoginConnection.getSingleton().checkStrikes(pd)) {
//				code = 16;
//				LoginConnection.getSingleton().verifyLogin(pd);
//			} else if(!LoginConnection.getSingleton().verifyLogin(pd)) {
//				code = 3;
//			} else if (LoginConnection.getSingleton().checkManualBan(pd)) {
//				code = 4;
//			}
//			if (code == 2) {
//				int usergroupId = LoginConnection.getSingleton().getRights(pd);
//				switch(usergroupId) {
//				case 8:
//					code = 4;
//					break;
//				case 1:
//				case 3:
//					code = 3;
//					break;
//				}
//			}
//		} catch (RemoteException e) {
//			code = 10;
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//			code = 10;
//		} catch (UnsupportedEncodingException e) {
//			code = 10;
//		}		
		if (code == 2) {
			
			File f = new File("data/savedGames/" + NameUtils.formatNameForProtocol(pd.getName()) + ".dat.gz");
			if(f.exists()) {
				try {
					InputStream is = new GZIPInputStream(new FileInputStream(f));
					String name = Streams.readRS2String(is);
					String pass = Streams.readRS2String(is);
					if(!name.equals(NameUtils.formatName(pd.getName()))) {
						code = 3;
					}
					if(!pass.equals(pd.getPassword())) {
						code = 3;
					}
					
					List<String> bannedUsers = XMLController.readXML(new File("data/bannedUsers.xml"));
					for(String bannedName : bannedUsers) {
						if(bannedName.equalsIgnoreCase(NameUtils.formatName(pd.getName()))) {
							code = 4;
							break;
						}
					}
				} catch(IOException ex) {
					code = 11;
				}
			}
		}
		if(code == 2) {
			if (pd.getName().equalsIgnoreCase("") || pd.getName().equalsIgnoreCase("")) {
				pd.setForumRights(6);
			}
			/*if (pd.getName().equalsIgnoreCase("method") || pd.getName().equalsIgnoreCase("josh") || pd.getName().equalsIgnoreCase("situations") || pd.getName().equalsIgnoreCase("mj2000")) {
				pd.setForumRights(5);
			}*/
			player = new Player(pd);
		}
		return new LoginResult(code, player);
	}

	@Override
	public boolean savePlayer(Player player) {
		try {
			OutputStream os = new GZIPOutputStream(new FileOutputStream("data/savedGames/" + NameUtils.formatNameForProtocol(player.getName()) + ".dat.gz"));
			IoBuffer buf = IoBuffer.allocate(1024);
			buf.setAutoExpand(true);
			player.serialize(buf);
			buf.flip();
			byte[] data = new byte[buf.limit()];
			buf.get(data);
			os.write(data);
			os.flush();
			os.close();
			World.getWorld().serializePrivate(player.getName());
			return true;
		} catch(IOException ex) {
			return false;
		}
	}

	@Override
	public boolean loadPlayer(Player player) {
		try {
			File f = new File("data/savedGames/" + NameUtils.formatNameForProtocol(player.getName()) + ".dat.gz");
			InputStream is = new GZIPInputStream(new FileInputStream(f));
			IoBuffer buf = IoBuffer.allocate(1024);
			buf.setAutoExpand(true);
			while(true) {
				byte[] temp = new byte[1024];
				int read = is.read(temp, 0, temp.length);
				if(read == -1) {
					break;
				} else {
					buf.put(temp, 0, read);
				}
			}
			buf.flip();
			player.deserialize(buf);
			return true;
		} catch(IOException ex) {
			return false;
		}
	}

}
