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

import com.vaadin.data.provider.GridSortOrderBuilder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.ItemClick;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.components.grid.ItemClickListener;
import com.vaadin.ui.renderers.HtmlRenderer;

import my.vaadin.bugrap.AppGlobalData;
import my.vaadin.bugrap.ReportsOverview;
import my.vaadin.bugrap.ReportsProviderService;
import my.vaadin.bugrap.events.UpdateReportDetailsEvent;
import my.vaadin.bugrap.utils.BugrapWindowOpener;
import my.vaadin.bugrap.utils.PriorityHtmlProvider;
import my.vaadin.bugrap.utils.RelativeDateUtils;

public class ReportsOverviewLayout extends ReportsOverview {

	private static final String COOKIE_VERSION = "bugrap-version";
	private static final String ALL_VERSIONS = "All versions";

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
	private Reporter me;
	private ProjectVersion allVersions;

	public ReportsOverviewLayout() {
		super();

		init();
	}

	private void initBasicData() {
		me = AppGlobalData.getUserData().getCurrentUser();
		if (me == null) {
			me = new Reporter();
			me.setName("undefined");
		}

		accountBtn.setCaption(me.getName());

		allVersions = new ProjectVersion();
		allVersions.setVersion(ALL_VERSIONS);

		logoutBtn.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				AppGlobalData.getUserData().setCurrentUser(null);
				BugrapWindowOpener.openLogin();
			}
		});
	}

	private void init() {
		initBasicData();

		initFiltersButtons();
		initReportsTable();
		versionSelector.addSelectionListener(new SingleSelectionListener<ProjectVersion>() {

			@Override
			public void selectionChange(SingleSelectionEvent<ProjectVersion> event) {
				if (event.getValue() == null)
					return;
				saveVersionToCookie(event.getValue().getVersion());
				boolean allVersionsSelected = versionSelector.getValue() == allVersions;
				reportsGrid.getColumn(ReportColumn.VERSION.id).setHidden(!allVersionsSelected);
				setSortOrder(allVersionsSelected);

				updateDistributionBar();
				updateData();
			}
		});

		projectSelector.addSelectionListener(new SingleSelectionListener<Project>() {

			@Override
			public void selectionChange(SingleSelectionEvent<Project> event) {
				updateVersions(event.getValue());
			}

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
		int[] a = new int[3];
		ProjectVersion projectVersion = versionSelector.getValue();
		if (projectVersion == null)
			return;

		if (projectVersion == allVersions) {
			Project prj = projectSelector.getValue();
			a[0] = (int) dataSource().countClosedReports(prj);
			a[1] = (int) dataSource().countOpenedReports(prj);
			a[2] = (int) dataSource().countUnassignedReports(prj);
		} else {
			a[0] = (int) dataSource().countClosedReports(projectVersion);
			a[1] = (int) dataSource().countOpenedReports(projectVersion);
			a[2] = (int) dataSource().countUnassignedReports(projectVersion);
		}
		distributionBar.setValues(a);
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
		onlyMeBtn.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				everyoneBtn.setEnabled(true);
				updateData();
			}
		});
		everyoneBtn.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				onlyMeBtn.setEnabled(true);
				updateData();
			}
		});
		openBtn.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				allKindsBtn.setEnabled(true);
				customBtn.removeStyleName("toggled");
				updateData();
			}
		});
		allKindsBtn.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				openBtn.setEnabled(true);
				customBtn.removeStyleName("toggled");
				updateData();
			}
		});

		content = new CustomStatusPopupContent() {
			@Override
			public void changeAction() {
				updateData();
			}
		};
		final PopupView customStatusPopup = new PopupView(content);
		customContainer.addComponent(customStatusPopup);
		customBtn.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (allKindsBtn.isEnabled() && openBtn.isEnabled()) {
					customStatusPopup.setPopupVisible(true);
					return;
				}
				allKindsBtn.setEnabled(true);
				openBtn.setEnabled(true);
				customBtn.addStyleName("toggled");
				updateData();
				customStatusPopup.setPopupVisible(true);

			}
		});

	}

	@SuppressWarnings({ "unchecked", "serial" })
	private void initReportsTable() {
		setupColumns();

		reportsGrid.setSelectionMode(SelectionMode.MULTI);
		// reportsGrid.getSelectionModel().setUserSelectionAllowed(false);
		//
		// gridContainer.addShortcutListener(new ShortcutListener("upPress2",
		// ShortcutAction.KeyCode.ARROW_UP, null) {
		// @Override
		// public void handleAction(Object sender, Object target) {
		// System.out.println("ReportsOverviewLayout" + target.getClass());
		// JavaScript.eval("window.$(\".v-grid-cell
		// .v-grid-cell-focused\").click();");
		// }
		// });
		//
		// reportsGrid.addShortcutListener(new ShortcutListener("upPress3",
		// ShortcutAction.KeyCode.ARROW_UP, null) {
		// @Override
		// public void handleAction(Object sender, Object target) {
		// System.out.println("ReportsOverviewLayout" + target.getClass());
		// JavaScript.eval("window.$(\".v-grid-cell
		// .v-grid-cell-focused\").click();");
		// }
		// });
		//
		// addShortcutListener(new ShortcutListener("upPress4",
		// ShortcutAction.KeyCode.ARROW_UP, null) {
		// @Override
		// public void handleAction(Object sender, Object target) {
		// System.out.println("ReportsOverviewLayout" + target.getClass());
		// JavaScript.eval("window.$(\".v-grid-cell
		// .v-grid-cell-focused\").click();");
		// }
		// });
		//
		// mainSplitter.addShortcutListener(new ShortcutListener("upPress5",
		// ShortcutAction.KeyCode.ARROW_UP, null) {
		// @Override
		// public void handleAction(Object sender, Object target) {
		// System.out.println("ReportsOverviewLayout" + target.getClass());
		// JavaScript.eval("window.$(\".v-grid-cell
		// .v-grid-cell-focused\").click();");
		// }
		// });
		//
		// reportsGrid.addShortcutListener(new ShortcutListener("downPress",
		// ShortcutAction.KeyCode.ARROW_DOWN, null) {
		//
		// @Override
		// public void handleAction(Object sender, Object target) {
		// System.out.println(
		// "ReportsOverviewLayout" + target.getClass());
		// JavaScript
		// .eval("if(!window.(\".v-grid-body .v-grid-row-focused
		// .v-grid-row-selected\").length){window.(\".v-grid-body
		// .v-grid-cell-focused\").click();}"
		// + "");
		// }
		// });

		reportsGrid.addShortcutListener(new ShortcutListener("enterPress", ShortcutAction.KeyCode.ENTER, null) {

			@Override
			public void handleAction(Object sender, Object target) {
				if (!reportsGrid.getSelectedItems().isEmpty())
					openReport(reportsGrid.getSelectedItems().iterator().next());
			}
		});

		reportsGrid.addItemClickListener(new ItemClickListener<Report>() {

			@Override
			public void itemClick(ItemClick<Report> event) {
				if (event.getMouseEventDetails().isDoubleClick()) {
					reportsGrid.deselectAll();
					openReport(event.getItem());
					return;
				}
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

		reportsGrid.addSelectionListener(new SelectionListener<Report>() {

			@Override
			public void selectionChange(SelectionEvent<Report> event) {
				showReportDetails(event.getAllSelectedItems());
			}
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

	protected void openReport(Report item) {
		BugrapWindowOpener.openReport(item);
	}

	private void initProjects() {
		projectSelector.clear();
		Set<Project> projectNames = dataSource().findProjects();
		if (projectNames == null || projectNames.isEmpty()) {
			projectCountLbl.setValue("0");
			return;
		}
		projectSelector.setItems(projectNames);
		projectSelector.setSelectedItem(projectNames.iterator().next());
		projectCountLbl.setValue("" + projectNames.size());
	}

	private void updateVersions(Project project) {
		versionSelector.clear();
		if (project == null)
			return;

		List<ProjectVersion> versionsList = new ArrayList<>(dataSource().findProjectVersions(project));
		if (versionsList.size() > 1)
			versionsList.add(0, allVersions);

		versionSelector.setItems(versionsList);
		ProjectVersion versionToSelect = getVersionByName(getVersionFromCookie(), versionsList);
		if (!versionsList.contains(versionToSelect))
			versionToSelect = versionsList.get(0);
		versionSelector.setSelectedItem(versionToSelect);
	}

	private ProjectVersion getVersionByName(String name, List<ProjectVersion> versions) {
		if (name == null)
			return null;

		return versions.stream().filter(v -> name.equals(v.getVersion())).findFirst().orElse(null);
	}

	private String getVersionFromCookie() {
		Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
		if (cookies == null)
			return "";
		for (Cookie cookie : cookies) {
			if (COOKIE_VERSION.equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return "";
	}

	private void saveVersionToCookie(String value) {
		final Cookie versionCookie = new Cookie(COOKIE_VERSION, value);
		versionCookie.setPath(VaadinService.getCurrentRequest().getContextPath());

		VaadinService.getCurrentResponse().addCookie(versionCookie);

		// Page.getCurrent().getJavaScript().execute(String.format("document.cookie
		// = '%s=%s;';", COOKIE_VERSION, value));
	}

	private void updateData() {
		// reportsGrid.deselectAll();
		dp = new ListDataProvider<>(dataSource().findReports(getQuery()));
		reportsGrid.setDataProvider(dp);
	}

	private ReportsQuery getQuery() {
		ReportsQuery query = new ReportsQuery();
		query.project = projectSelector.getValue();
		if (versionSelector.getValue() != allVersions)
			query.projectVersion = versionSelector.getValue();

		if (everyoneBtn.isEnabled())
			query.reportAssignee = me;

		if (!openBtn.isEnabled())
			query.reportStatuses = Collections.singleton(Status.OPEN);
		else if (!allKindsBtn.isEnabled())
			query.reportStatuses = null;
		else
			query.reportStatuses = content.getSelectedItems();

		return query;
	}

	private BugrapRepository dataSource() {
		return ReportsProviderService.get();
	}
}
