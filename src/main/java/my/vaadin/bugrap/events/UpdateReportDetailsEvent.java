package my.vaadin.bugrap.events;

import java.util.List;

import org.vaadin.bugrap.domain.entities.Report;

import com.vaadin.ui.Component;

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
