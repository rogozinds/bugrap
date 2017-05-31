package my.vaadin.bugrap.utils;

import com.vaadin.server.Page;

import my.vaadin.bugrap.Report;

public class ReportWindowOpener {

	public static void openReport(Report report) {
		Page.getCurrent().open("/reports/" + report.getId(), "_blank", true);
	}
}
