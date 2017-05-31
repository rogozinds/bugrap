package my.vaadin.bugrap.layouts;

import java.util.Collections;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import my.vaadin.bugrap.Report;
import my.vaadin.bugrap.ReportWindow;
import my.vaadin.bugrap.ReportsProviderService;

public class ReportWindowLayout extends ReportWindow {

	public ReportWindowLayout(int id) {
		Report report = ReportsProviderService.getReportById(id);
		if (report == null) {
			show404(id);
			return;
		}

		reportDetails.showOpenInNewWindowBtn(false);
		setReport(report);
	}

	private void setReport(Report report) {
		prjLbl.setValue(report.getProject());
		versionLbl.setValue(report.getVersion());
		reportDetails.setReports(Collections.singletonList(report));
	}

	private void show404(int id) {
		Notification.show("Report with id " + id + " was not found", Type.ERROR_MESSAGE);
	}

}
