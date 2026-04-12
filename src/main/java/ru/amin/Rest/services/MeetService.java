package ru.amin.Rest.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.amin.Rest.dto.MeetRequestCreateDTO;
import ru.amin.Rest.dto.MeetResponseDTO;
import ru.amin.Rest.entity.MeetRequest;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.MeetRequestRepository;
import ru.amin.Rest.util.MeetStatus;
import ru.amin.Rest.util.UserNotFoundException;

import java.util.List;

@Service
public class MeetService {

    private final MeetRequestRepository meetRequestRepository;
    private final FriendshipService friendshipService;
    private final BlockService blockService;

    public MeetService(MeetRequestRepository meetRequestRepository,
                       FriendshipService friendshipService,
                       BlockService blockService) {
        this.meetRequestRepository = meetRequestRepository;
        this.friendshipService = friendshipService;
        this.blockService = blockService;
    }

    // Предложить встречу другу
    @Transactional
    public MeetResponseDTO requestMeet(Users requester, Users receiver, MeetRequestCreateDTO dto) {
        if (requester.getId() == receiver.getId()) {
            throw new UserNotFoundException("Нельзя предложить встречу самому себе");
        }

        if (blockService.isBlocked(requester, receiver)) {
            throw new UserNotFoundException("Невозможно предложить встречу этому пользователю");
        }

        // Нельзя предложить встречу не-другу
        boolean areFriends = friendshipService.getAcceptedFriends(requester)
                .stream()
                .anyMatch(f -> f.getId() == receiver.getId());

        if (!areFriends) {
            throw new UserNotFoundException("Встречу можно предложить только другу");
        }

        if (meetRequestRepository.findActiveBetweenUsers(requester, receiver, MeetStatus.PENDING).isPresent()) {
            throw new UserNotFoundException("Активный запрос встречи уже существует");
        }

        MeetRequest meet = new MeetRequest(requester, receiver, dto.getMeetLat(), dto.getMeetLng());
        meetRequestRepository.save(meet);
        return toDTO(meet);
    }

    // Принять встречу
    @Transactional
    public MeetResponseDTO acceptMeet(int meetId, Users receiver) {
        MeetRequest meet = findAndValidate(meetId, receiver);
        meet.setStatus(MeetStatus.ACCEPTED);
        return toDTO(meet);
    }

    // Отклонить встречу
    @Transactional
    public MeetResponseDTO declineMeet(int meetId, Users receiver) {
        MeetRequest meet = findAndValidate(meetId, receiver);
        meet.setStatus(MeetStatus.DECLINED);
        return toDTO(meet);
    }

    // Активные (PENDING/ACCEPTED) встречи пользователя
    public List<MeetResponseDTO> getActiveMeets(Users user) {
        List<MeetRequest> pending = meetRequestRepository.findByUserAndStatus(user, MeetStatus.PENDING);
        List<MeetRequest> accepted = meetRequestRepository.findByUserAndStatus(user, MeetStatus.ACCEPTED);
        return java.util.stream.Stream.concat(pending.stream(), accepted.stream())
                .distinct()
                .map(this::toDTO)
                .toList();
    }

    private MeetRequest findAndValidate(int meetId, Users receiver) {
        MeetRequest meet = meetRequestRepository.findById(meetId)
                .orElseThrow(() -> new UserNotFoundException("Запрос встречи не найден"));

        if (meet.getReceiver().getId() != receiver.getId()) {
            throw new UserNotFoundException("Это не ваш запрос встречи");
        }
        if (meet.getStatus() != MeetStatus.PENDING) {
            throw new UserNotFoundException("Запрос уже обработан");
        }
        return meet;
    }

    public MeetResponseDTO toDTO(MeetRequest meet) {
        return new MeetResponseDTO(
                meet.getId(),
                meet.getRequester().getId(),
                meet.getRequester().getName(),
                meet.getReceiver().getId(),
                meet.getReceiver().getName(),
                meet.getMeetLat(),
                meet.getMeetLng(),
                meet.getStatus(),
                meet.getCreatedAt()
        );
    }
}
