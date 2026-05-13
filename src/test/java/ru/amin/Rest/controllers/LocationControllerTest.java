package ru.amin.Rest.controllers;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.security.JWTUtil;
import ru.amin.Rest.security.UsersDetails;
import ru.amin.Rest.services.LocationService;
import ru.amin.Rest.services.UsersDetailsService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocationController.class)
public class LocationControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ModelMapper modelMapper;

    @MockitoBean
    LocationService locationService;

    @MockitoBean
    UsersDetailsService usersDetailsService;

    @MockitoBean
    JWTUtil jwtUtil;

    @Test
    void getFriendsLocationsReturnsLocations() throws Exception {
        Users user=new Users();
        user.setId(1);
        user.setName("amin");
        user.setRole("ROLE_USER");
        UsersDetails usersDetails=new UsersDetails(user);

        when(locationService.getFriendsLocations(user)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/location/friends")
                .with(authentication(new UsernamePasswordAuthenticationToken(usersDetails,null,usersDetails.getAuthorities())))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
