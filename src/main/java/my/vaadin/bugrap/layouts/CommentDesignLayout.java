package my.vaadin.bugrap.layouts;

import org.vaadin.bugrap.domain.entities.Comment;

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
				.setValue(comment.getAuthor() + " (" + RelativeDateUtils.getRelativeTime(comment.getTimestamp()) + ")");
		commentSection.setValue(comment.getComment());
	}
}
