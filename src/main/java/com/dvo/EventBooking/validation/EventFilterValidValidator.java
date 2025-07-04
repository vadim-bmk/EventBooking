package com.dvo.EventBooking.validation;

import com.dvo.EventBooking.web.model.filter.EventFilter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventFilterValidValidator implements ConstraintValidator<EventFilterValid, EventFilter> {
    @Override
    public boolean isValid(EventFilter eventFilter, ConstraintValidatorContext constraintValidatorContext) {
        return eventFilter.getPageNumber() != null && eventFilter.getPageSize() != null;
    }
}
