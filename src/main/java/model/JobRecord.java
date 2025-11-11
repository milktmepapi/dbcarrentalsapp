package model;
/**
 * The {@code JobRecord} class represents a job position within a department
 * in the Car Rental system. It mirrors the structure of the {@code job_record}
 * table in the database.
 * <p>
 * Each JobRecord contains details such as the job ID, title, associated department,
 * and salary. This class is part of the Model layer (M in MVC), responsible for
 * holding and managing job-related data.
 * </p>
 *
 * @author Galicia
 * @author Marcelino
 * @author Samarista
 * @author Sy
 */
public class JobRecord {
    private String jobId;
    private String jobTitle;
    private String jobDepartmentId;
    private double jobSalary;

    /**
     * Creates a new {@code JobRecord} with the specified details.
     *
     * @param jobId            the unique identifier for the job
     * @param jobTitle         the title or name of the job
     * @param jobDepartmentId  the ID of the department this job belongs to
     * @param jobSalary        the salary assigned to the job
     */
    public JobRecord(String jobId, String jobTitle, String jobDepartmentId, double jobSalary) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.jobDepartmentId = jobDepartmentId;
        this.jobSalary = jobSalary;
    }

    /**
     * Creates an empty {@code JobRecord} with no initial values.
     */
    public JobRecord() {}

    /**
     * Returns the job ID.
     *
     * @return the job ID
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Sets the job ID.
     *
     * @param jobId the job ID to set
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * Returns the job title.
     *
     * @return the job title
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * Sets the job title.
     *
     * @param jobTitle the job title to set
     */
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    /**
     * Returns the department ID that this job belongs to.
     *
     * @return the department ID
     */
    public String getJobDepartmentId() {
        return jobDepartmentId;
    }

    /**
     * Sets the department ID for this job.
     *
     * @param jobDepartmentId the department ID to set
     */
    public void setJobDepartmentId(String jobDepartmentId) {
        this.jobDepartmentId = jobDepartmentId;
    }

    /**
     * Returns the job salary.
     *
     * @return the job salary
     */
    public double getJobSalary() {
        return jobSalary;
    }

    /**
     * Sets the job salary.
     *
     * @param jobSalary the salary to set
     */
    public void setJobSalary(double jobSalary) {
        this.jobSalary = jobSalary;
    }

    /**
     * Returns a string representation of the JobRecord,
     * useful for debugging or logging.
     *
     * @return a formatted string containing job details
     */
    @Override
    public String toString() {
        return "JobRecord{" +
                "jobId='" + jobId + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", jobDepartmentId='" + jobDepartmentId + '\'' +
                ", jobSalary=" + jobSalary +
                '}';
    }
}
