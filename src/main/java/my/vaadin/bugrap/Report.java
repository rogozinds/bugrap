package my.vaadin.bugrap;

public class Report {

	public enum IssueType {
		BUG("Bug"), FEATURE("Feature");

		private String name;

		private IssueType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public enum Status {
		OPEN("Open"), FIXED("Fixed"), INVALID("Invalid"), WONTFIX("Won't fix"), CANTFIX("Can't fix"), DUPLICATE(
				"Duplicate"), WORKSFORME("Workd for me"), NEEDSMOREINFORMATION("Needs more informartion");

		private String name;

		private Status(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	private String project;
	private String version;
	private int priority = 1;
	private IssueType type;
	private String summary;
	private String assignedTo;
	private long lastModified;
	private long reported;
	private Status status;

	public Report() {

	}

	public Report(String project, String version, int priority, IssueType type, String summary, String assignedTo,
			long lastModified, long reported, Status status) {
		this.project = project;
		this.version = version;
		this.priority = priority;
		this.type = type;
		this.summary = summary;
		this.assignedTo = assignedTo;
		this.reported = reported;
		this.status = status;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public IssueType getType() {
		return type;
	}

	public void setType(IssueType type) {
		this.type = type;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public long getReported() {
		return reported;
	}

	public void setReported(long reported) {
		this.reported = reported;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
