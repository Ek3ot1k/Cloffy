package ru.amin.Rest.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.amin.Rest.dto.LocationDTO;
import ru.amin.Rest.entity.Location;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.LocationRepository;
import ru.amin.Rest.repositories.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    @Mock
    LocationRepository locationRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    LocationService locationService;

    @Test
    void saveOrUpdateLocation(){
        Users user=new Users();
        user.setId(1);
        user.setName("amin");

        LocationDTO locationDTO=new LocationDTO();
        locationDTO.setLat(55.75);
        locationDTO.setLng(37.61);

        when(locationRepository.findByUser(user)).thenReturn(Optional.empty());

        locationService.saveOrUpdateLocation(user,locationDTO);

        verify(locationRepository).save(any(Location.class));
    }

    @Test
    void updateLocationWhenAlreadyExists(){
        Users user=new Users();

        Location location=new Location();
        location.setLat(55.00);
        location.setLng(37.00);

        LocationDTO locationDTO=new LocationDTO();
        locationDTO.setLat(55.75);
        locationDTO.setLng(37.61);

        when(locationRepository.findByUser(user)).thenReturn(Optional.of(location));
        locationService.saveOrUpdateLocation(user,locationDTO);

        assertEquals(55.75,location.getLat());
        assertEquals(37.61,location.getLng());
        verify(locationRepository).save(location);

    }

    @Test
    void saveLocationUpdatesBatteryLevel(){
        Users user=new Users();
        user.setId(1);
        user.setName("amin");

        LocationDTO locationDTO=new LocationDTO();
        locationDTO.setBatteryLevel(67);

        when(locationRepository.findByUser(user)).thenReturn(Optional.empty());
        locationService.saveOrUpdateLocation(user,locationDTO);
        verify(userRepository).save(user);
        assertEquals(67,user.getBatteryLevel());
    }
}
