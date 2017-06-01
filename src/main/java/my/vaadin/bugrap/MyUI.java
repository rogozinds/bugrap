package my.vaadin.bugrap;

import javax.servlet.annotation.WebServlet;

import org.vaadin.bugrap.domain.entities.Reporter;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import my.vaadin.bugrap.exceptions.BugrapException;
import my.vaadin.bugrap.layouts.ReportsOverviewLayout;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		try {
			UserData userData = AppGlobalData.getUserData();
			if (userData == null)
				throw new BugrapException("Something went wrong");

			if (userData.getCurrentUser() == null)
				userData.setCurrentUser(authorize());

			setContent(new ReportsOverviewLayout());
		} catch (BugrapException e) {
			Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
		}
	}

	private Reporter authorize() throws BugrapException {
		Reporter authUser = null;

		try {
			authUser = ReportsProviderService.get().authenticate("developer", "developer");
		} catch (Exception e) {
		}

		if (authUser == null)
			throw new BugrapException("Authentication failed.");
		return authUser;
	}

	@WebServlet(urlPatterns = "/reports/*", name = "ReportUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = SingleReportUI.class, productionMode = false)
	public static class ReportUIServlet extends VaadinServlet {
	}

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}
}
