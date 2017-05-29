package my.vaadin.bugrap.layouts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import my.vaadin.bugrap.Report;
import my.vaadin.bugrap.Report.IssueType;
import my.vaadin.bugrap.Report.Status;
import my.vaadin.bugrap.ReportProperties;
import my.vaadin.bugrap.ReportsProviderService;
import my.vaadin.bugrap.events.UpdateReportDetailsEvent;

public class ReportPropertiesLayout extends ReportProperties {

	private List<Report> reports = new ArrayList<>();

	public ReportPropertiesLayout() {
		super();

		init();
	}

	private void doUpdateReport() {
		for (Report report : reports) {
			if (prioritySelector.getValue() != null)
				report.setPriority(prioritySelector.getValue());
			if (typeSelector.getValue() != null)
				report.setType(typeSelector.getValue());
			if (statusSelector.getValue() != null)
				report.setStatus(statusSelector.getValue());
			if (assignedToSelector.getValue() != null)
				report.setAssignedTo(assignedToSelector.getValue());
			if (versionSelector.getValue() != null)
				report.setVersion(versionSelector.getValue());
		}
		ReportsProviderService.updateReports(reports);
		fireEvent(new UpdateReportDetailsEvent(this, new ArrayList<>(reports)));
	}

	private void init() {
		updateBtn.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				doUpdateReport();
			}
		});

		revertBtn.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				setReports(new ArrayList<>(reports));
			}
		});
		prioritySelector.setItems(1, 2, 3, 4);
		typeSelector.setItems(IssueType.values());
		statusSelector.setItems(Status.values());
		assignedToSelector.setItems(ReportsProviderService.getUsers());
	}

	public void setReports(Collection<Report> reports) {
		this.reports.clear();
		this.reports.addAll(reports);
		if (reports.size() == 0) {
			clear();
			return;
		}

		Report report = this.reports.get(0);
		versionSelector.setItems(ReportsProviderService.getProjectVersions(report.getProject()));
		if (this.reports.size() == 1) {
			openInNewWnd.setVisible(true);
			reportName.setValue(report.getSummary());

			setSingleReportProperties(report);
			return;
		}

		openInNewWnd.setVisible(false);
		reportName.setValue(reports.size() + " reports selected");
		setSingleReportProperties(getCommonReport());
	}

	private Report getCommonReport() {
		if (reports.size() == 0)
			return null;
		if (reports.size() == 1)
			return reports.get(0);

		Report result = new Report(reports.get(0));
		for (int i = 1; i < reports.size(); i++) {
			Report curRep = reports.get(i);
			if (result.getPriority() != null && !result.getPriority().equals(curRep.getPriority()))
				result.setPriority(null);

			if (result.getType() != null && !result.getType().equals(curRep.getType()))
				result.setType(null);

			if (result.getStatus() != null && !result.getStatus().equals(curRep.getStatus()))
				result.setStatus(null);

			if (result.getAssignedTo() != null && !result.getAssignedTo().equals(curRep.getAssignedTo()))
				result.setAssignedTo(null);

			if (result.getVersion() != null && !result.getVersion().equals(curRep.getVersion()))
				result.setVersion(null);
		}

		return result;
	}

	public void clear() {
		reports.clear();
		reportName.setValue("");
		prioritySelector.setValue(null);
		typeSelector.setValue(null);
		statusSelector.setValue(null);
		assignedToSelector.setValue(null);
		versionSelector.clear();
	}

	private void setSingleReportProperties(Report report) {
		if (report == null)
			return;
		prioritySelector.setValue(report.getPriority());
		typeSelector.setValue(report.getType());
		statusSelector.setValue(report.getStatus());
		assignedToSelector.setValue(report.getAssignedTo());
		versionSelector.setValue(report.getVersion());
	}

}
