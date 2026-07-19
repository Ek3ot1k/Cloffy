package ru.amin.Rest.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.amin.Rest.entity.Friendship;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.FriendshipRepository;
import ru.amin.Rest.repositories.UserRepository;
import ru.amin.Rest.util.FriendshipStatus;
import ru.amin.Rest.util.UserNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Testcontainers
@SpringBootTest
public class FriendshipServiceIT {

    @Container
    static PostgreSQLContainer<?> postgres=new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url",postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.redis.host",REDIS_CONTAINER::getHost);
        registry.add("spring.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());
    }


    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;



    @Container
    static GenericContainer<?> REDIS_CONTAINER=
            new GenericContainer<>(DockerImageName.parse("redis:7"))
                    .withExposedPorts(6379);

    @BeforeEach
    void clearDB(){
        friendshipRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void addFriendRequestSavesWithPendingStatus(){
        Users user1=new Users();
        Users user2=new Users();
        user1.setName("Amin");
        user2.setName("Zamin");
        Users savedUser1=userRepository.save(user1);
        Users savedUser2=userRepository.save(user2);

        friendshipService.addFriendRequest(savedUser1,savedUser2);

        Friendship friendship=friendshipRepository.findFriendshipBetween(savedUser1,savedUser2).orElse(null);

        assertThat(friendship).isNotNull();
        assertEquals(FriendshipStatus.PENDING,friendship.getStatus());
    }

    @Test
    void addFriendRequestRejectsDuplicateRequest(){
        Users user1=new Users();
        Users user2=new Users();
        user1.setName("Amin");
        user2.setName("Zamin");
        Users savedUser1=userRepository.save(user1);
        Users savedUser2=userRepository.save(user2);

        friendshipService.addFriendRequest(savedUser1,savedUser2);

        assertThrows(UserNotFoundException.class,()->friendshipService.addFriendRequest(
                savedUser1,savedUser2));
    }
}