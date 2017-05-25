package my.vaadin.bugrap.layouts;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.Column;

import my.vaadin.bugrap.Report;
import my.vaadin.bugrap.Report.Status;
import my.vaadin.bugrap.ReportProvider;
import my.vaadin.bugrap.ReportsOverview;

public class ReportsOverviewLayout extends ReportsOverview {

	private static final String ALL_VERSIONS = "All versions";

	private ListDataProvider<Report> dp;

	public ReportsOverviewLayout() {
		super();

		init();
	}

	private void init() {
		accountBtn.setCaption(ReportProvider.USER_NAME);
		initFiltersButtons();
		initReportsTable();
		versionSelector.addSelectionListener(new SingleSelectionListener<String>() {

			@Override
			public void selectionChange(SingleSelectionEvent<String> event) {
				updateData();
			}
		});

		projectSelector.addSelectionListener(new SingleSelectionListener<String>() {

			@Override
			public void selectionChange(SingleSelectionEvent<String> event) {
				updateVersions();
			}

		});

		updateProjects();
		// projectSelector.setSelectedItem("Project 1");

		distributionBar.setValues(new int[] { 5, 15, 100 });
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
				customBtn.setEnabled(true);
				updateData();
			}
		});
		allKindsBtn.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				openBtn.setEnabled(true);
				customBtn.setEnabled(true);
				updateData();
			}
		});
		customBtn.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				allKindsBtn.setEnabled(true);
				openBtn.setEnabled(true);
				updateData();
			}
		});
	}

	@SuppressWarnings({ "unchecked", "serial" })
	private void initReportsTable() {
		// DataProvider dataProvider = new ;
		// reportsTable.setDataProvider(dataProvider );

		reportsGrid.addSelectionListener(new SelectionListener<Report>() {

			@Override
			public void selectionChange(SelectionEvent<Report> event) {

			}
		});

		dp = new ListDataProvider<>(ReportProvider.getAllReports());
		reportsGrid.setDataProvider(dp);

		((Column<Report, Date>) reportsGrid.getColumn("reported")).setRenderer(new RelativeDateRenderer());
		((Column<Report, Date>) reportsGrid.getColumn("lastModified")).setRenderer(new RelativeDateRenderer());
	}

	private void updateProjects() {
		projectSelector.clear();
		Set<String> projectNames = new LinkedHashSet<>();
		dp.getItems().stream().forEach(a -> projectNames.add(a.getProject()));
		projectSelector.setItems(projectNames);
		projectSelector.setSelectedItem(projectNames.iterator().next());
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
		versionSelector.setSelectedItem(versionsList.get(0));
	}

	private void addVersion(Report a, String projectName, Set<String> versions) {
		if (a.getProject().equals(projectName))
			versions.add(a.getVersion());
	}

	private void updateData() {
		if (reportsGrid.getColumn("version") != null && (versionSelector.getValue() != null))
			reportsGrid.getColumn("version").setHidden(!versionSelector.getValue().equals(ALL_VERSIONS));

		distributionBar.setValues(new int[] { (int) Math.round((Math.random() * 100)),
				(int) Math.round((Math.random() * 100)), (int) Math.round((Math.random() * 100)) });

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

		return false;
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
