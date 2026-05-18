package ru.amin.Rest.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.amin.Rest.dto.UserDTO;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.repositories.UserRepository;
import ru.amin.Rest.security.UsersDetails;
import ru.amin.Rest.util.UserNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public UserController(ModelMapper modelMapper, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    // Профиль текущего авторизованного пользователя
    @GetMapping("/me")
    public Users getMe(@AuthenticationPrincipal UsersDetails usersDetails) {
        return usersDetails.getUser();
    }

    @GetMapping("/{id}/info")
    public UserDTO getUser(@PathVariable("id") int id) {
        return convertToUserDTO(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден")));
    }

    // Поиск пользователей по нику (частичное совпадение)
    @GetMapping("/search")
    public List<UserDTO> searchUsers(@RequestParam("name") String name,
                                     @AuthenticationPrincipal UsersDetails usersDetails) {
        return userRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .filter(u -> u.getId() != usersDetails.getUser().getId()) // исключаем себя
                .map(this::convertToUserDTO)
                .toList();
    }

//    @GetMapping("/{id}/friends")
//    public List<Friendship> getFriends(@PathVariable("id") int id){
//        UserDTO userDTO=convertToUserDTO(userRepository.findById(id).orElseThrow(null));
//        return userDTO.getFriendships();
//    }

//    @PostMapping("/{id}/edit")
//    public HttpEntity<HttpStatus> editProfile(@PathVariable("id") int id,
//                                              BindingResult bindingResult){
//        if(bindingResult.hasErrors()){
//            StringBuilder errorMsg=new StringBuilder();
//            List<FieldError> errors=bindingResult.getFieldErrors();
//            for(FieldError error:errors){
//                errorMsg.append(error.getField())
//                        .append(" - ").append(error.getDefaultMessage()).append(";");
//
//
//            }
//            throw  new UserNotEditedException(errorMsg.toString());
//        }
//
//        Users user=userRepository.findById(id).orElseThrow(null);
//        user.setAge();
//    }


    private UserDTO convertToUserDTO(Users user){
        return modelMapper.map(user, UserDTO.class);
    }

    private Users convertToUser(UserDTO userDTO){
        return modelMapper.map(userDTO, Users.class);
    }
}
