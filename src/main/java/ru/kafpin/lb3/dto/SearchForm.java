package ru.kafpin.lb3.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class SearchForm {
    @Size(max = 120, message = "Не более 120 символов")
    private String lastName = "";

    @Size(max = 120, message = "Не более 120 символов")
    private String nationality = "";

    @Size(max = 120, message = "Не более 120 символов")
    private String city = "";

    @Size(max = 120, message = "Не более 120 символов")
    private String position = "";

    @Positive(message = "Некорректное звание")
    private Long rankId;

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public Long getRankId() { return rankId; }
    public void setRankId(Long rankId) { this.rankId = rankId; }
}

