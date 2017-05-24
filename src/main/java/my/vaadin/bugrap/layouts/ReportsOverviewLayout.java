package my.vaadin.bugrap.layouts;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.server.SerializablePredicate;

import my.vaadin.bugrap.Report;
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
		initReportsTable();
		versionSelector.addSelectionListener(new SingleSelectionListener<String>() {

			@Override
			public void selectionChange(SingleSelectionEvent<String> event) {
				updateLayout();
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

	@SuppressWarnings({ "unchecked", "serial" })
	private void initReportsTable() {
		// DataProvider dataProvider = new ;
		// reportsTable.setDataProvider(dataProvider );
		reportsGrid.getColumn("project").setHidden(true);
		// reportsGrid.getColumn("status").setHidden(true);

		reportsGrid.addSelectionListener(new SelectionListener<Report>() {

			@Override
			public void selectionChange(SelectionEvent<Report> event) {

			}
		});

		dp = new ListDataProvider<>(ReportProvider.getAllReports());
		reportsGrid.setDataProvider(dp);
	}

	private void updateProjects() {
		projectSelector.clear();
		Set<String> projectNames = new LinkedHashSet<>();
		dp.getItems().stream().forEach(a -> projectNames.add(a.getProject()));
		projectSelector.setItems(projectNames);
		projectSelector.setSelectedItem(projectNames.iterator().next());
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

	private void updateLayout() {
		if (reportsGrid.getColumn("version") != null && (versionSelector.getValue() != null))
			reportsGrid.getColumn("version").setHidden(!versionSelector.getValue().equals(ALL_VERSIONS));
		distributionBar.setValues(new int[] { (int) Math.round((Math.random() * 100)),
				(int) Math.round((Math.random() * 100)), (int) Math.round((Math.random() * 100)) });

		dp.setFilter(getFilter());
	}

	private SerializablePredicate<Report> getFilter() {
		final String projectName = projectSelector.getValue();
		final String version = versionSelector.getValue();

		SerializablePredicate<Report> filter = new SerializablePredicate<Report>() {

			@Override
			public boolean test(Report t) {
				if (version.equals(ALL_VERSIONS))
					return t.getProject().equals(projectName);
				return t.getProject().equals(projectName) && t.getVersion().equals(version);
			}

		};

		return filter;
	}
}
