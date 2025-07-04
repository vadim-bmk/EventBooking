package com.dvo.EventBooking.service;

import com.dvo.EventBooking.entity.RoleType;
import com.dvo.EventBooking.entity.User;
import com.dvo.EventBooking.web.model.request.UpdateUserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    Page<User> findAll(Pageable pageable);

    User findByUsername(String username);

    User findById(Long id);

    User save(User user, RoleType roleType);

    User update(UpdateUserRequest user, String username);

    void deleteById(Long id);

    boolean existsByUsernameAndEmail(String username, String email);

    void deleteByUsername(String username);
}
