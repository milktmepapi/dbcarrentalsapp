package model;

import javafx.beans.property.*;

import java.time.LocalDateTime;

/**
 * The ViolationRecord class represents a violation entry in the Car Rental system.
 * It maps to the violation_details table in the database and uses JavaFX properties
 * for seamless integration with TableView and other UI components.
 *
 * This class follows the Model layer in MVC architecture and provides:
 * - Data storage for violation information
 * - JavaFX property bindings for UI integration
 * - Getters and setters for all properties
 *
 */
public class ViolationRecord {

    // Properties for JavaFX binding
    private final StringProperty violationId = new SimpleStringProperty();
    private final StringProperty rentalId = new SimpleStringProperty();
    private final StringProperty staffId = new SimpleStringProperty();
    private final StringProperty violationType = new SimpleStringProperty();
    private final DoubleProperty penaltyFee = new SimpleDoubleProperty();
    private final StringProperty reason = new SimpleStringProperty();
    private final IntegerProperty durationHours = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDateTime> timestamp = new SimpleObjectProperty<>();

    /**
     * Default constructor - creates an empty ViolationRecord.
     * Required for JavaFX and serialization.
     */
    public ViolationRecord() {}

    /**
     * Parameterized constructor - creates a ViolationRecord with all fields populated.
     *
     * @param violationId   Unique identifier for the violation (e.g., "VLN001")
     * @param rentalId      Rental ID associated with this violation
     * @param staffId       Staff ID who processed the violation
     * @param violationType Type of violation (Late Return, Car Damage, etc.)
     * @param penaltyFee    Monetary penalty amount in local currency
     * @param reason        Detailed description of the violation
     * @param durationHours Duration in hours (mainly for late returns)
     * @param timestamp     Date and time when violation was recorded
     */
    public ViolationRecord(String violationId, String rentalId, String staffId,
                           String violationType, double penaltyFee, String reason,
                           int durationHours, LocalDateTime timestamp) {
        setViolationId(violationId);
        setRentalId(rentalId);
        setStaffId(staffId);
        setViolationType(violationType);
        setPenaltyFee(penaltyFee);
        setReason(reason);
        setDurationHours(durationHours);
        setTimestamp(timestamp);
    }

    // ===== PROPERTY GETTERS AND SETTERS =====
    // These methods provide access to the JavaFX properties for data binding

    /**
     * @return Violation ID as a String
     */
    public String getViolationId() { return violationId.get(); }

    /**
     * @param value Sets the violation ID
     */
    public void setViolationId(String value) { violationId.set(value); }

    /**
     * @return Violation ID as a StringProperty for JavaFX binding
     */
    public StringProperty violationIdProperty() { return violationId; }

    /**
     * @return Rental ID associated with this violation
     */
    public String getRentalId() { return rentalId.get(); }

    /**
     * @param value Sets the rental ID
     */
    public void setRentalId(String value) { rentalId.set(value); }

    /**
     * @return Rental ID as a StringProperty for JavaFX binding
     */
    public StringProperty rentalIdProperty() { return rentalId; }

    /**
     * @return Staff ID who processed the violation
     */
    public String getStaffId() { return staffId.get(); }

    /**
     * @param value Sets the staff ID
     */
    public void setStaffId(String value) { staffId.set(value); }

    /**
     * @return Staff ID as a StringProperty for JavaFX binding
     */
    public StringProperty staffIdProperty() { return staffId; }

    /**
     * @return Type of violation (Late Return, Car Damage, etc.)
     */
    public String getViolationType() { return violationType.get(); }

    /**
     * @param value Sets the violation type
     */
    public void setViolationType(String value) { violationType.set(value); }

    /**
     * @return Violation type as a StringProperty for JavaFX binding
     */
    public StringProperty violationTypeProperty() { return violationType; }

    /**
     * @return Penalty fee amount
     */
    public double getPenaltyFee() { return penaltyFee.get(); }

    /**
     * @param value Sets the penalty fee
     */
    public void setPenaltyFee(double value) { penaltyFee.set(value); }

    /**
     * @return Penalty fee as a DoubleProperty for JavaFX binding
     */
    public DoubleProperty penaltyFeeProperty() { return penaltyFee; }

    /**
     * @return Reason/description of the violation
     */
    public String getReason() { return reason.get(); }

    /**
     * @param value Sets the violation reason
     */
    public void setReason(String value) { reason.set(value); }

    /**
     * @return Reason as a StringProperty for JavaFX binding
     */
    public StringProperty reasonProperty() { return reason; }

    /**
     * @return Duration in hours (for late returns)
     */
    public int getDurationHours() { return durationHours.get(); }

    /**
     * @param value Sets the duration in hours
     */
    public void setDurationHours(int value) { durationHours.set(value); }

    /**
     * @return Duration hours as an IntegerProperty for JavaFX binding
     */
    public IntegerProperty durationHoursProperty() { return durationHours; }

    /**
     * @return Timestamp when violation was recorded
     */
    public LocalDateTime getTimestamp() { return timestamp.get(); }

    /**
     * @param value Sets the timestamp
     */
    public void setTimestamp(LocalDateTime value) { timestamp.set(value); }

    /**
     * @return Timestamp as an ObjectProperty for JavaFX binding
     */
    public ObjectProperty<LocalDateTime> timestampProperty() { return timestamp; }

    /**
     * Returns a string representation of the ViolationRecord for debugging.
     *
     * @return Formatted string containing all violation details
     */
    @Override
    public String toString() {
        return "ViolationRecord{" +
                "violationId='" + getViolationId() + '\'' +
                ", rentalId='" + getRentalId() + '\'' +
                ", staffId='" + getStaffId() + '\'' +
                ", violationType='" + getViolationType() + '\'' +
                ", penaltyFee=" + getPenaltyFee() +
                ", reason='" + getReason() + '\'' +
                ", durationHours=" + getDurationHours() +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}