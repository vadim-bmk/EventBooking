package com.dvo.EventBooking.service.impl;

import com.dvo.EventBooking.entity.Event;
import com.dvo.EventBooking.exception.EntityNotFoundException;
import com.dvo.EventBooking.mapping.EventMapper;
import com.dvo.EventBooking.repository.BookingRepository;
import com.dvo.EventBooking.repository.EventRepository;
import com.dvo.EventBooking.repository.EventSpecification;
import com.dvo.EventBooking.service.EventService;
import com.dvo.EventBooking.web.model.filter.EventFilter;
import com.dvo.EventBooking.web.model.request.PaginationRequest;
import com.dvo.EventBooking.web.model.request.UpdateEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final EventMapper eventMapper;

    @Override
    public List<Event> findAll() {
        log.info("Call findAll in EventServiceImpl");

        return eventRepository.findAll();
    }

    @Override
    public List<Event> findAllByFilter(EventFilter filter) {
        log.info("Call findAllByFilter in EventServiceImpl with filter: {}", filter);

        return eventRepository.findAll(
                EventSpecification.withFilter(filter),
                PageRequest.of(filter.getPageNumber(), filter.getPageSize())
        ).getContent();
    }

    @Override
    public Event findById(Long id) {
        log.info("Call findById in EventServiceImpl with ID: {}", id);

        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Event not found with ID: {0}", id)));
    }

    @Override
    @Transactional
    public Event save(Event event) {
        log.info("Call save in EventServiceImpl with event: {}", event);

        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event update(UpdateEventRequest event, Long id) {
        log.info("Call update in EventServiceImpl for ID: {}, with event: {}", id, event);

        Event existEvent = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Event not found with ID: {0}", id)));

        if (event.getMaxAttendees() != null) {
            if (event.getMaxAttendees() < bookingRepository.countByEventId(id)) {
                throw new IllegalArgumentException(MessageFormat.format("Event with ID: {0}, new value max attendees less exists bookings {1}", id, bookingRepository.countByEventId(id)));
            }

            if (event.getMaxAttendees() <= 0) {
                throw new IllegalArgumentException(MessageFormat.format("Event with ID: {0}, new value max attendees less than 1", id));
            }
        }
        eventMapper.updateRequestToEvent(event, existEvent);

        return eventRepository.save(existEvent);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Call deleteById in EventServiceImpl with ID: {}", id);

        bookingRepository.deleteByEventId(id);
        eventRepository.deleteById(id);
    }
}
