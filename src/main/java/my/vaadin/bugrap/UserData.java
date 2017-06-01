package my.vaadin.bugrap;

import org.vaadin.bugrap.domain.entities.Reporter;

public class UserData {

	private Reporter currentUser;

	public Reporter getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(Reporter currentUser) {
		this.currentUser = currentUser;
	}

}
