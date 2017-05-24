package my.vaadin.bugrap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.vaadin.bugrap.Report.IssueType;
import my.vaadin.bugrap.Report.Status;

public class ReportProvider {

	private final static String PROJECT = "Project ";
	private final static String VERSION = "ver. ";
	private final static String SUMMARY = "Summary #";

	public static List<Report> getAllReports() {
		List<Report> result = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			result.add(new Report(PROJECT + (i % 3 + 1), VERSION + (i % 5 + 1), (i % 4 + 1), IssueType.BUG, SUMMARY + i,
					"Marc Manager", 0, (new Date()).getTime(), Status.OPEN));
		}

		return result;
	}
}
