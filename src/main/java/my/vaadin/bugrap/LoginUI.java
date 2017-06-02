package my.vaadin.bugrap;

import org.vaadin.bugrap.domain.entities.Reporter;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
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

@Theme("mytheme")
public class LoginUI extends UI {

	@Override
	protected void init(VaadinRequest request) {
		VerticalLayout v = new VerticalLayout();
		setContent(v);
		v.setStyleName("v-align-center v-align-middle");
		v.addComponent(new Label("Log in as: "));
		for (Reporter r : ReportsProviderService.get().findReporters()) {
			Button btn = new Button(r.getName());
			v.addComponent(btn);
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

	private Reporter authorize(String name, String password) throws BugrapException {
		Reporter authUser = null;
		try {
			authUser = ReportsProviderService.get().authenticate(name, name);
		} catch (Exception e) {
		}

		if (authUser == null)
			throw new BugrapException("Authentication failed.");

		AppGlobalData.getUserData().setCurrentUser(authUser);
		Notification.show("You are authorized as: " + authUser.getName());
		return authUser;
	}

}
