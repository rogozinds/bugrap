package my.vaadin.bugrap.layouts;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Report;

import com.vaadin.shared.Registration;
import com.vaadin.ui.VerticalLayout;

import my.vaadin.bugrap.ReportDetails;
import my.vaadin.bugrap.ReportsProviderService;

@SuppressWarnings("serial")
public class ReportDetailsLayout extends ReportDetails {

	public ReportDetailsLayout() {
	}

	public Registration addUpdateListener(Listener listener) {
		return reportProperties.addListener(listener);
	}

	public void setReports(Collection<Report> selectedReports) {
		if (selectedReports == null || selectedReports.isEmpty()) {
			clear();
			return;
		}
		Report report = selectedReports.iterator().next();
		reportProperties.setReports(Collections.singletonList(report));
		setComments(report);
	}

	private void setComments(Report report) {
		List<Comment> comments = ReportsProviderService.get().findComments(report);
		VerticalLayout commentsContainer = new VerticalLayout();
		commentsSection.setContent(commentsContainer);
		for (Comment comment : comments) {
			commentsContainer.addComponent(new CommentDesignLayout(comment));
		}
	}

	public void clear() {
		reportProperties.clear();
	}

	public void showOpenInNewWindowBtn(boolean value) {
		reportProperties.showOpenInNewWindowBtn(value);
	}
}
