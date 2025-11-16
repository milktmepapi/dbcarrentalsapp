package model;

import java.util.Date;
import javafx.beans.property.*;

/**
 * The {@code ViolationRecord} class represents a violation entry in the Car Rental system.
 * It mirrors the structure of the {@code violation_details} table in the database.
 * <p>
 * Each {@code ViolationRecord} contains information about a rental violation, including
 * the violation type, penalty fee, reason, and staff who processed it.
 * </p>
 * <p>
 * This class is part of the Model layer (M in MVC) and is used to store
 * and manage violation-related data before interaction with the database.
 * </p>
 *
 * @author Galicia
 * @author Marcelino
 * @author Samarista
 * @author Sy
 */
public class ViolationRecord {
    private String violationId;
    private String rentalId;
    private String staffId;
    private String violationType;
    private double penaltyFee;
    private String reason;
    private int durationHours;
    private Date timestamp;

    /**
     * Default constructor that creates an empty {@code ViolationRecord}.
     * Useful for frameworks or libraries that require a no-argument constructor.
     */
    public ViolationRecord() {}

    /**
     * Creates a new {@code ViolationRecord} with the specified details.
     *
     * @param violationId     the unique identifier for the violation
     * @param rentalId        the rental ID associated with the violation
     * @param staffId         the staff ID who processed the violation
     * @param violationType   the type of violation (Late Return, Car Damage, etc.)
     * @param penaltyFee      the penalty fee charged for the violation
     * @param reason          the reason/details of the violation
     * @param durationHours   the duration of the violation in hours (for late returns)
     * @param timestamp       the date and time when the violation was recorded
     */
    public ViolationRecord(String violationId, String rentalId, String staffId,
                           String violationType, double penaltyFee, String reason,
                           int durationHours, Date timestamp) {
        this.violationId = violationId;
        this.rentalId = rentalId;
        this.staffId = staffId;
        this.violationType = violationType;
        this.penaltyFee = penaltyFee;
        this.reason = reason;
        this.durationHours = durationHours;
        this.timestamp = timestamp;
    }

    /** @return the violation ID */
    public String getViolationId() {
        return violationId;
    }

    /** @param violationId sets the violation ID */
    public void setViolationId(String violationId) {
        this.violationId = violationId;
    }

    /** @return the rental ID */
    public String getRentalId() {
        return rentalId;
    }

    /** @param rentalId sets the rental ID */
    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    /** @return the staff ID */
    public String getStaffId() {
        return staffId;
    }

    /** @param staffId sets the staff ID */
    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    /** @return the violation type */
    public String getViolationType() {
        return violationType;
    }

    /** @param violationType sets the violation type */
    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }

    /** @return the penalty fee */
    public double getPenaltyFee() {
        return penaltyFee;
    }

    /** @param penaltyFee sets the penalty fee */
    public void setPenaltyFee(double penaltyFee) {
        this.penaltyFee = penaltyFee;
    }

    /** @return the violation reason */
    public String getReason() {
        return reason;
    }

    /** @param reason sets the violation reason */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /** @return the duration in hours */
    public int getDurationHours() {
        return durationHours;
    }

    /** @param durationHours sets the duration in hours */
    public void setDurationHours(int durationHours) {
        this.durationHours = durationHours;
    }

    /** @return the timestamp */
    public Date getTimestamp() {
        return timestamp;
    }

    /** @param timestamp sets the timestamp */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    //For table view
    public StringProperty violationIdProperty() {
        return new SimpleStringProperty(violationId);
    }

    public StringProperty rentalIdProperty() {
        return new SimpleStringProperty(rentalId);
    }

    public StringProperty violationTypeProperty() {
        return new SimpleStringProperty(violationType);
    }

    public DoubleProperty penaltyFeeProperty() {
        return new SimpleDoubleProperty(penaltyFee);
    }

    public StringProperty reasonProperty() {
        return new SimpleStringProperty(reason);
    }

    public IntegerProperty durationHoursProperty() {
        return new SimpleIntegerProperty(durationHours);
    }

    public ObjectProperty<Date> timestampProperty() {
        return new SimpleObjectProperty<>(timestamp);
    }

    /**
     * Returns a string representation of this {@code ViolationRecord},
     * useful for debugging or logging.
     *
     * @return a formatted string containing the violation details
     */
    @Override
    public String toString() {
        return "ViolationRecord{" +
                "violationId='" + violationId + '\'' +
                ", rentalId='" + rentalId + '\'' +
                ", staffId='" + staffId + '\'' +
                ", violationType='" + violationType + '\'' +
                ", penaltyFee=" + penaltyFee +
                ", reason='" + reason + '\'' +
                ", durationHours=" + durationHours +
                ", timestamp=" + timestamp +
                '}';
    }
}