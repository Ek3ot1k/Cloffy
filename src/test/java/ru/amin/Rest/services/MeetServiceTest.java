package ru.amin.Rest.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.amin.Rest.dto.MeetRequestCreateDTO;
import ru.amin.Rest.dto.MeetResponseDTO;
import ru.amin.Rest.entity.MeetRequest;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.MeetRequestRepository;
import ru.amin.Rest.util.MeetStatus;
import ru.amin.Rest.util.UserNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetServiceTest {

    @Mock
    private MeetRequestRepository meetRequestRepository;

    @Mock
    private FriendshipService friendshipService;

    @Mock
    private BlockService blockService;

    @InjectMocks
    private MeetService meetService;

    @Test
    void requestMeetCreatesPendingRequestForFriend() {
        Users requester = user(1, "amin");
        Users receiver = user(2, "ivan");
        MeetRequestCreateDTO dto = new MeetRequestCreateDTO();
        dto.setMeetLat(55.75);
        dto.setMeetLng(37.61);

        when(blockService.isBlocked(requester, receiver)).thenReturn(false);
        when(friendshipService.getAcceptedFriends(requester)).thenReturn(List.of(receiver));
        when(meetRequestRepository.findActiveBetweenUsers(requester, receiver, MeetStatus.PENDING))
                .thenReturn(Optional.empty());

        MeetResponseDTO response = meetService.requestMeet(requester, receiver, dto);

        assertEquals(requester.getId(), response.getRequesterId());
        assertEquals(receiver.getId(), response.getReceiverId());
        assertEquals(MeetStatus.PENDING, response.getStatus());
        verify(meetRequestRepository).save(any(MeetRequest.class));
    }

    @Test
    void requestMeetRejectsDuplicatePendingRequest() {
        Users requester = user(1, "amin");
        Users receiver = user(2, "ivan");
        MeetRequestCreateDTO dto = new MeetRequestCreateDTO();
        dto.setMeetLat(55.75);
        dto.setMeetLng(37.61);

        when(blockService.isBlocked(requester, receiver)).thenReturn(false);
        when(friendshipService.getAcceptedFriends(requester)).thenReturn(List.of(receiver));
        when(meetRequestRepository.findActiveBetweenUsers(requester, receiver, MeetStatus.PENDING))
                .thenReturn(Optional.of(new MeetRequest(requester, receiver, dto.getMeetLat(), dto.getMeetLng())));

        assertThrows(UserNotFoundException.class, () -> meetService.requestMeet(requester, receiver, dto));
        verify(meetRequestRepository, never()).save(any(MeetRequest.class));
    }

    @Test
    void requestMeetRejectsSelfRequest() {
        Users requester = user(1, "amin");
        MeetRequestCreateDTO dto = new MeetRequestCreateDTO();
        dto.setMeetLat(55.75);
        dto.setMeetLng(37.61);

        assertThrows(UserNotFoundException.class, () -> meetService.requestMeet(requester, requester, dto));
        verify(meetRequestRepository, never()).save(any(MeetRequest.class));
    }

    private Users user(int id, String name) {
        Users user = new Users();
        user.setId(id);
        user.setName(name);
        return user;
    }
}
