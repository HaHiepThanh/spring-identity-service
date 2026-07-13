package com.hahiepthanh.identity_service.controller;

import com.hahiepthanh.identity_service.dto.request.UserCreationRequest;
import com.hahiepthanh.identity_service.dto.response.UserResponse;
import com.hahiepthanh.identity_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;
    private LocalDate dob;

    @BeforeEach
    public void initData(){
        dob = LocalDate.of(2005,06,21);
        userCreationRequest = UserCreationRequest.builder()
                .username("thanh")
                .firstName("Thanh")
                .lastName("Ha")
                .password("12345678")
                .dob(dob)
                .build();

        userResponse = UserResponse.builder()
                .id("cde25fd38fae")
                .username("thanh")
                .firstName("Thanh")
                .lastName("Ha")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        //GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(userCreationRequest);

        Mockito.when(userService.createUser(ArgumentMatchers.any()))
                .thenReturn(userResponse);

        //WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.id")
                        .value("cde25fd38fae")
        );
    }

    @Test
    void createUser_usernameInvalid_fail() throws Exception {
        //GIVEN
        userCreationRequest.setUsername("to");
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(userCreationRequest);

        //WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value(1003))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("User must be at least 4 characters")
                );
    }

//    @Test
//    void createUser_dobInvalid_fail() throws Exception {
//        //GIVEN
//        dob = LocalDate.of(2025, 06, 21);
//        userCreationRequest.setDob(dob);
//        ObjectMapper objectMapper = new ObjectMapper();
//        String content = objectMapper.writeValueAsString(userCreationRequest);
//
//        //WHEN, THEN
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/users")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(content))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andExpect(MockMvcResultMatchers.jsonPath("code")
//                        .value(1008))
//                .andExpect(MockMvcResultMatchers.jsonPath("message")
//                        .value("Your age must be at least 18")
//                );
//    }
}
