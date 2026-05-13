package ru.amin.Rest.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.amin.Rest.dto.MessageDTO;
import ru.amin.Rest.dto.MessageResponseDTO;
import ru.amin.Rest.entity.Message;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.MessageRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {
    @Mock
    MessageRepository messageRepository;

    @InjectMocks
    MessageService messageService;

    @Test
    void sendMessageTest(){
        Users user=new Users();
        user.setId(1);
        Users friend=new Users();
        friend.setId(2);

        messageService.sendMessage(user,friend,"kmem");
        verify(messageRepository).save(any(Message.class));
        MessageResponseDTO result=messageService.sendMessage(user,friend,"kmem");
        assertEquals("kmem",result.getContent());
    }

    @Test
    void sendMessageReturnsCorrectDTO(){
        Users user=new Users();
        user.setId(1);
        Users friend=new Users();
        friend.setId(2);

        MessageResponseDTO responseDTO=messageService.sendMessage(user,friend,"speed");
        assertEquals("speed",responseDTO.getContent());
    }
}
