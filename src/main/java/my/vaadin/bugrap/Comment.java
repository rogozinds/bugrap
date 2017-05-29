package my.vaadin.bugrap;

import java.util.Date;

public class Comment {

	@Override
	public String toString() {
		return "Comment [userName=" + userName + ", date=" + date + ", message=" + message + "]";
	}

	private String userName;
	private Date date;
	private String message;

	public Comment(String userName, Date date, String message) {
		this.userName = userName;
		this.date = date;
		this.message = message;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
