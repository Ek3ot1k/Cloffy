package ru.amin.Rest.services;

import org.springframework.stereotype.Service;
import ru.amin.Rest.dto.ProximityNotificationDTO;
import ru.amin.Rest.entity.Location;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.BlockRepository;
import ru.amin.Rest.repositories.FriendshipRepository;
import ru.amin.Rest.repositories.LocationRepository;

import java.time.LocalDateTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProximityService {

    // Порог расстояния для уведомления (метры)
    private static final double PROXIMITY_THRESHOLD_METERS = 50.0;

    // Минимальный интервал между уведомлениями для одной пары (секунды)
    private static final long NOTIFICATION_COOLDOWN_SECONDS = 300;

    // Считаем только недавние локации; иначе будут ложные proximity-уведомления.
    private static final long LOCATION_FRESHNESS_SECONDS = 300;

    private final LocationRepository locationRepository;
    private final FriendshipRepository friendshipRepository;
    private final BlockRepository blockRepository;

    // Кэш последних уведомлений: "userId1_userId2" → timestamp
    private final Map<String, Long> notificationCache = new ConcurrentHashMap<>();

    public ProximityService(LocationRepository locationRepository,
                            FriendshipRepository friendshipRepository,
                            BlockRepository blockRepository) {
        this.locationRepository = locationRepository;
        this.friendshipRepository = friendshipRepository;
        this.blockRepository = blockRepository;
    }

    // Возвращает список незнакомых пользователей рядом с currentUser
    public List<ProximityResult> findNearbyStrangers(Users currentUser, double lat, double lng) {
        LocalDateTime freshnessThreshold = LocalDateTime.now().minusSeconds(LOCATION_FRESHNESS_SECONDS);
        List<Location> allLocations = locationRepository.findByTimestampAfter(freshnessThreshold);
        List<ProximityResult> nearby = new ArrayList<>();

        for (Location loc : allLocations) {
            Users other = loc.getUser();

            // Пропускаем себя
            if (other.getId() == currentUser.getId()) continue;

            // Пропускаем уже друзей
            if (friendshipRepository.findFriendshipBetween(currentUser, other).isPresent()) continue;

            // Пропускаем заблокированных
            if (blockRepository.isBlockedInAnyDirection(currentUser, other)) continue;

            // Проверяем кулдаун для этой пары
            String pairKey = makePairKey(currentUser.getId(), other.getId());
            long now = Instant.now().getEpochSecond();
            Long lastNotified = notificationCache.get(pairKey);
            if (lastNotified != null && now - lastNotified < NOTIFICATION_COOLDOWN_SECONDS) continue;

            double distance = haversine(lat, lng, loc.getLat(), loc.getLng());
            if (distance <= PROXIMITY_THRESHOLD_METERS) {
                nearby.add(new ProximityResult(other,
                        new ProximityNotificationDTO(currentUser.getId(), currentUser.getName(), roundDistance(distance)),
                        new ProximityNotificationDTO(other.getId(), other.getName(), roundDistance(distance))));
                notificationCache.put(pairKey, now);
            }
        }

        return nearby;
    }

    // Формула Haversine — расстояние в метрах между двумя точками
    private double haversine(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private String makePairKey(int id1, int id2) {
        return Math.min(id1, id2) + "_" + Math.max(id1, id2);
    }

    private double roundDistance(double distance) {
        return Math.round(distance * 10.0) / 10.0;
    }

    // Результат проверки близости: другой пользователь + уведомления для обоих
    public record ProximityResult(
            Users otherUser,
            ProximityNotificationDTO notificationForOther,   // шлём otherUser
            ProximityNotificationDTO notificationForCurrent  // шлём currentUser
    ) {}
}
