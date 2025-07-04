package com.dvo.EventBooking.web.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertUserRequest {
    @NotBlank(message = "Поле логин (username) должно быть заполнено и уникально")
    private String username;

    @NotBlank(message = "Поле пароль (password) должно быть заполнено")
    @Size(min = 5, max = 30, message = "Пароль не может быть меньше {min} и больше {max}!")
    private String password;

    @NotBlank(message = "Поле электронная почта (email) должно быть заполнено")
    @Email
    private String email;

    @NotBlank(message = "Поле имя должно быть заполнено")
    private String firstName;

    @NotBlank(message = "Поле фамилия должно быть заполнено")
    private String lastName;
}
