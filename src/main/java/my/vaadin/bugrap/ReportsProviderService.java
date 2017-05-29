package my.vaadin.bugrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import my.vaadin.bugrap.Report.IssueType;
import my.vaadin.bugrap.Report.Status;

public class ReportsProviderService {

	public static final String USER_NAME = "Marc Manager";
	private static final String USER_NAME_2 = "some another manager";

	private final static String PROJECT = "Project ";
	private final static String VERSION = "ver. ";
	private final static String SUMMARY = "Summary #";

	private static List<Report> allReports;
	private static List<String> allUsers;

	public synchronized static List<Report> getAllReports() {
		if (allReports != null)
			return allReports;

		allReports = new ArrayList<>();
		for (int i = 0; i < 40; i++) {
			allReports.add(new Report(PROJECT + (i % 3 + 1), VERSION + (i % 5 + 1), (i % 4 + 1), IssueType.BUG,
					SUMMARY + i, USER_NAME, null, new Date(116, i % 12, i % 28, i % 24, i % 60, i % 10), Status.FIXED));
			allReports.add(new Report(PROJECT + (i % 7 + 1), VERSION + (i % 6 + 1), (i % 5 + 1), IssueType.BUG,
					SUMMARY + i, USER_NAME_2, null, new Date(), Status.OPEN));
		}

		return allReports;
	}

	public synchronized static List<String> getProjectNames() {
		Set<String> projectNames = new LinkedHashSet<>();
		getAllReports().stream().forEach(a -> projectNames.add(a.getProject()));
		return new ArrayList<String>(projectNames);
	}

	private static void addVersion(Report a, String projectName, Set<String> versions) {
		if (a.getProject().equals(projectName))
			versions.add(a.getVersion());
	}

	public synchronized static List<String> getProjectVersions(String projectName) {
		Set<String> versions = new LinkedHashSet<>();
		getAllReports().stream().forEach(a -> addVersion(a, projectName, versions));
		return new ArrayList<>(versions);
	}

	public static List<Comment> getReportComments(Report report) {
		// TODO Auto-generated method stub
		return null;
	}

	public synchronized static List<String> getUsers() {
		if (allUsers != null)
			return allUsers;

		allUsers = new ArrayList<>();
		allUsers.add(USER_NAME);
		allUsers.add(USER_NAME_2);
		allUsers.add("user3");

		return allUsers;
	}

	public static boolean updateReports(Collection<Report> reports) {
		boolean result = true;
		List<Report> allReports = getAllReports();
		Date curDate = new Date();
		for (Report report : reports) {
			if (result)
				result = allReports.contains(report);

			report.setLastModified(curDate);
		}
		return result;
	}
}
