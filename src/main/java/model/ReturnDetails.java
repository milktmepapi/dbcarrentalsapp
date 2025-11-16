package model;
/**
 * The {@code ReturnDetails} class represents a rental transaction in the Car Rental system.
 * It mirrors the structure of the {@code return_details} table in the database.
 * <p>
 * Each {@code ReturnDetails} contains information about a car return transaction,
 * including rental details,and staff who processed it.
 * </p>
 * <p>
 * This class is part of the Model layer (M in MVC) and is used to store
 * and manage return-related data before interaction with the database.
 * </p>
 *
 * @author Galicia
 * @author Marcelino
 * @author Samarista
 * @author Sy
 */
public class ReturnDetails {
    private String returnID;
    private String returnRentalID;
    private String returnStaffID;

    /**
     * Default constructor that creates an empty {@code ReturnDetails}.
     * Useful for frameworks or libraries that require a no-argument constructor.
     */

    public ReturnDetails(){}

    /**
     * Creates a new {@code ReturnDetails} with the specified details.
     *
     * @param returnID           the unique identifiers for return
     * @param returnRentalID    the id for return rentals
     * @param returnStaffID     the id for staff
     */

    public ReturnDetails(String returnID, String returnRentalID, String returnStaffID){
        this.returnID = returnID;
        this.returnRentalID = returnRentalID;
        this.returnStaffID = returnStaffID;
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

    @Override
    public String toString() {
        return "ReturnDetails{" +
                "returnId='" + returnID + '\'' +
                ", returnRentalID='" + returnRentalID + '\'' +
                ", returnStaffID='" + returnStaffID +
                '}';
    }
}

