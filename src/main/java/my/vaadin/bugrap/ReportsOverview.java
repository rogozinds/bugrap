package my.vaadin.bugrap;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.declarative.Design;
import my.vaadin.distributionbar.DistributionBar;

/** 
 * !! DO NOT EDIT THIS FILE !!
 * 
 * This class is generated by Vaadin Designer and will be overwritten.
 * 
 * Please make a subclass with logic and additional interfaces as needed,
 * e.g class LoginView extends LoginDesign implements View { }
 */
@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class ReportsOverview extends VerticalLayout {
	protected HorizontalLayout topHeader;
	protected VerticalLayout headerContainer;
	protected HorizontalLayout firstLineHeader;
	protected NativeSelect<java.lang.String> projectSelector;
	protected HorizontalLayout userButtonsContainer;
	protected Button accountBtn;
	protected Button logoutBtn;
	protected HorizontalLayout manageButtonsContainer;
	protected Button reportBug;
	protected Button requestFeature;
	protected Button manageProjectsBtn;
	protected Label projectCountLbl;
	protected TextField searchField;
	protected HorizontalLayout versionsContainer;
	protected NativeSelect<java.lang.String> versionSelector;
	protected DistributionBar distributionBar;
	protected HorizontalLayout filtersContainer;
	protected Button onlyMeBtn;
	protected Button everyoneBtn;
	protected Button openBtn;
	protected Button allKindsBtn;
	protected VerticalLayout customContainer;
	protected Button customBtn;
	protected VerticalSplitPanel mainSplitter;
	protected Panel gridContainer;
	protected Grid<my.vaadin.bugrap.Report> reportsGrid;
	protected HorizontalLayout bottomFooter;

	public ReportsOverview() {
		Design.read(this);
	}
}
