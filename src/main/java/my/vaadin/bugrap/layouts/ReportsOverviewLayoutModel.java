package my.vaadin.bugrap.layouts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Cookie;

import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.BugrapRepository.ReportsQuery;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Report.Status;
import org.vaadin.bugrap.domain.entities.Reporter;

import com.vaadin.server.VaadinService;
import my.vaadin.bugrap.AppGlobalData;
import my.vaadin.bugrap.ReportsProviderService;
import my.vaadin.bugrap.utils.BugrapWindowOpener;

public class ReportsOverviewLayoutModel {
	private static final String COOKIE_VERSION = "bugrap-version";
	private static final String ALL_VERSIONS = "All versions";
	private Project currentProject;

	public Set<Report> getReports(boolean filterEveryone, boolean isOpen, boolean isAllKinds, Set<Status> statuses) {
		return dataSource().findReports( getQuery(filterEveryone, isOpen, isAllKinds, statuses));
	}

	public void setCurrentProject(Project currentProject) {
		this.currentProject = currentProject;
	}

	public Project getCurrentProject() {
		return currentProject;
	}

	public Set<Project> getProjects() {
		return dataSource().findProjects();
	}

	public List<ProjectVersion> getVersions(Project project) {
		List<ProjectVersion> versionsList = new ArrayList<ProjectVersion>(dataSource().findProjectVersions(project));

		if (versionsList.size() > 1)
			versionsList.add(0, allVersions);
		return new ArrayList<ProjectVersion>(dataSource().findProjectVersions(project));
	}

	public int[] getReportCounts(ProjectVersion projectVersion) {
		int[] reportCounts= new int [3];
		if (projectVersion == allVersions) {
			reportCounts[0] = (int) dataSource().countClosedReports(currentProject);
			reportCounts[1] = (int) dataSource().countOpenedReports(currentProject);
			reportCounts[2] = (int) dataSource().countUnassignedReports(currentProject);
		} else {
			reportCounts[0] = (int) dataSource().countClosedReports(projectVersion);
			reportCounts[1] = (int) dataSource().countOpenedReports(projectVersion);
			reportCounts[2] = (int) dataSource().countUnassignedReports(projectVersion);
		}
		return reportCounts;
	}

	private Reporter currentReporter;
	private ProjectVersion currentVersion;
	private ProjectVersion allVersions;
	public ReportsOverviewLayoutModel() {
		super();
		init();
	}
	public void logout(){
		AppGlobalData.getUserData().setCurrentUser(null);
		BugrapWindowOpener.openLogin();
	}
	public Reporter getReporter() {
		return currentReporter;
	}
	private void initBasicData() {
		currentReporter = AppGlobalData.getUserData().getCurrentUser();
		if (currentReporter == null) {
			currentReporter = new Reporter();
			currentReporter.setName("undefined");
		}

		currentVersion = new ProjectVersion();
		currentVersion.setVersion(ALL_VERSIONS);

	}
	public ProjectVersion getCurrentVersion() {
		return currentVersion;
	}
	private void init() {
		allVersions = new ProjectVersion();
		allVersions.setVersion(ALL_VERSIONS);
		initBasicData();
	}

	protected void openReport(Report item) {
		BugrapWindowOpener.openReport(item);
	}

	public void saveVersionToCookie(String value) {
		final Cookie versionCookie = new Cookie(COOKIE_VERSION, value);
		versionCookie.setPath(VaadinService.getCurrentRequest().getContextPath());
		VaadinService.getCurrentResponse().addCookie(versionCookie);
	}


	private ReportsQuery getQuery(boolean filterEveryone, boolean isOpen, boolean isAllKinds, Set<Status> statuses) {
		ReportsQuery query = new ReportsQuery();
		query.project = getCurrentProject();
		query.projectVersion = getCurrentVersion();
		if (filterEveryone)
			query.reportAssignee = currentReporter;

		if (isOpen)
			query.reportStatuses = Collections.singleton(Status.OPEN);
		else if (!isAllKinds)
			query.reportStatuses = null;
		else
			query.reportStatuses = statuses;
		return query;
	}

	private BugrapRepository dataSource() {
		return ReportsProviderService.get();
	}
}
