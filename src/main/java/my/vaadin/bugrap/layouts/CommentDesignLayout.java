package my.vaadin.bugrap.layouts;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Comment.Type;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.Link;

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

		if (comment.getType() == Type.ATTACHMENT) {
			Link attachLink = getAttachmentLink(comment.getAttachmentName(), comment.getAttachment());

			if (attachLink != null)
				attachmentsContainer.addComponent(attachLink);
		}
	}

	private Link getAttachmentLink(String attachmentName, byte[] attachment) {
		if (attachment == null)
			return null;
		if (attachmentName == null)
			attachmentName = "undefined";

		Link link = new Link();
		link.setCaption(attachmentName);
		StreamResource streamResource = new StreamResource(new StreamResource.StreamSource() {
			@Override
			public InputStream getStream() {
				return new ByteArrayInputStream(attachment);
			}
		}, attachmentName);

		link.setResource(streamResource);
		link.setTargetName("_blank");
		return link;
	}
}
