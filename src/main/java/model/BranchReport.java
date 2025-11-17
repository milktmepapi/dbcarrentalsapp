package model;

import java.util.Date;

public class BranchReport {
    private String branchName;
    private String carTransmission;
    private String rentalDuration;
    private int totalRentals;

    public BranchReport(String branchName, String carTransmission, String rentalDuration, int totalRentals){
        this.branchName = branchName;
        this.carTransmission = carTransmission;
        this.rentalDuration = rentalDuration;
        this.totalRentals = totalRentals;
    }
    public String getBranchName() { return branchName; }
    public String getCarTransmission() { return carTransmission; }
    public String getRentalDuration() { return rentalDuration; }
    public int getTotalRentals() { return totalRentals; }
}

