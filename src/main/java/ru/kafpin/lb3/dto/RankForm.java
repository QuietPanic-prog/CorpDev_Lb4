package ru.kafpin.lb3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RankForm {
    @NotBlank(message = "Заполните поле")
    @Size(max = 80, message = "Не более 80 символов")
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

