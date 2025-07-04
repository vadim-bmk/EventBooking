package com.dvo.EventBooking.web.controller;

import com.dvo.EventBooking.entity.Event;
import com.dvo.EventBooking.mapping.BookingMapper;
import com.dvo.EventBooking.mapping.EventMapper;
import com.dvo.EventBooking.service.BookingService;
import com.dvo.EventBooking.service.EventService;
import com.dvo.EventBooking.web.model.filter.EventFilter;
import com.dvo.EventBooking.web.model.request.UpdateEventRequest;
import com.dvo.EventBooking.web.model.request.UpsertEventRequest;
import com.dvo.EventBooking.web.model.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ModelListResponse<EventShortResponse>> findAll(@Valid EventFilter filter) {
        List<Event> eventList = eventService.findAllByFilter(filter);

        ModelListResponse<EventShortResponse> response = ModelListResponse.<EventShortResponse>builder()
                .totalCount((long) eventList.size())
                .data(eventList.stream().map(event -> {
                    EventShortResponse eventShortResponse = eventMapper.eventToShortResponse(event);
                    eventShortResponse.setAvailableAttendees(eventShortResponse.getMaxAttendees() - bookingService.countByEventId(event.getId()));

                    return eventShortResponse;
                }).toList())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<EventResponse> findById(@PathVariable Long id) {
        List<BookingShortResponse> bookings = bookingService.findAllByEventId(id).stream().map(bookingMapper::bookingToShortResponse).toList();
        EventResponse response = eventMapper.eventToResponse(eventService.findById(id), bookings);

        //response.setAvailableAttendees(response.getMaxAttendees() - bookingService.countByEventId(id));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EventResponse> create(@Valid @RequestBody UpsertEventRequest request) {
        Event event = eventService.save(eventMapper.requestToEvent(request));
        List<BookingShortResponse> bookings = bookingService.findAllByEventId(event.getId()).stream().map(bookingMapper::bookingToShortResponse).toList();
        EventResponse response = eventMapper.eventToResponse(event, bookings);
        //response.setAvailableAttendees(event.getMaxAttendees() - bookingService.countByEventId(event.getId()));

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EventResponse> update(@Valid @RequestBody UpdateEventRequest request,
                                                @PathVariable Long id) {

        Event updatedEvent = eventService.update(request, id);
        List<BookingShortResponse> bookings = bookingService.findAllByEventId(updatedEvent.getId()).stream().map(bookingMapper::bookingToShortResponse).toList();
        EventResponse response = eventMapper.eventToResponse(updatedEvent, bookings);
        //response.setAvailableAttendees(updatedEvent.getMaxAttendees() - bookingService.countByEventId(updatedEvent.getId()));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        eventService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
