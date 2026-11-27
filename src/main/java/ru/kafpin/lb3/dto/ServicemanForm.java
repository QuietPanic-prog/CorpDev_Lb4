package ru.kafpin.lb3.dto;

import java.time.LocalDate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

public class ServicemanForm {
    @NotBlank(message = "Заполните поле")
    @Size(max = 80, message = "Не более 80 символов")
    private String lastName;

    @NotBlank(message = "Заполните поле")
    @Size(max = 80, message = "Не более 80 символов")
    private String firstName;

    @NotBlank(message = "Заполните поле")
    @Size(max = 80, message = "Не более 80 символов")
    private String patronymic;

    @NotBlank(message = "Заполните поле")
    @Size(max = 80, message = "Не более 80 символов")
    private String nationality;

    @NotNull(message = "Укажите дату рождения")
    @Past(message = "Дата рождения должна быть в прошлом")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    @NotBlank(message = "Заполните поле")
    @Size(max = 120, message = "Не более 120 символов")
    private String position;

    @NotNull(message = "Выберите звание")
    @Positive(message = "Некорректное звание")
    private Long rankId;

    @Valid
    @NotNull(message = "Укажите адрес")
    private AddressForm address = new AddressForm();

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getPatronymic() { return patronymic; }
    public void setPatronymic(String patronymic) { this.patronymic = patronymic; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public Long getRankId() { return rankId; }
    public void setRankId(Long rankId) { this.rankId = rankId; }

    public AddressForm getAddress() { return address; }
    public void setAddress(AddressForm address) { this.address = address; }
}

