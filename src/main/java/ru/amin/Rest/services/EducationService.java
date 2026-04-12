package ru.amin.Rest.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.amin.Rest.dto.ClassmateDTO;
import ru.amin.Rest.dto.EducationDTO;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.UserRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EducationService {

    private final UserRepository userRepository;

    public EducationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Обновляет школу и университет текущего пользователя.
    @Transactional
    public EducationDTO updateEducation(Users currentUser, EducationDTO dto) {
        Users managedUser = userRepository.findById(currentUser.getId()).orElse(currentUser);
        managedUser.setSchool(normalize(dto.getSchool()));
        managedUser.setUniversity(normalize(dto.getUniversity()));
        userRepository.save(managedUser);

        EducationDTO response = new EducationDTO();
        response.setSchool(managedUser.getSchool());
        response.setUniversity(managedUser.getUniversity());
        return response;
    }

    // Возвращает сохранённые данные об образовании текущего пользователя.
    public EducationDTO getEducation(Users currentUser) {
        Users managedUser = userRepository.findById(currentUser.getId()).orElse(currentUser);
        EducationDTO response = new EducationDTO();
        response.setSchool(managedUser.getSchool());
        response.setUniversity(managedUser.getUniversity());
        return response;
    }

    // Ищет пользователей по школе и/или университету и исключает текущего пользователя.
    public List<ClassmateDTO> searchClassmates(Users currentUser, String school, String university) {
        String normalizedSchool = normalize(school);
        String normalizedUniversity = normalize(university);

        Map<Integer, ClassmateDTO> results = new LinkedHashMap<>();

        if (normalizedSchool != null) {
            userRepository.findBySchoolIgnoreCase(normalizedSchool).stream()
                    .filter(user -> user.getId() != currentUser.getId())
                    .forEach(user -> results.put(user.getId(), toDTO(user)));
        }

        if (normalizedUniversity != null) {
            userRepository.findByUniversityIgnoreCase(normalizedUniversity).stream()
                    .filter(user -> user.getId() != currentUser.getId())
                    .forEach(user -> results.put(user.getId(), toDTO(user)));
        }

        return List.copyOf(results.values());
    }

    private ClassmateDTO toDTO(Users user) {
        return new ClassmateDTO(
                user.getId(),
                user.getName(),
                user.getSchool(),
                user.getUniversity()
        );
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
