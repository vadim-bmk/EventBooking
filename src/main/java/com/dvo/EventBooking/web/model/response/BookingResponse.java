package com.dvo.EventBooking.web.model.response;

import com.dvo.EventBooking.entity.Event;
import com.dvo.EventBooking.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private UserResponse user;
    private EventShortResponse event;
    private LocalDate createDate;
}
