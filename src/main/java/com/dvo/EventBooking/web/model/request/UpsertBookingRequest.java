package com.dvo.EventBooking.web.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertBookingRequest {
    @NotNull(message = "Номер пользователя должен быть указан")
    private Long userId;

    @NotNull(message = "Номер мероприятия должен быть указан")
    private Long eventId;
}
