package com.dvo.EventBooking.web.controller;

import com.dvo.EventBooking.configuration.SecurityConfiguration;
import com.dvo.EventBooking.entity.User;
import com.dvo.EventBooking.mapping.UserMapper;
import com.dvo.EventBooking.service.UserService;
import com.dvo.EventBooking.web.model.request.UpdateUserRequest;
import com.dvo.EventBooking.web.model.request.UpsertUserRequest;
import com.dvo.EventBooking.web.model.response.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({UserControllerTest.MockConfig.class, SecurityConfiguration.class})
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    private User user;
    private UserResponse response;
    private final String URL = "/api/users";

    @TestConfiguration
    static class MockConfig {
        @Bean
        public UserService userService() {
            return mock(UserService.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
        }

        @Bean
        public UserMapper userMapper() {
            return mock(UserMapper.class);
        }
    }

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("user").email("user@mail.ru").build();
        response = UserResponse.builder().username("user").email("user@mail.ru").build();
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testGetAllUsers() throws Exception {
        when(userService.findAll(any())).thenReturn(new PageImpl<>(List.of(user)));
        when(userMapper.userToResponse(user)).thenReturn(response);

        mockMvc.perform(get(URL + "?pageNumber=0&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].username").value("user"))
                .andExpect(jsonPath("$.totalCount").value(1));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testGetByUsername() throws Exception {
        when(userService.findByUsername("user")).thenReturn(user);
        when(userMapper.userToResponse(user)).thenReturn(response);

        mockMvc.perform(get(URL + "/username/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testGetById() throws Exception {
        when(userService.findById(1L)).thenReturn(user);
        when(userMapper.userToResponse(user)).thenReturn(response);

        mockMvc.perform(get(URL + "/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    void testCreateUser() throws Exception {
        UpsertUserRequest request = UpsertUserRequest.builder()
                .username("user")
                .password("12345")
                .email("user@mail.ru")
                .firstName("first name")
                .lastName("last name")
                .build();

        when(userMapper.requestToUser(request)).thenReturn(user);
        when(userService.save(any(), any())).thenReturn(user);
        when(userMapper.userToResponse(user)).thenReturn(response);

        mockMvc.perform(post(URL + "/create?roleType=ROLE_USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testUpdateUser() throws Exception {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("email@ya.ru")
                .lastName("last name")
                .build();

        when(userService.update(request, "user")).thenReturn(user);
        when(userMapper.userToResponse(user)).thenReturn(response);

        mockMvc.perform(put(URL + "/update/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testDeleteUserByUsername() throws Exception {
        mockMvc.perform(delete(URL + "/delete/user"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).deleteByUsername("user");
    }
}
