package my.vaadin.bugrap.layouts;

import java.util.List;
import java.util.Set;

import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Report.Status;
import org.vaadin.bugrap.domain.entities.Reporter;

import my.vaadin.bugrap.ReportsProviderService;
import my.vaadin.bugrap.utils.BugrapWindowOpener;

public class ReportsOverviewLayoutPresenter {

	public static final String COOKIE_VERSION = "bugrap-version";
	public static final String ALL_VERSIONS = "All versions";

	public void selectVersion(ProjectVersion version) {
		model.saveVersionToCookie(version.getVersion());
		view.setCurrentVersion(version);
	}
	public void setCurrentProject(Project project) {
		model.setCurrentProject(project);
	}
	public Set<Project> getProjects() {
		return model.getProjects();
	}
	public Set<Report> getReports(boolean filterEveryone, boolean isOpen, boolean isAllKinds, Set<Status> statuses) {
		return model.getReports(filterEveryone, isOpen, isAllKinds, statuses);
	}

	public Project getCurrentProject() {
		return model.getCurrentProject();
	}
	public List<ProjectVersion> getVersions(Project project) {
		return model.getVersions(project);
	}

	public int[] getReportCounts(ProjectVersion projectVersion) {
		return model.getReportCounts(projectVersion);
	}


	private ReportsOverviewLayout view = null;
	private ReportsOverviewLayoutModel model;
	public ReportsOverviewLayoutPresenter(ReportsOverviewLayout view) {
		super();
		this.model = new ReportsOverviewLayoutModel();
		this.view = view;
	}
	public void logout() {
		model.logout();
	}
	public Reporter getReporter() {
		return model.getReporter();
	}




	protected void openReport(Report item) {
		BugrapWindowOpener.openReport(item);
	}

	private BugrapRepository dataSource() {
		return ReportsProviderService.get();
	}
}
