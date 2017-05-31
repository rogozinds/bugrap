package my.vaadin.bugrap.utils;

import org.vaadin.bugrap.domain.entities.Report;

import com.vaadin.server.Page;

public class ReportWindowOpener {

	public static void openReport(Report report) {
		Page.getCurrent().open("/reports/" + report.getId(), "_blank", true);
	}
}
