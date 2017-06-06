package my.vaadin.bugrap;

import org.vaadin.bugrap.domain.entities.Reporter;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import my.vaadin.bugrap.exceptions.BugrapException;
import my.vaadin.bugrap.utils.BugrapWindowOpener;

//I've create .gitignore file, where you can add all system files, IDE, css (not scss), etc.
@Theme("mytheme")
public class LoginUI extends UI {

	@Override
	protected void init(VaadinRequest request) {
		VerticalLayout v = new VerticalLayout();
		setContent(v);

		//Better not to add internal styles yourself.
		//Better to use higher lvl API(Java API for that), because Vaadin may have some hacks/fixes for different browsers( some old versions of IE).
		//Also these selectors might change (very unlikely, but still).
		//In this case it won't be a problem, but in general, you should try first to style things using Java API and predefined themes.
		//For example take a look at https://demo.vaadin.com/valo-theme/

		// In this case you can use setComponentAlignment.
		//v.setComponentAlignment(btn, Alignment.MIDDLE_CENTER);
		v.setStyleName("v-align-center v-align-middle");
		v.addComponent(new Label("Log in as: "));

		//Here, better just to have two text fields for username and password and a login button.
		//And then just check the values when logging in.
		//Imagine how your UI will look like if there are 50 users.
		for (Reporter r : ReportsProviderService.get().findReporters()) {
			Button btn = new Button(r.getName());
			v.addComponent(btn);
			v.setComponentAlignment(btn, Alignment.MIDDLE_CENTER);
			btn.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						authorize(r.getName(), r.getPassword());
						BugrapWindowOpener.openRoot();
					} catch (Exception e) {
						Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
					}
				}
			});
		}
	}

	private void authorize(String name, String password) throws BugrapException {
		Reporter authUser = null;
		try {
			authUser = ReportsProviderService.get().authenticate(name, name);
		} catch (Exception e) {
		}

		if (authUser == null)
			throw new BugrapException("Authentication failed.");

		AppGlobalData.getUserData().setCurrentUser(authUser);
		Notification.show("You are authorized as: " + authUser.getName());
	}

}
