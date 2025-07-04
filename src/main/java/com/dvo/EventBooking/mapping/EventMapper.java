package com.dvo.EventBooking.mapping;

import com.dvo.EventBooking.entity.Event;
import com.dvo.EventBooking.service.BookingService;
import com.dvo.EventBooking.web.model.request.UpdateEventRequest;
import com.dvo.EventBooking.web.model.request.UpsertEventRequest;
import com.dvo.EventBooking.web.model.response.BookingShortResponse;
import com.dvo.EventBooking.web.model.response.EventResponse;
import com.dvo.EventBooking.web.model.response.EventShortResponse;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, uses = {BookingMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class EventMapper {
    @Autowired
    BookingService bookingService = null;

    @Mapping(target = "id", ignore = true)
    public abstract Event requestToEvent(UpsertEventRequest request);

    @Mapping(target = "id", ignore = true)
    public abstract void updateRequestToEvent(UpdateEventRequest request, @MappingTarget Event event);

    @Mapping(target = "availableAttendees", expression = "java(event.getMaxAttendees() - bookingService.countByEventId(event.getId()))")
    public abstract EventShortResponse eventToShortResponse(Event event);

    @Mappings({
            @Mapping(target = "bookings", source = "bookings"),
            @Mapping(target = "availableAttendees", expression = "java(event.getMaxAttendees() - bookingService.countByEventId(event.getId()))")
    })
    public abstract EventResponse eventToResponse(Event event, List<BookingShortResponse> bookings);
}
