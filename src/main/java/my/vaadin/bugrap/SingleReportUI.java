package my.vaadin.bugrap;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import my.vaadin.bugrap.exceptions.BugrapException;
import my.vaadin.bugrap.layouts.ReportWindowLayout;

@Theme("mytheme")
public class SingleReportUI extends UI {

	@Override
	protected void init(VaadinRequest request) {
		try {
			UserData userData = AppGlobalData.getUserData();
			if (userData == null || userData.getCurrentUser() == null)
				throw new BugrapException("Unauthorized!");

			int id = -1;
			String idStr = "";
			try {
				idStr = request.getPathInfo().substring(1);
				id = Integer.parseUnsignedInt(idStr);
			} catch (Exception e) {
				throw new BugrapException("Wrong report id: " + idStr);
			}
			if (id < 0)
				return;

			setContent(new ReportWindowLayout(id));
		} catch (BugrapException e) {
			Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
		}
	}

}
