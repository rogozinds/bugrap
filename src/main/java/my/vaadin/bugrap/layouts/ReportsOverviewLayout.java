package my.vaadin.bugrap.layouts;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;

import com.vaadin.data.provider.GridSortOrderBuilder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.renderers.HtmlRenderer;
import my.vaadin.bugrap.ReportsOverview;
import my.vaadin.bugrap.events.UpdateReportDetailsEvent;
import my.vaadin.bugrap.utils.PriorityHtmlProvider;
import my.vaadin.bugrap.utils.RelativeDateUtils;

public class ReportsOverviewLayout extends ReportsOverview {
	private enum ReportColumn {
		VERSION("version", "VERSION"), PRIORITY("priority", "PRIORITY"), TYPE("type", "TYPE"), SUMMARY("summary",
			"SUMMARY"), ASSIGNEDTO("assignedTo",
			"ASSIGNED TO"), LASTMODIFIED("lastModified", "LAST MODIFIED"), REPORTED("reported", "REPORTED");

		private String id;
		private String caption;

		private ReportColumn(String id, String caption) {
			this.id = id;
			this.caption = caption;
		}
	}
	private ListDataProvider<Report> dp;
	private Listener updateListener;

	private CustomStatusPopupContent content;

	ReportsOverviewLayoutPresenter presenter;

	//Here can be an interface ReportsOverviewLayoutPresenterInterface so the view will be separated from presenter
	public void setPresenter(ReportsOverviewLayoutPresenter presenter) {

	}
	public ReportsOverviewLayout() {
		super();
		//set default presenter;
		this.presenter = new ReportsOverviewLayoutPresenter(this);
		init();
	}

	private void initBasicData() {
		accountBtn.setCaption(presenter.getReporter().getName());
		logoutBtn.addClickListener(e->{
			presenter.logout();
		});
	}
	public ProjectVersion getCurrentVersion() {
		return versionSelector.getValue();
	}
	public void setCurrentVersion(ProjectVersion version){
		boolean allVersionsSelected = version.getVersion().equals(ReportsOverviewLayoutPresenter.ALL_VERSIONS);
		reportsGrid.getColumn(ReportColumn.VERSION.id).setHidden(!allVersionsSelected);
		setSortOrder(allVersionsSelected);
		updateDistributionBar();
		updateData();

	}
	private void init() {
		initBasicData();

		initFiltersButtons();
		initReportsTable();
		versionSelector.addSelectionListener(event ->{
				if (event.getValue() == null)
					return;
				presenter.selectVersion(event.getValue());
		});

		projectSelector.addSelectionListener(event -> {
				updateVersions(event.getValue());
		});

		initProjects();
	}

	private void setSortOrder(boolean sortByVersion) {
		GridSortOrderBuilder<Report> builder = new GridSortOrderBuilder<>();

		if (sortByVersion)
			builder.thenAsc(reportsGrid.getColumn(ReportColumn.VERSION.id));
		builder.thenDesc(reportsGrid.getColumn(ReportColumn.PRIORITY.id));

		reportsGrid.setSortOrder(builder);
	}

	private void updateDistributionBar() {
		ProjectVersion projectVersion = versionSelector.getValue();
		if (projectVersion == null)
			return;
		int[] reportCounts = presenter.getReportCounts(projectVersion);
		distributionBar.setValues(reportCounts);
	}

	private void showReportDetails(Set<Report> allSelectedItems) {
		if (allSelectedItems == null || allSelectedItems.size() == 0) {
			mainSplitter.setLocked(true);
			mainSplitter.setSplitPosition(100, Unit.PERCENTAGE);
			mainSplitter.removeComponent(mainSplitter.getSecondComponent());
			return;
		}
		if (allSelectedItems.size() == 1) {
			ReportDetailsLayout reportDetails = new ReportDetailsLayout();
			if (mainSplitter.getSecondComponent() instanceof ReportDetailsLayout)
				reportDetails = (ReportDetailsLayout) mainSplitter.getSecondComponent();
			else {
				reportDetails = new ReportDetailsLayout();
				reportDetails.addUpdateListener(updateListener);
				mainSplitter.setSecondComponent(reportDetails);
			}

			reportDetails.setReport(allSelectedItems.iterator().next());
			mainSplitter.setLocked(false);
			mainSplitter.setSplitPosition(50, Unit.PERCENTAGE);

		} else {
			ReportPropertiesLayout reportProperties;
			if (mainSplitter.getSecondComponent() instanceof ReportPropertiesLayout)
				reportProperties = (ReportPropertiesLayout) mainSplitter.getSecondComponent();
			else {
				reportProperties = new ReportPropertiesLayout();
				reportProperties.addListener(updateListener);

				mainSplitter.setSecondComponent(reportProperties);
			}
			reportProperties.setReports(allSelectedItems);

			mainSplitter.setLocked(true);
			mainSplitter.setSplitPosition(reportProperties.getHeight(), reportProperties.getHeightUnits(), true);
		}
	}

	private void initFiltersButtons() {
		onlyMeBtn.setEnabled(false);
		openBtn.setEnabled(false);
		onlyMeBtn.addClickListener(e ->{
				everyoneBtn.setEnabled(true);
				updateData();
		});
		everyoneBtn.addClickListener(e ->{
				onlyMeBtn.setEnabled(true);
				updateData();
		});
		openBtn.addClickListener(e-> {
				allKindsBtn.setEnabled(true);
				customBtn.removeStyleName("toggled");
				updateData();
		});

		allKindsBtn.addClickListener(e-> {
			openBtn.setEnabled(true);
			customBtn.removeStyleName("toggled");
			updateData();
		});

		content = new CustomStatusPopupContent() {
			@Override
			public void changeAction() {
				updateData();
			}
		};
		final PopupView customStatusPopup = new PopupView(content);
		customContainer.addComponent(customStatusPopup);
		customBtn.addClickListener(event -> {
			if (allKindsBtn.isEnabled() && openBtn.isEnabled()) {
				customStatusPopup.setPopupVisible(true);
				return;
			}
			allKindsBtn.setEnabled(true);
			openBtn.setEnabled(true);
			customBtn.addStyleName("toggled");
			updateData();
			customStatusPopup.setPopupVisible(true);
		});

	}

	@SuppressWarnings({ "unchecked", "serial" })
	private void initReportsTable() {
		setupColumns();
		reportsGrid.setSelectionMode(SelectionMode.MULTI);
		reportsGrid.addShortcutListener(new ShortcutListener("enterPress", ShortcutAction.KeyCode.ENTER, null) {

			@Override
			public void handleAction(Object sender, Object target) {
				if (!reportsGrid.getSelectedItems().isEmpty())
					presenter.openReport(reportsGrid.getSelectedItems().iterator().next());
			}
		});

		reportsGrid.addItemClickListener(event -> {
			if (event.getMouseEventDetails().isDoubleClick()) {
				reportsGrid.deselectAll();
				presenter.openReport(event.getItem());
			}
		});

		updateListener = new Listener() {

			@Override
			public void componentEvent(Event event) {
				if (!(event instanceof UpdateReportDetailsEvent))
					return;

				for (Report item : ((UpdateReportDetailsEvent) event).getReports())
					dp.refreshItem(item);
				updateDistributionBar();
			}
		};

		reportsGrid.addSelectionListener(event ->  {
				showReportDetails(event.getAllSelectedItems());
		});

		dp = new ListDataProvider<>(Collections.emptyList());
		reportsGrid.setDataProvider(dp);

		setSortOrder(false);
	}

	private void setupColumns() {
		reportsGrid.addColumn(Report::getVersion).setCaption(ReportColumn.VERSION.caption)
				.setId(ReportColumn.VERSION.id).setWidth(200);

		reportsGrid.addColumn(PriorityHtmlProvider.fromReport(), new HtmlRenderer())
				.setCaption(ReportColumn.PRIORITY.caption).setId(ReportColumn.PRIORITY.id).setWidth(110);

		reportsGrid.addColumn(Report::getType).setCaption(ReportColumn.TYPE.caption).setId(ReportColumn.TYPE.id)
				.setWidth(120);

		reportsGrid.addColumn(Report::getSummary).setCaption(ReportColumn.SUMMARY.caption)
				.setId(ReportColumn.SUMMARY.id);

		reportsGrid.addColumn(Report::getAssigned).setCaption(ReportColumn.ASSIGNEDTO.caption)
				.setId(ReportColumn.ASSIGNEDTO.id).setWidth(200);

		reportsGrid.addColumn(Report::getTimestamp, RelativeDateUtils.getRelativeDateRenderer())
				.setCaption(ReportColumn.LASTMODIFIED.caption).setId(ReportColumn.LASTMODIFIED.id).setWidth(150);

		reportsGrid.addColumn(Report::getReportedTimestamp, RelativeDateUtils.getRelativeDateRenderer())
				.setCaption(ReportColumn.REPORTED.caption).setId(ReportColumn.REPORTED.id).setWidth(150);

	}

	private void initProjects() {
		projectSelector.clear();
		Set<Project> projects = presenter.getProjects();
		if (projects == null || projects.isEmpty()) {
			projectCountLbl.setValue("0");
			return;
		}
		projectSelector.setItems(projects);
		projectSelector.setSelectedItem(presenter.getCurrentProject());
		projectCountLbl.setValue("" + projects.size());
	}

	private void updateVersions(Project project) {
		versionSelector.clear();
		if (project == null)
			return;
		List<ProjectVersion> versionsList = presenter.getVersions(project);
		versionSelector.setItems(versionsList);
		//TODO put that logic to presenter
		//setVersionOnOpen();
	}


	private void updateData() {
		Set <Report> reports = presenter.getReports(
			everyoneBtn.isEnabled(),
			openBtn.isEnabled(),
			allKindsBtn.isEnabled(),
			content.getSelectedItems());
		dp = new ListDataProvider<>(reports);
		reportsGrid.setDataProvider(dp);
	}

	//	private void setVersionOnOpen() {
	//	TODO put this logic to presenter
	//				ProjectVersion versionToSelect = getVersionByName(getVersionFromCookie(), versionsList);
	//				if (!versionsList.contains(versionToSelect))
	//					versionToSelect = versionsList.get(0);
	//				versionSelector.setSelectedItem(versionToSelect);
	//	}
	//	private ProjectVersion getVersionByName(String name, List<ProjectVersion> versions) {
	//		if (name == null)
	//			return null;
	//
	//		return versions.stream().filter(v -> name.equals(v.getVersion())).findFirst().orElse(null);
	//	}

	//	private String getVersionFromCookie() {
	//		Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
	//		if (cookies == null)
	//			return "";
	//		for (Cookie cookie : cookies) {
	//			if (COOKIE_VERSION.equals(cookie.getName())) {
	//				return cookie.getValue();
	//			}
	//		}
	//		return "";
	//	}

}
