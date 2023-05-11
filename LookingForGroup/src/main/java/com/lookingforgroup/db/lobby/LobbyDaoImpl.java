package com.lookingforgroup.db.lobby;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.lookingforgroup.model.lobby.Lobby;
import com.lookingforgroup.model.lobby.LobbyAddress;
import com.lookingforgroup.model.lobby.LobbyMember;
import com.lookingforgroup.model.lobby.LobbyService;
import com.lookingforgroup.model.lobby.WebLobby;
import com.lookingforgroup.util.filters.Filter;
import com.lookingforgroup.util.filters.FilterResult;
import com.lookingforgroup.util.filters.LobbyFilter;

@Repository
public class LobbyDaoImpl implements LobbyDao {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DataSourceTransactionManager transactionManager;
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}

	// ------------------------------------------------
	// CREATE OPS
	// ------------------------------------------------
	@Override
	public WebLobby addLobby(WebLobby lobby) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		
		try {
			final String lobbySQL = "INSERT INTO lfg_lobby (owner_id, owner_username, lobby_name, system, max_players, pay_to_play, pay_details, homebrew, third_party, remote, details)"
							 + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			KeyHolder keyHolder = new GeneratedKeyHolder();
			
			jdbcTemplate.update(connection -> {
				PreparedStatement ps = connection.prepareStatement(lobbySQL, new String[] { "id" });
				ps.setInt(1, lobby.getOwnerId());
				ps.setString(2, lobby.getOwnerUsername());
				ps.setString(3, lobby.getLobbyName().trim());
				ps.setString(4, lobby.getSystem());
				ps.setInt(5, lobby.getMaxPlayers());
				ps.setBoolean(6, lobby.isPayToPlay());
				ps.setString(7, lobby.getPayDetails().trim());
				ps.setBoolean(8, lobby.isHomebrew());
				ps.setBoolean(9, lobby.isThirdParty());
				ps.setBoolean(10, lobby.isRemote());
				ps.setString(11, lobby.getDetails().trim());
				return ps;
			}, keyHolder);
			
			lobby.setId(keyHolder.getKey().intValue());
			
			logger.info("Lobby created with id = " + lobby.getId() + ".");
			
			if(lobby.getLobbyTags().size() > 0) {
				String tagsSQL = "INSERT INTO lfg_lobby_tags (id, tag) VALUES (?, ?)";
				for(String tag : lobby.getLobbyTags()) {
					jdbcTemplate.update(tagsSQL, lobby.getId(), tag);
				}
			}
			
			if(lobby.isRemote()) {
				String serviceSQL = "INSERT INTO lfg_lobby_service (id, service_name, service_url)"
										+ " VALUES (?, ?, ?)";
				
				jdbcTemplate.update(serviceSQL, lobby.getId(), lobby.getServiceName().trim(), lobby.getServiceUrl().trim());
				
				logger.info("Lobby service info created with id = " + lobby.getId() + ".");
			}
			else {
				String addressSQL = "INSERT INTO lfg_lobby_address (id, line_1, line_2, line_3, city, county_province,"
										+ " zip_postcode, country, other_details)"
										+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
				
				jdbcTemplate.update(addressSQL, lobby.getId(), lobby.getLine1().trim(), lobby.getLine2().trim(), lobby.getLine3().trim(),
									lobby.getCity().trim(), lobby.getCountyProvince().trim(), lobby.getZipPostcode().trim(),
									lobby.getCountry(), lobby.getOtherDetails().trim());
				
				logger.info("Lobby address info created with id = " + lobby.getId() + ".");
			}
			
			String memberSQL = "INSERT INTO lfg_lobby_members (lobby_id, profile_id, profile_username) VALUES (?, ?, ?)";
			
			jdbcTemplate.update(memberSQL, lobby.getId(), lobby.getOwnerId(), lobby.getOwnerUsername());
			
			logger.info(lobby.getOwnerUsername() + " has been designated a member of " + lobby.getId());
			
			transactionManager.commit(status);
		}
		catch (DataAccessException e) {
			System.out.println("Error in creating lobby, rolling back.");
			transactionManager.rollback(status);
			throw e;
		}	

		return lobby;
	}
	
	@Override
	public LobbyMember sendJoinRequest(LobbyMember lobbyMember) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		
		LobbyMember temp = new LobbyMember();
		
		try {
			String SQL = "INSERT INTO lfg_lobby_join_requests (lobby_id, profile_id, profile_username, request_details) " +
						 "VALUES (?, ?, ?, ?)";
			
			jdbcTemplate.update(SQL, lobbyMember.getLobbyId(), lobbyMember.getProfileId(), lobbyMember.getProfileUsername(), lobbyMember.getRequestDetails());
			
			logger.info("Join request sent from " + lobbyMember.getProfileUsername() + " to Lobby ID: " + lobbyMember.getLobbyId() + ".");
			
			temp = lobbyMember;
			
			transactionManager.commit(status);
		}
		catch (DataAccessException e) {
			System.out.println("Error in sending lobby join request, rolling back!");
			transactionManager.rollback(status);
			throw e;
		}
		return temp;		
	}
	
	@Override
	public LobbyMember acceptJoinRequest(LobbyMember lobbyMember) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		
		LobbyMember temp = new LobbyMember();
		
		try {
			String acceptSQL = "INSERT INTO lfg_lobby_members (lobby_id, profile_id, profile_username) " +
							   "VALUES (?, ?, ?)";
			
			jdbcTemplate.update(acceptSQL, lobbyMember.getLobbyId(), lobbyMember.getProfileId(), lobbyMember.getProfileUsername());
			
			String deleteSQL = "DELETE FROM lfg_lobby_join_requests WHERE lobby_id = ? AND profile_id = ?";
			
			jdbcTemplate.update(deleteSQL, lobbyMember.getLobbyId(), lobbyMember.getProfileId());
			
			temp = lobbyMember;
			
			transactionManager.commit(status);
		}
		catch (DataAccessException e) {
			System.out.println("Error in accepting lobby join request, rolling back!");
			transactionManager.rollback(status);
			throw e;
		}
		return temp;
	}
	
	@Override
	public LobbyMember declineJoinRequest(LobbyMember lobbyMember) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		
		LobbyMember temp = new LobbyMember();
		
		try {
			String deleteSQL = "DELETE FROM lfg_lobby_join_requests WHERE lobby_id = ? AND profile_id = ?";
			
			jdbcTemplate.update(deleteSQL, lobbyMember.getLobbyId(), lobbyMember.getProfileId());
			
			temp = lobbyMember;
			
			transactionManager.commit(status);
		}
		catch (DataAccessException e) {
			System.out.println("Error in declining lobby join request, rolling back!");
			transactionManager.rollback(status);
			throw e;
		}
		return temp;
	}

	// ------------------------------------------------
	// READ OPS
	// ------------------------------------------------
	@Override
	public Lobby getLobbyById(int id) {
		String lobbySQL = "SELECT * FROM lfg_lobby WHERE id = ?";
		List<Lobby> lobbies = jdbcTemplate.query(lobbySQL, new LobbyMapper(), new Object[] { id });
		
		if(lobbies.size() < 1) {
			return null;
		}
		else {
			Lobby lobby = lobbies.get(0);
			
			if(lobby.isRemote()) {
				String remoteSQL = "SELECT * FROM lfg_lobby_service WHERE id = ?";
				lobby.setLobbyService(jdbcTemplate.queryForObject(remoteSQL, new ServiceMapper(), new Object[] { id }));
			}
			else {
				String addressSQL = "SELECT * FROM lfg_lobby_address WHERE id = ?";
				lobby.setLobbyAddress(jdbcTemplate.queryForObject(addressSQL, new AddressMapper(), new Object[] { id }));
			}
			
			String tagsSQL = "SELECT tag FROM lfg_lobby_tags WHERE id = ?";
			lobby.setTags(jdbcTemplate.queryForList(tagsSQL, String.class, id));
			
			String memberSQL = "SELECT * FROM lfg_lobby_members WHERE lobby_id = ?";
			lobby.setMembers(jdbcTemplate.query(memberSQL, new MemberMapper(), new Object[] { id }));
			
			String requestSQL = "SELECT * FROM lfg_lobby_join_requests WHERE lobby_id = ? ORDER BY creation_time";
			lobby.setRequests(jdbcTemplate.query(requestSQL, new MemberMapper(), new Object[] { id }));
			
			return lobby;
		}
	}
	
	@Override
	public List<String> getLobbyTagList() {
		String tagsSQL = "SELECT * FROM lfg_lobby_tags_list ORDER BY tag";
		
		List<String> tags = jdbcTemplate.queryForList(tagsSQL, String.class);
		
		return tags;
	}
	
	@Override
	public List<String> getLobbySystemList() {
		String systemSQL = "SELECT system FROM lfg_lobby_systems_list ORDER BY system";
		
		List<String> systems = jdbcTemplate.queryForList(systemSQL, String.class);
		
		return systems;
	}
	
	@Override
	public List<String> getCountryList() {
		String countrySQL = "SELECT country FROM lfg_country_list ORDER BY country";
		
		List<String> countries = jdbcTemplate.queryForList(countrySQL, String.class);
		
		return countries;
	}

	@Override
	public List<Lobby> getLobbies() {
		String lobbiesSQL = "SELECT * FROM lfg_lobby ORDER BY updated_time DESC";
		
		List<Lobby> lobbies = jdbcTemplate.query(lobbiesSQL, new LobbyMapper());
		
		String lobbyAddressSQL = "SELECT * FROM lfg_lobby_address WHERE id = ?";
		String lobbyServiceSQL = "SELECT * FROM lfg_lobby_service WHERE id = ?";
		String tagsSQL = "SELECT tag FROM lfg_lobby_tags WHERE id = ?";
		String memberSQL = "SELECT * FROM lfg_lobby_members WHERE lobby_id = ?";
		
		// Retrieve needed components for listing the data.
		for(Lobby lobby : lobbies) {
			lobby.setTags(jdbcTemplate.queryForList(tagsSQL, String.class, lobby.getId()));
			if(lobby.isRemote()) {
				lobby.setLobbyService(jdbcTemplate.queryForObject(lobbyServiceSQL, new ServiceMapper(), lobby.getId()));
			}
			else {
				lobby.setLobbyAddress(jdbcTemplate.queryForObject(lobbyAddressSQL, new AddressMapper(), lobby.getId()));
			}
			lobby.setMembers(jdbcTemplate.query(memberSQL, new MemberMapper(), lobby.getId()));
		}
		
		return lobbies;
	}
	
	@Override
	public List<Lobby> getLobbies(String orderBy, int limit) {
		String lobbiesSQL = "SELECT * FROM lfg_lobby ORDER BY " + orderBy + " DESC LIMIT ?";
		
		List<Lobby> lobbies = jdbcTemplate.query(lobbiesSQL, new LobbyMapper(), limit);
		
		String lobbyAddressSQL = "SELECT * FROM lfg_lobby_address WHERE id = ?";
		String lobbyServiceSQL = "SELECT * FROM lfg_lobby_service WHERE id = ?";
		String tagsSQL = "SELECT tag FROM lfg_lobby_tags WHERE id = ?";
		String memberSQL = "SELECT * FROM lfg_lobby_members WHERE lobby_id = ?";
		
		// Retrieve needed components for listing the data.
		for(Lobby lobby : lobbies) {
			logger.info("Adding information to Lobby with lobby_name = " + lobby.getLobbyName());
			lobby.setTags(jdbcTemplate.queryForList(tagsSQL, String.class, lobby.getId()));
			if(lobby.isRemote()) {
				lobby.setLobbyService(jdbcTemplate.queryForObject(lobbyServiceSQL, new ServiceMapper(), lobby.getId()));
			}
			else {
				lobby.setLobbyAddress(jdbcTemplate.queryForObject(lobbyAddressSQL, new AddressMapper(), lobby.getId()));
			}
			lobby.setMembers(jdbcTemplate.query(memberSQL, new MemberMapper(), lobby.getId()));
		}
		
		return lobbies;
	}
	
	@Override
	public List<Lobby> getLobbies(String filterInput) {
		Filter lobbyFilter = new LobbyFilter();
		FilterResult result = lobbyFilter.generateFilterResult(filterInput);
		
		//TODO: Eventually update Filter.java so that it has an "end" for the SQL for ordering properly by the updated time.
		List<Lobby> lobbies = jdbcTemplate.query(result.getSQL() + " ORDER BY creation_time", new LobbyMapper(), result.getValues().toArray());
		
		String lobbyAddressSQL = "SELECT * FROM lfg_lobby_address WHERE id = ?";
		String lobbyServiceSQL = "SELECT * FROM lfg_lobby_service WHERE id = ?";
		String tagsSQL = "SELECT tag FROM lfg_lobby_tags WHERE id = ?";
		String memberSQL = "SELECT * FROM lfg_lobby_members WHERE lobby_id = ?";
		
		// Retrieve needed components for listing the data.
		for(Lobby lobby : lobbies) {
			lobby.setTags(jdbcTemplate.queryForList(tagsSQL, String.class, lobby.getId()));
			if(lobby.isRemote()) {
				lobby.setLobbyService(jdbcTemplate.queryForObject(lobbyServiceSQL, new ServiceMapper(), lobby.getId()));
			}
			else {
				lobby.setLobbyAddress(jdbcTemplate.queryForObject(lobbyAddressSQL, new AddressMapper(), lobby.getId()));
			}
			lobby.setMembers(jdbcTemplate.query(memberSQL, new MemberMapper(), lobby.getId()));
		}
		
		return lobbies;
	}
	
	@Override
	public List<Lobby> getMyLobbies(String username) {
		String lobbiesSQL = "SELECT * FROM lfg_lobby WHERE id IN (SELECT lobby_id FROM lfg_lobby_members WHERE profile_username = ?)";
		
		List<Lobby> lobbies = jdbcTemplate.query(lobbiesSQL, new LobbyMapper(), username);
		
		String lobbyAddressSQL = "SELECT * FROM lfg_lobby_address WHERE id = ?";
		String lobbyServiceSQL = "SELECT * FROM lfg_lobby_service WHERE id = ?";
		String tagsSQL = "SELECT tag FROM lfg_lobby_tags WHERE id = ?";
		String memberSQL = "SELECT * FROM lfg_lobby_members WHERE lobby_id = ?";
		
		// Retrieve needed components for listing the data.
		for(Lobby lobby : lobbies) {
			lobby.setTags(jdbcTemplate.queryForList(tagsSQL, String.class, lobby.getId()));
			if(lobby.isRemote()) {
				lobby.setLobbyService(jdbcTemplate.queryForObject(lobbyServiceSQL, new ServiceMapper(), lobby.getId()));
			}
			else {
				lobby.setLobbyAddress(jdbcTemplate.queryForObject(lobbyAddressSQL, new AddressMapper(), lobby.getId()));
			}
			lobby.setMembers(jdbcTemplate.query(memberSQL, new MemberMapper(), lobby.getId()));
		}
		
		return lobbies;
	}
	
	@Override
	public LobbyMember getJoinRequest(int lobby, int sender) {
		String requestSQL = "SELECT * FROM lfg_lobby_join_requests WHERE lobby_id = ? AND profile_id = ?";
		
		List<LobbyMember> requests = jdbcTemplate.query(requestSQL, new MemberMapper(), lobby, sender);
		
		if(requests.size() < 1) {
			return null;
		}
		return requests.get(0);
	}

	// ------------------------------------------------
	// UPDATE OPS
	// ------------------------------------------------
	@Override
	public WebLobby updateLobby(WebLobby lobby) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		
		try {
			//Lobby Primary Scripts
			String lobbySQL = "UPDATE lfg_lobby SET lobby_name = ?, system  = ?, max_players = ?, "
							+ "pay_to_play = ?, pay_details = ?, homebrew = ?, third_party = ?, "
							+ "remote = ?, details = ?, updated_time = NOW() WHERE id = ?";
			
			jdbcTemplate.update(lobbySQL, lobby.getLobbyName(), lobby.getSystem(), lobby.getMaxPlayers(), lobby.isPayToPlay(),
								lobby.getPayDetails(), lobby.isHomebrew(), lobby.isThirdParty(),
								lobby.isRemote(), lobby.getDetails(), lobby.getId());
			
			//Address or Service Scripts
			if(lobby.isRemote()) {
				String checkSQL = "SELECT * FROM lfg_lobby_service WHERE id = ?";
				List<LobbyService> lobbyServices = jdbcTemplate.query(checkSQL, new ServiceMapper(), lobby.getId());
				
				//LobbyService entry exists, we must update.
				if(lobbyServices.size() > 0) {
					String serviceUpdateSQL = "UPDATE lfg_lobby_service SET service_name = ?, service_url = ? WHERE id = ?";
					
					jdbcTemplate.update(serviceUpdateSQL, lobby.getServiceName().trim(), lobby.getServiceUrl().trim(), lobby.getId());
				}
				//LobbyService entry not found, delete LobbyAddress entry and create LobbyService entry.
				else {
					String addressDeleteSQL = "DELETE FROM lfg_lobby_address WHERE id = ?";
					
					jdbcTemplate.update(addressDeleteSQL, lobby.getId());
					
					String serviceInsertSQL = "INSERT INTO lfg_lobby_service (id, service_name, service_url)"
											+ " VALUES (?, ?, ?)";
					
					jdbcTemplate.update(serviceInsertSQL, lobby.getId(), lobby.getServiceName().trim(), lobby.getServiceUrl().trim());
				}
			}
			else {
				String checkSQL = "SELECT * FROM lfg_lobby_address WHERE id = ?";
				List<LobbyAddress> lobbyAddresses = jdbcTemplate.query(checkSQL, new AddressMapper(), lobby.getId());
				
				//LobbyAddress entry exists, we must update.
				if(lobbyAddresses.size() > 0) {
					String addressUpdateSQL = "UPDATE lfg_lobby_address SET line_1 = ?, line_2 = ?, line_3 = ?, city = ?, "
											+ "county_province = ?, zip_postcode = ?, country = ?, other_details = ? WHERE id = ?";
					
					jdbcTemplate.update(addressUpdateSQL, lobby.getLine1().trim(), lobby.getLine2().trim(), lobby.getLine3().trim(),
										lobby.getCity().trim(),lobby.getCountyProvince().trim(), lobby.getZipPostcode().trim(),
										lobby.getCountry().trim(), lobby.getOtherDetails().trim(),lobby.getId());
				}
				//LobbyAddress entry not found, delete LobbyService entry and create LobbyAddress entry.
				else {
					String serviceDeleteSQL = "DELETE FROM lfg_lobby_service WHERE id = ?";
					
					jdbcTemplate.update(serviceDeleteSQL, lobby.getId());
					
					String addressInsertSQL = "INSERT INTO lfg_lobby_address (id, line_1, line_2, line_3, city, county_province,"
							+ " zip_postcode, country, other_details)"
							+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
					jdbcTemplate.update(addressInsertSQL, lobby.getId(), lobby.getLine1().trim(), lobby.getLine2().trim(), lobby.getLine3().trim(),
						lobby.getCity().trim(), lobby.getCountyProvince().trim(), lobby.getZipPostcode().trim(),
						lobby.getCountry(), lobby.getOtherDetails().trim());					
				}
			}
			
			//Lobby Tags Scripts
			String tagDeleteSQL = "DELETE FROM lfg_lobby_tags WHERE id = ?";
			jdbcTemplate.update(tagDeleteSQL, lobby.getId());
			
			if(lobby.getLobbyTags().size() > 0) {
				String tagsSQL = "INSERT INTO lfg_lobby_tags (id, tag) VALUES (?, ?)";
				for(String tag : lobby.getLobbyTags()) {
					jdbcTemplate.update(tagsSQL, lobby.getId(), tag);
				}				
			}
			
			transactionManager.commit(status);
		}
		catch (DataAccessException e) {
			System.out.println("Error in updating lobby, rolling back.");
			transactionManager.rollback(status);
			throw e;
		}	

		return lobby;
	}
	
	public LobbyMember promoteMember(LobbyMember lobbyMember) {
		String SQL = "UPDATE lfg_lobby SET owner_id = ?, owner_username = ? WHERE id = ?";
		
		jdbcTemplate.update(SQL, lobbyMember.getProfileId(), lobbyMember.getProfileUsername(), lobbyMember.getLobbyId());
		
		return lobbyMember;
	}

	// ------------------------------------------------
	// DELETE OPS
	// ------------------------------------------------
	@Override
	public Lobby deleteLobby(Lobby lobby) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		
		int lobbyId = lobby.getId();
		
		try {
			logger.info("Removing tables reliant on lobby for Lobby = " + lobby.getLobbyName());
			
			// First remove all appropriate records that rely on a Lobby
			if(lobby.isRemote()) {
				String deleteLobbyServiceSQL = "DELETE FROM lfg_lobby_service WHERE id = ?";
				jdbcTemplate.update(deleteLobbyServiceSQL, lobbyId);
			}
			else {
				String deleteLobbyAddressSQL = "DELETE FROM lfg_lobby_address WHERE id = ?";
				jdbcTemplate.update(deleteLobbyAddressSQL, lobbyId);
			}
			
			String deleteLobbyJoinRequestsSQL = "DELETE FROM lfg_lobby_join_requests WHERE lobby_id = ?";
			jdbcTemplate.update(deleteLobbyJoinRequestsSQL, lobbyId);
			
			String deleteLobbyMembersSQL = "DELETE FROM lfg_lobby_members WHERE lobby_id = ?";
			jdbcTemplate.update(deleteLobbyMembersSQL, lobbyId);
			
			String deleteLobbyTagsSQL = "DELETE FROM lfg_lobby_tags WHERE id = ?";
			jdbcTemplate.update(deleteLobbyTagsSQL, lobbyId);
			logger.info("Successfully removed reliant tables for Lobby = " + lobby.getLobbyName() + "; Now removing lobby.");
			
			//Finally remove the Lobby
			String deleteLobbySQL = "DELETE FROM lfg_lobby WHERE id = ?";
			jdbcTemplate.update(deleteLobbySQL, lobbyId);
			
			logger.info("Successfully removed Lobby = " + lobby.getLobbyName() + "!");
			
			transactionManager.commit(status);
		}
		catch (DataAccessException e) {
			System.out.println("Error in deleting lobby, rolling back.");
			transactionManager.rollback(status);
			throw e;
		}	

		return lobby;
	}
	
	// Utilized for both removing a member as the owner and removing oneself from the lobby.
	@Override
	public LobbyMember removeMember(LobbyMember lobbyMember) {
		String SQL = "DELETE FROM lfg_lobby_members WHERE lobby_id = ? AND profile_id = ?";
		
		jdbcTemplate.update(SQL, lobbyMember.getLobbyId(), lobbyMember.getProfileId());
		
		return lobbyMember;
	}
	
	// ------------------------------------------------
	// MAPPERS
	// ------------------------------------------------
	class LobbyMapper implements RowMapper<Lobby> {

		@Override
		public Lobby mapRow(ResultSet rs, int rowNum) throws SQLException {
			Lobby lobby = new Lobby();
			lobby.setId(rs.getInt("id"));
			lobby.setCreationTime(rs.getObject("creation_time", LocalDateTime.class));
			lobby.setUpdatedTime(rs.getObject("updated_time", LocalDateTime.class));
			lobby.setOwnerId(rs.getInt("owner_id"));
			lobby.setOwnerUsername(rs.getString("owner_username"));
			lobby.setLobbyName(rs.getString("lobby_name"));
			lobby.setSystem(rs.getString("system"));
			lobby.setMaxPlayers(rs.getInt("max_players"));
			lobby.setPayToPlay(rs.getBoolean("pay_to_play"));
			lobby.setPayDetails(rs.getString("pay_details"));
			lobby.setHomebrew(rs.getBoolean("homebrew"));
			lobby.setThirdParty(rs.getBoolean("third_party"));
			lobby.setRemote(rs.getBoolean("remote"));
			lobby.setDetails(rs.getString("details"));
			return lobby;
		}
	}
	
	class ServiceMapper implements RowMapper<LobbyService> {

		@Override
		public LobbyService mapRow(ResultSet rs, int rowNum) throws SQLException {
			LobbyService lobbyService = new LobbyService();
			lobbyService.setId(rs.getInt("id"));
			lobbyService.setServiceName(rs.getString("service_name"));
			lobbyService.setServiceUrl(rs.getString("service_url"));
			return lobbyService;
		}
	}

	class AddressMapper implements RowMapper<LobbyAddress> {

		@Override
		public LobbyAddress mapRow(ResultSet rs, int rowNum) throws SQLException {
			LobbyAddress lobbyAddress = new LobbyAddress();
			lobbyAddress.setId(rs.getInt("id"));
			lobbyAddress.setLine1(rs.getString("line_1"));
			lobbyAddress.setLine2(rs.getString("line_2"));
			lobbyAddress.setLine3(rs.getString("line_3"));
			lobbyAddress.setCity(rs.getString("city"));
			lobbyAddress.setCountyProvince(rs.getString("county_province"));
			lobbyAddress.setZipPostcode(rs.getString("zip_postcode"));
			lobbyAddress.setCountry(rs.getString("country"));
			lobbyAddress.setOtherDetails(rs.getString("other_details"));
			return lobbyAddress;
		}
	}
	
	class MemberMapper implements RowMapper<LobbyMember> {

		@Override
		public LobbyMember mapRow(ResultSet rs, int rowNum) throws SQLException {
			LobbyMember lobbyMember = new LobbyMember();
			lobbyMember.setLobbyId(rs.getInt("lobby_id"));
			lobbyMember.setProfileId(rs.getInt("profile_id"));
			lobbyMember.setProfileUsername(rs.getString("profile_username"));
			lobbyMember.setRequestDetails(rs.getString("request_details"));
			lobbyMember.setCreationTime(rs.getObject("creation_time", LocalDateTime.class));
			return lobbyMember;
		}
		
	}
}
