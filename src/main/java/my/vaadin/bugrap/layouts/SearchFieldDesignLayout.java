package my.vaadin.bugrap.layouts;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import my.vaadin.bugrap.SearchFieldDesign;

public class SearchFieldDesignLayout extends SearchFieldDesign {

	public SearchFieldDesignLayout() {
		init();
	}

	private void init() {
		magnifierIcon.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				textField.focus();
			}
		});

		resetBtn.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				textField.clear();
				textField.focus();
			}
		});

	}

}
