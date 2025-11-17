package model;

import java.math.BigDecimal;

public class RevenueByBranchRecord {
    private String branchId;
    private String branchName;
    private BigDecimal rentalIncome;
    private BigDecimal penaltyIncome;
    private BigDecimal salaryExpenses;

    public RevenueByBranchRecord(String branchId,
                                 String branchName,
                                 BigDecimal rentalIncome,
                                 BigDecimal penaltyIncome,
                                 BigDecimal salaryExpenses) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.rentalIncome = rentalIncome != null ? rentalIncome : BigDecimal.ZERO;
        this.penaltyIncome = penaltyIncome != null ? penaltyIncome : BigDecimal.ZERO;
        this.salaryExpenses = salaryExpenses != null ? salaryExpenses : BigDecimal.ZERO;
    }

    // Getters and Setters
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

    public BigDecimal getSalaryExpenses() {
        return salaryExpenses;
    }

    public void setSalaryExpenses(BigDecimal salaryExpenses) {
        this.salaryExpenses = salaryExpenses != null ? salaryExpenses : BigDecimal.ZERO;
    }

    // Helper method for net revenue
    public BigDecimal getNetRevenue() {
        return rentalIncome.add(penaltyIncome).subtract(salaryExpenses);
    }
}