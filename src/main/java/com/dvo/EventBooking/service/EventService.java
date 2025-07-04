package com.dvo.EventBooking.service;

import com.dvo.EventBooking.entity.Event;
import com.dvo.EventBooking.web.model.filter.EventFilter;
import com.dvo.EventBooking.web.model.request.UpdateEventRequest;

import java.util.List;

public interface EventService {
    List<Event> findAll();

    List<Event> findAllByFilter(EventFilter filter);

    Event findById(Long id);

    Event save(Event event);

    Event update(UpdateEventRequest event, Long id);

    void deleteById(Long id);
}
