package my.vaadin.bugrap.layouts;

import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;

import my.vaadin.bugrap.ReportsOverview;

public class ReportsOverviewLayout extends ReportsOverview {

	public ReportsOverviewLayout() {
		super();

		init();
	}

	private void init() {
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
				updateLayout();
			}

		});

		updateProjects();
		projectSelector.setSelectedItem("Project 1");
		updateVersions();

		distributionBar.setValues(new int[] { 5, 15, 100 });
	}

	private void updateProjects() {
		projectSelector.clear();
		projectSelector.setItems("Project 1", "Project 2", "Project 3");
	}

	private void updateVersions() {
		versionSelector.clear();
		String projectName = projectSelector.getValue();
		versionSelector.setItems(projectName + " version 1", projectName + " version 2", projectName + " version 3");
	}

	private void updateLayout() {
		distributionBar.setValues(new int[] { (int) Math.round((Math.random() * 100)),
				(int) Math.round((Math.random() * 100)), (int) Math.round((Math.random() * 100)) });
	}
}
