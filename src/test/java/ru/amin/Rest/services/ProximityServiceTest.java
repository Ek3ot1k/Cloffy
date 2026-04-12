package ru.amin.Rest.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.amin.Rest.entity.Location;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.BlockRepository;
import ru.amin.Rest.repositories.FriendshipRepository;
import ru.amin.Rest.repositories.LocationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProximityServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private BlockRepository blockRepository;

    @InjectMocks
    private ProximityService proximityService;

    @Test
    void findNearbyStrangersReturnsNearbyNonFriend() {
        Users currentUser = user(1, "amin");
        Users stranger = user(2, "ivan");

        when(locationRepository.findByTimestampAfter(any(LocalDateTime.class)))
                .thenReturn(List.of(location(stranger, 55.75581, 37.61731)));
        when(friendshipRepository.findFriendshipBetween(currentUser, stranger)).thenReturn(Optional.empty());
        when(blockRepository.isBlockedInAnyDirection(currentUser, stranger)).thenReturn(false);

        List<ProximityService.ProximityResult> results =
                proximityService.findNearbyStrangers(currentUser, 55.75580, 37.61730);

        assertEquals(1, results.size());
        assertEquals(stranger.getId(), results.get(0).otherUser().getId());
        assertTrue(results.get(0).notificationForOther().getDistanceMeters() <= 50.0);
    }

    @Test
    void findNearbyStrangersSkipsBlockedUsers() {
        Users currentUser = user(1, "amin");
        Users stranger = user(2, "ivan");

        when(locationRepository.findByTimestampAfter(any(LocalDateTime.class)))
                .thenReturn(List.of(location(stranger, 55.75581, 37.61731)));
        when(friendshipRepository.findFriendshipBetween(currentUser, stranger)).thenReturn(Optional.empty());
        when(blockRepository.isBlockedInAnyDirection(currentUser, stranger)).thenReturn(true);

        List<ProximityService.ProximityResult> results =
                proximityService.findNearbyStrangers(currentUser, 55.75580, 37.61730);

        assertTrue(results.isEmpty());
    }

    @Test
    void findNearbyStrangersRespectsCooldownForSamePair() {
        Users currentUser = user(1, "amin");
        Users stranger = user(2, "ivan");

        when(locationRepository.findByTimestampAfter(any(LocalDateTime.class)))
                .thenReturn(List.of(location(stranger, 55.75581, 37.61731)));
        when(friendshipRepository.findFriendshipBetween(eq(currentUser), eq(stranger))).thenReturn(Optional.empty());
        when(blockRepository.isBlockedInAnyDirection(currentUser, stranger)).thenReturn(false);

        List<ProximityService.ProximityResult> firstCall =
                proximityService.findNearbyStrangers(currentUser, 55.75580, 37.61730);
        List<ProximityService.ProximityResult> secondCall =
                proximityService.findNearbyStrangers(currentUser, 55.75580, 37.61730);

        assertEquals(1, firstCall.size());
        assertTrue(secondCall.isEmpty());
    }

    private Users user(int id, String name) {
        Users user = new Users();
        user.setId(id);
        user.setName(name);
        return user;
    }

    private Location location(Users user, double lat, double lng) {
        Location location = new Location();
        location.setUser(user);
        location.setLat(lat);
        location.setLng(lng);
        location.setTimestamp(LocalDateTime.now());
        return location;
    }
}
