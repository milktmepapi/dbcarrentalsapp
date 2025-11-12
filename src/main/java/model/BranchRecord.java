package model;

/**
 * The {@code BranchRecord} class represents a branch entry in the Car Rental system.
 * It mirrors the structure of the {@code branch_record} table in the database.
 * <p>
 * Each {@code BranchRecord} contains a unique branch ID, name, email address,
 * and a reference to a location (via {@code branch_location_id}).
 * </p>
 * <p>
 * This class belongs to the Model layer (M in MVC) and is used to store
 * and manage data related to car rental branches before interaction with the database.
 * </p>
 *
 * @author Galicia
 * @author Marcelino
 * @author Samarista
 * @author Sy
 */
public class BranchRecord {
    private String branchId;
    private String branchName;
    private String branchEmailAddress;
    private String branchLocationId;

    /**
     * Default constructor that creates an empty {@code BranchRecord}.
     * Useful for frameworks or libraries that require a no-argument constructor.
     */
    public BranchRecord() {}

    /**
     * Creates a new {@code BranchRecord} with the specified details.
     *
     * @param branchId           the unique identifier for the branch
     * @param branchName         the name of the branch
     * @param branchEmailAddress the official email address of the branch
     * @param branchLocationId   the ID of the location where this branch is situated
     */
    public BranchRecord(String branchId, String branchName, String branchEmailAddress, String branchLocationId) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.branchEmailAddress = branchEmailAddress;
        this.branchLocationId = branchLocationId;
    }

    /** @return the branch ID */
    public String getBranchId() {
        return branchId;
    }

    /** @param branchId sets the branch ID */
    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    /** @return the branch name */
    public String getBranchName() {
        return branchName;
    }

    /** @param branchName sets the branch name */
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    /** @return the branch email address */
    public String getBranchEmailAddress() {
        return branchEmailAddress;
    }

    /** @param branchEmailAddress sets the branch email address */
    public void setBranchEmailAddress(String branchEmailAddress) {
        this.branchEmailAddress = branchEmailAddress;
    }

    /** @return the branch location ID */
    public String getBranchLocationId() {
        return branchLocationId;
    }

    /** @param branchLocationId sets the branch location ID (foreign key reference to location_record) */
    public void setBranchLocationId(String branchLocationId) {
        this.branchLocationId = branchLocationId;
    }

    /**
     * Returns a string representation of this {@code BranchRecord},
     * useful for debugging or logging.
     *
     * @return a formatted string containing the branch details
     */
    @Override
    public String toString() {
        return "BranchRecord{" +
                "branchId='" + branchId + '\'' +
                ", branchName='" + branchName + '\'' +
                ", branchEmailAddress='" + branchEmailAddress + '\'' +
                ", branchLocationId='" + branchLocationId + '\'' +
                '}';
    }
}
