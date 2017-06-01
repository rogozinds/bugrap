package my.vaadin.bugrap.utils;

import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Report.Priority;

import com.vaadin.data.ValueProvider;

public class PriorityHtmlProvider {

	public static ValueProvider<Report, String> fromReport() {
		return new ValueProvider<Report, String>() {

			@Override
			public String apply(Report source) {
				return convertPriority(source.getPriority());
			}
		};

	}

	private static String convertPriority(Priority priority) {
		if (priority == null)
			return "";

		String singleP = "<span class=\"v-align-right v-align-middle bugrap-priority\" style=\" width: 15%; height: 100%; \" >&nbsp;&nbsp;</span>";
		StringBuilder sb = new StringBuilder();
		sb.append(
				"<div class=\"v-horizontallayout v-layout v-horizontal v-widget v-has-width v-has-height v-align-middle\" style=\" width: 100%; height: 90%\">");
		for (int i = 0; i <= priority.ordinal(); i++)
			sb.append(singleP);
		sb.append("</div>");
		return sb.toString();
	}

}
