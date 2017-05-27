package my.vaadin.bugrap.layouts;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import my.vaadin.bugrap.Comment;
import my.vaadin.bugrap.Report;
import my.vaadin.bugrap.ReportDetails;
import my.vaadin.bugrap.ReportsProviderService;

@SuppressWarnings("serial")
public class ReportDetailsLayout extends ReportDetails {

	public ReportDetailsLayout() {
		super();
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
		List<Comment> comments = ReportsProviderService.getReportComments(report);
		// TODO:
	}

	public void clear() {
		reportProperties.clear();

	}
}
