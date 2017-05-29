package my.vaadin.bugrap.events;

import java.util.List;

import com.vaadin.ui.Component;

import my.vaadin.bugrap.Report;

public class UpdateReportDetailsEvent extends Component.Event {

	private List<Report> reports;

	public UpdateReportDetailsEvent(Component source, List<Report> reports) {
		super(source);
		this.reports = reports;
	}

	public List<Report> getReports() {
		return reports;
	}

}
