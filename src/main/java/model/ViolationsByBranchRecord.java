package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Data model class representing violation statistics for a branch.
 * Contains counts by violation type, penalty amounts, and temporal information.
 * Used for displaying branch violation reports and summaries.
 */
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

    /**
     * Constructs a new ViolationsByBranchRecord with the specified data.
     * @param branchId the unique identifier for the branch
     * @param branchName the display name of the branch
     * @param totalViolations total number of violations for the branch
     * @param lateReturnCount number of late return violations
     * @param carDamageCount number of car damage violations
     * @param trafficViolationCount number of traffic violation violations
     * @param cleaningFeeCount number of cleaning fee violations
     * @param otherViolationCount number of other type violations
     * @param totalPenaltyAmount total penalty amount for all violations
     * @param lastViolationDate timestamp of the most recent violation
     */
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

    // ============================================================
    // STANDARD GETTERS AND SETTERS
    // ============================================================

    /**
     * @return the branch identifier
     */
    public String getBranchId() {
        return branchId;
    }

    /**
     * @param branchId sets the branch identifier
     */
    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    /**
     * @return the branch display name
     */
    public String getBranchName() {
        return branchName;
    }

    /**
     * @param branchName sets the branch display name
     */
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    /**
     * @return total number of violations for this branch
     */
    public int getTotalViolations() {
        return totalViolations;
    }

    /**
     * @param totalViolations sets the total violation count
     */
    public void setTotalViolations(int totalViolations) {
        this.totalViolations = totalViolations;
    }

    /**
     * @return count of late return violations
     */
    public int getLateReturnCount() {
        return lateReturnCount;
    }

    /**
     * @param lateReturnCount sets the late return violation count
     */
    public void setLateReturnCount(int lateReturnCount) {
        this.lateReturnCount = lateReturnCount;
    }

    /**
     * @return count of car damage violations
     */
    public int getCarDamageCount() {
        return carDamageCount;
    }

    /**
     * @param carDamageCount sets the car damage violation count
     */
    public void setCarDamageCount(int carDamageCount) {
        this.carDamageCount = carDamageCount;
    }

    /**
     * @return count of traffic violation violations
     */
    public int getTrafficViolationCount() {
        return trafficViolationCount;
    }

    /**
     * @param trafficViolationCount sets the traffic violation count
     */
    public void setTrafficViolationCount(int trafficViolationCount) {
        this.trafficViolationCount = trafficViolationCount;
    }

    /**
     * @return count of cleaning fee violations
     */
    public int getCleaningFeeCount() {
        return cleaningFeeCount;
    }

    /**
     * @param cleaningFeeCount sets the cleaning fee violation count
     */
    public void setCleaningFeeCount(int cleaningFeeCount) {
        this.cleaningFeeCount = cleaningFeeCount;
    }

    /**
     * @return count of other type violations
     */
    public int getOtherViolationCount() {
        return otherViolationCount;
    }

    /**
     * @param otherViolationCount sets the other violation count
     */
    public void setOtherViolationCount(int otherViolationCount) {
        this.otherViolationCount = otherViolationCount;
    }

    /**
     * @return total penalty amount for all violations
     */
    public BigDecimal getTotalPenaltyAmount() {
        return totalPenaltyAmount;
    }

    /**
     * @param totalPenaltyAmount sets the total penalty amount
     */
    public void setTotalPenaltyAmount(BigDecimal totalPenaltyAmount) {
        this.totalPenaltyAmount = totalPenaltyAmount != null ? totalPenaltyAmount : BigDecimal.ZERO;
    }

    /**
     * @return timestamp of the most recent violation
     */
    public LocalDateTime getLastViolationDate() {
        return lastViolationDate;
    }

    /**
     * @param lastViolationDate sets the last violation timestamp
     */
    public void setLastViolationDate(LocalDateTime lastViolationDate) {
        this.lastViolationDate = lastViolationDate;
    }

    // ============================================================
    // CALCULATED PROPERTIES AND FORMATTING METHODS
    // ============================================================

    /**
     * Returns a formatted string representation of the last violation date.
     * Displays "Never" if no violations have occurred.
     * @return formatted date string or "Never" if no violations
     */
    public String getFormattedLastViolationDate() {
        if (lastViolationDate == null) {
            return "Never";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return lastViolationDate.format(formatter);
    }

    /**
     * Calculates the average penalty per violation for this branch.
     * Returns zero if there are no violations to avoid division by zero.
     * @return average penalty amount as BigDecimal
     */
    public BigDecimal getAveragePenalty() {
        if (totalViolations == 0) return BigDecimal.ZERO;
        return totalPenaltyAmount.divide(BigDecimal.valueOf(totalViolations), 2, BigDecimal.ROUND_HALF_UP);
    }
}