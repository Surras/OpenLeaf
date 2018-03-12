package de.ironicdev.spring.openleaf.models;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Location {

    @Id
    private String locationId;
    private double latitude;
    private double longitude;
    private String finder;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date foundAt;
    private String note;

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFinder() {
        return finder;
    }

    public void setFinder(String finder) {
        this.finder = finder;
    }

    public Date getFoundAt() {
        return foundAt;
    }

    public void setFoundAt(Date foundAt) {
        this.foundAt = foundAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
