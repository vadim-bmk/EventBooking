package com.dvo.EventBooking.web.controller;

import com.dvo.EventBooking.configuration.SecurityConfiguration;
import com.dvo.EventBooking.entity.Event;
import com.dvo.EventBooking.mapping.BookingMapper;
import com.dvo.EventBooking.mapping.EventMapper;
import com.dvo.EventBooking.service.BookingService;
import com.dvo.EventBooking.service.EventService;
import com.dvo.EventBooking.web.model.filter.EventFilter;
import com.dvo.EventBooking.web.model.request.UpdateEventRequest;
import com.dvo.EventBooking.web.model.request.UpsertEventRequest;
import com.dvo.EventBooking.web.model.response.BookingShortResponse;
import com.dvo.EventBooking.web.model.response.EventResponse;
import com.dvo.EventBooking.web.model.response.EventShortResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@Import({EventControllerTest.MockConfig.class, SecurityConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingMapper bookingMapper;

    private Event event;
    private EventShortResponse eventShortResponse;
    private EventResponse eventResponse;
    private BookingShortResponse bookingShortResponse;
    private final String URL = "/api/events";


    @TestConfiguration
    static class MockConfig {
        @Bean
        public EventService eventService() {
            return mock(EventService.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
        }

        @Bean
        public EventMapper eventMapper() {
            return mock(EventMapper.class);
        }

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
        event = Event.builder()
                .id(1L)
                .name("name")
                .description("description")
                .city("city")
                .address("address")
                .date(LocalDate.now())
                .maxAttendees(100)
                .build();

        eventShortResponse = EventShortResponse.builder()
                .id(1L)
                .name("name")
                .description("description")
                .city("city")
                .address("address")
                .date(LocalDate.now())
                .maxAttendees(100)
                .availableAttendees(90)
                .build();

        eventResponse = EventResponse.builder()
                .id(1L)
                .name("name")
                .description("description")
                .city("city")
                .address("address")
                .date(LocalDate.now())
                .maxAttendees(100)
                .availableAttendees(90)
                .bookings(null)
                .build();

        bookingShortResponse = BookingShortResponse.builder()
                .id(1L)
                .userId(1L)
                .eventId(1L)
                .createDate(LocalDate.now())
                .build();
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testFindAllByFilter() throws Exception {
        when(eventService.findAllByFilter(any(EventFilter.class))).thenReturn(List.of(event));
        when(eventMapper.eventToShortResponse(event)).thenReturn(eventShortResponse);
        when(bookingService.countByEventId(event.getId())).thenReturn(10);

        mockMvc.perform(get(URL)
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.data[0].id").value(event.getId()))
                .andExpect(jsonPath("$.data[0].availableAttendees").value(90));

        verify(eventService).findAllByFilter(any(EventFilter.class));
        verify(eventMapper).eventToShortResponse(event);
        verify(bookingService).countByEventId(event.getId());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testFindById() throws Exception {
        when(eventService.findById(1L)).thenReturn(event);
        when(bookingService.findAllByEventId(1L)).thenReturn(List.of());
        when(eventMapper.eventToResponse(eq(event), anyList())).thenReturn(eventResponse);

        mockMvc.perform(get(URL + "/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(event.getId()))
                .andExpect(jsonPath("$.name").value("name"));

        verify(eventService).findById(1L);
        verify(bookingService).findAllByEventId(1L);
        verify(eventMapper).eventToResponse(eq(event), anyList());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testCreateEvent() throws Exception {
        UpsertEventRequest request = UpsertEventRequest.builder()
                .name("name")
                .description("description")
                .city("city")
                .address("address")
                .date(LocalDate.now())
                .maxAttendees(100)
                .build();

        when(eventMapper.requestToEvent(request)).thenReturn(event);
        when(eventService.save(any(Event.class))).thenReturn(event);
        when(bookingService.findAllByEventId(event.getId())).thenReturn(List.of());
        when(eventMapper.eventToResponse(eq(event), anyList())).thenReturn(eventResponse);

        mockMvc.perform(post(URL + "/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(event.getId()))
                .andExpect(jsonPath("$.name").value(event.getName()));

        verify(eventMapper).eventToResponse(eq(event), anyList());
        verify(eventService).save(any(Event.class));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testUpdateEvent() throws Exception {
        UpdateEventRequest request = UpdateEventRequest.builder()
                .name("name")
                .description("description")
                .city("city")
                .address("address")
                .date(LocalDate.now())
                .maxAttendees(150)
                .build();

        Event updatedEvent = Event.builder()
                .id(1L)
                .name("updated event")
                .maxAttendees(150)
                .build();

        when(eventService.update(any(UpdateEventRequest.class), anyLong())).thenReturn(updatedEvent);
        when(bookingService.findAllByEventId(1L)).thenReturn(List.of());
        when(eventMapper.eventToResponse(eq(updatedEvent), anyList())).thenReturn(eventResponse);

        mockMvc.perform(put(URL + "/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"));

        verify(eventService).update(any(UpdateEventRequest.class), eq(1L));
        verify(bookingService).findAllByEventId(1L);
        verify(eventMapper).eventToResponse(eq(updatedEvent), anyList());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testDeleteById() throws Exception {
        doNothing().when(eventService).deleteById(1L);

        mockMvc.perform(delete(URL + "/delete/1"))
                .andExpect(status().isNoContent());

        verify(eventService).deleteById(1L);
    }
}
