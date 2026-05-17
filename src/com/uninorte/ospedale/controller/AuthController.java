/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uninorte.ospedale.controller;

import com.uninorte.ospedale.controller.response.Response;
import com.uninorte.ospedale.controller.response.ResponseFactory;
import com.uninorte.ospedale.controller.validator.UserValidator;
import com.uninorte.ospedale.model.dto.UserSessionDTO;
import com.uninorte.ospedale.model.enums.Role;
import com.uninorte.ospedale.model.entity.Administrator;
import com.uninorte.ospedale.model.entity.Doctor;
import com.uninorte.ospedale.model.entity.Patient;
import com.uninorte.ospedale.model.entity.User;
import com.uninorte.ospedale.model.repository.IUserRepository;
import java.util.Optional;


/**
 *
 * @author Matt
 */
public class AuthController {
    
     private final IUserRepository userRepository;

    public AuthController(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Response<UserSessionDTO> login(String username, String password) {

        // 1. Validar formato de inputs
        Optional<String> usernameError = UserValidator.validateUsername(username);
        if (usernameError.isPresent())
            return ResponseFactory.badRequest(usernameError.get());

        Optional<String> passwordError = UserValidator.validatePassword(password);
        if (passwordError.isPresent())
            return ResponseFactory.badRequest(passwordError.get());

        // 2. Buscar usuario por username
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty())
            return ResponseFactory.notFound("Usuario no encontrado");

        User user = userOpt.get();

        // 3. Validar contraseña
        if (!user.getPassword().equals(password))
            return ResponseFactory.unauthorized("Credenciales inválidas");

        // 4. Construir DTO con rol — único instanceof permitido en toda la app
        UserSessionDTO session = buildSession(user);

        return ResponseFactory.ok("Bienvenido " + user.getFirstname(), session);
    }

    // Único lugar donde se usa instanceof — resuelve el rol y encap    sula aquí
    private UserSessionDTO buildSession(User user) {
    Role role;
    if (user instanceof Administrator) {
        role = Role.ADMIN;
    } else if (user instanceof Patient) {
        role = Role.PATIENT;
    } else if (user instanceof Doctor) {
        role = Role.DOCTOR;
    } else {
        role = Role.ADMIN;
    }
    return new UserSessionDTO(
        user.getId(),
        user.getUsername(),
        user.getFirstname(),
        user.getLastname(),
        role
    );
    
    }
    
}