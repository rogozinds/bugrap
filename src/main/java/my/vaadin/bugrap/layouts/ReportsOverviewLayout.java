package my.vaadin.bugrap.layouts;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Cookie;

import com.vaadin.data.provider.GridSortOrderBuilder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.ItemClick;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.components.grid.ItemClickListener;

import my.vaadin.bugrap.Report;
import my.vaadin.bugrap.Report.Status;
import my.vaadin.bugrap.ReportsOverview;
import my.vaadin.bugrap.ReportsProviderService;
import my.vaadin.bugrap.utils.RelativeDateRenderer;

public class ReportsOverviewLayout extends ReportsOverview {

	private static final String COOKIE_VERSION = "bugrap-version";
	private static final String ALL_VERSIONS = "All versions";

	private static final String COLUMN_VERSION = "version";
	private static final String COLUMN_PRIORITY = "priority";

	private ListDataProvider<Report> dp;

	private CustomStatusPopupContent content;

	public ReportsOverviewLayout() {
		super();

		init();
	}

	private void init() {
		mainSplitter.setMinSplitPosition(25, Unit.PERCENTAGE);

		accountBtn.setCaption(ReportsProviderService.USER_NAME);
		initFiltersButtons();
		initReportsTable();
		versionSelector.addSelectionListener(new SingleSelectionListener<String>() {

			@Override
			public void selectionChange(SingleSelectionEvent<String> event) {
				saveVersionToCookie(event.getValue());
				if (reportsGrid.getColumn(COLUMN_VERSION) != null && (versionSelector.getValue() != null)) {
					boolean allVersionsSelected = versionSelector.getValue().equals(ALL_VERSIONS);
					reportsGrid.getColumn(COLUMN_VERSION).setHidden(!allVersionsSelected);
					setSortOrder(allVersionsSelected);
				}

				updateDistributionBar();
				updateData();
			}
		});

		projectSelector.addSelectionListener(new SingleSelectionListener<String>() {

			@Override
			public void selectionChange(SingleSelectionEvent<String> event) {
				updateVersions();
			}

		});

		initProjects();
	}

	private void setSortOrder(boolean sortByVersion) {
		GridSortOrderBuilder<Report> builder = new GridSortOrderBuilder<>();

		if (sortByVersion)
			builder.thenAsc(reportsGrid.getColumn(COLUMN_VERSION));
		builder.thenDesc(reportsGrid.getColumn(COLUMN_PRIORITY));

		reportsGrid.setSortOrder(builder);
	}

	private void updateDistributionBar() {
		// TODO:
		distributionBar.setValues(new int[] { (int) Math.round((Math.random() * 100)),
				(int) Math.round((Math.random() * 100)), (int) Math.round((Math.random() * 100)) });
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
				mainSplitter.setSecondComponent(reportDetails);
			}

			reportDetails.setReports(allSelectedItems);
			mainSplitter.setLocked(false);
			mainSplitter.setSplitPosition(50, Unit.PERCENTAGE);

		} else {
			ReportPropertiesLayout reportProperties;
			if (mainSplitter.getSecondComponent() instanceof ReportPropertiesLayout)
				reportProperties = (ReportPropertiesLayout) mainSplitter.getSecondComponent();
			else {
				reportProperties = new ReportPropertiesLayout();
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
		// reportsGrid.getSelectionModel().
		reportsGrid.setSelectionMode(SelectionMode.MULTI);
		reportsGrid.getSelectionModel().setUserSelectionAllowed(false);
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

				if (!reportsGrid.getSelectionModel().isSelected(event.getItem())) {
					if (!event.getMouseEventDetails().isCtrlKey())
						reportsGrid.deselectAll();
					reportsGrid.select(event.getItem());
				} else {
					if (!event.getMouseEventDetails().isCtrlKey())
						reportsGrid.deselectAll();
					else
						reportsGrid.deselect(event.getItem());
				}
			}
		});
		reportsGrid.addSelectionListener(new SelectionListener<Report>() {

			@Override
			public void selectionChange(SelectionEvent<Report> event) {
				showReportDetails(event.getAllSelectedItems());
			}
		});

		dp = new ListDataProvider<>(ReportsProviderService.getAllReports());
		reportsGrid.setDataProvider(dp);

		setSortOrder(false);

		((Column<Report, Date>) reportsGrid.getColumn("reported")).setRenderer(new RelativeDateRenderer());
		((Column<Report, Date>) reportsGrid.getColumn("lastModified")).setRenderer(new RelativeDateRenderer());
	}

	protected void openReport(Report item) {
		System.out.println("ReportsOverviewLayout.openReport()");
	}

	private void initProjects() {
		projectSelector.clear();
		List<String> projectNames = ReportsProviderService.getProjectNames();
		if (projectNames == null || projectNames.isEmpty()) {
			projectCountLbl.setValue("0");
			return;
		}
		projectSelector.setItems(projectNames);
		projectSelector.setSelectedItem(projectNames.get(0));
		projectCountLbl.setValue("" + projectNames.size());
	}

	private void updateVersions() {
		versionSelector.clear();
		String projectName = projectSelector.getValue();
		Set<String> versions = new LinkedHashSet<>();
		dp.getItems().stream().forEach(a -> addVersion(a, projectName, versions));
		List<String> versionsList = new ArrayList<>(versions);
		if (versionsList.size() > 1)
			versionsList.add(0, ALL_VERSIONS);
		versionSelector.setItems(versionsList);
		String versionToSelect = getVersionFromCookie();
		if (!versionsList.contains(versionToSelect))
			versionToSelect = versionsList.get(0);
		versionSelector.setSelectedItem(versionToSelect);
	}

	private String getVersionFromCookie() {
		Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
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

	private void addVersion(Report a, String projectName, Set<String> versions) {
		if (a.getProject().equals(projectName))
			versions.add(a.getVersion());
	}

	private void updateData() {
		reportsGrid.deselectAll();
		dp.setFilter(getFilter());
	}

	private SerializablePredicate<Report> getFilter() {
		return new SerializablePredicate<Report>() {

			@Override
			public boolean test(Report t) {
				return testProject(t) && testVersion(t) && testUser(t) && testStatus(t);
			}

		};
	}

	private boolean testStatus(Report t) {
		if (!openBtn.isEnabled())
			return t.getStatus() == Status.OPEN;

		if (!allKindsBtn.isEnabled())
			return true;

		return content.getSelectedItems().contains(t.getStatus());
	}

	private boolean testProject(Report t) {
		return t.getProject().equals(projectSelector.getValue());
	}

	private boolean testVersion(Report t) {
		if (versionSelector.getValue().equals(ALL_VERSIONS))
			return true;

		return t.getVersion().equals(versionSelector.getValue());
	}

	private boolean testUser(Report t) {
		if (!everyoneBtn.isEnabled())
			return true;
		return (accountBtn.getCaption() != null) && accountBtn.getCaption().equals(t.getAssignedTo());
	}
}
