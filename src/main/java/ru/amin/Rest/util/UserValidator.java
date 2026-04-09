package ru.amin.Rest.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.services.UsersDetailsService;

@Component
public class UserValidator implements Validator {
    private final UsersDetailsService usersDetailsService;

    public UserValidator(UsersDetailsService usersDetailsService) {
        this.usersDetailsService=usersDetailsService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Users.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Users user=(Users) o;

        try {
            usersDetailsService.loadUserByUsername(user.getName());
        } catch (UsernameNotFoundException ignored) {
            return; // все ок, пользователь не найден
        }

        errors.rejectValue("name", "", "Человек с таким именем пользователя уже существует");
    }
}
