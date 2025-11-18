package model;

public class CarUtilizationReport {
    private String branchName;
    private String carPlateNumber;
    private String carModel;
    private String carBrand;
    private String carTransmission;

    private int totalRentals;
    private int totalRentalDays;
    private double utilizationRate;


    public CarUtilizationReport(String branchName, String carPlateNumber, String carModel, String carBrand, String carTransmission, int totalRentals, int totalRentalDays, double utilizationRate){
        this.branchName = branchName;
        this.carPlateNumber = carPlateNumber;
        this.carModel = carModel;
        this.carBrand = carBrand;
        this.carTransmission = carTransmission;
        this.totalRentals = totalRentals;
        this.totalRentalDays = totalRentalDays;
        this.utilizationRate = utilizationRate;
    }
    public String getBranchName() { return branchName; }
    public String getCarPlateNumber() { return carPlateNumber; }
    public String getCarModel(){ return carModel; }
    public String getCarBrand(){ return carBrand; }
    public String getCarTransmission() { return carTransmission; }
    public int getTotalRentals() { return totalRentals; }
    public int getTotalRentalDays() { return totalRentalDays; }
    public double getUtilizationRate() { return utilizationRate; }
}
