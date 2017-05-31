package my.vaadin.bugrap.layouts;

import java.util.Set;

import org.vaadin.bugrap.domain.entities.Report.Status;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.ui.VerticalLayout;

public abstract class CustomStatusPopupContent implements Content {

	private VerticalLayout main = new VerticalLayout();
	private CheckBoxGroup<Status> customOptions = new CheckBoxGroup<>();

	public CustomStatusPopupContent() {
		main.addComponent(new Label("STATUS"));
		customOptions.setItems(Status.values());
		main.addComponent(customOptions);
		customOptions.addValueChangeListener(new ValueChangeListener<Set<Status>>() {

			@Override
			public void valueChange(ValueChangeEvent<Set<Status>> event) {
				changeAction();
			}
		});
	}

	public abstract void changeAction();

	public Set<Status> getSelectedItems() {
		return customOptions.getSelectedItems();
	}

	@Override
	public String getMinimizedValueAsHTML() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Component getPopupComponent() {
		return main;
	}

}
