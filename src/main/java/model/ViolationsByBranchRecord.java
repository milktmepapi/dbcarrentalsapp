package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ViolationsByBranchRecord {
    private String branchId;
    private String branchName;
    private int totalViolations;
    private int lateReturnCount;
    private int carDamageCount;
    private int trafficViolationCount;
    private int cleaningFeeCount;
    private int otherViolationCount;
    private BigDecimal totalPenaltyAmount;
    private LocalDateTime lastViolationDate;

    public ViolationsByBranchRecord(String branchId,
                                    String branchName,
                                    int totalViolations,
                                    int lateReturnCount,
                                    int carDamageCount,
                                    int trafficViolationCount,
                                    int cleaningFeeCount,
                                    int otherViolationCount,
                                    BigDecimal totalPenaltyAmount,
                                    LocalDateTime lastViolationDate) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.totalViolations = totalViolations;
        this.lateReturnCount = lateReturnCount;
        this.carDamageCount = carDamageCount;
        this.trafficViolationCount = trafficViolationCount;
        this.cleaningFeeCount = cleaningFeeCount;
        this.otherViolationCount = otherViolationCount;
        this.totalPenaltyAmount = totalPenaltyAmount != null ? totalPenaltyAmount : BigDecimal.ZERO;
        this.lastViolationDate = lastViolationDate;
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

    public int getTotalViolations() {
        return totalViolations;
    }

    public void setTotalViolations(int totalViolations) {
        this.totalViolations = totalViolations;
    }

    public int getLateReturnCount() {
        return lateReturnCount;
    }

    public void setLateReturnCount(int lateReturnCount) {
        this.lateReturnCount = lateReturnCount;
    }

    public int getCarDamageCount() {
        return carDamageCount;
    }

    public void setCarDamageCount(int carDamageCount) {
        this.carDamageCount = carDamageCount;
    }

    public int getTrafficViolationCount() {
        return trafficViolationCount;
    }

    public void setTrafficViolationCount(int trafficViolationCount) {
        this.trafficViolationCount = trafficViolationCount;
    }

    public int getCleaningFeeCount() {
        return cleaningFeeCount;
    }

    public void setCleaningFeeCount(int cleaningFeeCount) {
        this.cleaningFeeCount = cleaningFeeCount;
    }

    public int getOtherViolationCount() {
        return otherViolationCount;
    }

    public void setOtherViolationCount(int otherViolationCount) {
        this.otherViolationCount = otherViolationCount;
    }

    public BigDecimal getTotalPenaltyAmount() {
        return totalPenaltyAmount;
    }

    public void setTotalPenaltyAmount(BigDecimal totalPenaltyAmount) {
        this.totalPenaltyAmount = totalPenaltyAmount != null ? totalPenaltyAmount : BigDecimal.ZERO;
    }

    public LocalDateTime getLastViolationDate() {
        return lastViolationDate;
    }

    public void setLastViolationDate(LocalDateTime lastViolationDate) {
        this.lastViolationDate = lastViolationDate;
    }

    // Formatted date string for table display
    public String getFormattedLastViolationDate() {
        if (lastViolationDate == null) {
            return "Never";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return lastViolationDate.format(formatter);
    }

    // Helper method for average penalty
    public BigDecimal getAveragePenalty() {
        if (totalViolations == 0) return BigDecimal.ZERO;
        return totalPenaltyAmount.divide(BigDecimal.valueOf(totalViolations), 2, BigDecimal.ROUND_HALF_UP);
    }
}