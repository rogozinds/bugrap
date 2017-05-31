package my.vaadin.bugrap;

import org.vaadin.bugrap.domain.BugrapRepository;

public class ReportsProviderService {

	private static BugrapRepository dataSource;

	public static synchronized BugrapRepository get() {
		if (dataSource == null) {
			dataSource = new BugrapRepository("~/bugrap/bugrapdb/db");
			dataSource.populateWithTestData();
		}

		return dataSource;
	}
}
