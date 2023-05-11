package com.lookingforgroup.model.post;

import java.util.List;

public class Post extends PostCommentBase {
	private List<Comment> comments;
	
	public Post(int id, int commentId, int posterId, int posterUsername, String type, String content, List<Comment> comments) {
		super(id, commentId, posterId, posterUsername, type, content);
		this.comments = comments;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
}
