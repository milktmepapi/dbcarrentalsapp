package model;

public class CancellationDetails {
    private String returnID;
    private String returnRentalID;
    private String returnStaffID;
    private String reason;

    /**
     * Default constructor that creates an empty {@code CancelaltionDetails}.
     * Useful for frameworks or libraries that require a no-argument constructor.
     */

    public CancellationDetails(){}

    /**
     * Creates a new {@code CancellationDetails} with the specified details.
     *
     * @param returnID           the unique identifiers for return
     * @param returnRentalID    the id for return rentals
     * @param returnStaffID     the id for staff
     */

    public CancellationDetails(String returnID, String returnRentalID, String returnStaffID, String reason){
        this.returnID = returnID;
        this.returnRentalID = returnRentalID;
        this.returnStaffID = returnStaffID;
        this.reason = reason;
    }

    /** @return the return ID */
    public String getReturnID(){return returnID;}

    /** @param returnID sets the return ID */
    public void setReturnID(String returnID){ this.returnID = returnID;}

    /** @return the return rental ID */
    public String getReturnRentalID(){return returnRentalID;}

    /** @param returnRentalID sets the return rental ID */
    public void setReturnRentalID(String returnRentalID){ this.returnRentalID = returnRentalID;}

    /** @return the return staff ID */
    public String getReturnStaffID(){return returnStaffID;}

    /** @param returnStaffID sets the return staff ID */
    public void setReturnStaffID(String returnStaffID){ this.returnStaffID = returnStaffID;}
    /** @param reason sets the reason ID */
    public void setReason(String reason){ this.reason = reason;}

    @Override
    public String toString() {
        return "ReturnDetails{" +
                "returnId='" + returnID + '\'' +
                ", returnRentalID='" + returnRentalID + '\'' +
                ", returnStaffID='" + returnStaffID +
                ", reason='" + reason +
                '}';
    }
}
