package com.dvo.EventBooking.service.impl;

import com.dvo.EventBooking.entity.RoleType;
import com.dvo.EventBooking.entity.User;
import com.dvo.EventBooking.exception.EntityExistsException;
import com.dvo.EventBooking.exception.EntityNotFoundException;
import com.dvo.EventBooking.mapping.UserMapper;
import com.dvo.EventBooking.repository.BookingRepository;
import com.dvo.EventBooking.repository.UserRepository;
import com.dvo.EventBooking.web.model.request.UpdateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        Page<User> page = new PageImpl<>(List.of(new User()));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<User> result = userService.findAll(PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testFindByUsername_whenExists() {
        User user = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("user");
        assertEquals(user, result);
    }

    @Test
    void testFindByUsername_whenNotExists() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findByUsername("user"));
    }

    @Test
    void testFindById_whenExists() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);
        assertEquals(user, result);
    }

    @Test
    void testFindById_whenNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void testSave() {
        User user = User.builder()
                .username("user")
                .email("email@ya.ru")
                .password("12345")
                .firstName("firstname")
                .lastName("lastname")
                .build();

        when(userRepository.existsByUsernameAndEmail("user", "email@ya.ru")).thenReturn(false);
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(passwordEncoder.encode("12345")).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(user);

        User result = userService.save(user, RoleType.ROLE_USER);

        assertEquals("encoded", result.getPassword());
        assertEquals(RoleType.ROLE_USER, result.getRoleType());
        assertEquals("user", result.getUsername());
    }

    @Test
    void testSave_whenExistsByUsernameAndEmail() {
        User user = User.builder()
                .username("user")
                .email("email@ya.ru")
                .password("12345")
                .firstName("firstname")
                .lastName("lastname")
                .build();

        when(userRepository.existsByUsernameAndEmail("user", "email@ya.ru")).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> userService.save(user, RoleType.ROLE_USER));
    }

    @Test
    void testSave_whenExistsByUsernameOnly() {
        User user = User.builder()
                .username("user")
                .email("email@ya.ru")
                .password("12345")
                .firstName("firstname")
                .lastName("lastname")
                .build();

        when(userRepository.existsByUsernameAndEmail("user", "email@ya.ru")).thenReturn(false);
        when(userRepository.existsByUsername("user")).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> userService.save(user, RoleType.ROLE_USER));
    }

    @Test
    void testUpdate() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("email@ya.ru")
                .password("12345")
                .build();

        User existUser = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(existUser));
        when(passwordEncoder.encode("12345")).thenReturn("encoded");
        when(userRepository.save(existUser)).thenReturn(existUser);

        User result = userService.update(request, "user");
        verify(userMapper).updateRequestToUser(request, existUser);
        assertEquals(existUser, result);
    }

    @Test
    void testUpdate_whenNotExists() {
        UpdateUserRequest request = new UpdateUserRequest();
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.update(request, "user"));
    }

    @Test
    void testDeleteById() {
        userService.deleteById(1L);
        verify(bookingRepository).deleteByUserId(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteByUsername() {
        User user = User.builder()
                .id(1L)
                .build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        userService.deleteByUsername("user");
        verify(bookingRepository).deleteByUserId(1L);
        verify(userRepository).deleteByUsername("user");
    }

    @Test
    void testDeleteByUsername_whenNotExists() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.deleteByUsername("user"));
    }

    @Test
    void testExistsByUsernameAndEmail() {
        when(userRepository.existsByUsernameAndEmail("user", "email@ya.ru")).thenReturn(true);
        assertTrue(userService.existsByUsernameAndEmail("user", "email@ya.ru"));
    }
}
