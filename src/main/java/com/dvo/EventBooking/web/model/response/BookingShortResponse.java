package com.dvo.EventBooking.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingShortResponse {
    private Long id;
    private Long userId;
    private Long eventId;
    private LocalDate createDate;
}
