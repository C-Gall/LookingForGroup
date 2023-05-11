package com.lookingforgroup.db.profile;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.lookingforgroup.model.accountandprofile.OtherProfile;
import com.lookingforgroup.model.accountandprofile.Profile;
import com.lookingforgroup.model.accountandprofile.SimpleProfile;

@Repository
public class ProfileDaoImpl implements ProfileDao {
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
	public OtherProfile sendFriendRequest(OtherProfile otherProfile) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		
		OtherProfile temp = new OtherProfile();
		
		try {
			String SQL = "INSERT INTO lfg_friend_request(sender_id, sender_username, recipient_id, recipient_username)"
					   + "VALUES (?, ?, ?, ?)";
			
			jdbcTemplate.update(SQL, otherProfile.getSenderId(), otherProfile.getSenderUsername(),
							    otherProfile.getRecipientId(), otherProfile.getRecipientUsername());
			
			logger.info("Friend request sent from " + otherProfile.getSenderUsername() + " to " + otherProfile.getRecipientUsername() + ".");
			
			temp = otherProfile;
			
			transactionManager.commit(status);
		}
		catch (DataAccessException e) {
			System.out.println("Error in sending friend request, rolling back!");
			transactionManager.rollback(status);
			throw e;
		}
		return temp;
	}
	
	@Override
	public OtherProfile acceptFriendRequest(OtherProfile otherProfile) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		
		OtherProfile temp = new OtherProfile();
		
		try {
			String SQL = "INSERT INTO lfg_friend(sender_id, sender_username, recipient_id, recipient_username)"
					   + "VALUES (?, ?, ?, ?)";
			
			jdbcTemplate.update(SQL, otherProfile.getSenderId(), otherProfile.getSenderUsername(),
				    otherProfile.getRecipientId(), otherProfile.getRecipientUsername());
			jdbcTemplate.update(SQL, otherProfile.getRecipientId(), otherProfile.getRecipientUsername(),
					otherProfile.getSenderId(), otherProfile.getSenderUsername());
			
			SQL = "DELETE FROM lfg_friend_request WHERE sender_id = ? AND recipient_id = ?";
			
			jdbcTemplate.update(SQL, otherProfile.getSenderId(), otherProfile.getRecipientId());
			
			temp = otherProfile;
			
			transactionManager.commit(status);
		}
		catch (DataAccessException e) {
			System.out.println("Error in accepting friend request, rolling back!");
			transactionManager.rollback(status);
			throw e;
		}
		return temp;
	}
	
	@Override
	public OtherProfile blockUser(OtherProfile otherProfile) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		
		OtherProfile temp = new OtherProfile();
		
		try {
			String SQL = "INSERT INTO lfg_block(sender_id, sender_username, recipient_id, recipient_username)"
					   + "VALUES (?, ?, ?, ?)";
			
			jdbcTemplate.update(SQL, otherProfile.getSenderId(), otherProfile.getSenderUsername(),
							    otherProfile.getRecipientId(), otherProfile.getRecipientUsername());
			
			logger.info(otherProfile.getSenderUsername() + " has blocked " + otherProfile.getRecipientUsername() + ".");
			
			temp = otherProfile;
			
			transactionManager.commit(status);
		}
		catch (DataAccessException e) {
			System.out.println("Error in blocking user, rolling back!");
			transactionManager.rollback(status);
			throw e;
		}
		return temp;		
	}

	// ------------------------------------------------
	// READ OPS
	// ------------------------------------------------
	@Override
	public Profile getProfileById(int id) {
		String SQL = "SELECT * FROM lfg_profile WHERE id = ?";
		List<Profile> profiles = jdbcTemplate.query(SQL, new ProfileMapper(), new Object[] { id });
		
		if(profiles.size() < 1) {
			return null;
		}
		return profiles.get(0);
	}
	
	@Override
	public Profile getProfileByUsername(String username) {
		String SQL = "SELECT * FROM lfg_profile WHERE username = ?";
		List<Profile> profiles = jdbcTemplate.query(SQL, new ProfileMapper(), new Object[] { username });
		
		if(profiles.size() < 1) {
			return null;
		}
		return profiles.get(0);
	}
	
	@Override
	public List<Profile> getAllProfiles() {
		String SQL = "SELECT * FROM lfg_profile ORDER BY id ASC";
		List<Profile> profiles = jdbcTemplate.query(SQL, new ProfileMapper());
		
		logger.info("Total Profiles retrieved = " + profiles.size());
		
		return profiles;
	}
	
	@Override
	public List<Profile> getAllProfiles(int limit) {
		String SQL = "SELECT * FROM lfg_profile ORDER BY id ASC LIMIT ?";
		List<Profile> profiles = jdbcTemplate.query(SQL, new ProfileMapper(), limit);
		
		logger.info("Total Profiles retrieved = " + profiles.size());
		
		return profiles;
	}
	
	public List<SimpleProfile> getAllSimpleProfiles(int limit) {
		String SQL = "SELECT lfg_profile.id, lfg_profile.username, lfg_account.creation_time, lfg_account.updated_time FROM lfg_profile INNER JOIN"
				+ " lfg_account ON lfg_account.id = lfg_profile.id ORDER BY lfg_account.creation_time DESC LIMIT ?";
		List<SimpleProfile> profiles = jdbcTemplate.query(SQL, new SimpleProfileMapper(), limit);
		
		logger.info("Total Simple Profiles retrieved = " + profiles.size());
		
		return profiles;
	}
	
	@Override
	public String getUsernameByEmail(String email) {
		String SQL = "SELECT username FROM lfg_account INNER JOIN lfg_profile ON lfg_account.id = lfg_profile.id WHERE lfg_account.email = ?";
		List<String> usernames = jdbcTemplate.queryForList(SQL, String.class, new Object[] { email });
		
		if(usernames.size() < 1) {
			return "";
		}
		return usernames.get(0);
	}
	
	@Override
	public List<OtherProfile> getSentFriendRequests(int id) {
		String SQL = "SELECT * FROM lfg_friend_request WHERE sender_id = ? ORDER BY creation_time ASC";
		List<OtherProfile> requests = jdbcTemplate.query(SQL, new OtherProfileMapper(), new Object[] { id });
		
		if(requests.size() == 0) {
			logger.info("No friend requests sent from user with id: " + id);
		}
		else {
			logger.info("Username of first recipient of request from id " + id + ": " + requests.get(0).getRecipientUsername());
		}
		
		return requests;
	}
	
	@Override
	public List<OtherProfile> getReceivedFriendRequests(int id) {
		String SQL = "select * from lfg_friend_request where recipient_id = ? order by creation_time asc";
		List<OtherProfile> requests = jdbcTemplate.query(SQL, new OtherProfileMapper(), new Object[] { id });
		
		return requests;
	}
	
	@Override
	public OtherProfile getFriendRequest(int sender, int recipient) {
		String SQL = "SELECT * FROM lfg_friend_request WHERE (sender_id = ? AND recipient_id = ?)"
				   + " OR (sender_id = ? AND recipient_id = ?)";

		List<OtherProfile> requests = jdbcTemplate.query(SQL, new OtherProfileMapper(), sender, recipient, recipient, sender);
	
		if(requests.size() < 1) {
			return null;
		}
		return requests.get(0);
	}
	
	@Override
	public boolean isFriend(int first, int second) {
		String SQL = "SELECT * FROM lfg_friend WHERE (sender_id = ? AND recipient_id = ?)"
				   + " OR (sender_id = ? AND recipient_id = ?)";
		
		List<OtherProfile> friendList = jdbcTemplate.query(SQL, new OtherProfileMapper(), first, second, second, first);
		
		if(friendList.size() < 1) {
			return false;
		}
		else {
			return true;
		}
	}
	
	@Override
	public List<OtherProfile> getFriends(int id) {
		String SQL = "SELECT * FROM lfg_friend WHERE sender_id = ?";
		
		List<OtherProfile> friendList = jdbcTemplate.query(SQL, new OtherProfileMapper(), id);
		
		return friendList;
	}
	
	@Override
	public boolean isBlocked(int first, int second) {
		String SQL = "SELECT * FROM lfg_block WHERE (sender_id = ? AND recipient_id = ?) OR (sender_id = ? AND recipient_id = ?)";
		
		List<OtherProfile> blockList = jdbcTemplate.query(SQL, new OtherProfileMapper(), first, second, second, first);
		
		if(blockList.size() < 1) {
			return false;
		}
		else {
			return true;
		}
	}
	
	@Override
	public List<OtherProfile> getBlocked(int id) {
		String SQL = "SELECT * FROM lfg_block WHERE sender_id = ?";
		
		List<OtherProfile> blockList = jdbcTemplate.query(SQL, new OtherProfileMapper(), id);
		
		return blockList;
	}
	
	// ------------------------------------------------
	// UPDATE OPS
	// ------------------------------------------------
	public Profile updateProfile(int id, Profile profile) {
		String SQL = "UPDATE lfg_profile SET username = ?, bio = ?, interests = ?, gender = ?, nationality = ? WHERE id = ?";
		
		jdbcTemplate.update(SQL, profile.getUsername(), profile.getBio(), profile.getInterests(), profile.getGender(), profile.getNationality(), id);
		
		logger.info("Updated Profile for ID = " + id);
		
		return profile;
	}
	
	// ------------------------------------------------
	// DELETE OPS
	// ------------------------------------------------
	public void cancelOrDeleteFriendRequest(int sender, int recipient) {
		String SQL = "DELETE FROM lfg_friend_request WHERE sender_id = ? AND recipient_id = ?";
		
		jdbcTemplate.update(SQL, sender, recipient);
		
		logger.info("Friend request from sender ID = " + sender + " to recipient ID = " + recipient + " deleted!");
	}
	
	@Override
	public void removeFriend(int profileOne, int profileTwo) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		
		try {
			String SQL = "DELETE FROM lfg_friend WHERE sender_id = ? AND recipient_id = ?";
			
			//Removing each Friend listing from lfg_friend (ProfileOne is friends with ProfileTwo, and vice-versa).
			jdbcTemplate.update(SQL, profileOne, profileTwo);
			logger.info("Friend from sender ID = " + profileOne + " to recipient ID = " + profileTwo + " deleted!");
			
			jdbcTemplate.update(SQL, profileTwo, profileOne);
			logger.info("Friend from sender ID = " + profileTwo + " to recipient ID = " + profileOne + " deleted!");
			
			transactionManager.commit(status);
		}
		catch (DataAccessException e) {
			System.out.println("Error in removing friend, rolling back!");
			transactionManager.rollback(status);
			throw e;
		}
	}
	
	public void unblockUser(int blocker, int blocked) {
		String SQL = "DELETE FROM lfg_block WHERE sender_id = ? AND recipient_id = ?";
		
		jdbcTemplate.update(SQL, blocker, blocked);
		
		logger.info("User with ID = " + blocker + " has unblocked User with ID = " + blocked + ".");
	}

	// ------------------------------------------------
	// MAPPERS
	// ------------------------------------------------
	class ProfileMapper implements RowMapper<Profile> {
		@Override
		public Profile mapRow(ResultSet rs, int rowNum) throws SQLException {
			Profile profile = new Profile();
			profile.setId(rs.getInt("id"));
			profile.setUsername(rs.getString("username"));
			profile.setBio(rs.getString("bio"));
			profile.setInterests(rs.getString("interests"));
			profile.setGender(rs.getString("gender"));
			profile.setNationality(rs.getString("nationality"));
			return profile;
		}
	}
	
	class SimpleProfileMapper implements RowMapper<SimpleProfile> {

		@Override
		public SimpleProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
			SimpleProfile simpleProfile = new SimpleProfile();
			simpleProfile.setId(rs.getInt("id"));
			simpleProfile.setUsername(rs.getString("username"));
			simpleProfile.setCreationTime(rs.getObject("creation_time", LocalDateTime.class));
			simpleProfile.setUpdatedTime(rs.getObject("creation_time", LocalDateTime.class));
			return simpleProfile;
		}
		
	}
	
	class OtherProfileMapper implements RowMapper<OtherProfile> {

		@Override
		public OtherProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
			OtherProfile profile = new OtherProfile();
			profile.setSenderId(rs.getInt("sender_id"));
			profile.setSenderUsername(rs.getString("sender_username"));
			profile.setRecipientId(rs.getInt("recipient_id"));
			profile.setRecipientUsername(rs.getString("recipient_username"));
			profile.setSentDate(rs.getObject("creation_time", LocalDate.class));
			return profile;
		}
	}
}
