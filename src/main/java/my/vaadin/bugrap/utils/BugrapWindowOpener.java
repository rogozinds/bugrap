package my.vaadin.bugrap.utils;

import org.vaadin.bugrap.domain.entities.Report;

import com.vaadin.server.Page;

public class BugrapWindowOpener {

	//It's better to use Views and Navigator, instead of having separate UIs.
	//UIs a heavier than views
	//Take a look at com.vaadin.navigator.View and Navigator.
	//No need to change current approach, try to implement new views navigation by using views & Navigator.
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
