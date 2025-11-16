package model;

/**
 * Represents a return transaction in the Car Rental system.
 * This model corresponds to the {@code return_details} table in the database.
 */
public class ReturnRecord {

    private String returnID;
    private String returnRentalID;
    private String returnStaffID;

    public ReturnRecord(){}

    public ReturnRecord(String returnID,
                        String returnRentalID,
                        String returnStaffID
    ){
        this.returnID = returnID;
        this.returnRentalID = returnRentalID;
        this.returnStaffID = returnStaffID;
    }


    public String getReturnID(){return returnID;}
    public void setReturnID(String returnID){ this.returnID = returnID;}

    public String getReturnRentalID(){return returnRentalID;}
    public void setReturnRentalID(String returnRentalID){ this.returnRentalID = returnRentalID;}

    public String getReturnStaffID(){return returnStaffID;}
    public void setReturnStaffID(String returnStaffID){ this.returnStaffID = returnStaffID;}

    @Override
    public String toString() {
        return "ReturnRecord{" +
                "returnId='" + returnID + '\'' +
                ", returnRentalID='" + returnRentalID + '\'' +
                ", returnStaffID='" + returnStaffID +
                '}';
    }
}
