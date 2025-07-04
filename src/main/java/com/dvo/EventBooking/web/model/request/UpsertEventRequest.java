package com.dvo.EventBooking.web.model.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertEventRequest {
    @NotBlank(message = "Поле название должно быть заполнено")
    private String name;

    @NotBlank(message = "Поле описание должно быть заполнено")
    private String description;

    @NotBlank(message = "Поле город должно быть заполнено")
    private String city;

    @NotBlank(message = "Поле адрес должно быть заполнено")
    private String address;

    @NotNull(message = "Поле дата должно быть заполнено")
    @FutureOrPresent(message = "Дата мероприятия должна быть сегодня или в будущем")
    private LocalDate date;

    @Min(value = 1, message = "Максимальное количество участников должно быть больше 0")
    private int maxAttendees;
}
