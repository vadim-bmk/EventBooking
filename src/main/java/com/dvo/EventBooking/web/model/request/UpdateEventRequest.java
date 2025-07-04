package com.dvo.EventBooking.web.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEventRequest {
    private String name;
    private String description;
    private String city;
    private String address;
    private LocalDate date;
    private Integer maxAttendees;
}
