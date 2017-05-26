package my.vaadin.bugrap.layouts;

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

	public void setReportProperties(Report report) {
		reportName.setValue(report.getSummary());

		prioritySelector.setValue(report.getPriority());
		typeSelector.setValue(report.getType());
		statusSelector.setValue(report.getStatus());
		// assignedToSelector.setValue(report.getAssignedTo());
		// versionSelector.setValue(report.getVersion());
	}
}
