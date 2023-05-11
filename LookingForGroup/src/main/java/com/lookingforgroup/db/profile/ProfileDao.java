package com.lookingforgroup.db.profile;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.lookingforgroup.model.accountandprofile.OtherProfile;
import com.lookingforgroup.model.accountandprofile.Profile;
import com.lookingforgroup.model.accountandprofile.SimpleProfile;

@Repository
public interface ProfileDao {
	// CREATE OPS
	// Requests
	public OtherProfile sendFriendRequest(OtherProfile otherProfile);
	public OtherProfile acceptFriendRequest(OtherProfile otherProfile);
	public OtherProfile blockUser(OtherProfile otherProfile);
	
	// READ OPS
	// Profile
	public Profile getProfileById(int id);
	public Profile getProfileByUsername(String username);
	public String getUsernameByEmail(String email);
	public List<Profile> getAllProfiles();
	public List<Profile> getAllProfiles(int limit);
	public List<SimpleProfile> getAllSimpleProfiles(int limit);
	// Requests
	public List<OtherProfile> getSentFriendRequests(int id);
	public List<OtherProfile> getReceivedFriendRequests(int id);
	public OtherProfile getFriendRequest(int sender, int recipient);
	public boolean isFriend(int first, int second);
	public List<OtherProfile> getFriends(int id);
	public boolean isBlocked(int first, int second);
	public List<OtherProfile> getBlocked(int id);
	
	// UPDATE OPS
	// Profile
	public Profile updateProfile(int id, Profile profile);
	
	// DELETE OPS
	public void cancelOrDeleteFriendRequest(int sender, int recipient);
	public void removeFriend(int profileOne, int profileTwo);
	public void unblockUser(int blocker, int blocked);
}
