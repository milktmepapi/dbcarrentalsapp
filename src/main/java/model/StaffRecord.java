package model;
/**
 * The {@code StaffRecord} class represents a staff member in the Car Rental system.
 * It mirrors the structure of the {@code staff_record} table in the database.
 * <p>
 * Each {@code StaffRecord} contains information about a staff member, including their
 * unique ID, first and last name, job assignment, and the branch they belong to.
 * </p>
 * <p>
 * This class is part of the Model layer (M in MVC) and is responsible for
 * storing and managing staff-related data before interacting with the database.
 * </p>
 *
 * @author Galicia
 * @author Marcelino
 * @author Samarista
 * @author Sy
 */
public class StaffRecord {
    private String staffId;
    private String staffFirstName;
    private String staffLastName;
    private String staffJobId;     // Foreign key → job_record(job_id)
    private String staffBranchId;  // Foreign key → branch_record(branch_id)

    /**
     * Default constructor for {@code StaffRecord}.
     * Useful for frameworks or initializing empty staff records.
     */
    public StaffRecord() {}

    /**
     * Creates a {@code StaffRecord} with the specified details.
     *
     * @param staffId         the unique identifier for the staff member
     * @param staffFirstName  the first name of the staff member
     * @param staffLastName   the last name of the staff member
     * @param staffJobId      the job ID assigned to the staff member
     * @param staffBranchId   the branch ID where the staff member is assigned
     */
    public StaffRecord(String staffId, String staffFirstName, String staffLastName,
                       String staffJobId, String staffBranchId) {
        this.staffId = staffId;
        this.staffFirstName = staffFirstName;
        this.staffLastName = staffLastName;
        this.staffJobId = staffJobId;
        this.staffBranchId = staffBranchId;
    }

    /** @return the staff ID */
    public String getStaffId() {
        return staffId;
    }

    /** @param staffId sets the staff ID */
    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    /** @return the staff member's first name */
    public String getStaffFirstName() {
        return staffFirstName;
    }

    /** @param staffFirstName sets the staff member's first name */
    public void setStaffFirstName(String staffFirstName) {
        this.staffFirstName = staffFirstName;
    }

    /** @return the staff member's last name */
    public String getStaffLastName() {
        return staffLastName;
    }

    /** @param staffLastName sets the staff member's last name */
    public void setStaffLastName(String staffLastName) {
        this.staffLastName = staffLastName;
    }

    /** @return the job ID (foreign key reference to job_record) */
    public String getStaffJobId() {
        return staffJobId;
    }

    /** @param staffJobId sets the job ID (foreign key reference to job_record) */
    public void setStaffJobId(String staffJobId) {
        this.staffJobId = staffJobId;
    }

    /** @return the branch ID (foreign key reference to branch_record) */
    public String getStaffBranchId() {
        return staffBranchId;
    }

    /** @param staffBranchId sets the branch ID (foreign key reference to branch_record) */
    public void setStaffBranchId(String staffBranchId) {
        this.staffBranchId = staffBranchId;
    }

    /**
     * Returns a string representation of the {@code StaffRecord},
     * useful for debugging and logging.
     *
     * @return a formatted string containing staff details
     */
    @Override
    public String toString() {
        return "StaffRecord{" +
                "staffId='" + staffId + '\'' +
                ", staffFirstName='" + staffFirstName + '\'' +
                ", staffLastName='" + staffLastName + '\'' +
                ", staffJobId='" + staffJobId + '\'' +
                ", staffBranchId='" + staffBranchId + '\'' +
                '}';
    }
}
