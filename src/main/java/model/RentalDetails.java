package model;

import java.util.Date;

/**
 * The {@code RentalDetails} class represents a rental transaction in the Car Rental system.
 * It mirrors the structure of the {@code rental_details} table in the database.
 * <p>
 * Each {@code RentalDetails} contains information about a car rental transaction,
 * including rental dates, payment information, and associated records.
 * </p>
 * <p>
 * This class is part of the Model layer (M in MVC) and is used to store
 * and manage rental-related data before interaction with the database.
 * </p>
 *
 * @author Galicia
 * @author Marcelino
 * @author Samarista
 * @author Sy
 */
public class RentalDetails {
    private String rentalId;
    private String renterDlNumber;
    private String carPlateNumber;
    private String branchId;
    private String staffIdPickup;
    private String staffIdReturn;
    private Date rentalDatetime;
    private Date pickupDatetime;
    private Date expectedReturnDatetime;
    private Date actualReturnDatetime;
    private double totalPayment;
    private String status;

    /**
     * Default constructor that creates an empty {@code RentalDetails}.
     * Useful for frameworks or libraries that require a no-argument constructor.
     */
    public RentalDetails() {}

    /**
     * Creates a new {@code RentalDetails} with the specified details.
     *
     * @param rentalId                 the unique identifier for the rental
     * @param renterDlNumber           the driver's license number of the renter
     * @param carPlateNumber           the plate number of the rented car
     * @param branchId                 the branch ID where the rental occurred
     * @param staffIdPickup            the staff ID who processed the pickup
     * @param staffIdReturn            the staff ID who processed the return
     * @param rentalDatetime           the date and time when the rental was created
     * @param pickupDatetime           the date and time when the car was picked up
     * @param expectedReturnDatetime   the expected return date and time
     * @param actualReturnDatetime     the actual return date and time
     * @param totalPayment             the total payment amount
     * @param status                   the rental status (Upcoming, Active, Completed, Cancelled)
     */
    public RentalDetails(String rentalId, String renterDlNumber, String carPlateNumber,
                         String branchId, String staffIdPickup, String staffIdReturn,
                         Date rentalDatetime, Date pickupDatetime, Date expectedReturnDatetime,
                         Date actualReturnDatetime, double totalPayment, String status) {
        this.rentalId = rentalId;
        this.renterDlNumber = renterDlNumber;
        this.carPlateNumber = carPlateNumber;
        this.branchId = branchId;
        this.staffIdPickup = staffIdPickup;
        this.staffIdReturn = staffIdReturn;
        this.rentalDatetime = rentalDatetime;
        this.pickupDatetime = pickupDatetime;
        this.expectedReturnDatetime = expectedReturnDatetime;
        this.actualReturnDatetime = actualReturnDatetime;
        this.totalPayment = totalPayment;
        this.status = status;
    }

    public static final String STATUS_UPCOMING = "Upcoming";
    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_CANCELLED = "Cancelled";
    public static final String STATUS_OVERDUE = "Overdue";

    /**
     * Checks if this rental is currently late
     */
    public boolean isLate() {
        if (actualReturnDatetime != null) {
            // Already returned - check if it was late
            return actualReturnDatetime.after(expectedReturnDatetime);
        } else {
            // Not yet returned - check if it's overdue
            return new Date().after(expectedReturnDatetime) && STATUS_ACTIVE.equals(status);
        }
    }

    /**
     * Gets the current status considering overdue status
     */
    public String getEffectiveStatus() {
        if (isLate() && STATUS_ACTIVE.equals(status)) {
            return STATUS_OVERDUE;
        }
        return status;
    }

    /**
     * Calculates late hours if applicable
     */
    public int calculateLateHours() {
        if (actualReturnDatetime != null && actualReturnDatetime.after(expectedReturnDatetime)) {
            long diffMillis = actualReturnDatetime.getTime() - expectedReturnDatetime.getTime();
            return (int) Math.ceil(diffMillis / (1000.0 * 60 * 60));
        } else if (new Date().after(expectedReturnDatetime) && STATUS_ACTIVE.equals(status)) {
            long diffMillis = new Date().getTime() - expectedReturnDatetime.getTime();
            return (int) Math.ceil(diffMillis / (1000.0 * 60 * 60));
        }
        return 0;
    }

    // Getters and Setters following the same pattern as other model classes
    /** @return the rental ID */
    public String getRentalId() {
        return rentalId;
    }

    /** @param rentalId sets the rental ID */
    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    /** @return the renter DL number */
    public String getRenterDlNumber() {
        return renterDlNumber;
    }

    /** @param renterDlNumber sets the renter DL number */
    public void setRenterDlNumber(String renterDlNumber) {
        this.renterDlNumber = renterDlNumber;
    }

    /** @return the car plate number */
    public String getCarPlateNumber() {
        return carPlateNumber;
    }

    /** @param carPlateNumber sets the car plate number */
    public void setCarPlateNumber(String carPlateNumber) {
        this.carPlateNumber = carPlateNumber;
    }

    /** @return the branch ID */
    public String getBranchId() {
        return branchId;
    }

    /** @param branchId sets the branch ID */
    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    /** @return the staff ID for pickup */
    public String getStaffIdPickup() {
        return staffIdPickup;
    }

    /** @param staffIdPickup sets the staff ID for pickup */
    public void setStaffIdPickup(String staffIdPickup) {
        this.staffIdPickup = staffIdPickup;
    }

    /** @return the staff ID for return */
    public String getStaffIdReturn() {
        return staffIdReturn;
    }

    /** @param staffIdReturn sets the staff ID for return */
    public void setStaffIdReturn(String staffIdReturn) {
        this.staffIdReturn = staffIdReturn;
    }

    /** @return the rental datetime */
    public Date getRentalDatetime() {
        return rentalDatetime;
    }

    /** @param rentalDatetime sets the rental datetime */
    public void setRentalDatetime(Date rentalDatetime) {
        this.rentalDatetime = rentalDatetime;
    }

    /** @return the pickup datetime */
    public Date getPickupDatetime() {
        return pickupDatetime;
    }

    /** @param pickupDatetime sets the pickup datetime */
    public void setPickupDatetime(Date pickupDatetime) {
        this.pickupDatetime = pickupDatetime;
    }

    /** @return the expected return datetime */
    public Date getExpectedReturnDatetime() {
        return expectedReturnDatetime;
    }

    /** @param expectedReturnDatetime sets the expected return datetime */
    public void setExpectedReturnDatetime(Date expectedReturnDatetime) {
        this.expectedReturnDatetime = expectedReturnDatetime;
    }

    /** @return the actual return datetime */
    public Date getActualReturnDatetime() {
        return actualReturnDatetime;
    }

    /** @param actualReturnDatetime sets the actual return datetime */
    public void setActualReturnDatetime(Date actualReturnDatetime) {
        this.actualReturnDatetime = actualReturnDatetime;
    }

    /** @return the total payment */
    public double getTotalPayment() {
        return totalPayment;
    }

    /** @param totalPayment sets the total payment */
    public void setTotalPayment(double totalPayment) {
        this.totalPayment = totalPayment;
    }

    /** @return the rental status */
    public String getStatus() {
        return status;
    }

    /** @param status sets the rental status */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns a string representation of this {@code RentalDetails},
     * useful for debugging or logging.
     *
     * @return a formatted string containing the rental details
     */
    @Override
    public String toString() {
        return "RentalDetails{" +
                "rentalId='" + rentalId + '\'' +
                ", renterDlNumber='" + renterDlNumber + '\'' +
                ", carPlateNumber='" + carPlateNumber + '\'' +
                ", branchId='" + branchId + '\'' +
                ", staffIdPickup='" + staffIdPickup + '\'' +
                ", staffIdReturn='" + staffIdReturn + '\'' +
                ", rentalDatetime=" + rentalDatetime +
                ", pickupDatetime=" + pickupDatetime +
                ", expectedReturnDatetime=" + expectedReturnDatetime +
                ", actualReturnDatetime=" + actualReturnDatetime +
                ", totalPayment=" + totalPayment +
                ", status='" + status + '\'' +
                '}';
    }
}