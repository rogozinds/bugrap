package my.vaadin.bugrap;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import my.vaadin.bugrap.layouts.ReportWindowLayout;

@Theme("mytheme")
public class SingleReportUI extends UI {

	@Override
	protected void init(VaadinRequest request) {
		int id = -1;
		String idStr = "";
		try {
			idStr = request.getPathInfo().substring(1);
			id = Integer.parseUnsignedInt(idStr);
		} catch (Exception e) {
			Notification.show("Wrong report id: " + idStr, Type.ERROR_MESSAGE);
		}
		if (id < 0)
			return;
		setContent(new ReportWindowLayout(id));
	}

}
