package ru.amin.Rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import ru.amin.Rest.dto.ClassmateDTO;
import ru.amin.Rest.dto.EducationDTO;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.security.JWTUtil;
import ru.amin.Rest.security.UsersDetails;
import ru.amin.Rest.services.EducationService;
import ru.amin.Rest.services.UsersDetailsService;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EducationController.class)
class EducationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EducationService educationService;

    @MockitoBean
    private UsersDetailsService usersDetailsService;

    @MockitoBean
    private JWTUtil jwtUtil;

    @Test
    void updateEducationReturnsUpdatedPayload() throws Exception {
        Users user = new Users();
        user.setId(1);
        user.setName("amin");
        user.setRole("ROLE_USER");
        UsersDetails usersDetails = new UsersDetails(user);

        EducationDTO request = new EducationDTO();
        request.setSchool("School 179");
        request.setUniversity("MSU");

        EducationDTO response = new EducationDTO();
        response.setSchool("School 179");
        response.setUniversity("MSU");

        when(educationService.updateEducation(eq(user), eq(request))).thenReturn(response);

        mockMvc.perform(put("/api/v1/education")
                        .with(authentication(new UsernamePasswordAuthenticationToken(usersDetails, null, usersDetails.getAuthorities())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.school").value("School 179"))
                .andExpect(jsonPath("$.university").value("MSU"));
    }

    @Test
    void searchClassmatesRequiresAtLeastOneParameter() throws Exception {
        Users user = new Users();
        user.setId(1);
        user.setName("amin");
        user.setRole("ROLE_USER");
        UsersDetails usersDetails = new UsersDetails(user);

        mockMvc.perform(get("/api/v1/education/search")
                        .with(authentication(new UsernamePasswordAuthenticationToken(usersDetails, null, usersDetails.getAuthorities()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void searchClassmatesReturnsMatches() throws Exception {
        Users user = new Users();
        user.setId(1);
        user.setName("amin");
        user.setRole("ROLE_USER");
        UsersDetails usersDetails = new UsersDetails(user);

        when(educationService.searchClassmates(eq(user), eq("School 179"), eq(null)))
                .thenReturn(List.of(new ClassmateDTO(2, "ivan", "School 179", "MSU")));

        mockMvc.perform(get("/api/v1/education/search")
                        .with(authentication(new UsernamePasswordAuthenticationToken(usersDetails, null, usersDetails.getAuthorities())))
                        .param("school", "School 179"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(2))
                .andExpect(jsonPath("$[0].username").value("ivan"));
    }
}
