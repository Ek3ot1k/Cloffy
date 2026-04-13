package ru.amin.Rest.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.amin.Rest.entity.Frame;
import ru.amin.Rest.entity.UserFrame;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.FrameRepository;
import ru.amin.Rest.repositories.UserFrameRepository;
import ru.amin.Rest.repositories.UserRepository;
import ru.amin.Rest.util.UserNotFoundException;

import java.util.List;

@Service
public class ShopService {

    private final FrameRepository frameRepository;
    private final UserFrameRepository userFrameRepository;
    private final UserRepository userRepository;

    public ShopService(FrameRepository frameRepository,
                       UserFrameRepository userFrameRepository,
                       UserRepository userRepository) {
        this.frameRepository = frameRepository;
        this.userFrameRepository = userFrameRepository;
        this.userRepository = userRepository;
    }

    // Все доступные рамки в магазине
    public List<Frame> getAllFrames() {
        return frameRepository.findAll();
    }

    // Купить рамку за монеты
    @Transactional
    public int buyFrame(Users currentUser, int frameId) {
        Frame frame = frameRepository.findById(frameId)
                .orElseThrow(() -> new UserNotFoundException("Рамка не найдена"));

        Users user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (userFrameRepository.existsByUserAndFrame(user, frame)) {
            throw new IllegalStateException("Рамка уже куплена");
        }

        if (user.getCoins() < frame.getPrice()) {
            throw new IllegalStateException("Недостаточно монет. Нужно: " + frame.getPrice() + ", есть: " + user.getCoins());
        }

        user.setCoins(user.getCoins() - frame.getPrice());
        userRepository.save(user);
        userFrameRepository.save(new UserFrame(user, frame));

        return user.getCoins(); // возвращаем остаток монет
    }

    // Надеть рамку (должна быть куплена)
    @Transactional
    public void equipFrame(Users currentUser, int frameId) {
        Frame frame = frameRepository.findById(frameId)
                .orElseThrow(() -> new UserNotFoundException("Рамка не найдена"));

        Users user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (!userFrameRepository.existsByUserAndFrame(user, frame)) {
            throw new IllegalStateException("Сначала купите эту рамку");
        }

        user.setActiveFrame(frame);
        userRepository.save(user);
    }

    // Снять рамку
    @Transactional
    public void unequipFrame(Users currentUser) {
        Users user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        user.setActiveFrame(null);
        userRepository.save(user);
    }

    // Купленные рамки пользователя
    public List<Frame> getMyFrames(Users currentUser) {
        Users user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        return userFrameRepository.findByUser(user)
                .stream()
                .map(UserFrame::getFrame)
                .toList();
    }
}
