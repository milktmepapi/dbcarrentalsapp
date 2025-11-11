package model;
/**
 * The {@code DepartmentRecord} class represents a department in the Car Rental system.
 * It mirrors the structure of the {@code department_record} table in the database.
 * <p>
 * Each DepartmentRecord has a unique department ID and a department name.
 * This class is part of the Model layer (M in MVC), responsible for
 * holding and managing department-related data.
 * </p>
 *
 * @author Galicia
 * @author Marcelino
 * @author Samarista
 * @author Sy
 */
public class DepartmentRecord {
    private String departmentId;
    private String departmentName;

    /**
     * Creates a new DepartmentRecord with the specified ID and name.
     *
     * @param departmentId   the unique identifier for the department
     * @param departmentName the name of the department
     */
    public DepartmentRecord(String departmentId, String departmentName) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }

    /**
     * Creates an empty DepartmentRecord with no values set.
     */
    public DepartmentRecord() {}

    /**
     * Returns the department's unique ID.
     *
     * @return the department ID
     */
    public String getDepartmentId() {
        return departmentId;
    }

    /**
     * Sets the department's unique ID.
     *
     * @param departmentId the department ID to set
     */
    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * Returns the department's name.
     *
     * @return the department name
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * Sets the department's name.
     *
     * @param departmentName the department name to set
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    /**
     * Returns a string representation of the DepartmentRecord
     *
     * @return a string containing the department ID and name
     */
    @Override
    public String toString() {
        return "DepartmentRecord{" +
                "departmentId='" + departmentId + '\'' +
                ", departmentName='" + departmentName + '\'' +
                '}';
    }
}
