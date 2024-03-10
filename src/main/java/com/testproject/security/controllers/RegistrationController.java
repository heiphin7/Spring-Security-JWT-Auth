package com.testproject.security.controllers;

import com.testproject.security.dtos.JwtResponce;
import com.testproject.security.dtos.RegistrationUserDto;
import com.testproject.security.entity.Role;
import com.testproject.security.entity.User;
import com.testproject.security.exceptions.AppErorr;
import com.testproject.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/reg")
    public ResponseEntity<?> registration(@RequestBody RegistrationUserDto regUser) {
        if (!regUser.getPassword().equals(regUser.getConfirmPassword())) {
            return new ResponseEntity<>(new AppErorr(HttpStatus.CONFLICT.value(), "Пароли не совпадают"), HttpStatus.CONFLICT);
        } else if (regUser.getPassword() == null || regUser.getPassword().isEmpty()) {
            return new ResponseEntity<>(new AppErorr(HttpStatus.CONFLICT.value(), "Пароль не должен быть пустым"), HttpStatus.CONFLICT);
        }else{
            Optional<User> user = userService.findByUsername(regUser.getUsername());

            if(user.isPresent()){
                return new ResponseEntity<>(new AppErorr(HttpStatus.BAD_REQUEST.value(), "Имя пользователя занято"), HttpStatus.BAD_REQUEST);
            }
        }

        String encodedPassword = bCryptPasswordEncoder.encode(regUser.getPassword());
        User registrationUser = new User();
        registrationUser.setUsername(regUser.getUsername());
        registrationUser.setPassword(encodedPassword);
        registrationUser.setEmail(regUser.getEmail());

        userService.createNewUser(registrationUser);
        return ResponseEntity.ok("Успешная регистрация");
    }
}