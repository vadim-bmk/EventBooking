package com.dvo.EventBooking.service.impl;

import com.dvo.EventBooking.entity.Event;
import com.dvo.EventBooking.exception.EntityNotFoundException;
import com.dvo.EventBooking.mapping.EventMapper;
import com.dvo.EventBooking.repository.BookingRepository;
import com.dvo.EventBooking.repository.EventRepository;
import com.dvo.EventBooking.web.model.filter.EventFilter;
import com.dvo.EventBooking.web.model.request.UpdateEventRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        when(eventRepository.findAll()).thenReturn(List.of(new Event()));

        assertEquals(1, eventService.findAll().size());
    }

    @Test
    void testFindAllByFilter() {
        EventFilter filter = new EventFilter();
        filter.setPageNumber(0);
        filter.setPageSize(10);
        filter.setName("event");

        List<Event> events = List.of(new Event(), new Event());
        Page<Event> page = new PageImpl<>(events, PageRequest.of(filter.getPageNumber(), filter.getPageSize()), events.size());

        when(eventRepository.findAll(
                any((Class<Specification<Event>>) (Class<?>) Specification.class),
                eq(PageRequest.of(filter.getPageNumber(), filter.getPageSize()))
        )).thenReturn(page);

        List<Event> result = eventService.findAllByFilter(filter);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(events, result);
    }

    @Test
    void testFindById() {
        Event event = new Event();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Event result = eventService.findById(1L);
        assertEquals(event, result);
    }

    @Test
    void testFindById_whenNotExists() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> eventService.findById(1L));
    }

    @Test
    void testSave() {
        Event event = Event.builder()
                .name("name")
                .description("description")
                .maxAttendees(0)
                .build();

        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventService.save(event);
        assertEquals(event, result);
    }

    @Test
    void testUpdate() {
        UpdateEventRequest request = new UpdateEventRequest();
        request.setMaxAttendees(10);

        Event existEvent = new Event();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(existEvent));
        when(bookingRepository.countByEventId(1L)).thenReturn(5);
        when(eventRepository.save(existEvent)).thenReturn(existEvent);

        Event result = eventService.update(request, 1L);
        verify(eventMapper).updateRequestToEvent(request, existEvent);
        verify(eventRepository).save(existEvent);
        assertEquals(existEvent, result);
    }

    @Test
    void testUpdate_whenNotExists() {
        UpdateEventRequest request = new UpdateEventRequest();
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.update(request, 1L));
    }

    @Test
    void testUpdate_whenFullAttendees() {
        UpdateEventRequest request = new UpdateEventRequest();
        request.setMaxAttendees(3);

        Event existEvent = new Event();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(existEvent));
        when(bookingRepository.countByEventId(1L)).thenReturn(5);

        assertThrows(IllegalArgumentException.class, () -> eventService.update(request, 1L));
    }

    @Test
    void testDeleteById() {
        eventService.deleteById(1L);

        verify(bookingRepository).deleteByEventId(1L);
        verify(eventRepository).deleteById(1L);
    }
}
