package my.vaadin.bugrap.layouts;

import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;

import my.vaadin.bugrap.AppGlobalData;
import my.vaadin.bugrap.ReportWindow;
import my.vaadin.bugrap.ReportsProviderService;

public class ReportWindowLayout extends ReportWindow {

	private final Report report;
	private byte[] attachment;

	public ReportWindowLayout(long id) {
		report = ReportsProviderService.get().getReportById(id);
		if (report == null) {
			show404(id);
			return;
		}

		reportDetails.showOpenInNewWindowBtn(false);
		setReport(report);

		initClickHandlers(report);
		initUploader();
		attachmentWrapper.setVisible(false);
	}

	private void initUploader() {
		attachUpld.addStartedListener(new StartedListener() {

			@Override
			public void uploadStarted(StartedEvent event) {
				UploadDesignWidget w = new UploadDesignWidget(event.getFilename());
				w.addDeleteAttachmentHandler(new ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						attachmentWidgetsContainer.removeComponent(w);
						if (!hasAttachments())
							attachmentWrapper.setVisible(false);
					}
				});
				attachUpld.setReceiver(w);
				attachmentWidgetsContainer.addComponent(w);
				attachUpld.addProgressListener(w);
				attachUpld.addFailedListener(w);
				attachUpld.addSucceededListener(w);
				attachUpld.addFinishedListener(w);
				attachmentWrapper.setVisible(true);

			}
		});
	}

	private boolean hasAttachments() {
		return attachmentWidgetsContainer.getComponentCount() > 0;
	}

	private void initClickHandlers(final Report report) {
		doneBtn.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (commentArea.getValue().trim().isEmpty()) {
					Notification.show("Empty comment is not allowed", Type.ERROR_MESSAGE);
					return;
				}

				try {
					Reporter author = AppGlobalData.getUserData().getCurrentUser();
					String message = commentArea.getValue();

					if (!hasAttachments())
						reportDetails.addComment(doSaveComment(author, message, null, null, report));
					else {
						for (Component c : attachmentWidgetsContainer) {
							UploadDesignWidget w = (UploadDesignWidget) c;
							reportDetails.addComment(
									doSaveComment(author, message, w.getFileName(), w.getAttachment(), report));
						}
					}
				} finally {
					clearComment();
				}
			}
		});

		cancelBtn.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				clearComment();
			}
		});

	}

	private Comment doSaveComment(Reporter author, String message, String attachmentName, byte[] attachment,
			Report report) {
		Comment comment = new Comment();
		comment.setAuthor(author);
		comment.setComment(message);
		if (attachment != null) {
			comment.setAttachment(attachment);
			comment.setAttachmentName(attachmentName);
			comment.setType(Comment.Type.ATTACHMENT);
		} else
			comment.setType(Comment.Type.COMMENT);
		comment.setReport(report);
		return ReportsProviderService.get().save(comment);
	}

	private void clearComment() {
		commentArea.clear();
		attachmentWidgetsContainer.removeAllComponents();
		attachmentWrapper.setVisible(false);
		commentArea.focus();
	}

	private void setReport(Report report) {
		prjLbl.setValue(report.getProject().getName());
		versionLbl.setValue(report.getVersion().getVersion());
		reportDetails.setReport(report);
	}

	private void show404(long id) {
		Notification.show("Report with id " + id + " was not found", Type.ERROR_MESSAGE);
	}

}
