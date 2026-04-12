package ru.amin.Rest.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.amin.Rest.dto.MessageResponseDTO;
import ru.amin.Rest.entity.Message;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.MessageRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    // Сохраняем сообщение в БД и возвращаем DTO для рассылки
    @Transactional
    public MessageResponseDTO sendMessage(Users sender, Users receiver, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);

        return toDTO(message);
    }

    // История переписки между двумя пользователями
    public List<MessageResponseDTO> getConversation(Users user, Users other) {
        return messageRepository.findConversation(user, other)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private MessageResponseDTO toDTO(Message message) {
        return new MessageResponseDTO(
                message.getId(),
                message.getSender().getId(),
                message.getSender().getName(),
                message.getReceiver().getId(),
                message.getContent(),
                message.getTimestamp()
        );
    }
}
