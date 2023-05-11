package com.lookingforgroup.util.filters;

import java.util.List;
import java.util.Map;

public class LobbyFilter extends Filter{

	@Override
	public String sqlBase() {
		return "SELECT * " +
	             "FROM lfg_lobby " +
	             "WHERE id IN " +
	             "(SELECT lfg_lobby.id FROM lfg_lobby " +
	             "FULL OUTER JOIN lfg_lobby_service ON lfg_lobby.id = lfg_lobby_service.id " +
	             "FULL OUTER JOIN lfg_lobby_address ON lfg_lobby.id = lfg_lobby_address.id " +
	             "FULL OUTER JOIN lfg_lobby_tags ON lfg_lobby.id = lfg_lobby_tags.id " +
	             "FULL OUTER JOIN " +
	             "(SELECT lobby_id, COUNT(profile_id)-1 AS player_count " +
	             "FROM lfg_lobby_members " +
	             "GROUP BY lobby_id " +
	             ") AS members " +
	             "ON lfg_lobby.id = members.lobby_id " +
	             "WHERE 1=1 ";
	}

	@Override
	protected void generateKeys(Map<String, String> keyList) {
		keyList.put("owner", "owner_username");
		keyList.put("name", "lobby_name");
		keyList.put("system", "system");
		keyList.put("maxplayers", "max_players");
		keyList.put("paytoplay", "pay_to_play");
		keyList.put("homebrew", "homebrew");
		keyList.put("thirdparty", "third_party");
		keyList.put("tag", "tag");
		keyList.put("totalplayers", "player_count");
		keyList.put("service", "service_name");
		keyList.put("city", "city");
		keyList.put("state", "county_province");
		keyList.put("country", "country");
	}
	
	@Override
	protected void generateBooleanKeys(List<String> booleanKeyList) {
		booleanKeyList.add("pay_to_play");
		booleanKeyList.add("homebrew");
		booleanKeyList.add("third_party");
	}
	
	@Override
	protected void generateIntegerKeys(List<String> integerKeyList) {
		integerKeyList.add("max_players");
		integerKeyList.add("player_count");
	}
}
