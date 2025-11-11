package model;
/**
 * The {@code LocationRecord} class represents a location entry in the Car Rental system.
 * It mirrors the structure of the {@code location_record} table in the database.
 * <p>
 * Each {@code LocationRecord} contains a unique ID, city, and province.
 * The combination of city and province must be unique, reflecting the database constraint.
 * </p>
 * <p>
 * This class is part of the Model layer (M in MVC) and is responsible for
 * storing and managing location-related data before it is persisted to the database.
 * </p>
 *
 * @author Galicia
 * @author Marcelino
 * @author Samarista
 * @author Sy
 */
public class LocationRecord {
    private String locationId;
    private String locationCity;
    private String locationProvince;

    /**
     * Default constructor that creates an empty {@code LocationRecord}.
     * Useful for frameworks or libraries that require a no-argument constructor.
     */
    public LocationRecord() {}

    /**
     * Creates a new {@code LocationRecord} with the specified details.
     *
     * @param locationId       the unique identifier for the location
     * @param locationCity     the city name of the location
     * @param locationProvince the province name where the location is situated
     */
    public LocationRecord(String locationId, String locationCity, String locationProvince) {
        this.locationId = locationId;
        this.locationCity = locationCity;
        this.locationProvince = locationProvince;
    }

    /**
     * Returns the location ID.
     *
     * @return the unique location ID
     */
    public String getLocationId() {
        return locationId;
    }

    /**
     * Sets the location ID.
     *
     * @param locationId the location ID to set
     */
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    /**
     * Returns the city name of the location.
     *
     * @return the city name
     */
    public String getLocationCity() {
        return locationCity;
    }

    /**
     * Sets the city name of the location.
     *
     * @param locationCity the city name to set
     */
    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    /**
     * Returns the province name of the location.
     *
     * @return the province name
     */
    public String getLocationProvince() {
        return locationProvince;
    }

    /**
     * Sets the province name of the location.
     *
     * @param locationProvince the province name to set
     */
    public void setLocationProvince(String locationProvince) {
        this.locationProvince = locationProvince;
    }

    /**
     * Returns a string representation of this {@code LocationRecord},
     * useful for debugging or logging.
     *
     * @return a formatted string containing the location details
     */
    @Override
    public String toString() {
        return "LocationRecord{" +
                "locationId='" + locationId + '\'' +
                ", locationCity='" + locationCity + '\'' +
                ", locationProvince='" + locationProvince + '\'' +
                '}';
    }
}
