package ru.kafpin.lb4.dto;

import java.time.LocalDate;
import ru.kafpin.lb4.model.Serviceman;

public class ServicemanDTO {

    private String lastName;
    private String firstName;
    private String patronymic;
    private String nationality;
    private String position;
    private String postalCode;
    private String country;
    private String region;
    private String district;
    private String city;
    private String street;
    private String house;
    private String apartment;
    private LocalDate birthDate;
    private String rank;

    public ServicemanDTO() {
    }

    public ServicemanDTO(Serviceman serviceman) {
        this.lastName = serviceman.getLastName();
        this.firstName = serviceman.getFirstName();
        this.patronymic = serviceman.getPatronymic();
        this.nationality = serviceman.getNationality();
        this.position = serviceman.getPosition();
        this.postalCode = serviceman.getPostalCode();
        this.country = serviceman.getCountry();
        this.region = serviceman.getRegion();
        this.district = serviceman.getDistrict();
        this.city = serviceman.getCity();
        this.street = serviceman.getStreet();
        this.house = serviceman.getHouse();
        this.apartment = serviceman.getApartment();
        this.birthDate = serviceman.getBirthDate();
        this.rank = serviceman.getRank().getName();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
