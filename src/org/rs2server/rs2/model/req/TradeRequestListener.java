package org.rs2server.rs2.model.req;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.container.Trade;

/**
 * A class which listens to trade requests.
 * @author Graham Edgecombe
 *
 */
public class TradeRequestListener implements RequestListener {

	@Override
	public void requestAccepted(Player player, Player partner) {
		Trade.open(player, partner);
	}

	@Override
	public void requestCancelled(Player player, Player partner) {
		if(player.getTrade().size() > 0) {
			for(Item item : player.getTrade().toArray()) {
				if(item != null) {
					player.getInventory().add(item);
				}
			}
			player.getTrade().clear();
		}
		if(partner.getTrade().size() > 0) {
			for(Item item : partner.getTrade().toArray()) {
				if(item != null) {
					partner.getInventory().add(item);
				}
			}					
			partner.getTrade().clear();
		}
		player.getActionSender().removeAllInterfaces().sendMessage("Trade cancelled.");
		partner.getActionSender().removeAllInterfaces().sendMessage("Other player declined trade.");
	}

	@Override
	public void requestFinished(Player player, Player partner) {
		if(partner.getTrade().size() > 0) {
			for(Item item : partner.getTrade().toArray()) {
				if(item != null) {
					player.getInventory().add(item);
				}
			}
			partner.getTrade().clear();
		}
		if(player.getTrade().size() > 0) {
			for(Item item : player.getTrade().toArray()) {
				if(item != null) {
					partner.getInventory().add(item);
				}
			}
			player.getTrade().clear();
		}
		partner.getActionSender().removeAllInterfaces().sendMessage("Trade completed.");
		player.getActionSender().removeAllInterfaces().sendMessage("Trade completed.");	
		player.getRequestManager().setRequestType(null);
		player.getRequestManager().setAcquaintance(null);		
		partner.getRequestManager().setRequestType(null);
		partner.getRequestManager().setAcquaintance(null);
	}

}
