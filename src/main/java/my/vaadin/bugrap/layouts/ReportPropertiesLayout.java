package my.vaadin.bugrap.layouts;

import java.util.Collection;

import my.vaadin.bugrap.Report;
import my.vaadin.bugrap.Report.IssueType;
import my.vaadin.bugrap.Report.Status;
import my.vaadin.bugrap.ReportProperties;

public class ReportPropertiesLayout extends ReportProperties {

	public ReportPropertiesLayout() {
		super();

		prioritySelector.setItems(1, 2, 3, 4);
		typeSelector.setItems(IssueType.values());
		statusSelector.setItems(Status.values());
	}

	public void setReports(Collection<Report> reports) {
		if (reports.size() == 0) {
			clear();
			return;
		}
		if (reports.size() == 1) {
			openInNewWnd.setVisible(true);
			Report report = reports.iterator().next();
			reportName.setValue(report.getSummary());
			setSingleReportProperties(report);
			return;
		}

		openInNewWnd.setVisible(false);
		reportName.setValue(reports.size() + " reports selected");

	}

	public void clear() {
		reportName.setValue("");
		prioritySelector.clear();
		typeSelector.clear();
		statusSelector.clear();
		assignedToSelector.clear();
		versionSelector.clear();
	}

	private void setSingleReportProperties(Report report) {
		prioritySelector.setValue(report.getPriority());
		typeSelector.setValue(report.getType());
		statusSelector.setValue(report.getStatus());
		// assignedToSelector.setValue(report.getAssignedTo());
		// versionSelector.setValue(report.getVersion());
	}
}
