package com.dvo.EventBooking.web.controller;

import com.dvo.EventBooking.entity.RoleType;
import com.dvo.EventBooking.entity.User;
import com.dvo.EventBooking.mapping.UserMapper;
import com.dvo.EventBooking.service.UserService;
import com.dvo.EventBooking.web.model.request.PaginationRequest;
import com.dvo.EventBooking.web.model.request.UpdateUserRequest;
import com.dvo.EventBooking.web.model.request.UpsertUserRequest;
import com.dvo.EventBooking.web.model.response.ModelListResponse;
import com.dvo.EventBooking.web.model.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ModelListResponse<UserResponse>> getAll(@Valid PaginationRequest request) {
        Page<User> userPage = userService.findAll(request.pageRequest());

        return ResponseEntity.ok(ModelListResponse.<UserResponse>builder()
                .totalCount((long) userPage.getTotalElements())
                .data(userPage.stream().map(userMapper::userToResponse).toList())
                .build());
    }

    @GetMapping("/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userMapper.userToResponse(userService.findById(id)));
    }

    @GetMapping("/username/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userMapper.userToResponse(userService.findByUsername(username)));
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UpsertUserRequest request,
                                               @RequestParam RoleType roleType) {
        User newUser = userService.save(userMapper.requestToUser(request), roleType);

        return ResponseEntity.ok(userMapper.userToResponse(newUser));
    }

    @PutMapping("/update/{username}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> update(@Valid @RequestBody UpdateUserRequest request,
                                               @PathVariable String username) {
        User updatedUser = userService.update(request, username);

        return ResponseEntity.ok(userMapper.userToResponse(updatedUser));
    }

    @DeleteMapping("/delete/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteByUsername(@PathVariable String username) {
        userService.deleteByUsername(username);

        return ResponseEntity.noContent().build();
    }

}
