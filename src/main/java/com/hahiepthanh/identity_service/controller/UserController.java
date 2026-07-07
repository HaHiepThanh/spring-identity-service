package com.hahiepthanh.identity_service.controller;

import com.hahiepthanh.identity_service.configuration.SecurityConfig;
import com.hahiepthanh.identity_service.dto.request.ApiResponse;
import com.hahiepthanh.identity_service.dto.request.UserCreationRequest;
import com.hahiepthanh.identity_service.dto.request.UserUpdateRequest;
import com.hahiepthanh.identity_service.dto.response.UserResponse;
import com.hahiepthanh.identity_service.entity.User;
import com.hahiepthanh.identity_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {

    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request){
        log.info("Controller: createUser");
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();

        apiResponse.setResult(userService.createUser(request));

        return apiResponse;
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getUsers(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUser())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUserById(@PathVariable("userId") String userId){

        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(userId))
                .build();
    }

    @PutMapping("/{userId}")
    UserResponse updateUserById(@PathVariable("userId") String userId, @RequestBody UserUpdateRequest request){
        return userService.updateUserById(userId, request);
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo(){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @DeleteMapping("/{userId}")
    String deleteUserById(@PathVariable("userId") String userId){
        userService.deleteUserById(userId);
        return "User have been deleted";
    }





}
