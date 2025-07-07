package com.dvo.EventBooking.web.controller;

import com.dvo.EventBooking.configuration.SecurityConfiguration;
import com.dvo.EventBooking.entity.Booking;
import com.dvo.EventBooking.entity.Event;
import com.dvo.EventBooking.entity.User;
import com.dvo.EventBooking.mapping.BookingMapper;
import com.dvo.EventBooking.service.BookingService;
import com.dvo.EventBooking.web.model.request.UpdateBookingRequest;
import com.dvo.EventBooking.web.model.request.UpsertBookingRequest;
import com.dvo.EventBooking.web.model.response.BookingResponse;
import com.dvo.EventBooking.web.model.response.BookingShortResponse;
import com.dvo.EventBooking.web.model.response.EventShortResponse;
import com.dvo.EventBooking.web.model.response.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@Import({BookingControllerTest.MockConfig.class, SecurityConfiguration.class})
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Event event;
    private UserResponse userResponse;
    private EventShortResponse eventShortResponse;
    private Booking booking;
    private BookingShortResponse bookingShortResponse;
    private BookingResponse bookingResponse;
    private final String URL = "/api/bookings";

    @TestConfiguration
    static class MockConfig {
        @Bean
        public BookingService bookingService() {
            return mock(BookingService.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
        }

        @Bean
        public BookingMapper bookingMapper() {
            return mock(BookingMapper.class);
        }
    }

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("user").build();
        event = Event.builder().id(1L).name("event").build();
        userResponse = UserResponse.builder().username("user").build();
        eventShortResponse = EventShortResponse.builder().id(1L).name("event").build();
        booking = Booking.builder().id(1L).user(user).event(event).createDate(LocalDate.now()).build();

        bookingShortResponse = BookingShortResponse.builder().id(1L).userId(user.getId()).eventId(event.getId()).createDate(LocalDate.now()).build();
        bookingResponse = BookingResponse.builder().id(1L).user(userResponse).event(eventShortResponse).createDate(LocalDate.now()).build();
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testGetAll() throws Exception {
        when(bookingService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingMapper.bookingToShortResponse(any(Booking.class))).thenReturn(bookingShortResponse);

        mockMvc.perform(get(URL + "?pageNumber=0&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.data[0].userId").value(1L));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testGetById() throws Exception {
        when(bookingService.findById(1L)).thenReturn(booking);
        when(bookingMapper.bookingToResponse(booking)).thenReturn(bookingResponse);

        mockMvc.perform(get(URL + "/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.user.username").value("user"))
                .andExpect(jsonPath("$.event.name").value("event"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testCreate() throws Exception {
        UpsertBookingRequest request = UpsertBookingRequest.builder().userId(1L).eventId(1L).build();

        when(bookingService.save(request)).thenReturn(booking);
        when(bookingMapper.bookingToResponse(booking)).thenReturn(bookingResponse);

        mockMvc.perform(post(URL + "/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(bookingService).save(any(UpsertBookingRequest.class));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testUpdate() throws Exception {
        UpdateBookingRequest request = UpdateBookingRequest.builder().userId(1L).eventId(1L).build();

        when(bookingService.update(request, 1L)).thenReturn(booking);
        when(bookingMapper.bookingToResponse(booking)).thenReturn(bookingResponse);

        mockMvc.perform(put(URL + "/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(bookingService).update(any(UpdateBookingRequest.class), anyLong());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testDeleteById() throws Exception {
        mockMvc.perform(delete(URL + "/delete/1"))
                .andExpect(status().isNoContent());

        verify(bookingService).deleteById(1L);
    }
}
