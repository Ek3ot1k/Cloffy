package ru.amin.Rest.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
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
import java.util.concurrent.TimeUnit;

@Service
public class LocationService {
    private static final Logger log = LoggerFactory.getLogger(LocationService.class);

    private final LocationRepository locationRepository;
    private final FriendshipService friendshipService;
    private final BlockService blockService;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public LocationService(LocationRepository locationRepository,
                           FriendshipService friendshipService,
                           BlockService blockService,
                           UserRepository userRepository,
                           RedisTemplate<String,String> redisTemplate,
                           ObjectMapper objectMapper) {
        this.locationRepository = locationRepository;
        this.friendshipService = friendshipService;
        this.blockService = blockService;
        this.userRepository = userRepository;
        this.redisTemplate=redisTemplate;
        this.objectMapper=objectMapper;
    }

    // Сохраняем или обновляем текущую геолокацию + уровень батареи (upsert)
    @Transactional
    public void saveOrUpdateLocation(Users user, LocationDTO dto){

        Location location = locationRepository.findByUser(user)
                .orElse(new Location());

        location.setUser(user);
        location.setLat(dto.getLat());
        location.setLng(dto.getLng());
        location.setTimestamp(LocalDateTime.now());
        locationRepository.save(location);
        try{
            String json = objectMapper.writeValueAsString(dto);
            redisTemplate.opsForValue().set("location:"+user.getId(), json, 60, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            // Redis-кэш не должен ломать сохранение локации.
            log.warn("Failed to cache location for user {}", user.getId(), e);
        }


        // Обновляем уровень батареи в профиле пользователя
        if (dto.getBatteryLevel() != null) {
            user.setBatteryLevel(dto.getBatteryLevel());
            userRepository.save(user);
        }
    }

    // Геолокации принятых друзей (заблокированные исключаются)
    public List<FriendLocationDTO> getFriendsLocations(Users currentUser) {
        List<Users> friends = friendshipService.getAcceptedFriends(currentUser)
                .stream()
                .filter(friend -> !blockService.isBlocked(currentUser, friend))
                .toList();

        return friends.stream().map(friend->{
            String json = null;
            try {
                json = redisTemplate.opsForValue().get("location:"+friend.getId());
            } catch (Exception e) {
                // Если Redis недоступен, откатываемся на БД.
                log.warn("Failed to read cached location for user {}", friend.getId(), e);
            }

            if(json!=null){
                try{
                    LocationDTO dto=objectMapper.readValue(json, LocationDTO.class);
                    return new FriendLocationDTO(
                            friend.getId(),
                            friend.getName(),
                            dto.getLat(),
                            dto.getLng(),
                            null,
                            friend.getBatteryLevel()
                    );
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            return locationRepository.findByUser(friend)
                    .map(loc->new FriendLocationDTO(
                            friend.getId(),
                            friend.getName(),
                            loc.getLat(),
                            loc.getLng(),
                            loc.getTimestamp(),
                            friend.getBatteryLevel()
                    )).orElse(null);

        }).filter(dto->dto!=null)
                .toList();
    }
}
