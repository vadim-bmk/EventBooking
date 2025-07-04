package com.dvo.EventBooking.web.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {
    @Email
    private String email;

    @Size(min = 5, max = 30, message = "Пароль не может быть меньше {min} и больше {max}!")
    private String password;

    private String firstName;

    private String lastName;
}
