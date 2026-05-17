/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller;

import com.uninorte.ospedale.controller.response.Response;
import com.uninorte.ospedale.controller.response.ResponseFactory;
import com.uninorte.ospedale.model.repository.IUserRepository;
import java.util.Optional;
import packagee.User;

/**
 *
 * @author Matt
 */
public class AuthController {
    
     private final IUserRepository userRepository;

    public AuthController(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Response<Object> login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return ResponseFactory.badRequest("Username and password are required");
        }
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isEmpty()) {
            return ResponseFactory.notFound("User not found");
        }
        User user = found.get();
        if (!user.getPassword().equals(password)) {
            return ResponseFactory.unauthorized("Invalid password");
        }
        return ResponseFactory.ok("Login successful", user.getClass().getSimpleName() + ":" + user.getId());
    }
}




