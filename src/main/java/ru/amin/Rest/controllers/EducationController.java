package ru.amin.Rest.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.amin.Rest.dto.ClassmateDTO;
import ru.amin.Rest.dto.EducationDTO;
import ru.amin.Rest.security.UsersDetails;
import ru.amin.Rest.services.EducationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/education")
public class EducationController {

    private final EducationService educationService;

    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    // Обновляет школу и университет текущего пользователя.
    @PutMapping
    public ResponseEntity<EducationDTO> updateEducation(@RequestBody @Valid EducationDTO dto,
                                                        @AuthenticationPrincipal UsersDetails usersDetails) {
        return ResponseEntity.ok(educationService.updateEducation(usersDetails.getUser(), dto));
    }

    // Возвращает сохранённые данные об образовании текущего пользователя.
    @GetMapping
    public ResponseEntity<EducationDTO> getEducation(@AuthenticationPrincipal UsersDetails usersDetails) {
        return ResponseEntity.ok(educationService.getEducation(usersDetails.getUser()));
    }

    // Ищет пользователей по школе и/или университету.
    @GetMapping("/search")
    public ResponseEntity<?> searchClassmates(@RequestParam(required = false) String school,
                                              @RequestParam(required = false) String university,
                                              @AuthenticationPrincipal UsersDetails usersDetails) {
        if ((school == null || school.trim().isEmpty()) && (university == null || university.trim().isEmpty())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Нужно передать school, university или оба параметра"));
        }

        List<ClassmateDTO> classmates = educationService.searchClassmates(usersDetails.getUser(), school, university);
        return ResponseEntity.ok(classmates);
    }
}
