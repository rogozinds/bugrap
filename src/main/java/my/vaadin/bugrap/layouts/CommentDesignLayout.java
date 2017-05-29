package my.vaadin.bugrap.layouts;

import my.vaadin.bugrap.Comment;
import my.vaadin.bugrap.CommentDesign;
import my.vaadin.bugrap.utils.RelativeDateUtils;

public class CommentDesignLayout extends CommentDesign {

	public CommentDesignLayout() {

	}

	public CommentDesignLayout(Comment comment) {
		this();

		setComment(comment);
	}

	public void setComment(Comment comment) {
		userNameAndDate
				.setValue(comment.getUserName() + " (" + RelativeDateUtils.getRelativeTime(comment.getDate()) + ")");
		commentSection.setValue(comment.getMessage());
	}
}
