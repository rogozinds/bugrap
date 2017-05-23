package my.vaadin.bugrap.layouts;

import java.util.List;

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

			}
		});

		fillVersionSelector(null);

	}

	private void fillVersionSelector(List<String> values) {
		versionSelector.clear();
		 versionSelector.setItems(values);
		versionSelector.setItems("version 1", "version 3", "version 2");
	}
}
