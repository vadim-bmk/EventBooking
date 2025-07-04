package com.dvo.EventBooking.service;

import com.dvo.EventBooking.entity.Booking;
import com.dvo.EventBooking.web.model.request.UpdateBookingRequest;
import com.dvo.EventBooking.web.model.request.UpsertBookingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {
    Page<Booking> findAll(Pageable pageable);

    List<Booking> findAllByEventId(Long eventId);

    Booking findById(Long id);

    Booking save(UpsertBookingRequest booking);

    Booking update(UpdateBookingRequest booking, Long id);

    void deleteById(Long id);

    void deleteByUserId(Long userId);

    void deleteByEventId(Long eventId);

    int countByEventId(Long eventId);

}
