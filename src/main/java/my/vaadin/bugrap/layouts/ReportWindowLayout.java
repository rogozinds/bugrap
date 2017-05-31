package my.vaadin.bugrap.layouts;

import java.util.Collections;

import org.vaadin.bugrap.domain.entities.Report;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import my.vaadin.bugrap.ReportWindow;
import my.vaadin.bugrap.ReportsProviderService;

public class ReportWindowLayout extends ReportWindow {

	public ReportWindowLayout(long id) {
		Report report = ReportsProviderService.get().getReportById(id);
		if (report == null) {
			show404(id);
			return;
		}

		reportDetails.showOpenInNewWindowBtn(false);
		setReport(report);
	}

	private void setReport(Report report) {
		prjLbl.setValue(report.getProject().getName());
		versionLbl.setValue(report.getVersion().getVersion());
		reportDetails.setReports(Collections.singletonList(report));
	}

	private void show404(long id) {
		Notification.show("Report with id " + id + " was not found", Type.ERROR_MESSAGE);
	}

}
