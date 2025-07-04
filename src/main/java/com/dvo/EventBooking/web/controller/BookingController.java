package com.dvo.EventBooking.web.controller;

import com.dvo.EventBooking.entity.Booking;
import com.dvo.EventBooking.mapping.BookingMapper;
import com.dvo.EventBooking.service.BookingService;
import com.dvo.EventBooking.web.model.request.PaginationRequest;
import com.dvo.EventBooking.web.model.request.UpdateBookingRequest;
import com.dvo.EventBooking.web.model.request.UpsertBookingRequest;
import com.dvo.EventBooking.web.model.response.BookingResponse;
import com.dvo.EventBooking.web.model.response.BookingShortResponse;
import com.dvo.EventBooking.web.model.response.ModelListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ModelListResponse<BookingShortResponse>> findAll(@Valid PaginationRequest paginationRequest){
        Page<Booking> bookingPage = bookingService.findAll(paginationRequest.pageRequest());

        return ResponseEntity.ok(ModelListResponse.<BookingShortResponse>builder()
                .totalCount((long) bookingPage.getTotalElements())
                .data(bookingPage.stream().map(bookingMapper::bookingToShortResponse).toList())
                .build());
    }

    @GetMapping("/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<BookingResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(bookingMapper.bookingToResponse(bookingService.findById(id)));
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody UpsertBookingRequest request){
        Booking newBooking = bookingService.save(request);

        return ResponseEntity.ok(bookingMapper.bookingToResponse(newBooking));
    }

    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<BookingResponse> update(@Valid @RequestBody UpdateBookingRequest request,
                                                  @PathVariable Long id){
        Booking updatedBooking = bookingService.update(request, id);

        return ResponseEntity.ok(bookingMapper.bookingToResponse(updatedBooking));
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        bookingService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
