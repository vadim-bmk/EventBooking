package com.dvo.EventBooking.service.impl;

import com.dvo.EventBooking.entity.Booking;
import com.dvo.EventBooking.entity.Event;
import com.dvo.EventBooking.entity.User;
import com.dvo.EventBooking.exception.EntityNotFoundException;
import com.dvo.EventBooking.repository.BookingRepository;
import com.dvo.EventBooking.repository.EventRepository;
import com.dvo.EventBooking.service.EventService;
import com.dvo.EventBooking.service.UserService;
import com.dvo.EventBooking.web.model.request.UpdateBookingRequest;
import com.dvo.EventBooking.web.model.request.UpsertBookingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;

    @InjectMocks
    BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        Page<Booking> bookings = new PageImpl<>(List.of(new Booking(), new Booking()));
        when(bookingRepository.findAll(any(Pageable.class))).thenReturn(bookings);

        Page<Booking> result = bookingService.findAll(PageRequest.of(0, 10));
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testFindAllByEventId() {
        List<Booking> bookings = List.of(new Booking(), new Booking());
        when(bookingRepository.findAllByEventId(1L)).thenReturn(bookings);

        List<Booking> result = bookingService.findAllByEventId(1L);
        assertEquals(2, result.size());
    }

    @Test
    void testFindById() {
        Booking booking = new Booking();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.findById(1L);
        assertEquals(booking, result);
    }

    @Test
    void testFindById_whenNotExists() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.findById(1L));
    }

    @Test
    void testSave() {
        User user = User.builder().id(1L).build();
        Event event = Event.builder().id(1L).maxAttendees(5).build();

        UpsertBookingRequest booking = UpsertBookingRequest.builder()
                .userId(1L)
                .eventId(1L)
                .build();

        when(userService.findById(1L)).thenReturn(user);
        when(eventService.findById(1L)).thenReturn(event);
        when(bookingRepository.countByEventId(1L)).thenReturn(3);
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.save(booking);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(event, result.getEvent());

        verify(userService).findById(1L);
        verify(eventService).findById(1L);
        verify(bookingRepository).countByEventId(1L);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testSave_whenEventFull() {
        UpsertBookingRequest booking = UpsertBookingRequest.builder()
                .userId(1L)
                .eventId(1L)
                .build();

        Event event = Event.builder().id(1L).maxAttendees(5).build();

        when(userService.findById(1L)).thenReturn(new User());
        when(eventService.findById(1L)).thenReturn(event);
        when(bookingRepository.countByEventId(1L)).thenReturn(5);

        assertThrows(EntityNotFoundException.class, () -> bookingService.save(booking));
        verify(userService).findById(1L);
        verify(eventService).findById(1L);
        verify(bookingRepository).countByEventId(1L);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testUpdate() {
        User user = User.builder().id(1L).build();
        Event event = Event.builder().id(1L).maxAttendees(5).build();

        Booking existBooking = Booking.builder()
                .id(1L)
                .event(event)
                .user(user)
                .build();

        UpdateBookingRequest request = UpdateBookingRequest.builder()
                .userId(1L)
                .eventId(1L)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existBooking));
        when(userService.findById(1L)).thenReturn(user);
        when(eventService.findById(1L)).thenReturn(event);
        when(bookingRepository.countByEventId(1L)).thenReturn(3);
        when(bookingRepository.save(existBooking)).thenReturn(existBooking);

        Booking result = bookingService.update(request, 1L);

        assertEquals(user, result.getUser());
        assertEquals(event, result.getEvent());
        verify(bookingRepository).save(existBooking);
    }

    @Test
    void testUpdate_whenEventFull() {
        User user = User.builder().id(1L).build();
        Event event = Event.builder().id(1L).maxAttendees(10).build();
        Event newEvent = Event.builder().id(2L).maxAttendees(5).build();

        Booking existBooking = Booking.builder()
                .id(1L)
                .event(event)
                .user(user)
                .build();

        UpdateBookingRequest request = UpdateBookingRequest.builder()
                .userId(1L)
                .eventId(2L)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existBooking));
        when(userService.findById(1L)).thenReturn(user);
        when(eventService.findById(2L)).thenReturn(newEvent);
        when(bookingRepository.countByEventId(2L)).thenReturn(5);

        assertThrows(EntityNotFoundException.class, () -> bookingService.update(request, 1L));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testUpdate_whenBookingNotExists(){
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());
        UpdateBookingRequest request = UpdateBookingRequest.builder()
                .userId(1L)
                .eventId(1L)
                .build();

        assertThrows(EntityNotFoundException.class, () -> bookingService.update(request, 1L));
    }

    @Test
    void testDeleteById() {
        bookingService.deleteById(1L);

        verify(bookingRepository).deleteById(1L);
    }

    @Test
    void testDeleteByUserId() {
        bookingService.deleteByUserId(1L);

        verify(bookingRepository).deleteByUserId(1L);
    }

    @Test
    void testDeleteByEventId(){
        bookingService.deleteByEventId(1L);

        verify(bookingRepository).deleteByEventId(1L);
    }
}
