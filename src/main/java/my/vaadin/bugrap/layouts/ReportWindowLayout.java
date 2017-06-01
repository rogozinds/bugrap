package my.vaadin.bugrap.layouts;

import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Report;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import my.vaadin.bugrap.AppGlobalData;
import my.vaadin.bugrap.ReportWindow;
import my.vaadin.bugrap.ReportsProviderService;

public class ReportWindowLayout extends ReportWindow {

	private final Report report;

	public ReportWindowLayout(long id) {
		report = ReportsProviderService.get().getReportById(id);
		if (report == null) {
			show404(id);
			return;
		}

		reportDetails.showOpenInNewWindowBtn(false);
		setReport(report);

		initClickHandlers(report);
	}

	private void initClickHandlers(final Report report) {
		doneBtn.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (commentArea.getValue().trim().isEmpty()) {
					Notification.show("Empty comment is not allowed", Type.ERROR_MESSAGE);
					return;
				}

				Comment comment = new Comment();
				comment.setType(Comment.Type.COMMENT);
				comment.setAuthor(AppGlobalData.getUserData().getCurrentUser());
				comment.setComment(commentArea.getValue());
				comment.setReport(report);
				try {
					comment = ReportsProviderService.get().save(comment);
					reportDetails.addComment(comment);
				} finally {
					commentArea.clear();
				}
			}
		});

		cancelBtn.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				commentArea.clear();

				commentArea.focus();
			}
		});
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
