package ru.amin.Rest.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.amin.Rest.security.JWTUtil;
import ru.amin.Rest.services.UsersDetailsService;

// Перехватчик WebSocket-соединений для аутентификации по JWT
@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JWTUtil jwtUtil;
    private final UsersDetailsService usersDetailsService;

    public WebSocketAuthChannelInterceptor(JWTUtil jwtUtil, UsersDetailsService usersDetailsService) {
        this.jwtUtil = jwtUtil;
        this.usersDetailsService = usersDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    String username = jwtUtil.validateTokenAndRetrieveClaim(token);
                    UserDetails userDetails = usersDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    accessor.setUser(auth);
                } catch (JWTVerificationException e) {
                    throw new IllegalArgumentException("Невалидный JWT токен в WebSocket");
                }
            }
        }
        return message;
    }
}
