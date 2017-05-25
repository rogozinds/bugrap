package my.vaadin.bugrap.layouts;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.vaadin.ui.renderers.DateRenderer;

import elemental.json.JsonValue;

public class RelativeDateRenderer extends DateRenderer {

	@Override
	public JsonValue encode(Date value) {
		return encode(getRelativeTime(value), String.class);
	}

	private String getRelativeTime(Date value) {
		if (value == null)
			return getNullRepresentation();

		String result;
		Date now = new Date();

		long diff = 0;
		diff = TimeUnit.MILLISECONDS.toDays(now.getTime() - value.getTime());
		result = getRelativeTime(diff/365, "year");
		if (result != null)
			return result;
		result = getRelativeTime(diff/30, "month");
		if (result != null)
			return result;
		result = getRelativeTime(diff, "day");
		if (result != null)
			return result;

		diff = TimeUnit.MILLISECONDS.toHours(now.getTime() - value.getTime());
		result = getRelativeTime(diff, "hour");
		if (result != null)
			return result;

		diff = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - value.getTime());
		result = getRelativeTime(diff, "min");
		if (result != null)
			return result;

		return "just now";
	}

	private String getRelativeTime(long diff, String word) {
		if (diff <= 0)
			return null;

		if (diff == 1)
			return "1 " + word + " ago";
		return diff + " " + word + "s ago";

	}

}
