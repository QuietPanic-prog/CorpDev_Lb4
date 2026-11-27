package ru.kafpin.lb3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddressForm {
    @NotBlank(message = "Заполните поле")
    @Size(max = 120, message = "Не более 120 символов")
    private String postalCode;

    @NotBlank(message = "Заполните поле")
    @Size(max = 120, message = "Не более 120 символов")
    private String country;

    @NotBlank(message = "Заполните поле")
    @Size(max = 120, message = "Не более 120 символов")
    private String region;

    @NotBlank(message = "Заполните поле")
    @Size(max = 120, message = "Не более 120 символов")
    private String district;

    @NotBlank(message = "Заполните поле")
    @Size(max = 120, message = "Не более 120 символов")
    private String city;

    @NotBlank(message = "Заполните поле")
    @Size(max = 120, message = "Не более 120 символов")
    private String street;

    @NotBlank(message = "Заполните поле")
    @Size(max = 120, message = "Не более 120 символов")
    private String house;

    @NotBlank(message = "Заполните поле")
    @Size(max = 120, message = "Не более 120 символов")
    private String apartment;

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getHouse() { return house; }
    public void setHouse(String house) { this.house = house; }

    public String getApartment() { return apartment; }
    public void setApartment(String apartment) { this.apartment = apartment; }
}

