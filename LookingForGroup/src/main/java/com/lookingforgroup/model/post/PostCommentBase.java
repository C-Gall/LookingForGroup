package com.lookingforgroup.model.post;

public abstract class PostCommentBase {
	int id;
	int commentId;
	int posterId;
	int posterUsername;
	String type; //DOMAIN: ProfilePost, ProfileComment, GroupPost, GroupComment, AdvertComment
	String content;
	
	public PostCommentBase(int id, int commentId, int posterId, int posterUsername, String type, String content) {
		super();
		this.id = id;
		this.commentId = commentId;
		this.posterId = posterId;
		this.posterUsername = posterUsername;
		this.type = type;
		this.content = content;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCommentId() {
		return commentId;
	}
	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}
	public int getPosterId() {
		return posterId;
	}
	public void setPosterId(int posterId) {
		this.posterId = posterId;
	}
	public int getPosterUsername() {
		return posterUsername;
	}
	public void setPosterUsername(int posterUsername) {
		this.posterUsername = posterUsername;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
