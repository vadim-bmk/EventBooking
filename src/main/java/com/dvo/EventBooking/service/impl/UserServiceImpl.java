package com.dvo.EventBooking.service.impl;

import com.dvo.EventBooking.exception.EntityExistsException;
import com.dvo.EventBooking.entity.RoleType;
import com.dvo.EventBooking.entity.User;
import com.dvo.EventBooking.exception.EntityNotFoundException;
import com.dvo.EventBooking.mapping.UserMapper;
import com.dvo.EventBooking.repository.BookingRepository;
import com.dvo.EventBooking.repository.UserRepository;
import com.dvo.EventBooking.service.UserService;
import com.dvo.EventBooking.web.model.request.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookingRepository bookingRepository;
    private final UserMapper userMapper;

    @Override
    public Page<User> findAll(Pageable pageable) {
        log.info("Call findAll in UserServiceImpl");

        return userRepository.findAll(pageable);
    }

    @Override
    public User findByUsername(String username) {
        log.info("Call findByUsername in UserServiceImpl with username: {}", username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("User not found with username: {0}", username)));
    }

    @Override
    public User findById(Long id) {
        log.info("Call findById in UserServiceImpl with ID: {}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("User not found with ID: {0}", id)));
    }

    @Override
    @Transactional
    public User save(User user, RoleType roleType) {
        log.info("Call save in UserServiceImpl with user: {}", user);

        if (existsByUsernameAndEmail(user.getUsername(), user.getEmail())) {
            throw new EntityExistsException(MessageFormat.format("User with username {0} and email {1} is exists", user.getUsername(), user.getEmail()));
        }

        if (userRepository.existsByUsername(user.getUsername())){
            throw new EntityExistsException(MessageFormat.format("User with username {0} is exists", user.getUsername()));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoleType(Objects.requireNonNullElse(roleType, RoleType.ROLE_USER));

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(UpdateUserRequest user, String username) {
        log.info("Call update in UserServiceImpl for username {} with user: {}", username, user);

        User existUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("User not found with username: {0}", username)));

        if (user.getPassword()!=null) user.setPassword(passwordEncoder.encode(user.getPassword()));

        userMapper.updateRequestToUser(user, existUser);

        return userRepository.save(existUser);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Call deleteById in UserServiceImpl with ID: {}", id);

        bookingRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsernameAndEmail(String username, String email) {
        log.info("Call existsByUsernameAndEmail in UserServiceImpl with username: {} and email: {}", username, email);

        return userRepository.existsByUsernameAndEmail(username, email);
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        log.info("Call deleteByUsername in UserServiceImpl with username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("User not found with username: {0}", username)));

        bookingRepository.deleteByUserId(user.getId());
        userRepository.deleteByUsername(username);
    }
}
