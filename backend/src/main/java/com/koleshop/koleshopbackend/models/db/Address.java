package com.koleshop.koleshopbackend.models.db;

/**
 * Created by Gundeep on 10/01/16.
 */
public class Address {

    Long id;
    Long userId;
    String name;
    String address;
    int addressType;
    Long phoneNumber;
    int countryCode;
    String nickname;
    Double gpsLong;
    Double gpsLat;

    public Address() {

    }

    public Address(Long id, Long userId, String name, String address, int addressType, Long phoneNumber, int countryCode, String nickname, Double gpsLong, Double gpsLat) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.addressType = addressType;
        this.phoneNumber = phoneNumber;
        this.countryCode = countryCode;
        this.nickname = nickname;
        this.gpsLong = gpsLong;
        this.gpsLat = gpsLat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public int getAddressType() {
        return addressType;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(int countryCode) {
        this.countryCode = countryCode;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Double getGpsLong() {
        return gpsLong;
    }

    public void setGpsLong(Double gpsLong) {
        this.gpsLong = gpsLong;
    }

    public Double getGpsLat() {
        return gpsLat;
    }

    public void setGpsLat(Double gpsLat) {
        this.gpsLat = gpsLat;
    }
}
