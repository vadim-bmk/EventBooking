package com.dvo.EventBooking.service.impl;

import com.dvo.EventBooking.entity.Booking;
import com.dvo.EventBooking.entity.Event;
import com.dvo.EventBooking.entity.User;
import com.dvo.EventBooking.exception.EntityNotFoundException;
import com.dvo.EventBooking.repository.BookingRepository;
import com.dvo.EventBooking.service.BookingService;
import com.dvo.EventBooking.service.EventService;
import com.dvo.EventBooking.service.UserService;
import com.dvo.EventBooking.web.model.request.UpdateBookingRequest;
import com.dvo.EventBooking.web.model.request.UpsertBookingRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final EventService eventService;
    private final UserService userService;

    @Override
    public Page<Booking> findAll(Pageable pageable) {
        log.info("Call findAll in BookingServiceImpl");

        return bookingRepository.findAll(pageable);
    }

    @Override
    public List<Booking> findAllByEventId(Long eventId) {
        log.info("Call findAllByEventId in BookingServiceImpl with event ID: {}", eventId);

        return bookingRepository.findAllByEventId(eventId);
    }

    @Override
    public Booking findById(Long id) {
        log.info("Call findById in BookingServiceImpl with ID: {}", id);

        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Booking not found with ID: {0}", id)));
    }

    @Override
    @Transactional
    public Booking save(UpsertBookingRequest booking) {
        log.info("Call save in BookingServiceImpl with booking: {}", booking);

        User user = userService.findById(booking.getUserId());
        Event event = eventService.findById(booking.getEventId());

        int count = bookingRepository.countByEventId(booking.getEventId());

        if (count == event.getMaxAttendees()) {
            throw new EntityNotFoundException(MessageFormat.format("Event with ID: {0} is full", booking.getEventId()));
        }

        Booking newBooking = Booking.builder()
                .user(user)
                .event(event)
                .createDate(LocalDate.now())
                .build();
        return bookingRepository.save(newBooking);
    }

    @Override
    @Transactional
    public Booking update(UpdateBookingRequest booking, Long id) {
        log.info("Call update in BookingServiceImpl for ID:{}, with booking: {}", id, booking);

        Booking existBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Booking not found with ID: {0}", id)));

        if (booking.getUserId() != null) {
            User user = userService.findById(booking.getUserId());
            existBooking.setUser(user);
        }

        if (booking.getEventId() != null) {
            Event event = eventService.findById(booking.getEventId());
            if (!Objects.equals(existBooking.getEvent().getId(), booking.getEventId())) {
                int count = bookingRepository.countByEventId(booking.getEventId());
                if (count == event.getMaxAttendees()) {
                    throw new EntityNotFoundException(MessageFormat.format("Event with ID: {0} is full", booking.getEventId()));
                }
            }
            existBooking.setEvent(event);
        }

        return bookingRepository.save(existBooking);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Call deleteById in BookingServiceImpl with ID: {}", id);

        bookingRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        log.info("Call deleteByUserId in BookingServiceImpl with userID: {}", userId);

        bookingRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteByEventId(Long eventId) {
        log.info("Call deleteByEventId in BookingServiceImpl with eventId: {}", eventId);

        bookingRepository.deleteByEventId(eventId);
    }

    @Override
    public int countByEventId(Long eventId) {

        return bookingRepository.countByEventId(eventId);
    }
}
