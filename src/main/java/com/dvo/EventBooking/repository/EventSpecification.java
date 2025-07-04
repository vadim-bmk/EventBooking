package com.dvo.EventBooking.repository;

import com.dvo.EventBooking.entity.Event;
import com.dvo.EventBooking.web.model.filter.EventFilter;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public interface EventSpecification {
    static Specification<Event> withFilter(EventFilter filter){
        return byName(filter.getName())
                .and(byDescription(filter.getDescription()))
                .and(byDate(filter.getDate()))
                .and(byCity(filter.getCity()))
                .and(byAddress(filter.getAddress()))
                .and(byMaxAttendees(filter.getMaxAttendees()))
                ;
    }

    static Specification<Event> byName(String name) {
        return ((root, query, criteriaBuilder) -> {
            if (name == null) {
                return null;
            }

            return criteriaBuilder.like(root.get(Event.Fields.name), "%" + name + "%");
        });
    }

    static Specification<Event> byDescription(String description) {
        return ((root, query, criteriaBuilder) -> {
            if (description == null) {
                return null;
            }

            return criteriaBuilder.like(root.get(Event.Fields.description), "%" + description + "%");
        });
    }

    static Specification<Event> byDate(LocalDate date) {
        return ((root, query, criteriaBuilder) -> {
            if (date == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get(Event.Fields.date), date);
        });
    }

    static Specification<Event> byCity(String city) {
        return ((root, query, criteriaBuilder) -> {
            if (city == null) {
                return null;
            }

            return criteriaBuilder.like(root.get(Event.Fields.city), "%" + city + "%");
        });
    }

    static Specification<Event> byAddress(String address) {
        return ((root, query, criteriaBuilder) -> {
            if (address == null) {
                return null;
            }

            return criteriaBuilder.like(root.get(Event.Fields.address), "%" + address + "%");
        });
    }

    static Specification<Event> byMaxAttendees(Integer maxAttendees) {
        return ((root, query, criteriaBuilder) -> {
            if (maxAttendees == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get(Event.Fields.maxAttendees), maxAttendees);
        });
    }
}
