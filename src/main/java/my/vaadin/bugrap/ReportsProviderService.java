package my.vaadin.bugrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import my.vaadin.bugrap.Report.IssueType;
import my.vaadin.bugrap.Report.Status;

public class ReportsProviderService {

	public static final String USER_NAME = "Marc Manager";
	private static final String USER_NAME_2 = "some another manager";
	private static final String USER_NAME_HANK = "Hank Backwoodling";

	private final static String PROJECT = "Project ";
	private final static String PROJECT_LONG = "Project name that is rather long pellentesque habitant morbi";
	private final static String VERSION = "ver. ";
	private final static String SUMMARY = "Summary #";
	private final static String SUMMARY2 = "Rather long summary pellentesque habitant morbi #";

	private static List<Report> allReports;
	private static List<String> allUsers;
	private static Map<Report, List<Comment>> commentsMap = new HashMap<>();

	public synchronized static List<Report> getAllReports() {
		if (allReports != null)
			return allReports;

		allReports = new ArrayList<>();
		for (int i = 0; i < 40; i++) {
			allReports.add(new Report(PROJECT_LONG, VERSION + "1.0." + (i % 9), (i % 5 + 1), IssueType.FEATURE,
					SUMMARY + i, null, null, new Date(), Status.values()[i%8]));

			allReports.add(new Report(PROJECT + (i % 3 + 1), VERSION + (i % 5 + 1), (i % 4 + 1), IssueType.BUG,
					SUMMARY + i, USER_NAME, null, new Date(116, i % 12, i % 28, i % 24, i % 60, i % 10), Status.FIXED));
			allReports.add(new Report(PROJECT + (i % 7 + 1), VERSION + (i % 6 + 1), (i % 5 + 1), IssueType.BUG,
					SUMMARY + i, USER_NAME_2, null, new Date(), Status.values()[i%4]));
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

	public synchronized static List<String> getUsers() {
		if (allUsers != null)
			return allUsers;

		allUsers = new ArrayList<>();
		allUsers.add(USER_NAME);
		allUsers.add(USER_NAME_2);
		allUsers.add("user3");
		allUsers.add(USER_NAME_HANK);

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

	public static List<Comment> getComments(Report report) {
		List<Comment> comments = commentsMap.get(report);
		if (comments == null) {
			comments = new ArrayList<>();
			commentsMap.put(report, comments);
			for (long i = 0; i < Math.round(Math.random() * 3); i++) {
				comments.add(generateComment(report));
			}
		}
		return comments;
	}

	private static Comment generateComment(Report report) {
		Comment comment = new Comment(USER_NAME_HANK, new Date(),
				"Generated comment for report: " + report.getSummary() + "\n \n" + "some more text");

		return comment;
	}
}
