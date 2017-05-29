package my.vaadin.bugrap;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import my.vaadin.bugrap.Report.IssueType;
import my.vaadin.bugrap.Report.Status;

public class ReportsProviderService {

	public static final String USER_NAME = "Marc Manager";

	private final static String PROJECT = "Project ";
	private final static String VERSION = "ver. ";
	private final static String SUMMARY = "Summary #";

	private static List<Report> allReports;

	public synchronized static List<Report> getAllReports() {
		if (allReports != null)
			return allReports;

		allReports = new ArrayList<>();
		for (int i = 0; i < 40; i++) {
			allReports.add(new Report(PROJECT + (i % 3 + 1), VERSION + (i % 5 + 1), (i % 4 + 1), IssueType.BUG,
					SUMMARY + i, USER_NAME, null, new Date(116, i % 12, i % 28, i % 24, i % 60, i % 10), Status.FIXED));
			allReports.add(new Report(PROJECT + (i % 7 + 1), VERSION + (i % 6 + 1), (i % 5 + 1), IssueType.BUG,
					SUMMARY + i, "some another manager", null, new Date(), Status.OPEN));
		}

		return allReports;
	}

	public synchronized static List<String> getProjectNames() {
		Set<String> projectNames = new LinkedHashSet<>();
		getAllReports().stream().forEach(a -> projectNames.add(a.getProject()));
		return new ArrayList<String>(projectNames);
	}

	public static List<Comment> getReportComments(Report report) {
		// TODO Auto-generated method stub
		return null;
	}
}
