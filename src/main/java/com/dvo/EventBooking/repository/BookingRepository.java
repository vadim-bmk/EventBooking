package com.dvo.EventBooking.repository;

import com.dvo.EventBooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    void deleteByEventId(Long eventId);
    void deleteByUserId(Long userId);
    int countByEventId(Long eventId);
    List<Booking> findAllByEventId(Long eventId);
}
