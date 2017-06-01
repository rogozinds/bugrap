package my.vaadin.bugrap.layouts;

import java.util.Collections;
import java.util.List;

import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Report;

import com.vaadin.shared.Registration;

import my.vaadin.bugrap.ReportDetails;
import my.vaadin.bugrap.ReportsProviderService;

@SuppressWarnings("serial")
public class ReportDetailsLayout extends ReportDetails {

	public ReportDetailsLayout() {
	}

	public Registration addUpdateListener(Listener listener) {
		return reportProperties.addListener(listener);
	}

	public void setReport(Report report) {
		if (report == null) {
			clear();
			return;
		}
		reportProperties.setReports(Collections.singletonList(report));
		setComments(report);
	}

	private void setComments(Report report) {
		List<Comment> comments = ReportsProviderService.get().findComments(report);
		commentsContainer.removeAllComponents();
		for (Comment comment : comments) {
			commentsContainer.addComponent(new CommentDesignLayout(comment));
		}
	}

	public void addComment(Comment comment) {
		commentsContainer.addComponent(new CommentDesignLayout(comment));
	}

	public void clear() {
		reportProperties.clear();
		commentsContainer.removeAllComponents();
	}

	public void showOpenInNewWindowBtn(boolean value) {
		reportProperties.showOpenInNewWindowBtn(value);
	}
}
