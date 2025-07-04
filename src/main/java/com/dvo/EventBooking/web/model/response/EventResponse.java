package com.dvo.EventBooking.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventResponse {
    private Long id;
    private String name;
    private String description;
    private String city;
    private String address;
    private LocalDate date;
    private int maxAttendees;
    private int availableAttendees;
    private List<BookingShortResponse> bookings;
}
