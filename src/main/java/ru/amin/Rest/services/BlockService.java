package ru.amin.Rest.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.amin.Rest.dto.CommentResponseDTO;
import ru.amin.Rest.entity.Block;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.BlockRepository;
import ru.amin.Rest.repositories.FriendshipRepository;
import ru.amin.Rest.util.UserNotFoundException;

import java.util.List;

@Service
public class BlockService {

    private final BlockRepository blockRepository;
    private final FriendshipRepository friendshipRepository;

    public BlockService(BlockRepository blockRepository, FriendshipRepository friendshipRepository) {
        this.blockRepository = blockRepository;
        this.friendshipRepository = friendshipRepository;
    }

    @Transactional
    public void blockUser(Users blocker, Users blocked) {
        if (blockRepository.findByBlockerAndBlocked(blocker, blocked).isPresent()) {
            return; // уже заблокирован
        }
        blockRepository.save(new Block(blocker, blocked));
        friendshipRepository.findFriendshipBetween(blocker, blocked)
                .ifPresent(friendshipRepository::delete);
    }

    @Transactional
    public void unblockUser(Users blocker, Users blocked) {
        Block block = blockRepository.findByBlockerAndBlocked(blocker, blocked)
                .orElseThrow(() -> new UserNotFoundException("Блокировка не найдена"));
        blockRepository.delete(block);
    }

    // Список пользователей, заблокированных текущим пользователем
    public List<Users> getBlockedUsers(Users blocker) {
        return blockRepository.findByBlocker(blocker)
                .stream()
                .map(Block::getBlocked)
                .toList();
    }

    // Проверка блокировки в любом направлении
    public boolean isBlocked(Users a, Users b) {
        return blockRepository.isBlockedInAnyDirection(a, b);
    }
}
