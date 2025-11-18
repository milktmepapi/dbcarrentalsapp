package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a rental transaction in the Car Rental system.
 * This model corresponds to the {@code rental_details} table in the database.
 */
public class RentalRecord {

    private String rentalId;
    private String renterDlNumber;
    private String carPlateNumber;
    private String branchId;
    private String staffIdPickup;
    private String staffIdReturn;
    private LocalDateTime rentalDateTime;          // Time transaction was made
    private LocalDateTime expectedPickupDateTime;  // Scheduled pickup
    private LocalDateTime actualPickupDateTime;    // When car was actually picked up
    private LocalDateTime expectedReturnDateTime;
    private LocalDateTime actualReturnDateTime;
    private BigDecimal totalPayment;
    private RentalStatus rentalStatus;

    // No-argument constructor
    public RentalRecord() {}

    public RentalRecord(
            String rentalId,
            String renterDlNumber,
            String carPlateNumber,
            String branchId,
            String staffIdPickup,
            String staffIdReturn,
            LocalDateTime rentalDateTime,
            LocalDateTime expectedPickupDateTime,
            LocalDateTime actualPickupDateTime,
            LocalDateTime expectedReturnDateTime,
            LocalDateTime actualReturnDateTime,
            BigDecimal totalPayment,
            RentalStatus rentalStatus
    ) {
        this.rentalId = rentalId;
        this.renterDlNumber = renterDlNumber;
        this.carPlateNumber = carPlateNumber;
        this.branchId = branchId;
        this.staffIdPickup = staffIdPickup;
        this.staffIdReturn = staffIdReturn;
        this.rentalDateTime = rentalDateTime;
        this.expectedPickupDateTime = expectedPickupDateTime;
        this.actualPickupDateTime = actualPickupDateTime;
        this.expectedReturnDateTime = expectedReturnDateTime;
        this.actualReturnDateTime = actualReturnDateTime;
        this.totalPayment = totalPayment;
        this.rentalStatus = rentalStatus;
    }

    // Getters and Setters
    public String getRentalId() { return rentalId; }
    public void setRentalId(String rentalId) { this.rentalId = rentalId; }

    public String getRenterDlNumber() { return renterDlNumber; }
    public void setRenterDlNumber(String renterDlNumber) { this.renterDlNumber = renterDlNumber; }

    public String getCarPlateNumber() { return carPlateNumber; }
    public void setCarPlateNumber(String carPlateNumber) { this.carPlateNumber = carPlateNumber; }

    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }

    public String getStaffIdPickup() { return staffIdPickup; }
    public void setStaffIdPickup(String staffIdPickup) { this.staffIdPickup = staffIdPickup; }

    public String getStaffIdReturn() { return staffIdReturn; }
    public void setStaffIdReturn(String staffIdReturn) { this.staffIdReturn = staffIdReturn; }

    public LocalDateTime getRentalDateTime() { return rentalDateTime; }
    public void setRentalDateTime(LocalDateTime rentalDateTime) { this.rentalDateTime = rentalDateTime; }

    public LocalDateTime getExpectedPickupDateTime() { return expectedPickupDateTime; }
    public void setExpectedPickupDateTime(LocalDateTime expectedPickupDateTime) { this.expectedPickupDateTime = expectedPickupDateTime; }

    public LocalDateTime getActualPickupDateTime() { return actualPickupDateTime; }
    public void setActualPickupDateTime(LocalDateTime actualPickupDateTime) { this.actualPickupDateTime = actualPickupDateTime; }

    public LocalDateTime getExpectedReturnDateTime() { return expectedReturnDateTime; }
    public void setExpectedReturnDateTime(LocalDateTime expectedReturnDateTime) { this.expectedReturnDateTime = expectedReturnDateTime; }

    public LocalDateTime getActualReturnDateTime() { return actualReturnDateTime; }
    public void setActualReturnDateTime(LocalDateTime actualReturnDateTime) { this.actualReturnDateTime = actualReturnDateTime; }

    public BigDecimal getTotalPayment() { return totalPayment; }
    public void setTotalPayment(BigDecimal totalPayment) { this.totalPayment = totalPayment; }

    public RentalStatus getRentalStatus() { return rentalStatus; }
    public void setRentalStatus(RentalStatus rentalStatus) { this.rentalStatus = rentalStatus; }

    public void setRentalStatus(String status) {
        if (status == null) {
            this.rentalStatus = null;
            return;
        }

        try {
            this.rentalStatus = RentalStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Optionally default or log
            this.rentalStatus = RentalStatus.UPCOMING;
        }
    }


    @Override
    public String toString() {
        return "RentalRecord{" +
                "rentalId='" + rentalId + '\'' +
                ", renterDlNumber='" + renterDlNumber + '\'' +
                ", carPlateNumber='" + carPlateNumber + '\'' +
                ", branchId='" + branchId + '\'' +
                ", staffIdPickup='" + staffIdPickup + '\'' +
                ", staffIdReturn='" + staffIdReturn + '\'' +
                ", rentalDateTime=" + rentalDateTime +
                ", expectedPickupDateTime=" + expectedPickupDateTime +
                ", actualPickupDateTime=" + actualPickupDateTime +
                ", expectedReturnDateTime=" + expectedReturnDateTime +
                ", actualReturnDateTime=" + actualReturnDateTime +
                ", totalPayment=" + totalPayment +
                ", rentalStatus=" + rentalStatus +
                '}';
    }

    /**
     * Enum representing valid statuses for a rental.
     */
    public enum RentalStatus {
        UPCOMING,
        ACTIVE,
        COMPLETED,
        CANCELLED
    }
}