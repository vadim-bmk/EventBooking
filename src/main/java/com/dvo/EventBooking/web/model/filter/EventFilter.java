package com.dvo.EventBooking.web.model.filter;

import com.dvo.EventBooking.validation.EventFilterValid;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Data
@EventFilterValid
public class EventFilter {
    private Integer pageNumber;
    private Integer pageSize;

    private LocalDate date;
    private String name;
    private String description;
    private String city;
    private String address;
    private Integer maxAttendees;
}
