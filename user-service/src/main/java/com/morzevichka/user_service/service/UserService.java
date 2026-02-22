package com.morzevichka.user_service.service;

import com.morzevichka.user_service.model.User;
import com.morzevichka.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public void addUser(String email, String login) {
        User user = User.builder()
                .email(email)
                .login(login)
                .build();

        repository.save(user);
    }

}
