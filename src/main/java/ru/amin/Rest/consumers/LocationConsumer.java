package ru.amin.Rest.consumers;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import ru.amin.Rest.dto.LocationDTO;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.UserRepository;
import ru.amin.Rest.services.LocationService;
import ru.amin.Rest.services.ProximityService;
import ru.amin.Rest.util.UserNotFoundException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LocationConsumer {
    private final LocationService locationService;
    private final UserRepository userRepository;
    private final ProximityService proximityService;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "location-updates",groupId = "location-consumer-group")
    public void handleLocationEvent(
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Payload LocationDTO locationDTO){
        int tempKey=Integer.parseInt(key);
        Users user=userRepository.findById(tempKey).
                orElseThrow(()->new UserNotFoundException("User not found: " + tempKey));
        locationService.saveOrUpdateLocation(user,locationDTO);

        // Проверяем близость с незнакомыми пользователями
        List<ProximityService.ProximityResult> nearby = proximityService.findNearbyStrangers(
                user, locationDTO.getLat(), locationDTO.getLng()
        );
        for (ProximityService.ProximityResult result : nearby) {
            // Уведомляем незнакомца о текущем пользователе
            messagingTemplate.convertAndSendToUser(
                    result.otherUser().getName(), "/queue/nearby", result.notificationForOther()
            );
            // Уведомляем текущего пользователя о незнакомце
            messagingTemplate.convertAndSendToUser(
                    user.getName(), "/queue/nearby", result.notificationForCurrent()
            );
        }

    }
}
