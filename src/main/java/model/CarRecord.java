package model;

/**
 * The {@code CarRecord} class represents a car entity in the Car Rental system.
 * It corresponds to a record in the {@code car_record} database table.
 */
public class CarRecord {

    private String carPlateNumber;
    private String carTransmission;
    private String carModel;
    private String carBrand;
    private int carYearManufactured;
    private int carMileage;
    private int carSeatNumber;

    /** NEW FIELD: rental fee per day */
    private double carRentalFee;

    private String carStatus;
    private String carBranchId;

    /** Default constructor */
    public CarRecord() {}

    /**
     * Full constructor including rental fee.
     */
    public CarRecord(String carPlateNumber, String carTransmission, String carModel,
                     String carBrand, int carYearManufactured, int carMileage,
                     int carSeatNumber, double carRentalFee,
                     String carStatus, String carBranchId) {

        this.carPlateNumber = carPlateNumber;
        this.carTransmission = carTransmission;
        this.carModel = carModel;
        this.carBrand = carBrand;
        this.carYearManufactured = carYearManufactured;
        this.carMileage = carMileage;
        this.carSeatNumber = carSeatNumber;
        this.carRentalFee = carRentalFee;
        this.carStatus = carStatus;
        this.carBranchId = carBranchId;
    }

    public String getCarPlateNumber() {
        return carPlateNumber;
    }

    public void setCarPlateNumber(String carPlateNumber) {
        this.carPlateNumber = carPlateNumber;
    }

    public String getCarTransmission() {
        return carTransmission;
    }

    public void setCarTransmission(String carTransmission) {
        this.carTransmission = carTransmission;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public int getCarYearManufactured() {
        return carYearManufactured;
    }

    public void setCarYearManufactured(int carYearManufactured) {
        this.carYearManufactured = carYearManufactured;
    }

    public int getCarMileage() {
        return carMileage;
    }

    public void setCarMileage(int carMileage) {
        this.carMileage = carMileage;
    }

    public int getCarSeatNumber() {
        return carSeatNumber;
    }

    public void setCarSeatNumber(int carSeatNumber) {
        this.carSeatNumber = carSeatNumber;
    }

    /** NEW GETTER */
    public double getCarRentalFee() {
        return carRentalFee;
    }

    /** NEW SETTER */
    public void setCarRentalFee(double carRentalFee) {
        this.carRentalFee = carRentalFee;
    }

    public String getCarStatus() {
        return carStatus;
    }

    public void setCarStatus(String carStatus) {
        this.carStatus = carStatus;
    }

    public String getCarBranchId() {
        return carBranchId;
    }

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

    public String getStringVersionOfCarRentalFee() {
        return String.valueOf(carRentalFee);
    }

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
                ", carRentalFee=" + carRentalFee +
                ", carStatus='" + carStatus + '\'' +
                ", carBranchId='" + carBranchId + '\'' +
                '}';
    }
}