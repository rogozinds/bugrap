package my.vaadin.bugrap.entities;

import java.util.Date;

public class Report {

	@Override
	public String toString() {
		return "Report [project=" + project + ", version=" + version + ", priority=" + priority + ", type=" + type
				+ ", summary=" + summary + ", assignedTo=" + assignedTo + ", lastModified=" + lastModified
				+ ", reported=" + reported + ", status=" + status + "]";
	}

	public enum IssueType {
		BUG("Bug"), FEATURE("Feature");

		private String name;

		private IssueType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public enum Status {
		OPEN("Open"), FIXED("Fixed"), INVALID("Invalid"), WONTFIX("Won't fix"), CANTFIX("Can't fix"), DUPLICATE(
				"Duplicate"), WORKSFORME("Works for me"), NEEDSMOREINFORMATION("Needs more informartion");

		private String name;

		private Status(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static int ID_SOURCE = 0;

	private String project;
	private String version;
	private Integer priority;
	private IssueType type;
	private String summary;
	private String assignedTo;
	private Date lastModified;
	private Date reported;
	private Status status;
	private final int id = ID_SOURCE++;

	public Report() {

	}

	public Report(String project, String version, int priority, IssueType type, String summary, String assignedTo,
			Date lastModified, Date reported, Status status) {
		this.project = project;
		this.version = version;
		this.priority = priority;
		this.type = type;
		this.summary = summary;
		this.assignedTo = assignedTo;
		this.lastModified = lastModified;
		this.reported = reported;
		this.status = status;
	}

	public Report(Report report) {
		this.project = report.project;
		this.version = report.version;
		this.priority = report.priority;
		this.type = report.type;
		this.summary = report.summary;
		this.assignedTo = report.assignedTo;
		this.lastModified = report.lastModified;
		this.reported = report.reported;
		this.status = report.status;
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

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
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

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Date getReported() {
		return reported;
	}

	public void setReported(Date reported) {
		this.reported = reported;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}
}
