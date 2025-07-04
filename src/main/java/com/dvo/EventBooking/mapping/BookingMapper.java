package com.dvo.EventBooking.mapping;

import com.dvo.EventBooking.entity.Booking;
import com.dvo.EventBooking.web.model.request.UpdateBookingRequest;
import com.dvo.EventBooking.web.model.response.BookingResponse;
import com.dvo.EventBooking.web.model.response.BookingShortResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class, EventMapper.class})
public interface BookingMapper {
    BookingResponse bookingToResponse(Booking booking);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "eventId", source = "event.id")
    BookingShortResponse bookingToShortResponse(Booking booking);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "event", ignore = true),
            @Mapping(target = "createDate", ignore = true)
    })
    Booking requestToBooking(UpdateBookingRequest request);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "event", ignore = true),
            @Mapping(target = "createDate", ignore = true)
    })
    void updateRequestToBooking(UpdateBookingRequest request, @MappingTarget Booking booking);

}
