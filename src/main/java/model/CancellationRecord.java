package model;
import javafx.beans.property.*;

import java.time.LocalDateTime;

/**
 * The CancellationRecord class represents a cancellation entry in the Car Rental system.
 * It maps to the cancellation_details table in the database and uses JavaFX properties
 * for seamless integration with TableView and other UI components.
 *
 * This class follows the Model layer in MVC architecture and provides:
 * - Data storage for cancellation information
 * - JavaFX property bindings for UI integration
 * - Getters and setters for all properties
 *
 */
public class CancellationRecord {

    // Properties for JavaFX binding
    private final StringProperty cancellationId = new SimpleStringProperty();
    private final StringProperty cancellationRentalId = new SimpleStringProperty();
    private final StringProperty cancellationStaffId = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> timestamp = new SimpleObjectProperty<>();
    private final StringProperty reason = new SimpleStringProperty();

    /**
     * Default constructor - creates an empty CancellationRecord.
     * Required for JavaFX and serialization.
     */
    public CancellationRecord() {}

    /**
     * Parameterized constructor - creates a CancellationRecord with all fields populated.
     *
     * @param cancellationId   Unique identifier for cancellation (e.g., "VLN001")
     * @param cancellationRentalId      Rental ID associated with this cancellation
     * @param cancellationStaffId       Staff ID who processed the cancellation
     * @param reason        Detailed description of the cancellation
     * @param timestamp     Date and time when cancellation was recorded
     */
    public CancellationRecord(String cancellationId, String cancellationRentalId, String cancellationStaffId,
                              LocalDateTime timestamp, String reason) {
        setCancellationId(cancellationId);
        setCancellationRentalId(cancellationRentalId);
        setCancellationStaffId(cancellationStaffId);
        setTimestamp(timestamp);
        setReason(reason);
    }

    // ===== PROPERTY GETTERS AND SETTERS =====
    // These methods provide access to the JavaFX properties for data binding

    /**
     * @return Cancellation ID as a String
     */
    public String getCancellationId() { return cancellationId.get(); }

    /**
     * @param value Sets the cancellation ID
     */
    public void setCancellationId(String value) { cancellationId.set(value); }

    /**
     * @return Cancellation ID as a StringProperty for JavaFX binding
     */
    public StringProperty cancellationIdProperty() { return cancellationId; }

    /**
     * @return Rental ID associated with this cancellation
     */
    public String getCancellationRentalId() { return cancellationRentalId.get(); }

    /**
     * @param value Sets the rental ID
     */
    public void setCancellationRentalId(String value) { cancellationRentalId.set(value); }

    /**
     * @return Rental ID as a StringProperty for JavaFX binding
     */
    public StringProperty cancellationRentalIdProperty() { return cancellationRentalId; }

    /**
     * @return Staff ID who processed the cancellation
     */
    public String getCancellationStaffId() { return cancellationStaffId.get(); }

    /**
     * @param value Sets the staff ID
     */
    public void setCancellationStaffId(String value) { cancellationStaffId.set(value); }

    /**
     * @return Staff ID as a StringProperty for JavaFX binding
     */
    public StringProperty cancellationStaffIdProperty() { return cancellationStaffId; }

    /**
     * @return Reason/description of the cancellation
     */
    public String getReason() { return reason.get(); }

    /**
     * @param value Sets the cancellation reason
     */
    public void setReason(String value) { reason.set(value); }

    /**
     * @return Reason as a StringProperty for JavaFX binding
     */
    public StringProperty reasonProperty() { return reason; }


    /**
     * @return Date when cancellation was recorded
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
     * Returns a string representation of the CancellationRecord for debugging.
     *
     * @return Formatted string containing all cancellation details
     */
    @Override
    public String toString() {
        return "CancellationRecord{" +
                "cancellationId='" + getCancellationId() + '\'' +
                ", rentalId='" + getCancellationRentalId() + '\'' +
                ", staffId='" + getCancellationStaffId() + '\'' +
                ", date=" + getTimestamp() +
                ", reason='" + getReason() + '\'' +
                '}';
    }
}
