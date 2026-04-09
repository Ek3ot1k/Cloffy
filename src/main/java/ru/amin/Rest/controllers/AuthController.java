package ru.amin.Rest.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.amin.Rest.dto.UserDTO;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.security.JWTUtil;
import ru.amin.Rest.services.RegistrationService;
import ru.amin.Rest.util.UserValidator;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ModelMapper modelMapper;
    private final RegistrationService registrationService;
    private final JWTUtil jwtUtil;
    private final UserValidator userValidator;
    private final AuthenticationManager authenticationManager;


    public AuthController(ModelMapper modelMapper, RegistrationService registrationService, JWTUtil jwtUtil, UserValidator userValidator, AuthenticationManager authenticationManager) {
        this.modelMapper = modelMapper;
        this.registrationService = registrationService;
        this.jwtUtil = jwtUtil;
        this.userValidator = userValidator;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody Map<String,String> loginRequest){
        try{
            String username=loginRequest.get("name");
            String password=loginRequest.get("password");

            Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token= jwtUtil.generateToken(username);

            return ResponseEntity.ok(Map.of("jwt-token", token));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<?> performRegistration(@RequestBody @Valid UserDTO userDTO,
                                                  BindingResult bindingResult) {
        Users user=convertToUser(userDTO);
        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Ошибка валидации"));
        }

        registrationService.register(user);

        String token=jwtUtil.generateToken(user.getName());
        return ResponseEntity.ok(Map.of("jwt-token",token));
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotReadable(Exception e) {
        return Map.of("error", "Invalid JSON: " + e.getMessage());
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    }

//    private Users convertToUser(UserDTO userDTO){
//        return this.modelMapper.map(userDTO, Users.class);
//    }
}
