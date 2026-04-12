package ru.amin.Rest.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.amin.Rest.dto.ClassmateDTO;
import ru.amin.Rest.dto.EducationDTO;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EducationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EducationService educationService;

    @Test
    void updateEducationSavesNormalizedValues() {
        Users currentUser = user(1, "amin", null, null);
        EducationDTO dto = new EducationDTO();
        dto.setSchool("  School 179  ");
        dto.setUniversity("   ");

        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));

        EducationDTO response = educationService.updateEducation(currentUser, dto);

        assertEquals("School 179", response.getSchool());
        assertNull(response.getUniversity());
        verify(userRepository).save(currentUser);
    }

    @Test
    void searchClassmatesReturnsUniqueUsersAndExcludesCurrentUser() {
        Users currentUser = user(1, "amin", "School 179", "MSU");
        Users classmate = user(2, "ivan", "School 179", "MSU");

        when(userRepository.findBySchoolIgnoreCase("School 179")).thenReturn(List.of(currentUser, classmate));
        when(userRepository.findByUniversityIgnoreCase("MSU")).thenReturn(List.of(classmate));

        List<ClassmateDTO> results = educationService.searchClassmates(currentUser, "School 179", "MSU");

        assertEquals(1, results.size());
        assertEquals(classmate.getId(), results.get(0).getUserId());
        assertEquals("ivan", results.get(0).getUsername());
    }

    private Users user(int id, String name, String school, String university) {
        Users user = new Users();
        user.setId(id);
        user.setName(name);
        user.setSchool(school);
        user.setUniversity(university);
        return user;
    }
}
