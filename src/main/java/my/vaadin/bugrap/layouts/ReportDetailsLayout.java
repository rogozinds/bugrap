package my.vaadin.bugrap.layouts;

import java.util.Collection;

import my.vaadin.bugrap.Report;
import my.vaadin.bugrap.ReportDetails;

@SuppressWarnings("serial")
public class ReportDetailsLayout extends ReportDetails {

	public ReportDetailsLayout() {
		super();
	}

	public void setReports(Collection<Report> selectedReports) {
		if (selectedReports.size() == 1) {
			setReport(selectedReports.iterator().next());
			return;
		}
	}

	private void setReport(Report report) {
		reportProperties.setReportProperties(report);
	}

	public void clear() {

	}
}
