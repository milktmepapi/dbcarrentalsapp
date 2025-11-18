package model;

import java.math.BigDecimal;

public class RevenueByBranchRecord {

    private String branchId;
    private String branchName;
    private BigDecimal rentalIncome;
    private BigDecimal penaltyIncome;

    public RevenueByBranchRecord(String branchId,
                                 String branchName,
                                 BigDecimal rentalIncome,
                                 BigDecimal penaltyIncome) {

        this.branchId = branchId;
        this.branchName = branchName;

        this.rentalIncome = rentalIncome != null ? rentalIncome : BigDecimal.ZERO;
        this.penaltyIncome = penaltyIncome != null ? penaltyIncome : BigDecimal.ZERO;
    }

    // ====================
    // Getters / Setters
    // ====================

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public BigDecimal getRentalIncome() {
        return rentalIncome;
    }

    public void setRentalIncome(BigDecimal rentalIncome) {
        this.rentalIncome = rentalIncome != null ? rentalIncome : BigDecimal.ZERO;
    }

    public BigDecimal getPenaltyIncome() {
        return penaltyIncome;
    }

    public void setPenaltyIncome(BigDecimal penaltyIncome) {
        this.penaltyIncome = penaltyIncome != null ? penaltyIncome : BigDecimal.ZERO;
    }

    // ====================
    // Derived Value
    // ====================
    public BigDecimal getTotalRevenue() {
        return rentalIncome.add(penaltyIncome);
    }
}