package model;
/**
 * The {@code CarRecord} class represents a car entity in the Car Rental system.
 * <p>
 * It corresponds to a record in the {@code car_record} database table and stores
 * details about a car such as its plate number, brand, model, and current status.
 * </p>
 * <p>
 * This class is part of the Model layer (M in MVC), serving as a data holder for
 * transferring car-related information between the database and application logic.
 * </p>
 *
 * @author Galicia
 * @author Marcelino
 * @author Samarista
 * @author Sy
 * @version 1.0
 */
public class CarRecord {
    /** The unique plate number of the car (Primary Key). */
    private String carPlateNumber;

    /** The transmission type of the car — either 'Manual' or 'Automatic'. */
    private String carTransmission;

    /** The model name of the car. */
    private String carModel;

    /** The brand or manufacturer of the car. */
    private String carBrand;

    /** The year the car was manufactured. */
    private int carYearManufactured;

    /** The total mileage of the car. */
    private int carMileage;

    /** The number of seats in the car. */
    private int carSeatNumber;

    /** The current status of the car (e.g., 'Available', 'Rented', 'Under Maintenance'). */
    private String carStatus;

    /** The ID of the branch to which the car belongs (Foreign Key → branch_record.branch_id). */
    private String carBranchId;

    /**
     * Default constructor for {@code CarRecord}.
     * <p>
     * Useful for frameworks or when creating an empty car record instance.
     * </p>
     */
    public CarRecord() {}

    /**
     * Constructs a {@code CarRecord} with all specified details.
     *
     * @param carPlateNumber      the unique plate number of the car
     * @param carTransmission     the transmission type (Manual or Automatic)
     * @param carModel            the car's model name
     * @param carBrand            the brand of the car
     * @param carYearManufactured the year the car was manufactured
     * @param carMileage          the current mileage of the car
     * @param carSeatNumber       the number of seats in the car
     * @param carStatus           the current status (Available, Rented, or Under Maintenance)
     * @param carBranchId         the ID of the branch where the car belongs
     */
    public CarRecord(String carPlateNumber, String carTransmission, String carModel,
                     String carBrand, int carYearManufactured, int carMileage,
                     int carSeatNumber, String carStatus, String carBranchId) {
        this.carPlateNumber = carPlateNumber;
        this.carTransmission = carTransmission;
        this.carModel = carModel;
        this.carBrand = carBrand;
        this.carYearManufactured = carYearManufactured;
        this.carMileage = carMileage;
        this.carSeatNumber = carSeatNumber;
        this.carStatus = carStatus;
        this.carBranchId = carBranchId;
    }

    /**
     * Returns the car's plate number.
     *
     * @return the car's plate number
     */
    public String getCarPlateNumber() {
        return carPlateNumber;
    }

    /**
     * Sets the car's plate number.
     *
     * @param carPlateNumber the unique plate number of the car
     */
    public void setCarPlateNumber(String carPlateNumber) {
        this.carPlateNumber = carPlateNumber;
    }

    /**
     * Returns the car's transmission type.
     *
     * @return the transmission type ('Manual' or 'Automatic')
     */
    public String getCarTransmission() {
        return carTransmission;
    }

    /**
     * Sets the car's transmission type.
     *
     * @param carTransmission the transmission type ('Manual' or 'Automatic')
     */
    public void setCarTransmission(String carTransmission) {
        this.carTransmission = carTransmission;
    }

    /**
     * Returns the car's model name.
     *
     * @return the model name
     */
    public String getCarModel() {
        return carModel;
    }

    /**
     * Sets the car's model name.
     *
     * @param carModel the model name
     */
    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    /**
     * Returns the car's brand.
     *
     * @return the brand of the car
     */
    public String getCarBrand() {
        return carBrand;
    }


    /**
     * Sets the car's brand.
     *
     * @param carBrand the brand or manufacturer name
     */
    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    /**
     * Returns the year the car was manufactured.
     *
     * @return the manufacturing year
     */
    public int getCarYearManufactured() {
        return carYearManufactured;
    }

    /**
     * Sets the year the car was manufactured.
     *
     * @param carYearManufactured the manufacturing year
     */
    public void setCarYearManufactured(int carYearManufactured) {
        this.carYearManufactured = carYearManufactured;
    }

    /**
     * Returns the car's mileage.
     *
     * @return the mileage value
     */
    public int getCarMileage() {
        return carMileage;
    }

    /**
     * Sets the car's mileage.
     *
     * @param carMileage the mileage value
     */
    public void setCarMileage(int carMileage) {
        this.carMileage = carMileage;
    }

    /**
     * Returns the car's seat capacity.
     *
     * @return the number of seats
     */
    public int getCarSeatNumber() {
        return carSeatNumber;
    }

    /**
     * Sets the car's seat capacity.
     *
     * @param carSeatNumber the number of seats
     */
    public void setCarSeatNumber(int carSeatNumber) {
        this.carSeatNumber = carSeatNumber;
    }

    /**
     * Returns the car's current status.
     *
     * @return the status (Available, Rented, or Under Maintenance)
     */
    public String getCarStatus() {
        return carStatus;
    }

    /**
     * Sets the car's current status.
     *
     * @param carStatus the status (Available, Rented, or Under Maintenance)
     */
    public void setCarStatus(String carStatus) {
        this.carStatus = carStatus;
    }

    /**
     * Returns the ID of the branch where the car is located.
     *
     * @return the branch ID
     */
    public String getCarBranchId() {
        return carBranchId;
    }

    /**
     * Sets the ID of the branch where the car is located.
     *
     * @param carBranchId the branch ID
     */
    public void setCarBranchId(String carBranchId) {
        this.carBranchId = carBranchId;
    }

    public String getStringVersionOfYearManufactured() {
        return String.valueOf(carYearManufactured);
    }

    public String getStringVersionOfCarMileage() {
        return String.valueOf(carMileage);
    }

    public String getStringVersionOfCarSeatNumber() {
        return String.valueOf(carSeatNumber);
    }

    /**
     * Returns a string representation of the {@code CarRecord}.
     *
     * @return a formatted string containing all car details
     */
    @Override
    public String toString() {
        return "CarRecord{" +
                "carPlateNumber='" + carPlateNumber + '\'' +
                ", carTransmission='" + carTransmission + '\'' +
                ", carModel='" + carModel + '\'' +
                ", carBrand='" + carBrand + '\'' +
                ", carYearManufactured=" + carYearManufactured +
                ", carMileage=" + carMileage +
                ", carSeatNumber=" + carSeatNumber +
                ", carStatus='" + carStatus + '\'' +
                ", carBranchId='" + carBranchId + '\'' +
                '}';
    }
}
