package ru.amin.Rest.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.amin.Rest.dto.FriendLocationDTO;
import ru.amin.Rest.dto.LocationDTO;
import ru.amin.Rest.entity.Location;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.LocationRepository;
import ru.amin.Rest.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final FriendshipService friendshipService;
    private final BlockService blockService;
    private final UserRepository userRepository;

    public LocationService(LocationRepository locationRepository,
                           FriendshipService friendshipService,
                           BlockService blockService,
                           UserRepository userRepository) {
        this.locationRepository = locationRepository;
        this.friendshipService = friendshipService;
        this.blockService = blockService;
        this.userRepository = userRepository;
    }

    // Сохраняем или обновляем текущую геолокацию + уровень батареи (upsert)
    @Transactional
    public void saveOrUpdateLocation(Users user, LocationDTO dto) {
        Location location = locationRepository.findByUser(user)
                .orElse(new Location());

        location.setUser(user);
        location.setLat(dto.getLat());
        location.setLng(dto.getLng());
        location.setTimestamp(LocalDateTime.now());
        locationRepository.save(location);

        // Обновляем уровень батареи в профиле пользователя
        if (dto.getBatteryLevel() != null) {
            Users managedUser = userRepository.findById(user.getId()).orElse(user);
            managedUser.setBatteryLevel(dto.getBatteryLevel());
            userRepository.save(managedUser);
        }
    }

    // Геолокации принятых друзей (заблокированные исключаются)
    public List<FriendLocationDTO> getFriendsLocations(Users currentUser) {
        List<Users> friends = friendshipService.getAcceptedFriends(currentUser)
                .stream()
                .filter(friend -> !blockService.isBlocked(currentUser, friend))
                .toList();

        return locationRepository.findByUserIn(friends)
                .stream()
                .map(loc -> new FriendLocationDTO(
                        loc.getUser().getId(),
                        loc.getUser().getName(),
                        loc.getLat(),
                        loc.getLng(),
                        loc.getTimestamp(),
                        loc.getUser().getBatteryLevel()
                ))
                .toList();
    }
}
