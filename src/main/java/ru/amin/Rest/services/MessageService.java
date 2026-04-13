package ru.amin.Rest.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.amin.Rest.dto.ConversationSummaryDTO;
import ru.amin.Rest.dto.MessageResponseDTO;
import ru.amin.Rest.entity.Message;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.MessageRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    // Список диалогов: один ConversationSummaryDTO на каждого собеседника (последнее сообщение)
    public List<ConversationSummaryDTO> getConversations(Users user) {
        List<Message> messages = messageRepository.findAllByUser(user);
        // Ключ — id собеседника. LinkedHashMap сохраняет порядок вставки (сначала новейшие)
        Map<Integer, ConversationSummaryDTO> map = new LinkedHashMap<>();
        for (Message m : messages) {
            Users partner = m.getSender().getId() == user.getId() ? m.getReceiver() : m.getSender();
            map.putIfAbsent(partner.getId(), new ConversationSummaryDTO(
                    partner.getId(),
                    partner.getName(),
                    m.getContent(),
                    m.getTimestamp()
            ));
        }
        return new ArrayList<>(map.values());
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
