package my.vaadin.bugrap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;

public class AppGlobalData {

	private static final String APP_ID = "bugrap-id";

	private static Map<String, UserData> map = new HashMap<>();
	private static UserData userData = new UserData();

	public static UserData getUserData() {
		return map.get(getId());
	}

	private static String getId() {
		String cookieId = getIdFromCookie();
		if (cookieId == null || cookieId.isEmpty() || map.get(cookieId) == null)
			return generateNewData();
		return cookieId;
	}

	private static synchronized String generateNewData() {
		String newId = UUID.randomUUID().toString();
		map.put(newId, new UserData());
		saveIdToCookie(newId);
		return newId;
	}

	private static String getIdFromCookie() {
		Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
		if (cookies == null)
			return "";
		for (Cookie cookie : cookies) {
			if (APP_ID.equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return "";
	}

	private static void saveIdToCookie(String value) {
		Page.getCurrent().getJavaScript().execute(String.format("document.cookie = '%s=%s;';", APP_ID, value));
	}
}
