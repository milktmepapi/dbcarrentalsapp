package model;
/**
 * The {@code RenterRecord} class represents a renter (customer) in the Car Rental system.
 * It mirrors the structure of the {@code renter_record} table in the database.
 * <p>
 * Each {@code RenterRecord} contains personal and contact details of a renter,
 * including their driver's license number, name, phone number, and email address.
 * </p>
 * <p>
 * This class is part of the Model layer (M in MVC) and is used to store
 * renter-related data before interacting with the database.
 * </p>
 *
 * @author Galicia
 * @author Marcelino
 * @author Samarista
 * @author Sy
 */
public class RenterRecord {
    private String renterDlNumber;      // Primary Key: Driver’s License Number
    private String renterFirstName;
    private String renterLastName;
    private String renterPhoneNumber;
    private String renterEmailAddress;

    /**
     * Default constructor for {@code RenterRecord}.
     * Useful for frameworks or initializing empty renter records.
     */
    public RenterRecord() {}

    /**
     * Creates a {@code RenterRecord} with the specified details.
     *
     * @param renterDlNumber    the unique driver's license number of the renter
     * @param renterFirstName   the renter's first name
     * @param renterLastName    the renter's last name
     * @param renterPhoneNumber the renter's contact phone number
     * @param renterEmailAddress the renter's email address
     */
    public RenterRecord(String renterDlNumber, String renterFirstName, String renterLastName,
                        String renterPhoneNumber, String renterEmailAddress) {
        this.renterDlNumber = renterDlNumber;
        this.renterFirstName = renterFirstName;
        this.renterLastName = renterLastName;
        this.renterPhoneNumber = renterPhoneNumber;
        this.renterEmailAddress = renterEmailAddress;
    }

    /** @return the renter's driver's license number */
    public String getRenterDlNumber() {
        return renterDlNumber;
    }

    /** @param renterDlNumber sets the renter's driver's license number */
    public void setRenterDlNumber(String renterDlNumber) {
        this.renterDlNumber = renterDlNumber;
    }

    /** @return the renter's first name */
    public String getRenterFirstName() {
        return renterFirstName;
    }

    /** @param renterFirstName sets the renter's first name */
    public void setRenterFirstName(String renterFirstName) {
        this.renterFirstName = renterFirstName;
    }

    /** @return the renter's last name */
    public String getRenterLastName() {
        return renterLastName;
    }

    /** @param renterLastName sets the renter's last name */
    public void setRenterLastName(String renterLastName) {
        this.renterLastName = renterLastName;
    }

    /** @return the renter's phone number */
    public String getRenterPhoneNumber() {
        return renterPhoneNumber;
    }

    /** @param renterPhoneNumber sets the renter's phone number */
    public void setRenterPhoneNumber(String renterPhoneNumber) {
        this.renterPhoneNumber = renterPhoneNumber;
    }

    /** @return the renter's email address */
    public String getRenterEmailAddress() {
        return renterEmailAddress;
    }

    /** @param renterEmailAddress sets the renter's email address */
    public void setRenterEmailAddress(String renterEmailAddress) {
        this.renterEmailAddress = renterEmailAddress;
    }

    /**
     * Returns a string representation of the {@code RenterRecord},
     * useful for debugging or logging.
     *
     * @return a formatted string containing renter details
     */
    @Override
    public String toString() {
        return "RenterRecord{" +
                "renterDlNumber='" + renterDlNumber + '\'' +
                ", renterFirstName='" + renterFirstName + '\'' +
                ", renterLastName='" + renterLastName + '\'' +
                ", renterPhoneNumber='" + renterPhoneNumber + '\'' +
                ", renterEmailAddress='" + renterEmailAddress + '\'' +
                '}';
    }
}
