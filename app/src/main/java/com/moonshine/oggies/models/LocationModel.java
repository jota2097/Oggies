package com.moonshine.oggies.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;

import java.util.List;

public class LocationModel {

    private String id;
    private String name;
    private String address;
    private String phoneNumber;
    private LatLng latLng;
    private OpeningHours openingHours;
    private double rating;
    private int priceLevel;
    private List<PhotoMetadata> photos;

    public LocationModel() {
    }

    public LocationModel(String id, String name, String address, String phoneNumber, LatLng latLng, OpeningHours openingHours, double rating, int priceLevel, List<PhotoMetadata> photos) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.latLng = latLng;
        this.openingHours = openingHours;
        this.rating = rating;
        this.priceLevel = priceLevel;
        this.photos = photos;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(int priceLevel) {
        this.priceLevel = priceLevel;
    }

    public List<PhotoMetadata> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoMetadata> photos) {
        this.photos = photos;
    }
}
