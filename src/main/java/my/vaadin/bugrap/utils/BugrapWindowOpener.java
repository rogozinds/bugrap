package my.vaadin.bugrap.utils;

import org.vaadin.bugrap.domain.entities.Report;

import com.vaadin.server.Page;

public class BugrapWindowOpener {

	public static void openReport(Report report) {
		Page.getCurrent().open("/reports/" + report.getId(), "_blank", true);
	}

	public static void openRoot() {
		Page.getCurrent().open("/", "");
	}

	public static void openLogin() {
		Page.getCurrent().open("/auth", "");
	}
}
