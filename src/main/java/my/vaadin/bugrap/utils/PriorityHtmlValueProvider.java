package my.vaadin.bugrap.utils;

import org.vaadin.bugrap.domain.entities.Report;

import com.vaadin.data.ValueProvider;

public class PriorityHtmlValueProvider {

	private static ValueProvider<Report, String> instance;

	public static ValueProvider<Report, String> get() {
		if (instance == null)
			instance = new ValueProvider<Report, String>() {

				@Override
				public String apply(Report source) {
					String singleP = "<span class=\"v-align-right v-align-middle bugrap-priority\" style=\" width: 15%; height: 100%; \" >&nbsp;&nbsp;</span>";
					StringBuilder sb = new StringBuilder();
					sb.append(
							"<div class=\"v-horizontallayout v-layout v-horizontal v-widget v-has-width v-has-height v-align-middle\" style=\" width: 100%; height: 90%\">");
					for (int i = 0; i <= source.getPriority().ordinal(); i++)
						sb.append(singleP);
					sb.append("</div>");
					return sb.toString();
				}
			};

		return instance;
	}

}
